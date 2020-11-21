DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '\"\"',
    `getup_date` datetime(0) NOT NULL DEFAULT '2020-01-01 00:00:00',
    PRIMARY KEY(`id`) USING BTREE,
    UNIQUE INDEX `user_name_index`(`user_name`) USING BTREE
)ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

INSERT INTO `user_info` VALUES (1, 'violet', '2020-06-11 7:26:00');
INSERT INTO `user_info` VALUES (2, 'violet2', '2020-10-19 8:00:00');
INSERT INTO `user_info` VALUES (3, '123', '2020-01-01 00:00:00');
INSERT INTO `user_info` VALUES (5, '1234', '2020-01-01 00:00:00');

DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `encrpt_password` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '“”',
    `user_id` int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY(`id`) USING BTREE
)ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

INSERT INTO `user_password` VALUES (1, 'ac59075b964b0715', 1);
INSERT INTO `user_password` VALUES (2, 'ac59075b964b0715', 2);
INSERT INTO `user_password` VALUES (3, 'ICy5YqxZB1uWSwcVLSNLcA==', 3);
INSERT INTO `user_password` VALUES (4, 'ICy5YqxZB1uWSwcVLSNLcA==', 5);