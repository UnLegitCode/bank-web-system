CREATE TABLE users
(
    id            VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    patronymic    VARCHAR(255) NOT NULL,
    birth_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    region        VARCHAR(255) NOT NULL,
    city          VARCHAR(255) NOT NULL,
    passport_id   VARCHAR(255),
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          SMALLINT     NOT NULL,
    registered_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE passports
(
    id         VARCHAR(255) NOT NULL,
    series     VARCHAR(255) NOT NULL,
    number     VARCHAR(255) NOT NULL,
    issue_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_passports PRIMARY KEY (id)
);

CREATE TABLE accounts
(
    id       VARCHAR(255)     NOT NULL,
    owner_id VARCHAR(255)     NOT NULL,
    number   BIGINT           NOT NULL,
    balance  DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

CREATE TABLE transactions
(
    id             VARCHAR(255)     NOT NULL,
    user_id        VARCHAR(255)     NOT NULL,
    source_account VARCHAR(255),
    target_account VARCHAR(255)     NOT NULL,
    operation      SMALLINT         NOT NULL,
    sum            DOUBLE PRECISION NOT NULL,
    title          VARCHAR(32)      NOT NULL,
    details        VARCHAR(255)     NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_PASSPORT FOREIGN KEY (passport_id) REFERENCES passports (id);

ALTER TABLE accounts
    ADD CONSTRAINT FK_ACCOUNTS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_SOURCE_ACCOUNT FOREIGN KEY (source_account) REFERENCES accounts (id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_TARGET_ACCOUNT FOREIGN KEY (target_account) REFERENCES accounts (id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);