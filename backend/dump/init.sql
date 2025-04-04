CREATE DATABASE IF NOT EXISTS `speg-db`;
USE `speg-db`;

CREATE TABLE IF NOT EXISTS `roles` (
    `id` INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `role` CHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS `rooms_types` (
    `id` INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `type` VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS `bans_types` (
    `id` INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `type` VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS `users` (
    `id` INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `username` VARCHAR(255) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `is_online` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS `rooms` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `creator_id` INT NOT NULL,
    `room_type_id` INT NOT NULL,
    CONSTRAINT `room_creator_id_foreign` FOREIGN KEY (`creator_id`) REFERENCES `users`(`id`),
    CONSTRAINT `room_room_type_id_foreign` FOREIGN KEY (`room_type_id`) REFERENCES `rooms_types`(`id`)
);

CREATE TABLE IF NOT EXISTS `messages` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `message` TEXT NOT NULL,
    `user_id` INT NOT NULL,
    `room_id` BIGINT NOT NULL,
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    CONSTRAINT `message_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    CONSTRAINT `message_room_id_foreign` FOREIGN KEY (`room_id`) REFERENCES `rooms`(`id`)
);

CREATE TABLE IF NOT EXISTS `users_rooms` (
    `id` BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `room_id` BIGINT NOT NULL,
    `role_id` INT NOT NULL,
    CONSTRAINT `user_room_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    CONSTRAINT `user_room_room_id_foreign` FOREIGN KEY (`room_id`) REFERENCES `rooms`(`id`),
    CONSTRAINT `user_room_role_id_foreign` FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`)
);

CREATE TABLE IF NOT EXISTS `bans` (
    `id` INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `ban_type_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `room_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `expires_at` DATETIME NOT NULL,
    `reason` TEXT NOT NULL,
    CONSTRAINT `ban_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    CONSTRAINT `ban_room_id_foreign` FOREIGN KEY (`room_id`) REFERENCES `rooms`(`id`),
    CONSTRAINT `ban_ban_type_id_foreign` FOREIGN KEY (`ban_type_id`) REFERENCES `bans_types`(`id`)
);

INSERT INTO `roles` (`role`) VALUES ('administrator'), ('moderator'), ('basic');

INSERT INTO `rooms_types` (`type`) VALUES ('public'), ('private');

INSERT INTO `bans_types` (`type`) VALUES ('temporary'), ('definitive');
