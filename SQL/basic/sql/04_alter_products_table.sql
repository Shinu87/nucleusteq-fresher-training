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







































