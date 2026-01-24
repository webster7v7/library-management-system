-- =====================================================
-- 图书馆管理系统 - 用户账号初始化脚本
-- 用于创建默认的 admin 和 user 账号
-- =====================================================

USE library_management;

-- 删除已存在的测试账号（如果有）
DELETE FROM sys_user WHERE username IN ('admin', 'user');

-- 插入管理员账号
-- 用户名: admin
-- 密码: admin
-- 角色: ADMIN
INSERT INTO sys_user (username, password, real_name, phone, email, role, status, create_time, update_time)
VALUES (
    'admin',
    '$2b$12$YekNkPPCQgSda5e39PNlxOPKS5j64sR/zhfZvYBj0NV/uV/NcplkG',
    '系统管理员',
    '13800138000',
    'admin@library.com',
    'ADMIN',
    1,
    NOW(),
    NOW()
);

-- 插入普通用户账号
-- 用户名: user
-- 密码: user
-- 角色: USER
INSERT INTO sys_user (username, password, real_name, phone, email, role, status, create_time, update_time)
VALUES (
    'user',
    '$2b$12$p9LfJmzARynEh3RE6R5sYucq03/fGo0p0U0KjOzX0wuVPdjjk2SD2',
    '张三',
    '13800138001',
    'user@library.com',
    'USER',
    1,
    NOW(),
    NOW()
);

-- 验证插入结果
SELECT
    id,
    username,
    real_name,
    phone,
    email,
    role,
    status,
    create_time
FROM sys_user
WHERE username IN ('admin', 'user');

-- =====================================================
-- 说明:
-- 1. 密码使用 BCrypt ($2a$10 或 $2b$12) 加密
-- 2. admin账号: username=admin, password=admin, role=ADMIN
-- 3. user账号: username=user, password=user, role=USER
-- 4. 这些哈希值已通过 BCryptPasswordEncoder 生成并验证
-- =====================================================
