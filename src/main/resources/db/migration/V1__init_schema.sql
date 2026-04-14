SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS basket_items;
DROP TABLE IF EXISTS book_items;
DROP TABLE IF EXISTS baskets;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(255),
                       role ENUM('CLIENT', 'EMPLOYEE') DEFAULT NULL,
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE employees (
                           id BIGINT NOT NULL,
                           phone VARCHAR(255),
                           birth_date DATE,
                           PRIMARY KEY (id),
                           CONSTRAINT fk_employees_users
                               FOREIGN KEY (id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE clients (
                         id BIGINT NOT NULL,
                         is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
                         balance DECIMAL(38,2),
                         PRIMARY KEY (id),
                         CONSTRAINT fk_clients_users
                             FOREIGN KEY (id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE books (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(255),
                       genre ENUM('FICTION','NON_FICTION','SCIENCE','HISTORY','BIOGRAPHY','CHILDREN','FANTASY','DETECTIVE','ROMANCE','HORROR','CLASSIC','POETRY','ADVENTURE','PSYCHOLOGY','PHILOSOPHY') NOT NULL,
                       age_group ENUM('CHILD','TEEN','ADULT') NOT NULL,
                       price DECIMAL(38,2),
                       publication_year INT,
                       author VARCHAR(255),
                       pages INT,
                       description VARCHAR(255),
                       language ENUM('ENGLISH','SPANISH','FRENCH','GERMAN','JAPANESE','UKRAINIAN') NOT NULL,
                       is_available BOOLEAN,
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE baskets (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         client_id BIGINT UNIQUE,
                         PRIMARY KEY (id),
                         CONSTRAINT fk_baskets_clients
                             FOREIGN KEY (client_id) REFERENCES clients(id)
) ENGINE=InnoDB;

CREATE TABLE orders (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        client_id BIGINT,
                        employee_id BIGINT,
                        order_date DATETIME(6),
                        price DECIMAL(38,2),
                        status ENUM('PENDING','CONFIRMED','REFUNDED') DEFAULT NULL,
                        PRIMARY KEY (id),
                        CONSTRAINT fk_orders_clients
                            FOREIGN KEY (client_id) REFERENCES clients(id),
                        CONSTRAINT fk_orders_employees
                            FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB;

CREATE TABLE basket_items (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              basket_id BIGINT,
                              book_id BIGINT,
                              quantity INT,
                              PRIMARY KEY (id),
                              CONSTRAINT fk_basket_items_baskets
                                  FOREIGN KEY (basket_id) REFERENCES baskets(id),
                              CONSTRAINT fk_basket_items_books
                                  FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB;

CREATE TABLE book_items (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            order_id BIGINT,
                            book_id BIGINT,
                            quantity INT,
                            PRIMARY KEY (id),
                            CONSTRAINT fk_book_items_orders
                                FOREIGN KEY (order_id) REFERENCES orders(id),
                            CONSTRAINT fk_book_items_books
                                FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB;