INSERT INTO USERS (email, password, name, role)
VALUES ('admin@bookstore.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Admin John', 'EMPLOYEE'),
       ('client1@mail.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Alice Wonderland', 'CLIENT'),
       ('client2@mail.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Bob Builder', 'CLIENT');

INSERT INTO EMPLOYEES (id, phone, birth_date)
VALUES (1, '+380991234567', '1985-05-20');

INSERT INTO CLIENTS (id, is_blocked, balance)
VALUES (2, false, 1500.50),
       (3, false, 300.00);

INSERT INTO BASKETS (client_id) VALUES (2), (3);

INSERT INTO BOOKS (name, genre, age_group, price, publication_date, author, pages, characteristics, description, language)
VALUES
    ('The Hobbit', 'FANTASY', 'TEEN', 350.00, '1937-09-21', 'J.R.R. Tolkien', 310, 'Hardcover', 'A great adventure', 'ENGLISH'),
    ('1984', 'FICTION', 'ADULT', 280.50, '1949-06-08', 'George Orwell', 328, 'Paperback', 'A dystopian society', 'ENGLISH'),
    ('To Kill a Mockingbird', 'CLASSIC', 'TEEN', 300.00, '1960-07-11', 'Harper Lee', 281, 'Paperback', 'Justice and race', 'ENGLISH'),
    ('Brave New World', 'FICTION', 'ADULT', 270.00, '1932-08-31', 'Aldous Huxley', 311, 'Hardcover', 'Future society', 'ENGLISH'),
    ('Moby Dick', 'ADVENTURE', 'ADULT', 320.00, '1851-10-18', 'Herman Melville', 635, 'Hardcover', 'Whale hunting', 'ENGLISH'),
    ('The Great Gatsby', 'CLASSIC', 'ADULT', 290.00, '1925-04-10', 'F. Scott Fitzgerald', 180, 'Paperback', 'American dream', 'ENGLISH'),
    ('War and Peace', 'HISTORY', 'ADULT', 500.00, '1869-01-01', 'Leo Tolstoy', 1225, 'Hardcover', 'Napoleonic wars', 'ENGLISH'),
    ('Crime and Punishment', 'PSYCHOLOGY', 'ADULT', 310.00, '1866-01-01', 'Fyodor Dostoevsky', 671, 'Paperback', 'Mental conflict', 'ENGLISH'),
    ('The Catcher in the Rye', 'FICTION', 'TEEN', 260.00, '1951-07-16', 'J.D. Salinger', 214, 'Paperback', 'Teen life', 'ENGLISH'),
    ('The Alchemist', 'PHILOSOPHY', 'TEEN', 240.00, '1988-01-01', 'Paulo Coelho', 208, 'Paperback', 'Self discovery', 'ENGLISH'),

    ('Harry Potter and the Sorcerer''s Stone', 'FANTASY', 'TEEN', 400.00, '1997-06-26', 'J.K. Rowling', 309, 'Hardcover', 'Wizard story', 'ENGLISH'),
    ('The Lord of the Rings', 'FANTASY', 'ADULT', 600.00, '1954-07-29', 'J.R.R. Tolkien', 1178, 'Hardcover', 'Epic fantasy', 'ENGLISH'),
    ('The Hunger Games', 'FICTION', 'TEEN', 350.00, '2008-09-14', 'Suzanne Collins', 374, 'Paperback', 'Survival game', 'ENGLISH'),
    ('Dune', 'SCIENCE', 'ADULT', 450.00, '1965-08-01', 'Frank Herbert', 412, 'Hardcover', 'Desert planet', 'ENGLISH'),
    ('The Fault in Our Stars', 'ROMANCE', 'TEEN', 280.00, '2012-01-10', 'John Green', 313, 'Paperback', 'Love story', 'ENGLISH'),
    ('Twilight', 'ROMANCE', 'TEEN', 300.00, '2005-10-05', 'Stephenie Meyer', 498, 'Paperback', 'Vampire love', 'ENGLISH'),
    ('Dracula', 'HORROR', 'ADULT', 270.00, '1897-05-26', 'Bram Stoker', 418, 'Hardcover', 'Vampire story', 'ENGLISH'),
    ('Frankenstein', 'HORROR', 'ADULT', 260.00, '1818-01-01', 'Mary Shelley', 280, 'Paperback', 'Science horror', 'ENGLISH'),
    ('The Shining', 'HORROR', 'ADULT', 330.00, '1977-01-28', 'Stephen King', 447, 'Hardcover', 'Haunted hotel', 'ENGLISH'),
    ('It', 'HORROR', 'ADULT', 550.00, '1986-09-15', 'Stephen King', 1138, 'Paperback', 'Evil clown', 'ENGLISH'),

    ('Gone Girl', 'DETECTIVE', 'ADULT', 320.00, '2012-06-05', 'Gillian Flynn', 422, 'Paperback', 'Crime mystery', 'ENGLISH'),
    ('The Girl with the Dragon Tattoo', 'DETECTIVE', 'ADULT', 340.00, '2005-08-01', 'Stieg Larsson', 465, 'Hardcover', 'Investigation', 'ENGLISH'),
    ('The Da Vinci Code', 'DETECTIVE', 'ADULT', 310.00, '2003-03-18', 'Dan Brown', 454, 'Paperback', 'Secret codes', 'ENGLISH'),
    ('Sherlock Holmes', 'DETECTIVE', 'TEEN', 280.00, '1892-10-14', 'Arthur Conan Doyle', 307, 'Hardcover', 'Detective stories', 'ENGLISH'),
    ('The Kite Runner', 'FICTION', 'ADULT', 290.00, '2003-05-29', 'Khaled Hosseini', 371, 'Paperback', 'Friendship', 'ENGLISH'),
    ('A Thousand Splendid Suns', 'FICTION', 'ADULT', 300.00, '2007-05-22', 'Khaled Hosseini', 384, 'Paperback', 'Life struggles', 'ENGLISH'),
    ('Life of Pi', 'ADVENTURE', 'TEEN', 310.00, '2001-09-11', 'Yann Martel', 319, 'Paperback', 'Survival', 'ENGLISH'),
    ('The Book Thief', 'HISTORY', 'TEEN', 320.00, '2005-03-14', 'Markus Zusak', 552, 'Paperback', 'WWII story', 'ENGLISH'),
    ('The Chronicles of Narnia', 'FANTASY', 'TEEN', 380.00, '1950-10-16', 'C.S. Lewis', 767, 'Hardcover', 'Fantasy world', 'ENGLISH'),
    ('Percy Jackson & The Lightning Thief', 'FANTASY', 'TEEN', 290.00, '2005-06-28', 'Rick Riordan', 377, 'Paperback', 'Greek gods', 'ENGLISH'),

    ('Eragon', 'FANTASY', 'TEEN', 310.00, '2002-08-26', 'Christopher Paolini', 544, 'Paperback', 'Dragon rider', 'ENGLISH'),
    ('The Maze Runner', 'FICTION', 'TEEN', 280.00, '2009-10-06', 'James Dashner', 375, 'Paperback', 'Maze survival', 'ENGLISH'),
    ('Divergent', 'FICTION', 'TEEN', 300.00, '2011-04-25', 'Veronica Roth', 487, 'Paperback', 'Society system', 'ENGLISH'),
    ('Ready Player One', 'SCIENCE', 'TEEN', 320.00, '2011-08-16', 'Ernest Cline', 374, 'Paperback', 'Virtual world', 'ENGLISH'),
    ('Ender''s Game', 'SCIENCE', 'TEEN', 310.00, '1985-01-15', 'Orson Scott Card', 324, 'Paperback', 'Space war', 'ENGLISH'),
    ('Foundation', 'SCIENCE', 'ADULT', 340.00, '1951-01-01', 'Isaac Asimov', 255, 'Hardcover', 'Galactic empire', 'ENGLISH'),
    ('Neuromancer', 'SCIENCE', 'ADULT', 330.00, '1984-07-01', 'William Gibson', 271, 'Paperback', 'Cyberpunk', 'ENGLISH'),
    ('The Martian', 'SCIENCE', 'ADULT', 350.00, '2011-09-27', 'Andy Weir', 369, 'Paperback', 'Mars survival', 'ENGLISH'),
    ('The Road', 'FICTION', 'ADULT', 300.00, '2006-09-26', 'Cormac McCarthy', 287, 'Paperback', 'Post-apocalypse', 'ENGLISH'),
    ('The Stand', 'HORROR', 'ADULT', 500.00, '1978-10-03', 'Stephen King', 823, 'Hardcover', 'Pandemic', 'ENGLISH'),
    ('Bird Box', 'HORROR', 'ADULT', 290.00, '2014-03-27', 'Josh Malerman', 262, 'Paperback', 'Blind survival', 'ENGLISH');

INSERT INTO BASKET_ITEMS (basket_id, book_id, quantity)
VALUES (1, 1, 2);

INSERT INTO ORDERS (client_id, employee_id, order_date, price)
VALUES (2, 1, '2023-10-25 14:30:00', 630.50);

INSERT INTO BOOK_ITEMS (order_id, book_id, quantity)
VALUES (1, 1, 1),
       (1, 2, 1);