package com.file.fileupload.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
    /**
    * id
    */
    private Integer id;

    /**
    * 相对路径
    */
    private String path;

    /**
    * 文件名
    */
    private String name;

    /**
    * 文件后缀
    */
    private String suffix;

    /**
    * 文件大小|字节B
    */
    private Integer size;

    /**
    * 文件创建时间
    */
    private Long createdAt;

    /**
    * 文件修改时间
    */
    private Long updatedAt;

    /**
    * 已上传分片
    */
    private Integer shardIndex;

    /**
    * 分片大小|B
    */
    private Integer shardSize;

    /**
    * 分片总数
    */
    private Integer shardTotal;

    /**
    * 文件标识
    */
    private String fileKey;
}