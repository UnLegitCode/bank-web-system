CREATE TABLE deposit_terms
(
    id                    VARCHAR(255) NOT NULL,
    name                  VARCHAR(32)  NOT NULL,
    description           VARCHAR(255) NOT NULL,
    min_sum               INTEGER      NOT NULL,
    interest_rate         INTEGER      NOT NULL,
    term_months           INTEGER      NOT NULL,
    capitalization_period SMALLINT     NOT NULL,
    replenishable         BOOLEAN      NOT NULL,
    partial_withdrawal    BOOLEAN      NOT NULL,
    CONSTRAINT pk_deposit_terms PRIMARY KEY (id)
);

CREATE TABLE deposits
(
    id                  VARCHAR(255)     NOT NULL,
    owner_id            VARCHAR(255)     NOT NULL,
    account_id          VARCHAR(255)     NOT NULL,
    terms_id            VARCHAR(255)     NOT NULL,
    initial_sum         DOUBLE PRECISION NOT NULL,
    auto_renew          BOOLEAN          NOT NULL,
    closed              BOOLEAN          NOT NULL,
    opened_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    next_capitalization TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_deposits PRIMARY KEY (id)
);

ALTER TABLE deposits
    ADD CONSTRAINT FK_DEPOSITS_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);

ALTER TABLE deposits
    ADD CONSTRAINT FK_DEPOSITS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);

ALTER TABLE deposits
    ADD CONSTRAINT FK_DEPOSITS_ON_TERMS FOREIGN KEY (terms_id) REFERENCES deposit_terms (id);