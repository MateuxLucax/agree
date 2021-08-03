-- Database: agree

-- DROP DATABASE agree;

/*CREATE DATABASE agree
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'    
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;*/
	
CREATE TABLE IF NOT EXISTS Users (
	nickname     varchar(32),
	pass         varchar(256),
	creationDate timestamp,
	primary key (nickname)	
);