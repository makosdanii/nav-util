CREATE DATABASE if not exists nav_util;
USE nav_util;

drop table if exists route;
drop table if exists marker;
drop table if exists user;
drop table if exists role;
create table user
(
    id       int         not null auto_increment,
    email    varchar(64) not null unique,
    name     varchar(64),
    password char(60)    not null,
    PRIMARY KEY (id)
);

create table role
(
    id   int         not null auto_increment,
    name varchar(64) not null unique,
    PRIMARY KEY (id)
);

create table marker
(
    id      int         not null auto_increment,
    name    varchar(64) not null,
    idx     int         not null,
    lat     float       not null,
    lng     float       not null,
    user_id int         not null,
    foreign key (user_id) references user (id),
    primary key (id)
);

create table route
(
    id      int         not null auto_increment,
    name    varchar(64) not null unique,
    route   JSON        not null,
    user_id int         not null,
    foreign key (user_id) references user (id),
    PRIMARY KEY (id)
);

insert into role(name)
values ('admin'),
       ('user');