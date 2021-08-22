CREATE TABLE IF NOT EXISTS Users (
    nickname     varchar(32),
    password     varchar(256),
    profileImage varchar(256) default 'https://via.placeholder.com/150',
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
        on update cascade,
    constraint friendship_ordered check (nickname1 < nickname2)
);

CREATE TABLE IF NOT EXISTS Groups (
    id            serial,
    name          varchar(128),
    description   varchar(128),
    ownerNickname varchar(32),
    picture       varchar(256) default 'https://via.placeholder.com/150',
    createdAt     timestamp,
    primary key (id),
    foreign key (ownerNickname) references Users(nickname)
        on delete no action
        on update cascade
);

CREATE TABLE IF NOT EXISTS GroupMembership (
    groupId      integer,
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
    primary key (nicknameFrom, nicknameTo),
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
    groupId      integer,
    primary key (nicknameFrom, nicknameTo, groupId),
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

CREATE TABLE IF NOT EXISTS GroupMessages  (
    id      serial,
    groupId integer,
    message varchar(255),
    sentAt  timestamp,
    sentBy  varchar(32),
    primary key(id),
    foreign key (sentBy) references Users(nickname)
        on update cascade
        on delete set null,
    foreign key (groupId) references Groups(id)
        on update cascade
        on delete cascade
);

CREATE TABLE IF NOT EXISTS FriendMessages (
    id        serial,
    nickname1 varchar(32),
    nickname2 varchar(32),
    message   varchar(255),
    sentAt    timestamp,
    sentBy    varchar(32),
    primary key(id),
    foreign key (nickname1, nickname2) references Friendship(nickname1, nickname2)
        on update cascade
        on delete cascade,
    foreign key (sentBy) references Users(nickname)
        on update cascade
        on delete cascade
);



-- Extensões pro trabalho de BAD



CREATE TABLE IF NOT EXISTS Servers (
    id        serial,
    name      varchar(255),
    createdAt timestamp,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS ServerMembership (
    userNickname varchar(32),
    serverId     integer,
    primary key (userNickname, serverId),
    foreign key (userNickname) references Users(nickname)
        on update cascade
        on delete cascade,
    foreign key (serverId) references Servers(id)
        on update cascade
        on delete cascade
);

CREATE TABLE IF NOT EXISTS Channels (
    id       serial,
    serverId integer,
    name     varchar(48),
    primary key (id),
    foreign key (serverId) references Servers(id)
        on update cascade
        on delete cascade
);

CREATE TABLE IF NOT EXISTS ChannelMessages (
    id        serial,
    message   varchar(255),
    sentAt    timestamp,
    sentBy    varchar(32),
    channelId integer,
    primary key(id),
    foreign key (sentBy) references Users(nickname)
        on update cascade
        on delete set null,
    foreign key (channelId) references Channels(id)
        on update cascade
        on delete cascade
);

CREATE TABLE IF NOT EXISTS Roles (
    id       serial,
    serverId integer,
    name     varchar(32),
    primary key (id),
    foreign key (serverId) references Servers(id)
        on update cascade
        on delete cascade
);

CREATE TABLE IF NOT EXISTS RolesPermissions (
    id         serial,
    permission integer,
    channelId  integer,
    role       integer,
    primary key (id),
    foreign key (channelId) references Channels(id)
        on update cascade
        on delete cascade
);

CREATE TABLE IF NOT EXISTS UserRoles (
    userNickname varchar(32),
    roleId       integer,
    primary key(userNickname),
    foreign key(roleId) references Roles(id)
        on update cascade
        on delete cascade

);

CREATE TABLE IF NOT EXISTS Themes(
    id             serial,
    createdBy      varchar(32),
    textColor      varchar(8),
    primaryColor   varchar(8),
    secondaryColor varchar(8),
    font           varchar(64),
    primary key (id),
    foreign key (createdBy) references Users(nickname)
        on update cascade
        on delete set null
);

INSERT INTO Themes (id, createdBy, textColor, primaryColor, secondaryColor, font)
            VALUES (1, null, '#FFFFFF', '#de0bd3', '#ddff00', 'comic.tff')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS UsesTheme (
    userNickname varchar(32) unique,
    themeId      integer default 1,
    primary key (userNickname, themeId),
    foreign key (userNickname) references Users(nickname)
        on update cascade
        on delete cascade,
    foreign key (themeId) references Themes(id)
        on update cascade
        on delete set default
);