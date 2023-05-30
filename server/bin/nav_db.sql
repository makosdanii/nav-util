CREATE DATABASE if not exists nav_util;
USE nav_util;

drop table if exists user;
drop table if exists role;
create table user
(
    id             int         not null auto_increment,
    email          varchar(64) not null unique,
    name           varchar(64),
    password       char(60)    not null,
    PRIMARY KEY (id)
);

create table role
(
    id int not null auto_increment,
    name varchar(64) not null unique,
    PRIMARY KEY (id)
);

insert into role(name) values
                           ('admin'),
                           ('user');