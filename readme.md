# Meeting planner

This program has been written to resolve an appropriate room for meeting in a compagny. There are
different types of meeting and each type has its own particularities when it comes to the tools
needed. The different rooms does not always have the necessary and this program is set to find you
the best room according to the meeting you are want to book.

This issue happened to be more relevant while we are living at period where sanitary rules are
really important. A room can only be filled at 70 percent of its initial capacity and not bookable
one hour after the last meeting.

For rooms that are not set for the specified meeting, you can rely on stock of removables tools.

## Types of meeting

```
Visioconference involves a speaker, a screen and a webcam
Simple Meeting involves nothing but a room
Sharing Meeting involves a board
Remote Meeting involves a board, a screen and a speacker
```

Those informations have been set hardly on the program. Later, they will become parameters that can
be set in database, so the program will be more flexible.

## Types of removables (tools)

```
Board
Screen
Speaker
Webcam
```

## How to use this app

### Install a Mongo DB

Get mongo Database compass. Please follow the
instruction [here](https://docs.mongodb.com/compass/current/install). Create a database through the
compass app Fulfilled the properties file

```
spring.data.mongodb.host= ip_database
spring.data.mongodb.port=port_database
spring.data.mongodb.database= database_name
```

### Launch the app

The application is Java /spring boot based app. You need a JVM. Launch is:

```
java- jar icecream-0.0.1-SNAPSHOT.jar
```






