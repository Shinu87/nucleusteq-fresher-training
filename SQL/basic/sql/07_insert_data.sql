show databases;

-- creating a new database for this assignment this will store all the tables like customers, products and orders
create database e_commerce;
show databases;

use e_commerce;
-- creating customers table to store basic customer details
create table Customers(
    customer_id INT auto_increment primary key,
    name varchar(50),
    email varchar(50),
    mobile varchar(15)
);

-- creating products table to store product related information
create table Products(
    id INT,
    name varchar(50) not null,
    description varchar(200),
    price decimal(10,2) not null,
    category varchar(50)
);

-- show all tables
show tables;

-- making name and email not null so empty values are not allowed
alter table Customers
modify name varchar(50) not null;

alter table Customers
modify email varchar(50) not null;

describe Customers;

-- adding unique constraint on email to avoid duplicate entries
alter table Customers
add constraint unique_email unique(email);

describe Customers;

-- adding age column
alter table Customers
add age INT;

describe Customers;


-- renaming id to product_id for better understanding
alter table Products
change id product_id INT;

describe Products;

-- setting product_id as primary key and enabling auto increment
alter table Products
modify product_id INT auto_increment primary key;

describe Products;


-- changing description datatype to text to allow longer content
alter table Products
modify description text;

describe Products;


-- creating order table to keep track of customer orders this will link customers and products together
CREATE TABLE `Order` (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    product_id INT,
    quantity INT NOT NULL,
    order_date DATE NOT NULL,
    status ENUM('Pending', 'Success', 'Cancel') DEFAULT 'Pending',
    payment_method ENUM('Credit', 'Debit', 'UPI') NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

SHOW TABLES;

-- renaming table from Order to Orders
ALTER TABLE `Order`
RENAME TO Orders;

SHOW TABLES;

-- setting default value of status as pending
ALTER TABLE Orders
MODIFY status ENUM('Pending', 'Success', 'Cancel') DEFAULT 'Pending';

describe Orders;

-- adding new payment option COD in payment_method
ALTER TABLE Orders
MODIFY payment_method ENUM('Credit', 'Debit', 'UPI', 'COD');

DESCRIBE Orders;

-- adding foreign key for product_id to maintain relation with products table
ALTER TABLE Orders
ADD CONSTRAINT foreign_key_product
FOREIGN KEY (product_id) REFERENCES Products(product_id);

DESCRIBE Orders;

-- inserting sample data into customers table
INSERT INTO Customers (name, email, mobile, age) VALUES
('Alice Johnson', 'alice1@gmail.com', '9876543210', 25),
('Bob Smith', 'bob2@gmail.com', '9876543211', 30),
('Charlie Brown', 'charlie3@gmail.com', '9876543212', 28),
('David Lee', 'david4@gmail.com', '9876543213', 35),
('Eva Green', 'eva5@gmail.com', '9876543214', 22),
('Frank White', 'frank6@gmail.com', '9876543215', 40),
('Grace Kim', 'grace7@gmail.com', '9876543216', 27),
('Hannah Scott', 'hannah8@gmail.com', '9876543217', 33),
('Ian Clark', 'ian9@gmail.com', '9876543218', 29),
('Jane Doe', 'jane10@gmail.com', '9876543219', 31),
('Kevin Hart', 'kevin11@gmail.com', '9876543220', 26),
('Linda Park', 'linda12@gmail.com', '9876543221', 24),
('Michael Chen', 'michael13@gmail.com', '9876543222', 36),
('Nina Patel', 'nina14@gmail.com', '9876543223', 32),
('Oscar Wilde', 'oscar15@gmail.com', '9876543224', 38),
('Paula Adams', 'paula16@gmail.com', '9876543225', 21),
('Quincy Jones', 'quincy17@gmail.com', '9876543226', 34),
('Rachel Moore', 'rachel18@gmail.com', '9876543227', 23),
('Steve Rogers', 'steve19@gmail.com', '9876543228', 37),
('Zara Lane', 'zara20@gmail.com', '9876543230', 28); 
SELECT * FROM Customers;

-- inserting sample data into products table
INSERT INTO Products (name, description, price, category) VALUES
('Laptop', 'Intel i5, 8GB RAM, 512GB SSD', 55000.00, 'Electronics'),
('Smartphone', '6.5 inch display, 128GB storage', 25000.00, 'Electronics'),
('Headphones', 'Wireless, Noise Cancelling', 5000.00, 'Accessories'),
('Keyboard', 'Mechanical RGB keyboard', 3000.00, 'Accessories'),
('Mouse', 'Wireless optical mouse', 1500.00, 'Accessories'),
('Monitor', '24 inch Full HD', 10000.00, 'Electronics'),
('Backpack', 'Waterproof laptop backpack', 2500.00, 'Bags'),
('Chair', 'Ergonomic office chair', 7000.00, 'Furniture'),
('Table', 'Wooden study table', 6000.00, 'Furniture'),
('Shoes', 'Running shoes, size 9', 4000.00, 'Footwear'),
('Watch', 'Analog wrist watch', 3500.00, 'Accessories'),
('Camera', 'DSLR 24MP with lens', 45000.00, 'Electronics'),
('Printer', 'Laser printer with WiFi', 12000.00, 'Electronics'),
('Notebook', '200 pages ruled notebook', 200.00, 'Stationery'),
('Pen', 'Gel pen pack of 5', 150.00, 'Stationery'),
('T-Shirt', 'Cotton round neck T-shirt', 700.00, 'Clothing'),
('Jeans', 'Slim fit denim jeans', 1500.00, 'Clothing'),
('Jacket', 'Winter jacket, waterproof', 3500.00, 'Clothing'),
('Adapter', 'USB-C adapter', 200.00, 'Electronics'),
('Desk Lamp', 'LED desk lamp', 1200.00, 'Furniture'); 
SELECT * FROM Products;

-- inserting sample data into orders table
INSERT INTO Orders (customer_id, product_id, quantity, order_date, status, payment_method, total_amount) VALUES
(1, 1, 1, '2026-03-01', 'Pending', 'UPI', 55000.00),
(2, 2, 2, '2026-03-02', 'Success', 'Credit', 50000.00),
(3, 3, 1, '2026-03-03', 'Cancel', 'Debit', 5000.00),
(4, 4, 1, '2026-03-04', 'Success', 'UPI', 3000.00),
(5, 5, 3, '2026-03-05', 'Pending', 'COD', 4500.00),
(6, 6, 2, '2026-03-06', 'Success', 'Credit', 20000.00),
(7, 7, 1, '2026-03-07', 'Pending', 'UPI', 2500.00),
(8, 8, 1, '2026-03-08', 'Success', 'Debit', 7000.00),
(9, 9, 1, '2026-03-09', 'Cancel', 'UPI', 6000.00),
(10, 10, 2, '2026-03-10', 'Success', 'Credit', 8000.00),
(11, 11, 1, '2026-03-11', 'Pending', 'UPI', 3500.00),
(12, 12, 1, '2026-03-12', 'Success', 'COD', 45000.00),
(13, 13, 1, '2026-03-13', 'Success', 'Credit', 12000.00),
(14, 14, 5, '2026-03-14', 'Pending', 'UPI', 1000.00),
(15, 15, 5, '2026-03-15', 'Success', 'Debit', 750.00),
(16, 16, 3, '2026-03-16', 'Pending', 'UPI', 2100.00),
(17, 17, 2, '2026-03-17', 'Success', 'Credit', 3000.00),
(18, 18, 1, '2026-03-18', 'Cancel', 'UPI', 3500.00),
(19, 19, 2, '2026-03-19', 'Success', 'COD', 2400.00),
(20, 20, 1, '2026-03-20', 'Pending', 'Debit', 1200.00);
SELECT * FROM Orders;




































