#TipoUser
#User
#tipo
#itemmodel
#item
#especificacion
#sala
#listaespecificacion
#reserva

DROP DATABASE IF EXISTS  mubook;
CREATE DATABASE IF NOT EXISTS mubook;
USE mubook;

CREATE TABLE IF NOT EXISTS USER_TYPE
(
    userTypeId      INT,
    description     VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS USER
(
    userId          BIGINT,
    userTypeId      INT,
    name            VARCHAR(32),
    surname         VARCHAR(32),
    DNI             VARCHAR(9),
    bornDate        DATE,
    validated       BOOLEAN,
    dniImg          VARCHAR(128),
    username        VARCHAR(64),
    password        VARCHAR(128),
    email           VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS ITEM_TYPE
(
    itemTypeId      INT,
    name            VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS ITEM_MODEL
(
    itemModelId     BIGINT,
    itemTypeId      INT,
    description     VARCHAR(2048),
    name            VARCHAR(128),
    identifier      VARCHAR(64),
    img             VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS ITEM
(
    itemId          BIGINT,
    itemModelId     BIGINT,
    serialNum       VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS SPECIFICATION
(
    specId          INT,
    description     VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS ROOM
(
    roomId          INT,
    description     VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS SPECIFICATION_LIST
(
    itemModelId    BIGINT,
    specId         INT,
    value          VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS RESERVATION
(
    reservationId   BIGINT,
    userID          BIGINT,
    itemId          BIGINT,
    roomId          INT,
    initDate        DATE,
    finalDate       DATE
);

CREATE TABLE IF NOT EXISTS OPINION
(
    userId          BIGINT,
    itemModelId     BIGINT,
    rate            INT CHECK ( rate > 0 AND rate <= 5 ),
    description     VARCHAR(2048)
);

#Create primary key
ALTER TABLE USER_TYPE ADD CONSTRAINT pk_user_type PRIMARY KEY (userTypeId);
ALTER TABLE USER ADD CONSTRAINT pk_user PRIMARY KEY (userId);
ALTER TABLE RESERVATION ADD CONSTRAINT pk_reservation PRIMARY KEY (reservationId);
ALTER TABLE ITEM ADD CONSTRAINT pk_item PRIMARY KEY (itemId);
ALTER TABLE ITEM_MODEL ADD CONSTRAINT pk_item_model PRIMARY KEY (itemModelId);
ALTER TABLE ITEM_TYPE ADD CONSTRAINT pk_item_type PRIMARY KEY (itemTypeId);
ALTER TABLE ROOM ADD CONSTRAINT pk_room PRIMARY KEY (roomId);
ALTER TABLE SPECIFICATION ADD CONSTRAINT pk_spec PRIMARY KEY (specId);
ALTER TABLE SPECIFICATION_LIST ADD CONSTRAINT pk_spec_list PRIMARY KEY (itemModelId, specId);
ALTER TABLE OPINION ADD CONSTRAINT pk_opinion PRIMARY KEY (userId, itemModelId);

#Set Auto Increments
ALTER TABLE USER MODIFY COLUMN userId BIGINT auto_increment;
ALTER TABLE ITEM_MODEL MODIFY COLUMN itemModelId BIGINT auto_increment;
ALTER TABLE ITEM MODIFY COLUMN itemId BIGINT auto_increment;
ALTER TABLE SPECIFICATION MODIFY COLUMN specId INT auto_increment;
ALTER TABLE RESERVATION MODIFY COLUMN reservationId BIGINT auto_increment;

#Create foreign key
ALTER TABLE USER ADD CONSTRAINT fk_user_type FOREIGN KEY (userTypeId) REFERENCES USER_TYPE(userTypeId);
ALTER TABLE RESERVATION ADD CONSTRAINT fk_reservation_user FOREIGN KEY (userID) REFERENCES USER (userId);
ALTER TABLE RESERVATION ADD CONSTRAINT fk_reservation_item FOREIGN KEY (itemId) REFERENCES ITEM (itemId);
ALTER TABLE RESERVATION ADD CONSTRAINT fk_reservation_room FOREIGN KEY (roomId) REFERENCES ROOM (roomId);
ALTER TABLE ITEM ADD CONSTRAINT fk_item_model FOREIGN KEY (itemModelId) REFERENCES ITEM_MODEL (itemModelId);
ALTER TABLE ITEM_MODEL ADD CONSTRAINT fk_item_model_type FOREIGN KEY (itemTypeId) REFERENCES ITEM_TYPE (itemTypeId);
ALTER TABLE SPECIFICATION_LIST ADD CONSTRAINT fk_spec_list_item FOREIGN KEY (itemModelId) REFERENCES ITEM_MODEL (itemModelId);
ALTER TABLE SPECIFICATION_LIST ADD CONSTRAINT fk_spec_list_spec FOREIGN KEY (specId) REFERENCES SPECIFICATION (specId);
ALTER TABLE OPINION ADD CONSTRAINT fk_opinion_user FOREIGN KEY (userId) REFERENCES USER (userId);
ALTER TABLE OPINION ADD CONSTRAINT fk_opinion_item_model FOREIGN KEY (itemModelId) REFERENCES ITEM_MODEL (itemModelId);