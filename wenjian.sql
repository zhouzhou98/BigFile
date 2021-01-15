/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50556
Source Host           : localhost:3306
Source Database       : wenjian

Target Server Type    : MYSQL
Target Server Version : 50556
File Encoding         : 65001

Date: 2021-01-14 20:43:57
*/

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
