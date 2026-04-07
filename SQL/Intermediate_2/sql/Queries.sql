SHOW DATABASES;

-- creating a new database for this assignment, will store tables like customers, products and orders
CREATE DATABASE e_commerce;
SHOW DATABASES;

USE e_commerce;

-- creating customers table to store basic customer details
CREATE TABLE Customers(
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(50),
    mobile VARCHAR(15)
);

-- creating products table to store product information
CREATE TABLE Products(
    id INT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50)
);

-- checking all tables
SHOW TABLES;

-- making name and email not null so empty values are not allowed
ALTER TABLE Customers
MODIFY name VARCHAR(50) NOT NULL;

ALTER TABLE Customers
MODIFY email VARCHAR(50) NOT NULL;

DESCRIBE Customers;

-- adding unique constraint on email to avoid duplicate emails
ALTER TABLE Customers
ADD CONSTRAINT unique_email UNIQUE(email);

DESCRIBE Customers;

-- adding age column for customers
ALTER TABLE Customers
ADD age INT;

DESCRIBE Customers;

-- renaming id to product_id to make it clear
ALTER TABLE Products
CHANGE id product_id INT;

DESCRIBE Products;

-- making product_id primary key and auto increment
ALTER TABLE Products
MODIFY product_id INT AUTO_INCREMENT PRIMARY KEY;

DESCRIBE Products;

-- changing description column to text so we can write longer description
ALTER TABLE Products
MODIFY description TEXT;

DESCRIBE Products;


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

-- Query 1: To find how many products are there in each category
SELECT 
    IFNULL(category, 'Unknown') AS category, 
    COUNT(*) AS product_count
FROM 
    Products
GROUP BY 
    category;


-- Query 2: To get all Electronics products that cost between 50 and 500 and have 'a' in their name
SELECT 
    *
FROM 
    Products
WHERE 
    category = 'Electronics'
    AND price BETWEEN 50 AND 500
    AND name LIKE '%a%';

-- Query 3: To Show top 5 expensive Electronics products, skip the first 2
-- To Show Electronics products sorted by price from highest to lowest
SELECT 
	*
FROM 
	Products
WHERE 
	category = 'Electronics'
ORDER BY 
	price DESC;

-- To Show top 5 expensive Electronics products, skip the first 2
SELECT 
	*
FROM 
	Products
WHERE 
	category = 'Electronics'
ORDER BY 
	price DESC
LIMIT 5 OFFSET 2;

-- Query 4: To show Customers who have not placed any orders

-- Inserting a new customer who has not placed any orders
INSERT INTO Customers(name, email, mobile, age)
VALUES ('Test Student', 'teststudent@email.com', '9999999999', 22);

-- Using LEFT JOIN to efficiently get customers without any matching orders
SELECT c.*
FROM Customers c
LEFT JOIN Orders o ON c.customer_id = o.customer_id
WHERE o.customer_id IS NULL;

-- Query 5: To show Average total amount spent by each customer
SELECT 
    c.customer_id,
    c.name,
    IFNULL(ROUND(AVG(o.total_amount),2), 0) AS average_amount_spent
FROM Customers c
LEFT JOIN Orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.name;

-- Query 6: To show Products with price less than the average price
SELECT *
FROM Products
WHERE price < (SELECT AVG(price) FROM Products);


-- Query 7: Total quantity of products ordered by each customer
SELECT 
    c.customer_id,
    c.name,
    IFNULL(SUM(o.quantity), 0) AS total_quantity_ordered
FROM Customers c
LEFT JOIN Orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.name;


-- Query 8: To List all orders with customer and product names
SELECT 
    o.order_id,
    c.name AS customer_name,
    p.name AS product_name,
    o.quantity,
    o.total_amount,
    o.status,
    o.order_date
FROM Orders o
JOIN Customers c ON o.customer_id = c.customer_id
JOIN Products p ON o.product_id = p.product_id;

-- Query 9 : To Find products that have never been ordered.
-- Inserting a product that has never been ordered
INSERT INTO Products(name, description, price, category)
VALUES ('Test Product', 'Sample product for assignment', 99.99, 'Test Category');
SELECT p.product_id, p.name, p.category, p.price
FROM Products p
LEFT JOIN Orders o ON p.product_id = o.product_id
WHERE o.product_id IS NULL;


-- Intermediate Assignment

-- Query: To Find products priced higher than the average price in their category
SELECT *
FROM Products P1
WHERE P1.price > (
	SELECT AVG(p2.price)
	FROM Products p2
	WHERE P1.category = p2.category
)
ORDER BY
category ASC,
price DESC;

-- Inserting sample data into Products table of Furniture category
INSERT INTO Products (name, description, price, category) VALUES
('Sofa', 'Comfortable 3-seater sofa', 30000, 'Furniture'),
('Dining Table', 'Wooden dining table', 20000, 'Furniture'),
('Chair', 'Plastic chair', 1500, 'Furniture'),
('Bed', 'King size bed', 40000, 'Furniture'),
('Desk', 'Office work desk', 10000, 'Furniture');


-- Query: To Get products that are more expensive than any 'Furniture' category product
SELECT *
FROM Products
WHERE price > (
	SELECT MAX(price)
	FROM Products
	WHERE category = 'Furniture'
);

-- alternative approach with ALL keyword
SELECT *
FROM Products
WHERE price > ALL(
	SELECT (price)
	FROM Products
	WHERE category = 'Furniture'
);


-- Procedure: UpdateProductDiscount
DELIMITER $$

CREATE PROCEDURE UpdateProductDiscount(
    IN inputcategory VARCHAR(50),
    IN inputdiscount INT 
)
BEGIN
    UPDATE Products
    SET price = price - price * (inputdiscount / 100.0)
    WHERE category = inputcategory;
	-- This shows how many rows were updated
    select ROW_COUNT() AS UpdatedRows;
END $$

DELIMITER ;

-- Before applying discount
SELECT * FROM Products WHERE category = 'Furniture';
-- Calling procedure (apply 10% discount)
CALL UpdateProductDiscount('Furniture', 10);
-- After applying discount
SELECT * FROM Products WHERE category = 'Furniture';

-- TRIGGER : to update a customer's total spending

-- I added this column because we need a place to store the total spending of each customer
ALTER TABLE Customers
ADD total_spending DECIMAL(10,2) DEFAULT 0;

-- I am calculating the sum of all past orders for each customer and storing it in total_spending
UPDATE Customers C
SET total_spending = (
    SELECT COALESCE(SUM(O.total_amount), 0) 
    FROM Orders O 
    WHERE C.customer_id = O.customer_id
);

-- Create trigger to automatically update total_spending for future orders

DELIMITER $$
CREATE TRIGGER afterspent
AFTER INSERT ON Orders
FOR EACH ROW
BEGIN
    UPDATE Customers C
    SET C.total_spending = C.total_spending + NEW.total_amount
    WHERE C.customer_id = NEW.customer_id;
END $$
DELIMITER ;

-- Before inserting new order
SELECT customer_id, total_spending FROM Customers WHERE customer_id = 1;

INSERT INTO Orders (customer_id, product_id, quantity, order_date, status, payment_method, total_amount) 
VALUES (1, 1, 1, '2026-03-01', 'Pending', 'UPI', 55000.00);

-- After inserting new order
SELECT customer_id, total_spending FROM Customers WHERE customer_id = 1;


-- Query : To Retrieve all customers along with their latest order details, showing "No orders placed" if no orders exist. Using COALESCE
SELECT 
    c.customer_id,
    c.name,
    o.order_id,
    o.product_id,
    COALESCE(o.order_date, 'No orders placed') AS latest_order_date
FROM Customers c
LEFT JOIN Orders o 
    ON o.order_id = (
        -- pick the order_id with the latest date for this customer
        SELECT order_id
        FROM Orders
        WHERE customer_id = c.customer_id
        ORDER BY order_date DESC, order_id ASC
        LIMIT 1
    );

-- Query : To Find customers whose email and mobile number are the same. 

INSERT INTO Customers (customer_id, name, email, mobile, age) VALUES
(23, 'Frank Miller', 'frank@gmail.com', 'frank@gmail.com', 29),
(24, 'Grace Hopper', 'grace@gmail.com', 'grace@gmail.com', 32),
(25, 'Hank Pym', 'hank@gmail.com', 'hank@gmail.com', 40),
(26, 'Ivy Chen', 'ivy@gmail.com', 'ivy@gmail.com', 27),
(27, 'Jack Black', 'jack@gmail.com', 'jack@gmail.com', 35);

SELECT 
    customer_id,
    name,
    NULLIF(email, mobile) AS email
FROM Customers;

-- Query : To Categorize orders based on total amount spent:
-- Low Value: Less than $100
-- Medium Value: $100 - $500
-- High Value: More than $500

INSERT INTO Orders (order_id, customer_id, product_id, quantity, order_date, status, payment_method, total_amount) VALUES
(24, 24, 24, 3, '2026-03-24', 'Success', 'Debit', 450.00),  
(25, 25, 25, 1, '2026-03-25', 'Pending', 'UPI', 80.00),  
(26, 26, 23, 1, '2026-03-23', 'Success', 'COD', 600.00); 

-- Query: Categorize orders based on total amount spent
-- Low Value: total_amount < 100
-- Medium Value: total_amount between 100 and 500
-- High Value: total_amount > 500
SELECT 
    order_id,
    CASE 
        WHEN total_amount < 100 THEN 'Low Value'        
        WHEN total_amount BETWEEN 100 AND 500 THEN 'Medium Value'  
        ELSE 'High Value'                                
    END AS order_category
FROM Orders;


-- Inserting sample Orders data across different months
INSERT INTO Orders (order_id, customer_id, product_id, quantity, order_date, status, payment_method, total_amount) VALUES
(301, 1, 1, 1, '2026-01-05', 'Success', 'UPI', 15000.00),
(302, 2, 2, 2, '2026-01-15', 'Success', 'Credit', 22000.00),
(303, 3, 3, 1, '2026-02-03', 'Pending', 'COD', 12000.00),
(304, 4, 4, 3, '2026-02-20', 'Success', 'UPI', 45000.00),
(305, 5, 5, 1, '2026-03-10', 'Pending', 'Credit', 9000.00),
(306, 1, 2, 2, '2026-03-25', 'Success', 'COD', 55000.00),
(307, 2, 3, 1, '2026-04-05', 'Success', 'UPI', 20000.00),
(308, 3, 4, 2, '2026-04-15', 'Pending', 'Debit', 35000.00),
(309, 4, 5, 1, '2026-05-02', 'Success', 'Credit', 12000.00),
(310, 5, 1, 3, '2026-05-18', 'Pending', 'UPI', 70000.00);
-- Query: Count number of orders placed per month 
SELECT 
    YEAR(order_date) AS order_year,
    MONTH(order_date) AS order_month,
    MONTHNAME(order_date) AS order_month_name,
    COUNT(*) AS total_orders
FROM Orders
GROUP BY YEAR(order_date), MONTH(order_date), MONTHNAME(order_date)
ORDER BY order_year, order_month;

-- Adding a discount column to Products
ALTER TABLE Products
ADD discount DECIMAL(5,2) DEFAULT 0;

-- Updating discount for products category-wise
-- Electronics: 10% discount
UPDATE Products
SET discount = 10
WHERE category = 'Electronics';

-- Accessories: 5% discount
UPDATE Products
SET discount = 5
WHERE category = 'Accessories';

-- Furniture: 15% discount
UPDATE Products
SET discount = 15
WHERE category = 'Furniture';

-- Query: Replace all 0 discount values with NULL in Products table
-- Before placing NULL values
select * from Products;

UPDATE Products
SET discount = NULLIF(discount,0);

-- After replacing 0 with null values
select * from Products;

select * from orders;
-- Inserting test data 
INSERT INTO Orders (order_id, customer_id, product_id, quantity, order_date, status, payment_method, total_amount)
VALUES
(105, 5, 5, 0, '2026-04-05', 'Pending', 'COD', 3000.00);   

-- Query : To Divide total_amount by quantity but avoid division by zero using NULLIF.
SELECT 
    order_id, 
    customer_id,
    product_id,
    total_amount,
    quantity,
    total_amount / NULLIF(quantity, 0) AS price_per_unit
FROM Orders;

-- Adding 'score' column to Products table
-- This column will store the product rating

ALTER TABLE Products
ADD score INT DEFAULT 0;

-- Updating some products with random ratings (1-5)

UPDATE Products
SET score = 5
WHERE product_id IN (1, 2, 12); 

UPDATE Products
SET score = 4
WHERE product_id IN (3, 4, 11); 

-- Query: To Count non-zero values in a column score.
SELECT COUNT(NULLIF(score,0)) AS non_zero_scores
FROM Products;

-- Table: Employees :- This table stores employee details including salary
CREATE TABLE Employees (
    employee_id INT PRIMARY KEY,    
    name VARCHAR(50) NOT NULL,       
    department VARCHAR(50),          
    salary INT DEFAULT 0            
);

-- Including some zero salaries to test average calculation excluding 0
INSERT INTO Employees (employee_id, name, department, salary) VALUES
(1, 'Rohit Sharma', 'IT', 50000),
(2, 'Priya Singh', 'HR', 0),
(3, 'Anil Kumar', 'Finance', 120000),
(4, 'Sneha Reddy', 'IT', 0),
(5, 'Vikram Joshi', 'Marketing', 75000),
(6, 'Neha Patel', 'Finance', 90000),
(7, 'Siddharth Mehta', 'HR', 0),
(8, 'Isha Kapoor', 'Marketing', 60000);

-- Query : To Calculate average salary excluding rows where salary = 0.
SELECT 
    SUM(salary) / COUNT(NULLIF(salary,0)) AS average_salary_excluding_zero
FROM Employees;

-- Alternative approach
SELECT AVG(NULLIF(salary,0)) as average_salary_excluding_zero FROM Employees;

-- Query : To Find percentage contribution of each row (value / total_value) safely handling zero totals.
SELECT 
    employee_id, 
    name, 
    salary,
    salary / NULLIF((SELECT SUM(salary) from Employees),0) * 100 AS percentage_contribution
FROM Employees;

ALTER TABLE Customers
MODIFY COLUMN email VARCHAR(255) NULL;

-- Inserting customer data for testing empty emails 
INSERT INTO Customers (customer_id, name, email, mobile, age, total_spending) VALUES
(28, 'Priya Singh', '', '9123456780', 25, 8000.00);       


-- Before updating the email
select * from Customers;
 -- Query : To Convert empty strings ('') in a column email to NULL.
UPDATE Customers
SET email = NULLIF(email,"");

select * from Customers;


-- Table: Events: event_id, event_name, start_date, end_date
CREATE TABLE Events (
    event_id INT PRIMARY KEY,
    event_name VARCHAR(100),
    start_date DATE,
    end_date DATE
);

-- Inserting records  
INSERT INTO Events (event_id, event_name, start_date, end_date) VALUES
(1, 'Meeting', '2026-04-01', '2026-04-01'), 
(2, 'Workshop', '2026-04-02', '2026-04-05'),
(3, 'Conference', '2026-04-03', '2026-04-03'), 
(4, 'Team Outing', '2026-04-04', '2026-04-06');

-- Query: Find rows where start_date and end_date are equal
SELECT *
FROM Events
WHERE NULLIF(start_date, end_date) IS NULL;    


-- Table: Scores : student_id, subject, score
CREATE TABLE Scores (
    student_id INT PRIMARY KEY,
    subject VARCHAR(50),
    score INT
);
-- Inserting sample scores, including -1 for missing data
INSERT INTO Scores (student_id, subject, score) VALUES
(1, 'Math', 85),
(2, 'Science', 90),
(3, 'Math', -1),      
(4, 'Science', 75),
(5, 'Math', 95),
(6, 'Science', -1);  

-- Query: To show Use of NULLIF inside an aggregation to ignore specific values (e.g., exclude -1 from avg).
SELECT 
    subject,
    AVG(NULLIF(score, -1)) AS average_score
FROM Scores
GROUP BY subject;

ALTER TABLE Events
ADD status VARCHAR(50) DEFAULT 'Pending';

-- Before replacing
SELECT * from Events;


-- Query : To Replace a value only when two columns match (hint: combine NULLIF + CASE).
UPDATE Events
SET status = CASE
                WHEN NULLIF(start_date, end_date) IS NULL THEN 'Same Day Event'
                ELSE status
             END;
             
-- After replacing
SELECT * from Events;

-- Intermediate Assignment 2 Solution

-- Query: Find the top 3 customers who have spent the most on orders using a CTE

WITH totalcustomerspending AS (
    SELECT 
        C.customer_id,
        C.name,
        COALESCE(SUM(O.total_amount), 0) AS total_spent
    FROM Customers C
    LEFT JOIN Orders O 
        ON C.customer_id = O.customer_id
    GROUP BY 
        C.customer_id, 
        C.name
)

SELECT 
    customer_id,
    name,
    total_spent
FROM totalcustomerspending
ORDER BY total_spent DESC
LIMIT 3;

-- Alternative Query: Using Subquery

SELECT 
    customer_id,
    name,
    total_spent
FROM (
    SELECT 
        C.customer_id,
        C.name,
        COALESCE(SUM(O.total_amount), 0) AS total_spent
    FROM Customers C
    LEFT JOIN Orders O 
        ON C.customer_id = O.customer_id
    GROUP BY 
        C.customer_id, 
        C.name
) AS customer_spending
ORDER BY total_spent DESC
LIMIT 3;

-- Query:To Retrieve all orders where total amount exceeds 500 after converting to integer
DESCRIBE Orders;
SELECT 
    order_id,
    CAST(total_amount AS SIGNED) AS total_amount_as_int
FROM Orders
WHERE CAST(total_amount AS SIGNED) > 500;


-- Query : To Write a CRON job to generate daily sales reports at 11:55 PM
SELECT * FROM Orders WHERE DATE(order_date) = CURDATE();

-- Query : To Write a transaction to insert a new customer and an order. If the order fails, roll back the customer creation.
-- Rollback Transaction
SET autocommit = 0;

START TRANSACTION;

INSERT INTO Customers (customer_id, name, email, mobile, age, total_spending)
VALUES (202, 'Test User', 'test@example.com', '9999999999', 30, 0);

-- This will FAIL (invalid product_id)
INSERT INTO Orders (order_id, customer_id, product_id, quantity, order_date, status, payment_method, total_amount)
VALUES (3002, 202, 9999, 2, CURDATE(), 'Success', 'UPI', 500);

ROLLBACK;

SELECT * FROM Customers WHERE customer_id = '202';




-- Commit Transaction

SET autocommit = 0;

START TRANSACTION;

-- Step 1: Insert customer
INSERT INTO Customers (customer_id, name, email, mobile, age, total_spending)
VALUES (202, 'Test User', 'test@example.com', '9999999999', 30, 0);

-- Step 2: Insert order 
INSERT INTO Orders (order_id, customer_id, product_id, quantity, order_date, status, payment_method, total_amount)
VALUES (3002, 202, 1, 2, CURDATE(), 'Success', 'UPI', 500);

-- If both queries succeed
COMMIT;

SELECT * FROM Customers WHERE customer_id = '202';





-- Query : To Rank customers based on their total spending, showing ranks even if values are tied.

-- Inserting another customer record to show tie situation
INSERT INTO Customers (customer_id, name, email, mobile, age, total_spending)
VALUES (999, 'Amit Verma', 'amit.verma@example.com', '9876543211', 29, 0);

-- Inserting order with same total amount (to create tie)
INSERT INTO Orders (order_id, customer_id, product_id, quantity, order_date, status, payment_method, total_amount)
VALUES (9001, 999, 1, 1, CURDATE(), 'Success', 'UPI', 290000);

WITH customer_spending AS (
    SELECT 
        customer_id,
        SUM(total_amount) AS total_spent
    FROM Orders
    GROUP BY customer_id
)

SELECT 
    customer_id,
    total_spent,
    DENSE_RANK() OVER (ORDER BY total_spent DESC) AS customer_rank
FROM customer_spending;



