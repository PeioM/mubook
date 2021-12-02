#TipoUser
#User
#tipo
#itemmodel
#item
#especificacion
#sala
#listaespecificacion
#reserva

CREATE TABLE IF NOT EXISTS USER_TYPE
(
    userTypeId              NUMERIC,
    description     VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS USER
(
    userId              NUMERIC,
    ptrUserType     NUMERIC,
    name            VARCHAR(20),
    surname         VARCHAR(30),
    DNI             VARCHAR(9),
    bornDate        DATE,
    validated       BOOLEAN,
    dniImg          VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS ITEM_TYPE
(
    itemTypeId              NUMERIC,
    name            VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS ITEM_MODEL
(
    itemModelId              NUMERIC,
    ptrItemType     NUMERIC,
    description     VARCHAR(2000),
    name            VARCHAR(100),
    identifyer      VARCHAR(30),
    img             VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS ITEM
(
    itemId              NUMERIC,
    ptrItemModel    NUMERIC,
    serialNum       VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS SPECIFICATION
(
    specId              NUMERIC,
    description     VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS ROOM
(
    roomId              NUMERIC,
    description     VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS SPECIFICATION_LIST
(
    ptrItemModel    NUMERIC,
    ptrSpec         NUMERIC,
    value           VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS RESERVATION
(
    reservationId   NUMERIC,
    ptrUser         NUMERIC,
    ptrItem         NUMERIC,
    ptrRoom         NUMERIC,
    initDate        DATE,
    finalDate       DATE
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
ALTER TABLE SPECIFICATION_LIST ADD CONSTRAINT pk_spec_list PRIMARY KEY (ptrItemModel, ptrSpec);

#Create foreign key
ALTER TABLE USER ADD CONSTRAINT fk_user_type FOREIGN KEY (ptrUserType) REFERENCES USER_TYPE(userTypeId);
ALTER TABLE RESERVATION ADD CONSTRAINT fk_reservation_user FOREIGN KEY (ptrUser) REFERENCES USER (userId);
ALTER TABLE RESERVATION ADD CONSTRAINT fk_reservation_item FOREIGN KEY (ptrItem) REFERENCES ITEM (itemId);
ALTER TABLE RESERVATION ADD CONSTRAINT fk_reservation_room FOREIGN KEY (ptrRoom) REFERENCES ROOM (roomId);
ALTER TABLE ITEM ADD CONSTRAINT fk_item_model FOREIGN KEY (ptrItemModel) REFERENCES ITEM_MODEL (itemModelId);
ALTER TABLE ITEM_MODEL ADD CONSTRAINT fk_item_model_type FOREIGN KEY (ptrItemType) REFERENCES ITEM_TYPE (itemTypeId);
ALTER TABLE SPECIFICATION_LIST ADD CONSTRAINT fk_spec_list_item FOREIGN KEY (ptrItemModel) REFERENCES ITEM_MODEL (itemModelId);
ALTER TABLE SPECIFICATION_LIST ADD CONSTRAINT fk_spec_list_spec FOREIGN KEY (ptrSpec) REFERENCES SPECIFICATION (specId);