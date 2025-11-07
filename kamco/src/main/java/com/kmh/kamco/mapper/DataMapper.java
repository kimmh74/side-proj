package com.kmh.kamco.mapper;

import com.kmh.kamco.model.Data;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DataMapper {
    void save(Data data);
    List<Data> findAll();
    void deleteAll();
    long count();
}