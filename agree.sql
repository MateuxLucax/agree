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

CREATE TABLE IF NOT EXISTS Friendship (
    nickname1 varchar(32),
    nickname2 varchar(32),
    primary key (nickname1, nickname2),
    foreign key (nickname1) references Users(nickname)
        on delete cascade
        on update cascade,
    foreign key (nickname2) references Users(nickname)
        on delete cascade
        on update cascade
);