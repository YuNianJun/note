SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
                             `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '书架名',
                             `user_id` int NULL DEFAULT NULL COMMENT '创建人',
                             `synopsis` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简介',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
                            `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论表id',
                            `note_id` int NOT NULL COMMENT '笔记id',
                            `user_id` int NOT NULL COMMENT '用户id',
                            `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '评论时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for note
-- ----------------------------
DROP TABLE IF EXISTS `note`;
CREATE TABLE `note`  (
                         `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                         `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '笔记标题',
                         `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '笔记标签',
                         `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '笔记内容',
                         `content_md` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '笔记完整内容',
                         `category_id` int NULL DEFAULT NULL COMMENT '所属书架',
                         `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                         `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                         `top` int NULL DEFAULT NULL COMMENT '是否置顶(1:置顶, 0:不置顶)',
                         `user_id` int NULL DEFAULT NULL COMMENT '笔记所属用户',
                         `status` int NULL DEFAULT 0 COMMENT '笔记状态(0:私有,1:公开)',
                         `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for note_share
-- ----------------------------
DROP TABLE IF EXISTS `note_share`;
CREATE TABLE `note_share`  (
                               `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                               `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一标识符，用于生成分享链接',
                               `note_id` int UNSIGNED NULL DEFAULT NULL COMMENT '关联的笔记 ID',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '记录生成时间，用于设置有效期',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                         `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                         `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                         `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电子邮件',
                         `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
                         `status` int UNSIGNED NULL DEFAULT 1 COMMENT '账号状态 ',
                         `permission` int NULL DEFAULT 0 COMMENT '账号权限',
                         `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
