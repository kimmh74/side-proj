package com.kmh.kamco.controller;

import com.kmh.kamco.mapper.DataMapper;
import com.kmh.kamco.model.Data;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController // REST 컨트롤러임을 선언
@RequestMapping("/api/data") // 기본 경로 설정
@CrossOrigin(origins = "http://localhost:5173") // React 프론트엔드 URL 허용
public class DataController {

    private final DataMapper dataMapper;

    public DataController(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    // 모든 datadb 데이터를 조회하여 반환하는 GET 요청 API
    @GetMapping("/list")
    public ResponseEntity<List<Data>> getAllData() {
        List<Data> dataList = dataMapper.findAll();
        return ResponseEntity.ok(dataList); // 조회된 데이터를 200 OK 응답과 함께 반환
    }
}