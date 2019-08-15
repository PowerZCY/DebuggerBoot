-- auto Generated on 2019-08-14 17:53:31 
-- DROP TABLE IF EXISTS `time_mark_detail`; 
CREATE TABLE time_mark_detail(
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `mark_content_id` BIGINT NOT NULL DEFAULT -1 COMMENT '主表关联ID',
    `content` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '内容',
    `content_signature` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '内容签名',
    `encrypt_type` INTEGER(12) NOT NULL DEFAULT -1 COMMENT '加密类型: 0-裸奔;1-统一;2-私人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'createTime',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updateTime',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'time_mark_detail';
