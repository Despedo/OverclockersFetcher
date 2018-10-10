CREATE SCHEMA `overclockers`;

CREATE TABLE `overclockers`.`users` (
`userId` int(11) NOT NULL AUTO_INCREMENT,
`userName` varchar(50) NOT NULL,
`profileForumId` varchar(10) NOT NULL,
`createdDateTime` datetime NOT NULL,
PRIMARY KEY (`userId`),
UNIQUE KEY (`profileForumId`)
);

CREATE TABLE `overclockers`.`topics` (
`topicId` int(11) NOT NULL AUTO_INCREMENT,
`topicStarterId` int(11) NOT NULL,
`location` varchar(50) NOT NULL,
`title` varchar(256) NOT NULL,
`topicForumId` varchar(10) NOT NULL,
`lastMessageDateTime` datetime NOT NULL,
`createdDateTime` datetime NOT NULL,
PRIMARY KEY (`topicId`),
UNIQUE KEY (`topicForumId`),
KEY `topics_users_userId_fk` (`topicStarterId`)
);

