INSERT INTO USERS (email, password, name, role)
VALUES
    ('admin@bookstore.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Admin John', 'EMPLOYEE'),
    ('manager@bookstore.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Manager Sarah', 'EMPLOYEE'),
    ('client1@mail.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Alice Wonderland', 'CLIENT'),
    ('client2@mail.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Bob Builder', 'CLIENT'),
    ('client3@mail.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Charlie Brown', 'CLIENT'),
    ('blocked@mail.com', '$2a$10$z0Wo4DrnasiuFLskDc40fuYPjhHgXhemmRVcdwzNzGmsGVChCnvdW', 'Bad Guy', 'CLIENT');

INSERT INTO EMPLOYEES (id, phone, birth_date)
VALUES
    (1, '+380991234567', '1985-05-20'),
    (2, '+380997654321', '1990-12-10');

INSERT INTO CLIENTS (id, is_blocked, balance)
VALUES
    (3, false, 2500.00),
    (4, false, 1500.50),
    (5, false, 300.00),
    (6, true, 0.00);

INSERT INTO BASKETS (client_id) VALUES (3), (4), (5), (6);

INSERT INTO BOOKS (name, genre, age_group, price, publication_year, author, pages, description, language, is_available)
VALUES
-- ПЕРША ПАРТІЯ (Класика та популярні)
('The Hobbit', 'FANTASY', 'TEEN', 350.00, 1937, 'J.R.R. Tolkien', 310, 'A great adventure of Bilbo Baggins.', 'ENGLISH', true),
('1984', 'FICTION', 'ADULT', 280.50, 1949, 'George Orwell', 328, 'A dystopian society and Big Brother.', 'ENGLISH', true),
('To Kill a Mockingbird', 'CLASSIC', 'TEEN', 300.00, 1960, 'Harper Lee', 281, 'Justice and race in American South.', 'ENGLISH', true),
('Brave New World', 'FICTION', 'ADULT', 270.00, 1932, 'Aldous Huxley', 311, 'A chilling vision of the future.', 'ENGLISH', true),
('Moby Dick', 'ADVENTURE', 'ADULT', 320.00, 1851, 'Herman Melville', 635, 'The saga of Captain Ahab and the whale.', 'ENGLISH', true),
('The Great Gatsby', 'CLASSIC', 'ADULT', 290.00, 1925, 'F. Scott Fitzgerald', 180, 'The American dream and jazz age.', 'ENGLISH', true),
('War and Peace', 'HISTORY', 'ADULT', 500.00, 1869, 'Leo Tolstoy', 1225, 'Napoleonic wars in Russia.', 'ENGLISH', true),
('Crime and Punishment', 'PSYCHOLOGY', 'ADULT', 310.00, 1866, 'Fyodor Dostoevsky', 671, 'A mental conflict after a crime.', 'ENGLISH', true),
('The Catcher in the Rye', 'FICTION', 'TEEN', 260.00, 1951, 'J.D. Salinger', 214, 'Teenage angst and alienation.', 'ENGLISH', true),
('The Alchemist', 'PHILOSOPHY', 'TEEN', 240.00, 1988, 'Paulo Coelho', 208, 'A journey of self-discovery.', 'ENGLISH', true),

('Harry Potter 1', 'FANTASY', 'TEEN', 400.00, 1997, 'J.K. Rowling', 309, 'The boy who lived.', 'ENGLISH', true),
('The Fellowship of the Ring', 'FANTASY', 'ADULT', 600.00, 1954, 'J.R.R. Tolkien', 423, 'Epic journey begins.', 'ENGLISH', true),
('The Hunger Games', 'FICTION', 'TEEN', 350.00, 2008, 'Suzanne Collins', 374, 'Survival game in Panem.', 'ENGLISH', true),
('Dune', 'SCIENCE', 'ADULT', 450.00, 1965, 'Frank Herbert', 412, 'Control of the spice on Arrakis.', 'ENGLISH', true),
('The Fault in Our Stars', 'ROMANCE', 'TEEN', 280.00, 2012, 'John Green', 313, 'A beautiful love story.', 'ENGLISH', true),
('Twilight', 'ROMANCE', 'TEEN', 300.00, 2005, 'Stephenie Meyer', 498, 'Vampire and human romance.', 'ENGLISH', true),
('Dracula', 'HORROR', 'ADULT', 270.00, 1897, 'Bram Stoker', 418, 'The original vampire story.', 'ENGLISH', true),
('Frankenstein', 'HORROR', 'ADULT', 260.00, 1818, 'Mary Shelley', 280, 'Man creates a monster.', 'ENGLISH', true),
('The Shining', 'HORROR', 'ADULT', 330.00, 1977, 'Stephen King', 447, 'Haunted hotel in the mountains.', 'ENGLISH', true),
('It', 'HORROR', 'ADULT', 550.00, 1986, 'Stephen King', 1138, 'The evil clown in Derry.', 'ENGLISH', true),

('Gone Girl', 'DETECTIVE', 'ADULT', 320.00, 2012, 'Gillian Flynn', 422, 'Mystery of a missing wife.', 'ENGLISH', true),
('The Girl with the Dragon Tattoo', 'DETECTIVE', 'ADULT', 340.00, 2005, 'Stieg Larsson', 465, 'A dark investigation in Sweden.', 'ENGLISH', true),
('The Da Vinci Code', 'DETECTIVE', 'ADULT', 310.00, 2003, 'Dan Brown', 454, 'Secret codes in art.', 'ENGLISH', true),
('Sherlock Holmes', 'DETECTIVE', 'TEEN', 280.00, 1892, 'Arthur Conan Doyle', 307, 'Classic detective stories.', 'ENGLISH', true),
('The Kite Runner', 'FICTION', 'ADULT', 290.00, 2003, 'Khaled Hosseini', 371, 'A story of friendship and redemption.', 'ENGLISH', true),
('A Thousand Splendid Suns', 'FICTION', 'ADULT', 300.00, 2007, 'Khaled Hosseini', 384, 'Struggles of women in Afghanistan.', 'ENGLISH', true),
('Life of Pi', 'ADVENTURE', 'TEEN', 310.00, 2001, 'Yann Martel', 319, 'Survival on a boat with a tiger.', 'ENGLISH', true),
('The Book Thief', 'HISTORY', 'TEEN', 320.00, 2005, 'Markus Zusak', 552, 'A girl and books in Nazi Germany.', 'ENGLISH', true),
('The Chronicles of Narnia', 'FANTASY', 'CHILD', 380.00, 1950, 'C.S. Lewis', 767, 'A magical world behind the wardrobe.', 'ENGLISH', true),
('Percy Jackson 1', 'FANTASY', 'TEEN', 290.00, 2005, 'Rick Riordan', 377, 'Greek gods in modern world.', 'ENGLISH', true),

('Foundation', 'SCIENCE', 'ADULT', 340.00, 1951, 'Isaac Asimov', 255, 'Saving the knowledge of galaxy.', 'ENGLISH', true),
('Neuromancer', 'SCIENCE', 'ADULT', 330.00, 1984, 'William Gibson', 271, 'The dawn of cyberpunk.', 'ENGLISH', true),
('The Martian', 'SCIENCE', 'ADULT', 350.00, 2011, 'Andy Weir', 369, 'Survival on Mars.', 'ENGLISH', true),
('The Road', 'FICTION', 'ADULT', 300.00, 2006, 'Cormac McCarthy', 287, 'Father and son in post-apocalypse.', 'ENGLISH', true),
('The Stand', 'HORROR', 'ADULT', 500.00, 1978, 'Stephen King', 823, 'A pandemic world.', 'ENGLISH', true),
('Bird Box', 'HORROR', 'ADULT', 290.00, 2014, 'Josh Malerman', 262, 'Survival without sight.', 'ENGLISH', true),
('Sapiens', 'HISTORY', 'ADULT', 420.00, 2011, 'Yuval Noah Harari', 443, 'Brief history of humankind.', 'ENGLISH', true),
('Thinking, Fast and Slow', 'PSYCHOLOGY', 'ADULT', 380.00, 2011, 'Daniel Kahneman', 499, 'How our mind works.', 'ENGLISH', true),
('The Power of Habit', 'PSYCHOLOGY', 'ADULT', 310.00, 2012, 'Charles Duhigg', 371, 'Why we do what we do.', 'ENGLISH', true),
('Man''s Search for Meaning', 'PSYCHOLOGY', 'ADULT', 250.00, 1946, 'Viktor Frankl', 165, 'Psychiatrist in Nazi camp.', 'ENGLISH', true),

('Kobzar', 'POETRY', 'ADULT', 200.00, 1840, 'Taras Shevchenko', 300, 'Classic Ukrainian poetry.', 'UKRAINIAN', true),
('Shadows of Forgotten Ancestors', 'FICTION', 'ADULT', 180.00, 1911, 'Mykhailo Kotsiubynsky', 150, 'Hutsul Romeo and Juliet.', 'UKRAINIAN', true),
('Forest Song', 'FANTASY', 'ADULT', 170.00, 1911, 'Lesya Ukrainka', 120, 'Drama-extravaganza.', 'UKRAINIAN', true),
('Tiger Trappers', 'ADVENTURE', 'TEEN', 220.00, 1944, 'Ivan Bahrianyi', 350, 'Survival in Siberian taiga.', 'UKRAINIAN', true),
('Intermezzo', 'FICTION', 'ADULT', 100.00, 1908, 'Mykhailo Kotsiubynsky', 40, 'Psychological impressionism.', 'UKRAINIAN', true),
('The Little Prince', 'CHILDREN', 'CHILD', 210.00, 1943, 'Antoine de Saint-Exupéry', 96, 'A philosophical tale.', 'FRENCH', true),
('The Stranger', 'PHILOSOPHY', 'ADULT', 240.00, 1942, 'Albert Camus', 123, 'Existentialist novel.', 'FRENCH', true),
('The Metamorphosis', 'FICTION', 'ADULT', 190.00, 1915, 'Franz Kafka', 80, 'Man turns into an insect.', 'GERMAN', true),
('Don Quixote', 'ADVENTURE', 'ADULT', 450.00, 1605, 'Miguel de Cervantes', 863, 'The knight of the sad countenance.', 'SPANISH', true),
('Norwegian Wood', 'ROMANCE', 'ADULT', 310.00, 1987, 'Haruki Murakami', 296, 'Nostalgic story of loss.', 'JAPANESE', true),

('The Silent Patient', 'DETECTIVE', 'ADULT', 290.00, 2019, 'Alex Michaelides', 336, 'A woman shoots her husband.', 'ENGLISH', true),
('The Seven Husbands of Evelyn Hugo', 'FICTION', 'ADULT', 320.00, 2017, 'Taylor Jenkins Reid', 389, 'Hollywood icon secrets.', 'ENGLISH', true),
('Circe', 'FANTASY', 'ADULT', 300.00, 2018, 'Madeline Miller', 393, 'A goddess found her place.', 'ENGLISH', true),
('Where the Crawdads Sing', 'FICTION', 'ADULT', 310.00, 2018, 'Delia Owens', 384, 'Mystery in the marsh.', 'ENGLISH', true),
('Project Hail Mary', 'SCIENCE', 'ADULT', 360.00, 2021, 'Andy Weir', 476, 'Lone astronaut saves Earth.', 'ENGLISH', true),
('Atomic Habits', 'PSYCHOLOGY', 'ADULT', 340.00, 2018, 'James Clear', 320, 'Small changes, big results.', 'ENGLISH', true),
('Deep Work', 'PSYCHOLOGY', 'ADULT', 290.00, 2016, 'Cal Newport', 304, 'Focus in a distracted world.', 'ENGLISH', true),
('The Subtle Art', 'PHILOSOPHY', 'ADULT', 270.00, 2016, 'Mark Manson', 224, 'A counterintuitive approach.', 'ENGLISH', true),
('Educated', 'BIOGRAPHY', 'ADULT', 330.00, 2018, 'Tara Westover', 335, 'Survival and education.', 'ENGLISH', true),
('The 5 AM Club', 'PSYCHOLOGY', 'ADULT', 280.00, 2018, 'Robin Sharma', 336, 'Own your morning.', 'ENGLISH', true),

('Ready Player One', 'SCIENCE', 'TEEN', 320.00, 2011, 'Ernest Cline', 374, 'Virtual world Oasis.', 'ENGLISH', true),
('Divergent', 'FICTION', 'TEEN', 300.00, 2011, 'Veronica Roth', 487, 'A divided society.', 'ENGLISH', true),
('The Maze Runner', 'FICTION', 'TEEN', 280.00, 2009, 'James Dashner', 375, 'Escape from the Glade.', 'ENGLISH', true),
('Inferno', 'DETECTIVE', 'ADULT', 330.00, 2013, 'Dan Brown', 480, 'Dante-inspired mystery.', 'ENGLISH', true),
('The Name of the Rose', 'HISTORY', 'ADULT', 350.00, 1980, 'Umberto Eco', 512, 'Murders in a monastery.', 'ENGLISH', true),
('The Shadow of the Wind', 'FICTION', 'ADULT', 340.00, 2001, 'Carlos Ruiz Zafon', 487, 'The Cemetery of Forgotten Books.', 'ENGLISH', true),
('The Nightingale', 'HISTORY', 'ADULT', 310.00, 2015, 'Kristin Hannah', 440, 'WWII sisters in France.', 'ENGLISH', true),
('The Goldfinch', 'FICTION', 'ADULT', 360.00, 2013, 'Donna Tartt', 771, 'A painting and a boy.', 'ENGLISH', true),
('Life After Life', 'FICTION', 'ADULT', 320.00, 2013, 'Kate Atkinson', 448, 'Infinite chances to live.', 'ENGLISH', true),
('Room', 'FICTION', 'ADULT', 280.00, 2010, 'Emma Donoghue', 321, 'Life in a single room.', 'ENGLISH', true),
('Wonder', 'CHILDREN', 'CHILD', 250.00, 2012, 'R.J. Palacio', 315, 'A boy with a different face.', 'ENGLISH', true),
('The Help', 'HISTORY', 'ADULT', 300.00, 2009, 'Kathryn Stockett', 451, 'Stories of African American maids.', 'ENGLISH', true),
('The Shack', 'FICTION', 'ADULT', 240.00, 2007, 'Wm. Paul Young', 256, 'Meeting God in a tragedy.', 'ENGLISH', true),
('Eat Pray Love', 'BIOGRAPHY', 'ADULT', 280.00, 2006, 'Elizabeth Gilbert', 352, 'Search for everything.', 'ENGLISH', true),
('The Girl on the Train', 'DETECTIVE', 'ADULT', 300.00, 2015, 'Paula Hawkins', 336, 'What she saw from the train.', 'ENGLISH', true);

INSERT INTO BASKET_ITEMS (basket_id, book_id, quantity)
VALUES
    (1, 1, 2),
    (1, 15, 1),
    (2, 45, 3);

INSERT INTO ORDERS (client_id, employee_id, order_date, price, status)
VALUES
    (3, 1, '2023-10-25 14:30:00', 630.50, 'CONFIRMED'),
    (4, 1, '2023-11-05 10:20:00', 1200.00, 'CONFIRMED'),
    (3, 2, '2024-01-15 16:45:00', 350.00, 'REFUNDED'),
    (5, NULL, '2024-04-10 09:00:00', 280.00, 'PENDING'),
    (4, NULL, '2024-04-10 11:30:00', 450.00, 'PENDING');

INSERT INTO BOOK_ITEMS (order_id, book_id, quantity)
VALUES
    (1, 1, 1), (1, 2, 1),
    (2, 12, 2),
    (3, 1, 1),
    (4, 15, 1),
    (5, 14, 1);