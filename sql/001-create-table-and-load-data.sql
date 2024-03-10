DROP TABLE IF EXISTS inventoryProducts;
DROP TABLE IF EXISTS products;

CREATE TABLE products (
  id int unsigned AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL,
  PRIMARY KEY(id)
);

INSERT INTO products (id, name) VALUES (1, "Bolt 1");
INSERT INTO products (id, name) VALUES (2, "Washer");

CREATE TABLE inventoryProducts (
  id int unsigned AUTO_INCREMENT,
  product_id int unsigned,
  quantity int DEFAULT 0,
  history DATETIME NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES products (id)
);

INSERT INTO inventoryProducts (product_id, quantity, history) VALUES (1, 100, '2023-12-10 23:58:10');
INSERT INTO inventoryProducts (product_id, quantity, history) VALUES (2, 200, '2024-1-10 12:58:10');

