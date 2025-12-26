CREATE TABLE goals
(
    id             VARCHAR(255)     NOT NULL,
    owner_id       VARCHAR(255)     NOT NULL,
    account_id     VARCHAR(255)     NOT NULL,
    target_balance DOUBLE PRECISION NOT NULL,
    display_name   VARCHAR(32)      NOT NULL,
    target_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    closed         BOOLEAN          NOT NULL,
    opened_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_goals PRIMARY KEY (id)
);

ALTER TABLE goals
    ADD CONSTRAINT FK_GOALS_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);

ALTER TABLE goals
    ADD CONSTRAINT FK_GOALS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);