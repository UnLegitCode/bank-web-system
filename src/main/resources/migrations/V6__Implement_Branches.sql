CREATE TABLE addresses
(
    id           VARCHAR(255) NOT NULL,
    region       VARCHAR(255) NOT NULL,
    city         VARCHAR(255) NOT NULL,
    street       VARCHAR(255) NOT NULL,
    house_number INTEGER      NOT NULL,
    CONSTRAINT pk_addresses PRIMARY KEY (id)
);

CREATE TABLE branches
(
    id                   VARCHAR(255) NOT NULL,
    address_id           VARCHAR(255) NOT NULL,
    working_hours        VARCHAR(64)  NOT NULL,
    contact_phone_number VARCHAR(255) NOT NULL,
    CONSTRAINT pk_branches PRIMARY KEY (id)
);

ALTER TABLE branches
    ADD CONSTRAINT uc_branches_contact_phone_number UNIQUE (contact_phone_number);

CREATE UNIQUE INDEX series_number_pair ON passports (series, number);

ALTER TABLE branches
    ADD CONSTRAINT FK_BRANCHES_ON_ADDRESS FOREIGN KEY (address_id) REFERENCES addresses (id);