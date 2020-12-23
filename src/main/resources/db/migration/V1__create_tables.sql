CREATE TABLE `users` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `code` varchar(255) DEFAULT NULL,
        `email` varchar(255) NOT NULL,
        `is_moderator` tinyint(4) NOT NULL,
        `name` varchar(255) NOT NULL,
        `password` varchar(255) NOT NULL,
        `photo` varchar(1000) DEFAULT NULL,
        `reg_time` datetime NOT NULL,
        PRIMARY KEY (`id`)
                     );
CREATE TABLE `posts` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `is_active` tinyint(4) NOT NULL,
        `moderation_status` varchar(255) NOT NULL DEFAULT 'NEW',
        `text` text NOT NULL,
        `time` datetime NOT NULL,
        `title` varchar(255) NOT NULL,
        `view_count` int(11) NOT NULL DEFAULT 0,
        `moderator_id` int(11) DEFAULT NULL,
        `user_id` int(11) NOT NULL,
        PRIMARY KEY (`id`),
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
                     );
CREATE TABLE `captcha_codes` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
		`code` text NOT NULL,
        `secret_code` tinytext NOT NULL,
        `time` datetime NOT NULL,
        PRIMARY KEY (`id`)
                             );
CREATE TABLE `global_settings` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `code` varchar(255) NOT NULL,
        `name` varchar(255) NOT NULL,
        `value` varchar(255) NOT NULL,
        PRIMARY KEY (`id`)
                               );
CREATE TABLE `post_comments` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `parent_id` int(11) DEFAULT NULL,
        `text` text NOT NULL,
        `time` datetime NOT NULL,
        `post_id` int(11) NOT NULL,
        `user_id` int(11) NOT NULL,
        PRIMARY KEY (`id`),
        FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
        FOREIGN KEY (`parent_id`) REFERENCES `post_comments` (`id`),
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
                             );
CREATE TABLE `post_votes` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `time` datetime NOT NULL,
        `value` tinyint(4) NOT NULL,
        `post_id` int(11) NOT NULL,
        `user_id` int(11) NOT NULL,
        PRIMARY KEY (`id`),
		FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
                          );
CREATE TABLE `tags` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `name` varchar(255) NOT NULL,
        PRIMARY KEY (`id`)
                    );
CREATE TABLE `tag2post` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `post_id` int(11) DEFAULT NULL,
        `tag_id` int(11) DEFAULT NULL,
        PRIMARY KEY (`id`),
        FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`),
        FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
                        );
