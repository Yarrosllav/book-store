-- 1. СТВОРЕННЯ КОРИСТУВАЧІВ (Пароль: password123)
INSERT INTO USERS (email, password, name, role)
VALUES ('admin@bookstore.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzBqVG9O8S1N6m7o6eC', 'Admin John', 'ROLE_EMPLOYEE'),
       ('client1@mail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzBqVG9O8S1N6m7o6eC', 'Alice Wonderland', 'ROLE_CLIENT'),
       ('client2@mail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzBqVG9O8S1N6m7o6eC', 'Bob Builder', 'ROLE_CLIENT');

-- 2. ДОДАВАННЯ СПЕЦИФІЧНИХ ДАНИХ
INSERT INTO EMPLOYEES (user_id, phone, birth_date)
VALUES (1, '+380991234567', '1985-05-20');

INSERT INTO CLIENTS (user_id, balance)
VALUES (2, 1500.50),
       (3, 300.00);

-- 3. СТВОРЕННЯ КОШИКІВ ДЛЯ КЛІЄНТІВ (Клієнт 2 та 3)
INSERT INTO BASKETS (client_id) VALUES (2), (3);

-- 4. ДОДАВАННЯ КНИГ
INSERT INTO BOOKS (name, genre, age_group, price, publication_date, author, pages, characteristics, description, language)
VALUES
    ('The Hobbit', 'Fantasy', 'TEEN', 350.00, '1937-09-21', 'J.R.R. Tolkien', 310, 'Hardcover', 'A great adventure', 'ENGLISH'),
    ('1984', 'Dystopian', 'ADULT', 280.50, '1949-06-08', 'George Orwell', 328, 'Paperback', 'A classic dystopian novel', 'ENGLISH');

-- 5. НАПОВНЕННЯ КОШИКА (Клієнт 2 додав 2 штуки книги 'The Hobbit' до свого кошика)
-- Кошик клієнта 2 має id = 1, Книга 'The Hobbit' має id = 1
INSERT INTO BASKET_ITEMS (basket_id, book_id, quantity)
VALUES (1, 1, 2);

-- 6. СТВОРЕННЯ ІСТОРИЧНИХ ЗАМОВЛЕНЬ
INSERT INTO ORDERS (client_id, employee_id, order_date, price)
VALUES (2, 1, '2023-10-25 14:30:00', 630.50);

INSERT INTO BOOK_ITEMS (order_id, book_id, quantity)
VALUES (1, 1, 1),
       (1, 2, 1);