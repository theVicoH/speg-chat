CREATE DATABASE IF NOT EXISTS `speg-db`;

use `speg-db`;

CREATE TABLE IF NOT EXISTS `user`(
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `role_id` BIGINT NOT NULL,
    `username` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    PRIMARY KEY(`id`)
);
CREATE TABLE IF NOT EXISTS `role`(
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `role` CHAR(255) NOT NULL,
    PRIMARY KEY(`id`)
);
CREATE TABLE IF NOT EXISTS `room`(
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `creator_id` BIGINT NOT NULL,
    `room_type_id` BIGINT NOT NULL,
    PRIMARY KEY(`id`)
);
CREATE TABLE IF NOT EXISTS `message`(
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `message` TEXT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `room_id` BIGINT NOT NULL,
    PRIMARY KEY(`id`)
);
CREATE TABLE IF NOT EXISTS `user_room`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `room_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS `room_type`(
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    PRIMARY KEY(`id`)
);
CREATE TABLE IF NOT EXISTS `ban`(
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `ban_type_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `room_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `expires_at` DATETIME NOT NULL,
    `reason` TEXT NOT NULL,
    PRIMARY KEY(`id`)
);
CREATE TABLE IF NOT EXISTS `ban_type`(
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    PRIMARY KEY(`id`)
);
ALTER TABLE
    `ban` ADD CONSTRAINT `ban_user_id_foreign` FOREIGN KEY(`user_id`) REFERENCES `user`(`id`);
ALTER TABLE
    `room` ADD CONSTRAINT `room_room_type_id_foreign` FOREIGN KEY(`room_type_id`) REFERENCES `room_type`(`id`);
ALTER TABLE
    `user_room` ADD CONSTRAINT `user_room_user_id_foreign` FOREIGN KEY(`user_id`) REFERENCES `user`(`id`);
ALTER TABLE
    `room` ADD CONSTRAINT `room_creator_id_foreign` FOREIGN KEY(`creator_id`) REFERENCES `user`(`id`);
ALTER TABLE
    `user_room` ADD CONSTRAINT `user_room_role_id_foreign` FOREIGN KEY(`role_id`) REFERENCES `role`(`id`);
ALTER TABLE
    `user` ADD CONSTRAINT `user_role_id_foreign` FOREIGN KEY(`role_id`) REFERENCES `role`(`id`);
ALTER TABLE
    `user_room` ADD CONSTRAINT `user_room_room_id_foreign` FOREIGN KEY(`room_id`) REFERENCES `room`(`id`);
ALTER TABLE
    `message` ADD CONSTRAINT `message_user_id_foreign` FOREIGN KEY(`user_id`) REFERENCES `user`(`id`);
ALTER TABLE
    `ban` ADD CONSTRAINT `ban_room_id_foreign` FOREIGN KEY(`room_id`) REFERENCES `room`(`id`);
ALTER TABLE
    `ban` ADD CONSTRAINT `ban_ban_type_id_foreign` FOREIGN KEY(`ban_type_id`) REFERENCES `ban_type`(`id`);
ALTER TABLE
    `message` ADD CONSTRAINT `message_room_id_foreign` FOREIGN KEY(`room_id`) REFERENCES `room`(`id`);

INSERT INTO `role` (role) VALUES ('administrator'), ('moderator'), ('basic');

INSERT INTO `room_type` (type) VALUES ('public'), ('private');

INSERT INTO `ban_type` (type) VALUES ('temporary'), ('definitive');
