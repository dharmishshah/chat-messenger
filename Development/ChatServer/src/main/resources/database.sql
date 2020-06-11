CREATE TABLE `chatter_group` (
 `unique_id` varchar(45) DEFAULT NULL,
 `name` varchar(45) DEFAULT NULL,
 `password` varchar(45) DEFAULT NULL,
 `group_id` int(11) NOT NULL AUTO_INCREMENT,
 PRIMARY KEY (`group_id`)
) ENGINE=InnoDB;


CREATE TABLE `chatter_user` (
 `unique_id` varchar(45) DEFAULT NULL,
 `name` varchar(45) DEFAULT NULL,
 `password` varchar(45) DEFAULT NULL,
 `user_id` int(11) NOT NULL AUTO_INCREMENT,
 PRIMARY KEY (`user_id`)
) ENGINE=InnoDB;

CREATE TABLE `group_users` (
 `user_id` INT NOT NULL,
 `group_id` INT NOT NULL);

CREATE TABLE `group_moderators` (
 `group_id` INT NOT NULL,
 `user_id` INT NULL);

 CREATE TABLE `message` (
   `message_id` int(11) NOT NULL AUTO_INCREMENT,
   `message_type` varchar(45) NOT NULL,
   `message_sender` int(11) NOT NULL,
   `message_receiver` int(11) NOT NULL,
   `message_text` varchar(200) NOT NULL,
   `time_stamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `status` varchar(45) NOT NULL,
   `isGroupMessage` varchar(10) NOT NULL,
   PRIMARY KEY (`message_id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=262 DEFAULT CHARSET=utf8;