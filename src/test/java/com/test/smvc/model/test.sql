CREATE SCHEMA IF NOT EXISTS `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
SHOW WARNINGS;
USE `test` ;

CREATE TABLE IF NOT EXISTS `test`.`student` (
    id integer primary key auto_increment,
    name varchar(32) not null,
    password varchar(64) not null,
    school  varchar(64) not null
    );
    
CREATE TABLE IF NOT EXISTS `test`.`grade` (
    id integer primary key auto_increment,
    className varchar(32) not null,
    score integer not null,
    student_id integer
    );