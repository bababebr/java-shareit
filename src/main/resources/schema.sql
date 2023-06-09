CREATE TABLE IF NOT EXISTS Users
(
    ID    INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    NAME  VARCHAR                                          NOT NULL,
    EMAIL VARCHAR(255)                                     NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS Items
(
    ID           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    OWNER_ID      INT                                              NOT NULL,
    IS_AVAILABLE BOOL                                             NOT NULL,
    DESCRIPTION  VARCHAR DEFAULT ('Enter Description here.'),
    NAME         VARCHAR(64)                                      NOT NULL,
    FOREIGN KEY (OWNER_ID) REFERENCES USERS(ID)
);

CREATE TABLE IF NOT EXISTS Bookings
(
    ID         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    BOOKER_ID  INT                                                 NOT NULL,
    ITEM_ID    INT                                                 NOT NULL,
    START_TIME DATETIME                                            NOT NULL,
    END_TIME   DATETIME                                            NOT NULL,
    STATE      VARCHAR(32)                                         NOT NULL,
    FOREIGN KEY (BOOKER_ID) references USERS (ID),
    FOREIGN KEY (ITEM_ID) references ITEMS (ID)
);

CREATE TABLE IF NOT EXISTS Comments
(
    ID        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    TEXT      VARCHAR                                             NOT NULL,
    ITEM_ID   INT                                                 NOT NULL,
    AUTHOR_ID INT                                                 NOT NULL,
    CREATED   DATETIME                                            NOT NULL,
    FOREIGN KEY (ITEM_ID) REFERENCES ITEMS(ID),
    FOREIGN KEY (AUTHOR_ID) REFERENCES USERS(ID)
);

CREATE TABLE IF NOT EXISTS Request (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    REQUESTER_ID INTEGER NOT NULL,
    DESCRIPTION VARCHAR NOT NULL,
    CREATED DATETIME NOT NULL,
    ITEM_ID INT,
    FOREIGN KEY (REQUESTER_ID) REFERENCES USERS(ID)
);