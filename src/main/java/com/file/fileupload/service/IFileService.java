package com.file.fileupload.service;

import com.file.fileupload.domain.File;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFileService {
    void save(File file);

    List<File> check(String key);

    void update(File file);

    List<File> getList();

    File getOne(Integer id);

    ResponseEntity<Object> createFile(String path,String name);
}
