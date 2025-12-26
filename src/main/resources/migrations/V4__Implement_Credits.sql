CREATE TABLE credit_terms
(
    id            VARCHAR(255)     NOT NULL,
    name          VARCHAR(32)      NOT NULL,
    description   VARCHAR(255)     NOT NULL,
    min_sum       DOUBLE PRECISION NOT NULL,
    max_sum       DOUBLE PRECISION NOT NULL,
    interest_rate INTEGER          NOT NULL,
    CONSTRAINT pk_credit_terms PRIMARY KEY (id)
);

CREATE TABLE credits
(
    id                    VARCHAR(255)     NOT NULL,
    owner_id              VARCHAR(255)     NOT NULL,
    account_id            VARCHAR(255)     NOT NULL,
    interest_rate         INTEGER          NOT NULL,
    initial_sum           DOUBLE PRECISION NOT NULL,
    target_balance        DOUBLE PRECISION NOT NULL,
    next_interest_accrual TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    closed                BOOLEAN          NOT NULL,
    opened_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_credits PRIMARY KEY (id)
);

ALTER TABLE credits
    ADD CONSTRAINT FK_CREDITS_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);

ALTER TABLE credits
    ADD CONSTRAINT FK_CREDITS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);