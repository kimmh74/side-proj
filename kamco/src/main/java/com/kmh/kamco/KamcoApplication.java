package com.kmh.kamco;

import com.kmh.kamco.mapper.UserMapper;
import com.kmh.kamco.mapper.DataMapper;
import com.kmh.kamco.model.User;
import com.kmh.kamco.model.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@MapperScan("com.kmh.kamco.mapper")
public class KamcoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KamcoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserMapper userMapper, DataMapper dataMapper, PasswordEncoder passwordEncoder) {
		return args -> {
			// --- userdb (구 logdb) 초기 사용자 등록 로직 유지 ---
			if (!userMapper.existsByUsername("testuser")) {
				User user = new User();
				user.setUsername("testuser");
				user.setPassword(passwordEncoder.encode("1234"));
				userMapper.save(user);
				System.out.println("초기 사용자 'testuser'가 등록되었습니다.");
			}
			if (!userMapper.existsByUsername("admin")) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("password"));
				userMapper.save(admin);
				System.out.println("초기 사용자 'admin'이 등록되었습니다.");
			}

			// --- datadb 10개 한글 더미 데이터 생성 로직 ---
			System.out.println("기존 datadb 데이터 초기화 및 10개 한글 더미 데이터 생성 시작.");
			dataMapper.deleteAll(); // 기존 데이터 모두 삭제
			Random random = new Random();

			String[] categoryPrefixes = {"부동산", "차량", "기계", "재산"};
			String[] categorySuffixes = {"(토지)", "(건물)", "(공장)", "(승용차)", "(건설장비)"};
			String[] itemNames = {"서울 아파트", "경기 오피스텔", "부산 상가", "제주도 토지", "벤츠 승용차", "포크레인 굴삭기"};
			String[] descriptions = {"탁월한 입지", "투자 가치", "신속 처분", "상태 최상", "특별 할인"};
			String[] cities = {"서울", "경기", "인천", "부산", "대구", "광주", "대전", "울산", "세종"};
			String[] guNames = {"강남구", "송파구", "해운대구", "유성구", "수성구", "북구", "남구", "동구", "서구"};
			String[] disposeMethods = {"일반경쟁입찰", "제한경쟁입찰", "지명경쟁입찰"};
			String[] auctionStatus = {"공고중", "입찰마감 임박", "유찰", "낙찰 완료"};
			
			for (int i = 1; i <= 1000; i++) { // 10개의 데이터만 생성
				Data data = new Data();

				String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

				data.setPlnmNo("공고-" + uuid); // 공고번호
				data.setPbctCdtnNo("조건-" + (100 + i)); // 공고조건번호
				data.setCltrNo("물건-" + uuid); // 물건관리번호
				data.setCltrHstrNo("이력-" + (500 + i)); // 물건이력번호
				
				String category = categoryPrefixes[random.nextInt(categoryPrefixes.length)] + categorySuffixes[random.nextInt(categorySuffixes.length)];
				data.setCtgrFullNm(category); // 카테고리명
				
				data.setBidMnmtNo("입찰-" + uuid); // 입찰물건관리번호
				String itemName = itemNames[random.nextInt(itemNames.length)] + " " + (i * 10) + "제곱미터";
				data.setCltrNm(itemName); // 물건이름
				data.setCltrMnmtNo("CM-" + uuid); // 물건관리번호 (요청대로 중복 필드)

				String city = cities[random.nextInt(cities.length)];
				String gu = guNames[random.nextInt(guNames.length)];
				data.setLdnmAdrs(city + " " + gu + " 지번주소 " + (random.nextInt(100) + 1) + "-" + (random.nextInt(20) + 1)); // 지번주소
				data.setNmrdAdrs(city + " " + gu + " 도로명주소 " + (random.nextInt(50) + 1) + "길 " + (random.nextInt(30) + 1)); // 도로명주소
				data.setLdnmPnu("PNU" + (10000000 + random.nextInt(90000000))); // 지번PNU

				String dpslMtd = disposeMethods[random.nextInt(disposeMethods.length)];
				data.setDpslMtdCd("M" + (random.nextInt(3) + 1)); // 처분방식코드
				data.setDpslMtdNm(dpslMtd); // 처분방식명

				BigDecimal minBidPrc = BigDecimal.valueOf(random.nextInt(900000000) + 100000000).setScale(0, BigDecimal.ROUND_DOWN); // 1억 ~ 10억
				BigDecimal apslAsesAvgAmt = minBidPrc.add(BigDecimal.valueOf(random.nextInt(50000000)).setScale(0, BigDecimal.ROUND_DOWN));
				data.setMinBidPrc(minBidPrc); // 최저입찰가격
				data.setApslAsesAvgAmt(apslAsesAvgAmt); // 감정평가평균액
				data.setFeeRate(BigDecimal.valueOf(0.1 + random.nextDouble() * 3).setScale(2, BigDecimal.ROUND_HALF_UP)); // 0.1% ~ 3.1%

				data.setPbctBegnDtm(LocalDateTime.now().minusDays(random.nextInt(7)).minusHours(random.nextInt(24))); // 공고개시일시 (최근 7일 이내)
				data.setPbctClsDtm(LocalDateTime.now().plusDays(random.nextInt(15)).plusHours(random.nextInt(24))); // 공고마감일시 (15일 이내)
				data.setPbctCltrStatNm(auctionStatus[random.nextInt(auctionStatus.length)]); // 공매물건상태명

				data.setUscbdCnt(random.nextInt(200)); // 0~199
				data.setIqryCnt(random.nextInt(5000)); // 0~4999
				data.setGoodsNm("KAMCO " + itemName + " " + descriptions[random.nextInt(descriptions.length)]); // 상품명

				data.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(60))); // 최근 두 달 이내 등록된 것처럼

				dataMapper.save(data);
			}
			System.out.println("datadb에 " + dataMapper.count() + "개 한글 더미 데이터가 생성되었습니다.");
		};
	}
}