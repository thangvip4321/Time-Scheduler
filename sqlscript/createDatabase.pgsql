DROP table if exists users,events,userevent,pendingUsers;
create table Users (
    username varchar(50) unique NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(60) NOT NULL,
    uid SERIAL PRIMARY KEY
);
create table Events (
    eventname varchar(50) NOT NULL,
    organizer varchar(50) NOT NULL,
    eid SERIAL PRIMARY KEY,
    start_time timestamp with time ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time timestamp with time ZONE NOT NULL,
    event_location varchar(60) ,
    priority varchar(10),
    CONSTRAINT fk_customer
      FOREIGN KEY(organizer) 
	  REFERENCES Users(username)
);

create table UserEvent (
    eid int,
    uid int,
    CONSTRAINT participation_unique UNIQUE (eid,uid),
    CONSTRAINT fk_event FOREIGN KEY(eid) REFERENCES events(eid),
    CONSTRAINT fk_user FOREIGN KEY(uid) REFERENCES users(uid)

);

create table PendingUsers (
    username varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(60) NOT NULL,
    CONSTRAINT identity_unique UNIQUE (username,email)

);
