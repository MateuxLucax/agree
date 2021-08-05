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

CREATE TABLE IF NOT EXISTS Groups (
	id            char(36),
	name          varchar(128),
	ownerNickname varchar(32),
	primary key(id),
	foreign key (ownerNickname) references Users(nickname)
		on delete no action
		on update cascade
);

CREATE TABLE IF NOT EXISTS GroupMembership (
	groupId      char(36),
	userNickname varchar(32),
	primary key (groupId, userNickname),
	foreign key (groupId) references Groups(id)
		on delete cascade
		on update cascade,
	foreign key (userNickname) references Users(nickname)
		on delete cascade
		on update cascade
);

CREATE TABLE IF NOT EXISTS FriendInvites (
	nicknameFrom varchar(32),
	nicknameTo   varchar(32),
	primary key(nicknameFrom, nicknameTo),
	foreign key (nicknameFrom) references Users(nickname)
		on delete cascade
		on update cascade,
	foreign key (nicknameTo) references Users(nickname)
		on delete cascade
		on update cascade
);

CREATE TABLE IF NOT EXISTS GroupInvites (
	nicknameFrom varchar(32),
	nicknameTo   varchar(32),
	groupId      char(36),
	primary key(nicknameFrom, nicknameTo, groupId),
	foreign key (nicknameFrom) references Users(nickname)
		on delete cascade
		on update cascade,
	foreign key (nicknameTo) references Users(nickname)
		on delete cascade
		on update cascade,
	foreign key (groupId) references Groups(id)
		on delete cascade
		on update cascade
);