# BigFile
该系统使用前端分片的方式实现大文件的上传
参考链接
https://blog.csdn.net/xiaoashuo/article/details/104808807?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-2&spm=1001.2101.3001.4242

https://gitee.com/luckytuan/fast-loader
https://www.jb51.net/article/190808.htm





上传的基本原理就是前端根据文件大小，按块大小分成很多块,目前是一个一个块的依次上传上去，暂时还没有开始研究多线程的方式，后期将会深化这一方面的应用。
创建数据库：

/*
Navicat MySQL Data Transfer


Source Server         : localhost_3306
Source Server Version : 50556
Source Host           : localhost:3306
Source Database       : wenjian


Target Server Type    : MYSQL
Target Server Version : 50556
File Encoding         : 65001


Date: 2021-01-14 20:43:57
*/

'''
SET FOREIGN_KEY_CHECKS=0;


-- ----------------------------
-- Table structure for `file`
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `path` varchar(100) NOT NULL COMMENT '相对路径',
  `name` varchar(100) DEFAULT NULL COMMENT '文件名',
  `suffix` varchar(10) DEFAULT NULL COMMENT '文件后缀',
  `size` int(11) DEFAULT NULL COMMENT '文件大小|字节B',
  `created_at` bigint(20) DEFAULT NULL COMMENT '文件创建时间',
  `updated_at` bigint(20) DEFAULT NULL COMMENT '文件修改时间',
  `shard_index` int(11) DEFAULT NULL COMMENT '已上传分片',
  `shard_size` int(11) DEFAULT NULL COMMENT '分片大小|B',
  `shard_total` int(11) DEFAULT NULL COMMENT '分片总数',
  `file_key` varchar(100) DEFAULT NULL COMMENT '文件标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES ('11', 'D:/BaiduNetdiskDownload/2zONpvymyy80sOKugOQmm.mp4', 'f1e93354eefb4ec49e78d0ee04007723', 'mp4', '601020885', '1610509526776', '1610509526776', '29', '20971520', '29', '2zONpvymyy80sOKugOQmm');
'''

前端划分的方法是：
首先前台有个上传的file标签：
'<input name="file" type="file" id="inputfile"/>'

'''
通过jq的形式获取文件的大小、确认分片的大小、定义分片索引、定义分片的起始位置、定义分片结束的位置、截取当前的分片数据、分片的大小、总片数、后缀名
//获取表单中的file
var file=$('#inputfile').get(0).files[0];
//文件分片 以20MB去分片
var shardSize = 20 * 1024 * 1024;
//定义分片索引
var shardIndex = shardIndex;
//定义分片的起始位置
var start = (shardIndex-1) * shardSize;
//定义分片结束的位置 file哪里来的?
var end = Math.min(file.size,start + shardSize);
//从文件中截取当前的分片数据
var fileShard = file.slice(start,end);
//分片的大小
var size = file.size;
//总片数
var shardTotal = Math.ceil(size / shardSize);
//文件的后缀名
var fileName = file.name;
var suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length).toLowerCase();

接着我们根据上面获得的消息，我们可以构建视频信息的md5密钥
//把视频的信息存储为一个字符串
var filedetails=file.name+file.size+file.type+file.lastModifiedDate;
//使用当前文件的信息用md5加密生成一个key 这个加密是根据文件的信息来加密的 如果相同的文件 加的密还是一样的
var key = hex_md5(filedetails);
var key10 = parseInt(key,16);
//把加密的信息 转为一个64位的
var key62 = Tool._10to62(key10);
'''

###md5密钥的用处：去数据库中判断是否有该key存在，如果存在，则判断是否已经上传成功了，如果不存在，则开始上传文件。

###项目整体流程：
####项目采用springboot + mybatis+ jquery +thymeleaf组成

添加以下依赖：
'''
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.1</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.file</groupId>
    <artifactId>fileupload</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>fileupload</name>
    <description>Demo project for Spring Boot</description>


    <properties>
        <java.version>1.8</java.version>
    </properties>


    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.4</version>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.2.0</version>
        </dependency>


        <!--文件上传依赖-->
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.4</version>
        </dependency>
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <!--hutool-all -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>4.6.6</version>
        </dependency>
    </dependencies>


    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>


</project>

'''
'''
application.yml
server:
  port: 8080


mybatis:
  mapper-locations: classpath:mapper/*Mapper.xml
logging:
  level:
    com.file.fileupload.mapper: debug


spring:
  web:
    resources:
      static-locations: classpath:/static
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/wenjian?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
    username: root
    password: 123456
    type: com.alibaba.druid.pool.DruidDataSource
    platform: mysql
  druid:
    initial-size: 20
    max-active: 100
    max-wait: 60000
    pool-prepared-statements: true
    max-pool-prepared-statement-per-connection-size: 30
    time-between-eviction-runs-millis: 60000
    min-evictable-idle-time-millis: 300000
    test-while-idle: true
    test-on-borrow: false
    test-on-return: false
    validation-query: select 1
  thymeleaf:
    cache: false


file:
  basepath: D:/BaiduNetdiskDownload/
 
'''

###转发Controller

'''
package com.file.fileupload.controller;


import com.file.fileupload.domain.File;
import com.file.fileupload.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private IFileService fileService;
    @GetMapping("/file")
    public String file() {
        return "file";
    }




    @RequestMapping("/list")
    public String getList(Model model) throws ServletException, IOException {
        List<File> list=fileService.getList();


        model.addAttribute("list", list);
        return "download";
    }
}

'''
###上传下载Controller
'''
package com.file.fileupload.controller;




import com.file.fileupload.service.IFileService;
import com.file.fileupload.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.List;
import java.util.UUID;


@RestController
@Slf4j
public class FileController {
    @Autowired
    private IFileService fileService;


    public static final String BUSINESS_NAME = "普通分片上传";


    // 设置图片上传路径
    @Value("${file.basepath}")
    private String basePath;


    @RequestMapping("/check")
    public Result check(String key){
        List<com.file.fileupload.domain.File> check = fileService.check(key);


        //如果这个key存在的话 那么就获取上一个分片去继续上传
        if(check.size()!=0){
            return Result.ok("查询成功",check.get(0));
        }
        return Result.fail("查询失败,可以添加");
    }


    @RequestMapping("/upload")
    public String upload(MultipartFile file,
                         String suffix,
                         Integer shardIndex,
                         Integer shardSize,
                         Integer shardTotal,
                         Integer size,
                         String key
    ) throws IOException, InterruptedException {
        log.info("上传文件开始");
        //文件的名称
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        // 获取文件的扩展名
        //设置图片新的名字
        String fileName = new StringBuffer().append(key).append(".").append(suffix).toString(); // course\6sfSqfOwzmik4A4icMYuUe.mp4
        //这个是分片的名字
        String localfileName = new StringBuffer(fileName)
                .append(".")
                .append(shardIndex)
                .toString(); // course\6sfSqfOwzmik4A4icMYuUe.mp4.1
        // 以绝对路径保存重名命后的图片
        File targeFile=new File(basePath,localfileName);
        //上传这个图片
        file.transferTo(targeFile);
        //数据库持久化这个数据
        com.file.fileupload.domain.File file1=new com.file.fileupload.domain.File();
        file1.setPath(basePath+localfileName);
        file1.setName(name);
        file1.setSuffix(suffix);
        file1.setSize(size);
        file1.setCreatedAt(System.currentTimeMillis());
        file1.setUpdatedAt(System.currentTimeMillis());
        file1.setShardIndex(shardIndex);
        file1.setShardSize(shardSize);
        file1.setShardTotal(shardTotal);
        file1.setFileKey(key);
        //插入到数据库中
        //保存的时候 去处理一下 这个逻辑
        fileService.save(file1);
        //判断当前是不是最后一个分页 如果不是就继续等待其他分页 合并分页
        if(shardIndex .equals(shardTotal) ){
            file1.setPath(basePath+fileName);
            this.merge(file1);
        }
        return "上传成功";


    }




    @RequestMapping("/download")
    public ResponseEntity<Object> showImageByPath(Integer id){
       com.file.fileupload.domain.File file= fileService.getOne(id);
       String path=file.getPath();
       String name=file.getFileKey()+"."+file.getSuffix();
       return fileService.createFile(path,name);
    }
    private void merge(com.file.fileupload.domain.File file) throws FileNotFoundException, InterruptedException {
        //合并分片开始
        log.info("分片合并开始");
        String path = file.getPath(); //获取到的路径 没有.1 .2 这样的东西
        //截取视频所在的路径
        path = path.replace(basePath,"");
        Integer shardTotal= file.getShardTotal();
        File newFile = new File(basePath + path);
        FileOutputStream outputStream = new FileOutputStream(newFile,true); // 文件追加写入
        FileInputStream fileInputStream = null; //分片文件
        byte[] byt = new byte[10 * 1024 * 1024];
        int len;
        try {
            for (int i = 0; i < shardTotal; i++) {
                // 读取第i个分片
                fileInputStream = new FileInputStream(new File(basePath + path + "." + (i + 1))); // course\6sfSqfOwzmik4A4icMYuUe.mp4.1
                while ((len = fileInputStream.read(byt)) != -1) {
                    outputStream.write(byt, 0, len);
                }
            }
            fileService.update(file);
        } catch (IOException e) {
            log.error("分片合并异常", e);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                outputStream.close();
                log.info("IO流关闭");
            } catch (Exception e) {
                log.error("IO流关闭", e);
            }
        }
        log.info("分片结束了");
        //告诉java虚拟机去回收垃圾 至于什么时候回收 这个取决于 虚拟机的决定
        System.gc();
        //等待100毫秒 等待垃圾回收去 回收完垃圾
        Thread.sleep(100);
        log.info("删除分片开始");
        for (int i = 0; i < shardTotal; i++) {
            String filePath = basePath + path + "." + (i + 1);
            File file1 = new File(filePath);
            boolean result = file1.delete();
            log.info("删除{}，{}", filePath, result ? "成功" : "失败");
        }
        log.info("删除分片结束");
    }




}
'''


###文件实体类
'''
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

'''
###FileMapper接口
'''
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

'''


###FileMapper.xml
'''
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.file.fileupload.mapper.FileMapper">
  <resultMap id="BaseResultMap" type="com.file.fileupload.domain.File">
    <!--@mbg.generated-->
    <!--@Table `file`-->
    <id column="id" jdbcType="INTEGER" property="id" />
    <result column="path" jdbcType="VARCHAR" property="path" />
    <result column="name" jdbcType="VARCHAR" property="name" />
    <result column="suffix" jdbcType="VARCHAR" property="suffix" />
    <result column="size" jdbcType="INTEGER" property="size" />
    <result column="created_at" jdbcType="BIGINT" property="createdAt" />
    <result column="updated_at" jdbcType="BIGINT" property="updatedAt" />
    <result column="shard_index" jdbcType="INTEGER" property="shardIndex" />
    <result column="shard_size" jdbcType="INTEGER" property="shardSize" />
    <result column="shard_total" jdbcType="INTEGER" property="shardTotal" />
    <result column="file_key" jdbcType="VARCHAR" property="fileKey" />
  </resultMap>
  <sql id="Base_Column_List">
    <!--@mbg.generated-->
    id, `path`, `name`, suffix, `size`, created_at, updated_at, shard_index, shard_size,
    shard_total, file_key
  </sql>
  <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
    <!--@mbg.generated-->
    select
    <include refid="Base_Column_List" />
    from `file`
    where id = #{id,jdbcType=INTEGER}
  </select>
  <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer">
    <!--@mbg.generated-->
    delete from `file`
    where id = #{id,jdbcType=INTEGER}
  </delete>
  <insert id="insert" keyColumn="id" keyProperty="id" parameterType="com.file.fileupload.domain.File" useGeneratedKeys="true">
    <!--@mbg.generated-->
    insert into `file` (`path`, `name`, suffix,
      `size`, created_at, updated_at,
      shard_index, shard_size, shard_total,
      file_key)
    values (#{path,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{suffix,jdbcType=VARCHAR},
      #{size,jdbcType=INTEGER}, #{createdAt,jdbcType=BIGINT}, #{updatedAt,jdbcType=BIGINT},
      #{shardIndex,jdbcType=INTEGER}, #{shardSize,jdbcType=INTEGER}, #{shardTotal,jdbcType=INTEGER},
      #{fileKey,jdbcType=VARCHAR})
  </insert>
  <insert id="insertSelective" keyColumn="id" keyProperty="id" parameterType="com.file.fileupload.domain.File" useGeneratedKeys="true">
    <!--@mbg.generated-->
    insert into `file`
    <trim prefix="(" suffix=")" suffixOverrides=",">
      <if test="path != null">
        `path`,
      </if>
      <if test="name != null">
        `name`,
      </if>
      <if test="suffix != null">
        suffix,
      </if>
      <if test="size != null">
        `size`,
      </if>
      <if test="createdAt != null">
        created_at,
      </if>
      <if test="updatedAt != null">
        updated_at,
      </if>
      <if test="shardIndex != null">
        shard_index,
      </if>
      <if test="shardSize != null">
        shard_size,
      </if>
      <if test="shardTotal != null">
        shard_total,
      </if>
      <if test="fileKey != null">
        file_key,
      </if>
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides=",">
      <if test="path != null">
        #{path,jdbcType=VARCHAR},
      </if>
      <if test="name != null">
        #{name,jdbcType=VARCHAR},
      </if>
      <if test="suffix != null">
        #{suffix,jdbcType=VARCHAR},
      </if>
      <if test="size != null">
        #{size,jdbcType=INTEGER},
      </if>
      <if test="createdAt != null">
        #{createdAt,jdbcType=BIGINT},
      </if>
      <if test="updatedAt != null">
        #{updatedAt,jdbcType=BIGINT},
      </if>
      <if test="shardIndex != null">
        #{shardIndex,jdbcType=INTEGER},
      </if>
      <if test="shardSize != null">
        #{shardSize,jdbcType=INTEGER},
      </if>
      <if test="shardTotal != null">
        #{shardTotal,jdbcType=INTEGER},
      </if>
      <if test="fileKey != null">
        #{fileKey,jdbcType=VARCHAR},
      </if>
    </trim>
  </insert>
  <update id="updateByPrimaryKeySelective" parameterType="com.file.fileupload.domain.File">
    <!--@mbg.generated-->
    update `file`
    <set>
      <if test="path != null">
        `path` = #{path,jdbcType=VARCHAR},
      </if>
      <if test="name != null">
        `name` = #{name,jdbcType=VARCHAR},
      </if>
      <if test="suffix != null">
        suffix = #{suffix,jdbcType=VARCHAR},
      </if>
      <if test="size != null">
        `size` = #{size,jdbcType=INTEGER},
      </if>
      <if test="createdAt != null">
        created_at = #{createdAt,jdbcType=BIGINT},
      </if>
      <if test="updatedAt != null">
        updated_at = #{updatedAt,jdbcType=BIGINT},
      </if>
      <if test="shardIndex != null">
        shard_index = #{shardIndex,jdbcType=INTEGER},
      </if>
      <if test="shardSize != null">
        shard_size = #{shardSize,jdbcType=INTEGER},
      </if>
      <if test="shardTotal != null">
        shard_total = #{shardTotal,jdbcType=INTEGER},
      </if>
      <if test="fileKey != null">
        file_key = #{fileKey,jdbcType=VARCHAR},
      </if>
    </set>
    where id = #{id,jdbcType=INTEGER}
  </update>
  <update id="updateByPrimaryKey" parameterType="com.file.fileupload.domain.File">
    <!--@mbg.generated-->
    update `file`
    set `path` = #{path,jdbcType=VARCHAR},
      `name` = #{name,jdbcType=VARCHAR},
      suffix = #{suffix,jdbcType=VARCHAR},
      `size` = #{size,jdbcType=INTEGER},
      created_at = #{createdAt,jdbcType=BIGINT},
      updated_at = #{updatedAt,jdbcType=BIGINT},
      shard_index = #{shardIndex,jdbcType=INTEGER},
      shard_size = #{shardSize,jdbcType=INTEGER},
      shard_total = #{shardTotal,jdbcType=INTEGER},
      file_key = #{fileKey,jdbcType=VARCHAR}
    where id = #{id,jdbcType=INTEGER}
  </update>
  <select id="selectList" resultMap="BaseResultMap">
  select
  <include refid="Base_Column_List" />
  from `file` where   file_key =#{fileKey}
</select>
  <update id="updateByFileKey">
    update `file`
    set `path` = #{path,jdbcType=VARCHAR},
    `name` = #{name,jdbcType=VARCHAR},
    suffix = #{suffix,jdbcType=VARCHAR},
    `size` = #{size,jdbcType=INTEGER},
    created_at = #{createdAt,jdbcType=BIGINT},
    updated_at = #{updatedAt,jdbcType=BIGINT},
    shard_index = #{shardIndex,jdbcType=INTEGER},
    shard_size = #{shardSize,jdbcType=INTEGER},
    shard_total = #{shardTotal,jdbcType=INTEGER}


    where file_key = #{fileKey,jdbcType=VARCHAR}
  </update>
  <select id="selectAll" resultMap="BaseResultMap">
    select
    <include refid="Base_Column_List" />
    from `file`
  </select>
</mapper>
'''


###IFileService接口
'''
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

'''

###FileServiceImpl
'''
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

'''

###Result消息返回工具类
'''
package com.file.fileupload.utils;


import lombok.Data;


/**
* 统一返回值
*
* @author zhangshuai
*
*/
@Data
public class Result {


    // 成功状态码
    public static final int SUCCESS_CODE = 200;


    // 请求失败状态码
    public static final int FAIL_CODE = 500;


    // 查无资源状态码
    public static final int NOTF_FOUNT_CODE = 404;


    // 无权访问状态码
    public static final int ACCESS_DINE_CODE = 403;


    /**
     * 状态码
     */
    private int code;


    /**
     * 提示信息
     */
    private String msg;


    /**
     * 数据信息
     */
    private Object data;


    /**
     * 请求成功
     *
     * @return
     */
    public static Result ok() {
        Result r = new Result();
        r.setCode(SUCCESS_CODE);
        r.setMsg("请求成功！");
        r.setData(null);
        return r;
    }


    /**
     * 请求失败
     *
     * @return
     */
    public static Result fail() {
        Result r = new Result();
        r.setCode(FAIL_CODE);
        r.setMsg("请求失败！");
        r.setData(null);
        return r;
    }


    /**
     * 请求成功，自定义信息
     *
     * @param msg
     * @return
     */
    public static Result ok(String msg) {
        Result r = new Result();
        r.setCode(SUCCESS_CODE);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }


    /**
     * 请求失败，自定义信息
     *
     * @param msg
     * @return
     */
    public static Result fail(String msg) {
        Result r = new Result();
        r.setCode(FAIL_CODE);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }


    /**
     * 请求成功，自定义信息，自定义数据
     *
     * @param msg
     * @return
     */
    public static Result ok(String msg, Object data) {
        Result r = new Result();
        r.setCode(SUCCESS_CODE);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }


    /**
     * 请求失败，自定义信息，自定义数据
     *
     * @param msg
     * @return
     */
    public static Result fail(String msg, Object data) {
        Result r = new Result();
        r.setCode(FAIL_CODE);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
    public Result code(Integer code){
        this.setCode(code);
        return this;
    }




    public Result data(Object data){
        this.setData(data);
        return this;
    }


    public Result msg(String msg){
        this.setMsg(msg);
        return this;
    }


}

'''

###File.html
'''
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script type="text/javascript" src="/md5.js"></script>
    <script type="text/javascript" src="/tool.js"></script>
</head>
    <body>
        <table border="1px solid red">
            <tr>
                <td>文件1</td>
                <td>
                    <input name="file" type="file" id="inputfile"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button onclick="check()">提交</button>
                </td>
            </tr>
        </table>
        <script type="text/javascript">
            //上传文件的话 得 单独出来
            function test1(shardIndex) {
                // console.log(shardIndex);
                //永安里from表单提交
                var fd = new FormData();
                //获取表单中的file
                var file=$('#inputfile').get(0).files[0];
                //文件分片 以20MB去分片
                var shardSize = 20 * 1024 * 1024;
                //定义分片索引
                var shardIndex = shardIndex;
                //定义分片的起始位置
                var start = (shardIndex-1) * shardSize;
                //定义分片结束的位置 file哪里来的?
                var end = Math.min(file.size,start + shardSize);
                //从文件中截取当前的分片数据
                var fileShard = file.slice(start,end);
                //分片的大小
                var size = file.size;
                //总片数
                var shardTotal = Math.ceil(size / shardSize);
                //文件的后缀名
                var fileName = file.name;
                var suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length).toLowerCase();


                //把视频的信息存储为一个字符串
                var filedetails=file.name+file.size+file.type+file.lastModifiedDate;
                //使用当前文件的信息用md5加密生成一个key 这个加密是根据文件的信息来加密的 如果相同的文件 加的密还是一样的
                var key = hex_md5(filedetails);
                var key10 = parseInt(key,16);
                //把加密的信息 转为一个64位的
                var key62 = Tool._10to62(key10);
                //前面的参数必须和controller层定义的一样
                fd.append('file',fileShard);
                fd.append('suffix',suffix);
                fd.append('shardIndex',shardIndex);
                fd.append('shardSize',shardSize);
                fd.append('shardTotal',shardTotal);
                fd.append('size',size);
                fd.append("key",key62)
                $.ajax({
                    url:"/upload",
                    type:"post",
                    cache: false,
                    data:fd,
                    processData: false,
                    contentType: false,
                    success:function(data){
                        //这里应该是一个递归调用
                        if(shardIndex < shardTotal){
                            var index=shardIndex +1;
                            test1(index);
                        }else
                        {
                            alert(data)
                        }


                    },
                    error:function(){
                        //请求出错处理
                    }
                })
                //发送ajax请求把参数传递到后台里面
            }


            //判断这个加密文件存在不存在
            function check() {
                var file=$('#inputfile').get(0).files[0];
                //把视频的信息存储为一个字符串
                var filedetails=file.name+file.size+file.type+file.lastModifiedDate;
                //使用当前文件的信息用md5加密生成一个key 这个加密是根据文件的信息来加密的 如果相同的文件 加的密还是一样的
                var key = hex_md5(filedetails);
                var key10 = parseInt(key,16);
                //把加密的信息 转为一个64位的
                var key62 = Tool._10to62(key10);
                //检查这个key存在不存在
                $.ajax({
                    url:"/check",
                    type:"post",
                    data:{'key':key62},
                    success:function (data) {


                        if(data.code==500){
                            //这个方法必须抽离出来
                            test1(1);
                        }
                        else
                        {
                            if(data.data.shardIndex == data.data.shardTotal)
                            {
                                alert("极速上传成功");
                            }else
                            {
                                //找到这个是第几片 去重新上传
                                test1(parseInt(data.data.shardIndex));
                            }
                        }
                    }
                })
            }


        </script>
    </body>
</html>

'''

###download.html
'''
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <div th:each="item : ${list}">
        <a  th:href="@{/download(id=${item.id})}" th:text="${item.name}"></a>
    </div>
</body>
</html>
'''




