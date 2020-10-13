CREATE TABLE orders
(
    id         INT8 GENERATED BY DEFAULT AS IDENTITY,
    orderstate VARCHAR(10)    NOT NULL,
    customerid INT8           NOT NULL,
    ordertotal DECIMAL(19, 2) NOT NULL,
    version    INT4           NOT NULL,
    PRIMARY KEY (id)
);