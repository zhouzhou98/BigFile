package com.file.fileupload;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.file.fileupload.mapper")
public class FileuploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileuploadApplication.class, args);
    }

}
