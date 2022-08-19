/*
Navicat MySQL Data Transfer

Source Server         : 本地开发虚拟机数据库
Source Server Version : 50721
Source Host           : 192.168.199.151:3306
Source Database       : nbcio-boot

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2022-08-19 09:14:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for im_chat_group
-- ----------------------------
DROP TABLE IF EXISTS `im_chat_group`;
CREATE TABLE `im_chat_group` (
  `id` varchar(64) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '群名称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '群头像',
  `master` varchar(100) DEFAULT NULL COMMENT '群主',
  `remarks` varchar(600) DEFAULT NULL COMMENT '说明',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(100) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(100) DEFAULT NULL,
  `del_flag` char(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='群';

-- ----------------------------
-- Records of im_chat_group
-- ----------------------------
INSERT INTO `im_chat_group` VALUES ('e83018ca92cb441aa93484af2deb8456', 'nbcio-boot讨论', 'http://192.168.199.152:9010/nbcio/temp/nbcio_1660480947146.png', 'admin', null, null, null, null, 'admin', '0');

-- ----------------------------
-- Table structure for im_chat_group_user
-- ----------------------------
DROP TABLE IF EXISTS `im_chat_group_user`;
CREATE TABLE `im_chat_group_user` (
  `chat_group_id` varchar(64) NOT NULL COMMENT '群id',
  `username` varchar(100) NOT NULL COMMENT '用户',
  `create_date` datetime DEFAULT NULL COMMENT '入群时间',
  PRIMARY KEY (`chat_group_id`,`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='群';

-- ----------------------------
-- Records of im_chat_group_user
-- ----------------------------
INSERT INTO `im_chat_group_user` VALUES ('e83018ca92cb441aa93484af2deb8456', '01015521328526', '2022-08-03 08:27:57');
INSERT INTO `im_chat_group_user` VALUES ('e83018ca92cb441aa93484af2deb8456', 'admin', '2022-08-01 11:13:51');
INSERT INTO `im_chat_group_user` VALUES ('e83018ca92cb441aa93484af2deb8456', 'jeecg', '2022-08-03 09:01:41');
INSERT INTO `im_chat_group_user` VALUES ('e83018ca92cb441aa93484af2deb8456', 'zhagnxiao', '2022-08-09 09:01:14');
INSERT INTO `im_chat_group_user` VALUES ('e83018ca92cb441aa93484af2deb8456', 'zhangsan', '2022-08-03 16:33:24');

-- ----------------------------
-- Table structure for im_group
-- ----------------------------
DROP TABLE IF EXISTS `im_group`;
CREATE TABLE `im_group` (
  `id` varchar(64) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(100) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(100) DEFAULT NULL,
  `del_flag` char(1) NOT NULL DEFAULT '0',
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of im_group
-- ----------------------------
INSERT INTO `im_group` VALUES ('04ab12ad65d54a818a6b373eb8e762c6', 'admin', '我的好友', '2018-12-20 10:05:41', null, '2018-12-20 10:05:41', null, '0', null);
INSERT INTO `im_group` VALUES ('0bdd1bf7fb034422a655de8866ba5a92', 'zhangsan', '我的好友', '2018-12-31 17:19:02', null, '2018-12-31 17:19:02', null, '0', null);

-- ----------------------------
-- Table structure for im_message
-- ----------------------------
DROP TABLE IF EXISTS `im_message`;
CREATE TABLE `im_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `to_name` varchar(100) DEFAULT NULL COMMENT '接收人',
  `from_name` varchar(100) DEFAULT NULL COMMENT '发送人id',
  `send_time` bigint(20) DEFAULT NULL,
  `content` varchar(4000) DEFAULT NULL,
  `type` char(1) DEFAULT NULL COMMENT '类型 0单聊 1 群聊',
  `read_status` char(1) DEFAULT NULL COMMENT '1 已读 0 未读',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1089 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of im_message
-- ----------------------------
INSERT INTO `im_message` VALUES ('972', 'admin', 'admin', '1660564074900', 'zhangsna给admin', '0', '0');
INSERT INTO `im_message` VALUES ('973', 'zhangsan', 'zhangsan', '1660564137108', 'admin给zhangsan ', '0', '0');

-- ----------------------------
-- Table structure for im_user_friend
-- ----------------------------
DROP TABLE IF EXISTS `im_user_friend`;
CREATE TABLE `im_user_friend` (
  `username` varchar(100) NOT NULL COMMENT '用户',
  `friendname` varchar(100) NOT NULL COMMENT '好友',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(100) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(100) DEFAULT NULL,
  `del_flag` char(1) DEFAULT '0',
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`username`,`friendname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of im_user_friend
-- ----------------------------
INSERT INTO `im_user_friend` VALUES ('admin', 'zhangsan', null, null, null, null, null, null);
INSERT INTO `im_user_friend` VALUES ('zhangsan', 'zhagnxiao', null, null, null, null, null, null);
