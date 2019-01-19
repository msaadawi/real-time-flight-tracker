CREATE DATABASE  IF NOT EXISTS `rft` ;
USE `rft`;


DROP TABLE IF EXISTS `airplane`;
CREATE TABLE `airplane` (
  `airplane_number` varchar(255) not null,
  `type` varchar(255) DEFAULT NULL,
  `size` double DEFAULT NULL,
  `capacity` int DEFAULT NULL,
  `date_of_purchase` date DEFAULT NULL,
  `status` varchar(255) default null,
  PRIMARY KEY (`airplane_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `flight`;
CREATE TABLE `flight` (
  `flight_number` varchar(255) not null,
  `departure_location` varchar(255) DEFAULT NULL,
  `destination` varchar(255) DEFAULT NULL,
  `dep_date` datetime DEFAULT NULL,
  `arr_date` datetime DEFAULT NULL,
  `airplane_number` varchar(255) not null,
  foreign key(`airplane_number`) references airplane(`airplane_number`) on update cascade on delete cascade,
  PRIMARY KEY (`flight_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

