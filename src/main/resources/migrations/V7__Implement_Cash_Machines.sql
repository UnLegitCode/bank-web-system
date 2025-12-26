CREATE TABLE cash_machines
(
    id         VARCHAR(255) NOT NULL,
    address_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_cash_machines PRIMARY KEY (id)
);

ALTER TABLE cash_machines
    ADD CONSTRAINT FK_CASH_MACHINES_ON_ADDRESS FOREIGN KEY (address_id) REFERENCES addresses (id);