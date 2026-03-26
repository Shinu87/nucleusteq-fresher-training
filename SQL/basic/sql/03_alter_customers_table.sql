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


