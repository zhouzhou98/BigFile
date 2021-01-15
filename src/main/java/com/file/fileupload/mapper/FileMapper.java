package com.file.fileupload.mapper;

import com.file.fileupload.domain.File;
import org.springframework.stereotype.Component;

import java.util.List;

public interface FileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(File record);

    int insertSelective(File record);

    File selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(File record);

    int updateByPrimaryKey(File record);


    List<File> selectList(String fileKey);



    void updateByFileKey(File file);


    List<File> selectAll();
}