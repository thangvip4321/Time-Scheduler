# Time-Scheduler

# Format for **user** entity:
- in `json`
```json
{
    "username": string,
    "password": string,
    "email": string,
    "events list": [event1,event2,...],
    "userID": int
}
```
- in `postgresql`:
```postgresql
Users (
    username varchar(50) unique NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(60) NOT NULL,
    uid SERIAL PRIMARY KEY
);
```

# Format for **event** entity:
- in `json`:
```json
{
    "eventID": int,
    "name": string,
    "organizer": string,
    "date": "2021-12-12T09:36:50.00",
    "priority": int,
    "participants list": [name1,name2,...],
}
```
- in `postgresql`:
```postgresql
Events (
    eventname varchar(50) NOT NULL,
    organizer varchar(50) NOT NULL,
    eid SERIAL PRIMARY KEY,
    event_date Timestamp NOT NULL,
    priority int,
    CONSTRAINT fk_customer
      FOREIGN KEY(organizer) 
	  REFERENCES Users(username)
);
```

# The schema for this can be found in `sqlscript/createDatabase.pgsql`