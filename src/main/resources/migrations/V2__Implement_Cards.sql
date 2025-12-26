CREATE TABLE cards
(
    id         VARCHAR(255) NOT NULL,
    owner_id   VARCHAR(255) NOT NULL,
    account_id VARCHAR(255) NOT NULL,
    number     VARCHAR(19)  NOT NULL,
    pin_hash   VARCHAR(255) NOT NULL,
    blocked    BOOLEAN      NOT NULL,
    closed     BOOLEAN      NOT NULL,
    opened_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_cards PRIMARY KEY (id)
);

ALTER TABLE cards
    ADD CONSTRAINT uc_cards_number UNIQUE (number);

ALTER TABLE cards
    ADD CONSTRAINT FK_CARDS_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);

ALTER TABLE cards
    ADD CONSTRAINT FK_CARDS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);