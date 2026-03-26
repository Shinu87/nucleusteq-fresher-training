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