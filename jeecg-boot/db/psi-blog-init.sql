SET NAMES utf8mb4;

-- 1. 用户表
DROP TABLE IF EXISTS sys_blog_user;
CREATE TABLE sys_blog_user (
    id VARCHAR(32) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    avatar VARCHAR(500),
    email VARCHAR(100),
    phone VARCHAR(20),
    bio TEXT,
    status VARCHAR(1) DEFAULT '1',
    del_flag VARCHAR(1) DEFAULT '0',
    create_by VARCHAR(50) DEFAULT '',
    create_time DATETIME,
    update_by VARCHAR(50) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 文章表
DROP TABLE IF EXISTS psi_blog_article;
CREATE TABLE psi_blog_article (
    id VARCHAR(32) NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    content TEXT NOT NULL,
    cover_image VARCHAR(500),
    author_id VARCHAR(32) NOT NULL,
    author_name VARCHAR(100),
    category_id VARCHAR(32),
    category_name VARCHAR(100),
    tags VARCHAR(500),
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    collect_count INT DEFAULT 0,
    status VARCHAR(1) DEFAULT '1',
    is_top VARCHAR(1) DEFAULT '0',
    is_recommend VARCHAR(1) DEFAULT '0',
    del_flag VARCHAR(1) DEFAULT '0',
    create_by VARCHAR(50) DEFAULT '',
    create_time DATETIME,
    update_by VARCHAR(50) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    publish_time DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 评论表
DROP TABLE IF EXISTS psi_blog_comment;
CREATE TABLE psi_blog_comment (
    id VARCHAR(32) NOT NULL,
    article_id VARCHAR(32) NOT NULL,
    parent_id VARCHAR(32),
    content TEXT NOT NULL,
    commentator_id VARCHAR(32) NOT NULL,
    commentator_name VARCHAR(100),
    commentator_avatar VARCHAR(500),
    reply_to_id VARCHAR(32),
    reply_to_name VARCHAR(100),
    like_count INT DEFAULT 0,
    status VARCHAR(1) DEFAULT '1',
    del_flag VARCHAR(1) DEFAULT '0',
    create_by VARCHAR(50) DEFAULT '',
    create_time DATETIME,
    update_by VARCHAR(50) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 收藏表
DROP TABLE IF EXISTS psi_blog_collect;
CREATE TABLE psi_blog_collect (
    id VARCHAR(32) NOT NULL,
    user_id VARCHAR(32) NOT NULL,
    article_id VARCHAR(32) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_article (user_id, article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 点赞表
DROP TABLE IF EXISTS psi_blog_like;
CREATE TABLE psi_blog_like (
    id VARCHAR(32) NOT NULL,
    user_id VARCHAR(32) NOT NULL,
    article_id VARCHAR(32) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_article (user_id, article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 分类表
DROP TABLE IF EXISTS psi_blog_category;
CREATE TABLE psi_blog_category (
    id VARCHAR(32) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(255),
    sort INT DEFAULT 0,
    article_count INT DEFAULT 0,
    status VARCHAR(1) DEFAULT '1',
    del_flag VARCHAR(1) DEFAULT '0',
    create_by VARCHAR(50) DEFAULT '',
    create_time DATETIME,
    update_by VARCHAR(50) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入分类数据
INSERT INTO psi_blog_category (id, name, description, icon, sort) VALUES
('cat001', '技术分享', '编程技术相关文章', 'code', 1),
('cat002', '生活随笔', '日常生活感悟', 'book', 2),
('cat003', '读书笔记', '读书心得体会', 'read', 3),
('cat004', '职场成长', '职业发展经验', 'briefcase', 4);

-- 插入测试用户（密码是 123456）
INSERT INTO sys_blog_user (id, username, password, nickname, email, bio, create_time) VALUES
('user001', 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU8Rxg7WqJm', '管理员', 'admin@blog.com', '我是博客管理员', NOW()),
('user002', 'test', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU8Rxg7WqJm', '测试用户', 'test@blog.com', '我是测试用户', NOW());