package com.file.fileupload.service.impl;


import cn.hutool.core.io.FileUtil;
import com.file.fileupload.domain.File;
import com.file.fileupload.mapper.FileMapper;
import com.file.fileupload.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements IFileService {
    @Autowired
    private FileMapper fileMapper;

    @Value("${file.basepath}")
    private String basePath;
    @Override
    public void save(File file) {
        //根据数据库的文件标识来查询 当前文件是否存在
        List<File> files = fileMapper.selectList(file.getFileKey());
        if(files.size()>0){
            fileMapper.updateByFileKey(file);
        }else {
            //不存在就添加
            fileMapper.insert(file);
        }
    }

    @Override
    public List<File> check(String key) {

        //根据数据库的文件标识来查询 当前文件是否存在
        List<File> files = fileMapper.selectList(key);
        return files;
    }

    @Override
    public void update(File file) {
        fileMapper.updateByFileKey(file);
    }

    @Override
    public List<File> getList() {
        return fileMapper.selectAll();
    }

    @Override
    public File getOne(Integer id) {
        return fileMapper.selectByPrimaryKey(id);
    }

    @Override
    public ResponseEntity<Object> createFile(String path,String name) {
        //1,构造文件对象
        java.io.File file=new java.io.File(basePath, name);
        if(file.exists()) {
            //将下载的文件，封装byte[]
            byte[] bytes=null;
            try {
                bytes = FileUtil.readBytes(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //创建封装响应头信息的对象
            HttpHeaders header=new HttpHeaders();
            //封装响应内容类型(APPLICATION_OCTET_STREAM 响应的内容不限定)
            header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            //设置下载的文件的名称
			header.setContentDispositionFormData("attachment", name);
            //创建ResponseEntity对象
            ResponseEntity<Object> entity=
                    new ResponseEntity<Object>(bytes, header, HttpStatus.CREATED);
            return entity;
        }
        return null;
    }
}
