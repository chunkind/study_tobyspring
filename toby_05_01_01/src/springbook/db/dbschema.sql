create table users(
	id varchar(10) primary key,
    name varchar(20) not null,
    password varchar(10) not null
);


--new �÷� �߰�.
alter table USERS add(LV INTEGER);
alter table USERS add(LOGIN INTEGER);
alter table USERS add(RECOMMEND INTEGER);
drop table USERS purge;
--oracle
CREATE TABLE USERS
( ID VARCHAR2(10) PRIMARY KEY
, NAME VARCHAR2(20) NOT NULL
, PASSWORD VARCHAR2(20) NOT NULL
, LV INTEGER
, LOGIN INTEGER
, RECOMMEND INTEGER
)
--mysql
CREATE TABLE USERS
( ID VARCHAR(10) PRIMARY KEY
, NAME VARCHAR(20) NOT NULL
, PASSWORD VARCHAR(20) NOT NULL
, LEVEL INTEGER
, LOGIN INTEGER
, RECOMMEND INTEGER
);