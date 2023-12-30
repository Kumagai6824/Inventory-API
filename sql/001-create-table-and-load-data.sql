DROP TABLE IF EXISTS products;

CREATE TABLE products (
  id int unsigned AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL,
  PRIMARY KEY(id)
);

INSERT INTO products (id, name) VALUES (1, "Bolt 1");
INSERT INTO products (id, name) VALUES (2, "Washer");
