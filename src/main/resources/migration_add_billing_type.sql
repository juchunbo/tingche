-- 为停车场表添加计费规则字段
-- 执行此脚本以支持两种计费方式：自然日计费和24小时制计费

ALTER TABLE `parking_lots` 
ADD COLUMN `billing_type` tinyint NOT NULL DEFAULT 1 COMMENT '计费规则类型：1-自然日计费，2-24小时制计费' AFTER `min_days`,
ADD COLUMN `price_per_hour` decimal(10,2) DEFAULT NULL COMMENT '超出最低天数后每小时费用（仅billingType=2时使用，单位：元/小时）' AFTER `billing_type`;

-- 为已有数据设置默认值
UPDATE `parking_lots` SET `billing_type` = 1 WHERE `billing_type` IS NULL;
