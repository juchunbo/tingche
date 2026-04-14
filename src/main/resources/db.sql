-- User table
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `openid` varchar(64) NOT NULL COMMENT 'WeChat openid',
  `unionid` varchar(64) DEFAULT NULL COMMENT 'WeChat unionid',
  `nickname` varchar(128) DEFAULT NULL COMMENT 'Nickname',
  `avatar_url` varchar(512) DEFAULT NULL COMMENT 'Avatar URL',
  `phone` varchar(20) DEFAULT NULL COMMENT 'Phone number',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User table';

-- Parking lot table
CREATE TABLE `parking_lots` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Parking lot ID',
  `name` varchar(128) NOT NULL COMMENT 'Parking lot name',
  `city` varchar(32) NOT NULL COMMENT 'City',
  `district` varchar(32) DEFAULT NULL COMMENT 'District',
  `address` varchar(256) NOT NULL COMMENT 'Address',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT 'Longitude',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT 'Latitude',
  `type` tinyint NOT NULL COMMENT 'Type: 1-Indoor 2-Outdoor',
  `price_per_day` decimal(10,2) NOT NULL COMMENT 'Price per day (CNY)',
  `min_days` int NOT NULL DEFAULT 1 COMMENT 'Minimum parking days',
  `rating` decimal(3,1) DEFAULT 5.0 COMMENT 'Rating',
  `monthly_sales` int DEFAULT 0 COMMENT 'Monthly sales',
  `images` json DEFAULT NULL COMMENT 'Image URLs array',
  `features` json DEFAULT NULL COMMENT 'Features tags',
  `description` text COMMENT 'Description',
  `distance_info` varchar(256) DEFAULT NULL COMMENT 'Distance info',
  `transfer_info` varchar(256) DEFAULT NULL COMMENT 'Transfer info',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT 'Status: 0-Disabled 1-Enabled',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_city` (`city`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Parking lot table';

-- Coupon template table
CREATE TABLE `coupon_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT 'Coupon name',
  `type` tinyint NOT NULL COMMENT 'Type: 1-Amount off 2-Discount',
  `discount_amount` decimal(10,2) DEFAULT NULL COMMENT 'Discount amount',
  `min_amount` decimal(10,2) NOT NULL DEFAULT 0 COMMENT 'Minimum amount',
  `discount_rate` decimal(3,2) DEFAULT NULL COMMENT 'Discount rate',
  `valid_days` int NOT NULL DEFAULT 30 COMMENT 'Valid days after receiving',
  `total_count` int NOT NULL COMMENT 'Total count',
  `issued_count` int DEFAULT 0 COMMENT 'Issued count',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT 'Status: 0-Disabled 1-Enabled',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Coupon template table';

-- User coupon table
CREATE TABLE `user_coupons` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `template_id` bigint NOT NULL COMMENT 'Coupon template ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT 'Status: 1-Unused 2-Used 3-Expired',
  `used_at` timestamp NULL DEFAULT NULL COMMENT 'Used time',
  `order_id` bigint DEFAULT NULL COMMENT 'Order ID',
  `expires_at` timestamp NOT NULL COMMENT 'Expire time',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User coupon table';

-- Order table
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT 'Order number',
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `parking_lot_id` bigint NOT NULL COMMENT 'Parking lot ID',
  `owner_name` varchar(64) NOT NULL COMMENT 'Owner name',
  `owner_phone` varchar(20) NOT NULL COMMENT 'Phone number',
  `plate_number` varchar(20) NOT NULL COMMENT 'Plate number',
  `parking_start` timestamp NOT NULL COMMENT 'Parking start time',
  `parking_end` timestamp NOT NULL COMMENT 'Parking end time',
  `parking_days` int NOT NULL COMMENT 'Parking days',
  `original_amount` decimal(10,2) NOT NULL COMMENT 'Original amount',
  `coupon_amount` decimal(10,2) DEFAULT 0 COMMENT 'Coupon discount',
  `pay_amount` decimal(10,2) NOT NULL COMMENT 'Actual payment',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT 'Status: 1-Pending 2-Parking 3-Completed 4-Cancelled',
  `pickup_code` varchar(16) DEFAULT NULL COMMENT 'Pickup code',
  `shuttle_type` varchar(32) DEFAULT NULL COMMENT 'Shuttle type',
  `remark` varchar(512) DEFAULT NULL COMMENT 'Remark',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT 'WeChat transaction ID',
  `paid_at` timestamp NULL DEFAULT NULL COMMENT 'Payment time',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order table';

-- Legacy counter table (keep for compatibility)
CREATE TABLE `Counters` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL DEFAULT '1',
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;