DELETE FROM events WHERE TRUE;
DELETE FROM users WHERE TRUE;

INSERT into users (username,password,email) VALUES ('thangvip','1234','qwe@gmail.com');
INSERT into events (eventname,organizer,priority,end_time) VALUES ('haha','thangvip','LOW','2021-03-13T23:15');

SELECT * from events;