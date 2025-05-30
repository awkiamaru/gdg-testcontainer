CREATE TABLE customer (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL
);


CREATE TABLE product (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    stock INTEGER NOT NULL
);


CREATE TYPE order_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELED');

CREATE TABLE customer_order (
    id UUID PRIMARY KEY NOT NULL,
    customer_id UUID NOT NULL,
    status order_status NOT NULL,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);

CREATE TABLE order_item (
    id UUID PRIMARY KEY NOT NULL,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES customer_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);