CREATE DATABASE IF NOT EXISTS library_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_management;

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色:ADMIN,USER',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记:0-未删除,1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (`username`),
    INDEX idx_role (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `book_category` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(200) COMMENT '分类描述',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记:0-未删除,1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_name (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID',
    `isbn` VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN编号',
    `title` VARCHAR(100) NOT NULL COMMENT '书名',
    `author` VARCHAR(50) NOT NULL COMMENT '作者',
    `publisher` VARCHAR(50) COMMENT '出版社',
    `publish_date` DATE COMMENT '出版日期',
    `category_id` BIGINT COMMENT '分类ID',
    `price` DECIMAL(10,2) COMMENT '价格',
    `total_quantity` INT NOT NULL DEFAULT 0 COMMENT '总数量',
    `available_quantity` INT NOT NULL DEFAULT 0 COMMENT '可借数量',
    `location` VARCHAR(50) COMMENT '存放位置',
    `description` TEXT COMMENT '图书描述',
    `cover_url` VARCHAR(200) COMMENT '封面图片URL',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记:0-未删除,1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_isbn (`isbn`),
    INDEX idx_title (`title`),
    INDEX idx_author (`author`),
    INDEX idx_category_id (`category_id`),
    FOREIGN KEY (`category_id`) REFERENCES `book_category`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

CREATE TABLE IF NOT EXISTS `borrow_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '借阅记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `borrow_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅日期',
    `due_date` DATETIME NOT NULL COMMENT '应还日期',
    `return_date` DATETIME COMMENT '实际归还日期',
    `status` VARCHAR(20) NOT NULL DEFAULT 'BORROWED' COMMENT '状态:BORROWED-借阅中,RETURNED-已归还,OVERDUE-逾期',
    `renew_count` INT NOT NULL DEFAULT 0 COMMENT '续借次数',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记:0-未删除,1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (`user_id`),
    INDEX idx_book_id (`book_id`),
    INDEX idx_status (`status`),
    INDEX idx_borrow_date (`borrow_date`),
    FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`book_id`) REFERENCES `book`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    `user_id` BIGINT COMMENT '操作用户ID',
    `username` VARCHAR(50) COMMENT '操作用户名',
    `operation` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `method` VARCHAR(200) COMMENT '请求方法',
    `params` TEXT COMMENT '请求参数',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-失败,1-成功',
    `error_msg` TEXT COMMENT '错误信息',
    `execute_time` BIGINT COMMENT '执行时间(ms)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (`user_id`),
    INDEX idx_operation (`operation`),
    INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`) VALUES
('admin', '$2b$12$YekNkPPCQgSda5e39PNlxOPKS5j64sR/zhfZvYBj0NV/uV/NcplkG', '管理员', '13800138000', 'admin@library.com', 'ADMIN', 1),
('user', '$2b$12$p9LfJmzARynEh3RE6R5sYucq03/fGo0p0U0KjOzX0wuVPdjjk2SD2', '张三', '13800138001', 'user@library.com', 'USER', 1);

INSERT INTO `book_category` (`category_name`, `description`) VALUES
('计算机', '计算机科学与技术类书籍'),
('文学', '文学类书籍'),
('历史', '历史类书籍'),
('科学', '自然科学类书籍'),
('艺术', '艺术类书籍');
