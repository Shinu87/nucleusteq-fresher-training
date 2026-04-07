CREATE DATABASE retail_db;

USE retail_db;

-- COLOR TABLE
CREATE TABLE color (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    extra_fee DECIMAL(5,2)
);

-- CUSTOMER TABLE
CREATE TABLE customer (
    id INT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    favorite_color_id INT,
    city VARCHAR(50),
    FOREIGN KEY (favorite_color_id) REFERENCES color(id)
);

-- CATEGORY TABLE
CREATE TABLE category (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    parent_id INT,
    FOREIGN KEY (parent_id) REFERENCES category(id)
);

-- ITEM TABLE
CREATE TABLE item (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    size VARCHAR(10),
    price DECIMAL(10,2),
    margin DECIMAL(10,2),
    color_id INT,
    category_id INT,
    FOREIGN KEY (color_id) REFERENCES color(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- STORE TABLE
CREATE TABLE store (
    id INT PRIMARY KEY,
    city VARCHAR(50)
);

-- ORDERS TABLE
CREATE TABLE orders (
    id INT PRIMARY KEY,
    customer_id INT,
    item_id INT,
    item_cnt INT,
    store_id INT,
    order_channel VARCHAR(20),
    day_dt DATE,
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (item_id) REFERENCES item(id),
    FOREIGN KEY (store_id) REFERENCES store(id)
);

INSERT INTO color VALUES
(1,'Red',10),
(2,'Blue',5),
(3,'Green',0),
(4,'Black',15),
(5,'White',0),
(6,'Yellow',5),
(7,'Pink',10),
(8,'Orange',5),
(9,'Purple',10),
(10,'Grey',0),
(11,'Brown',5),
(12,'Maroon',10),
(13,'Cyan',5),
(14,'Magenta',10),
(15,'Beige',0),
(16,'Navy',5),
(17,'Olive',5),
(18,'Teal',5),
(19,'Lavender',10),
(20,'Gold',20);

INSERT INTO category VALUES
(1,'mens',NULL),
(2,'womens',NULL),
(3,'kids',NULL),
(4,'mens_jeans',1),
(5,'mens_tshirt',1),
(6,'mens_shirts',1),
(7,'womens_dress',2),
(8,'womens_tops',2),
(9,'kids_wear',3),
(10,'mens_jackets',1),
(11,'womens_jeans',2),
(12,'kids_tshirts',3),
(13,'mens_shorts',1),
(14,'womens_skirts',2),
(15,'mens_ethnic',1),
(16,'womens_ethnic',2),
(17,'kids_ethnic',3),
(18,'mens_activewear',1),
(19,'womens_activewear',2),
(20,'kids_activewear',3);

INSERT INTO store VALUES
(1,'Mumbai'),
(2,'Delhi'),
(3,'Hyderabad'),
(4,'Chennai'),
(5,'Bangalore'),
(6,'Pune'),
(7,'Kolkata'),
(8,'Ahmedabad'),
(9,'Jaipur'),
(10,'Lucknow'),
(11,'Indore'),
(12,'Chandigarh'),
(13,'Kochi'),
(14,'Amritsar'),
(15,'Nagpur'),
(16,'Surat'),
(17,'Bhopal'),
(18,'Patna'),
(19,'Ranchi'),
(20,'Goa');

INSERT INTO customer VALUES
(1,'Amit','Sharma',1,'Mumbai'),
(2,'Priya','Verma',2,'Delhi'),
(3,'Rahul','Reddy',3,'Hyderabad'),
(4,'Sneha','Iyer',4,'Chennai'),
(5,'Vikas','Singh',5,'Lucknow'),
(6,'Neha','Gupta',6,'Mumbai'),
(7,'Arjun','Mehta',7,'Pune'),
(8,'Kavya','Nair',8,'Kochi'),
(9,'Rohit','Patel',9,'Ahmedabad'),
(10,'Pooja','Das',10,'Kolkata'),
(11,'Manish','Yadav',11,'Jaipur'),
(12,'Anjali','Joshi',12,'Mumbai'),
(13,'Suresh','Kumar',13,'Bangalore'),
(14,'Divya','Chopra',14,'Delhi'),
(15,'Kiran','Rao',15,'Hyderabad'),
(16,'Meena','Pillai',16,'Chennai'),
(17,'Deepak','Agarwal',17,'Indore'),
(18,'Nisha','Malhotra',18,'Mumbai'),
(19,'Ajay','Thakur',19,'Chandigarh'),
(20,'Simran','Kaur',20,'Amritsar');

INSERT INTO item VALUES
(1,'Slim Fit Jeans','M',2000,300,1,4),
(2,'Regular Jeans','L',1800,250,2,4),
(3,'Denim Jeans','XL',2200,350,3,4),
(4,'Black Jeans','M',2100,300,4,4),
(5,'White Jeans','S',1900,200,5,4),
(6,'Blue T-Shirt','M',800,100,2,5),
(7,'Green Shirt','L',1200,150,3,6),
(8,'Formal Shirt','XL',1500,200,4,6),
(9,'Casual Shirt','M',1300,150,5,6),
(10,'Jacket','L',2500,400,6,10),
(11,'Shorts','M',900,120,7,13),
(12,'Ethnic Kurta','L',1800,250,8,15),
(13,'Track Pants','XL',1600,200,9,18),
(14,'Gym Wear','M',1400,180,10,18),
(15,'Sports T-Shirt','S',1000,120,11,18),
(16,'Designer Jeans','L',3000,500,12,4),
(17,'Ripped Jeans','M',2700,450,13,4),
(18,'Classic Jeans','XL',2300,350,14,4),
(19,'Vintage Jeans','M',2100,300,15,4),
(20,'Modern Jeans','L',2400,400,16,4);

INSERT INTO orders VALUES
(1,1,1,2,1,'Store','2024-01-10'),
(2,2,2,1,2,'Online','2024-01-11'),
(3,3,3,3,3,'Store','2024-01-12'),
(4,4,4,1,4,'Store','2024-01-13'),
(5,5,5,2,5,'Online','2024-01-14'),
(6,6,6,1,1,'Store','2024-01-15'),
(7,7,7,2,6,'Store','2024-01-16'),
(8,8,8,1,13,'Online','2024-01-17'),
(9,9,9,3,8,'Store','2024-01-18'),
(10,10,10,1,7,'Store','2024-01-19'),
(11,11,11,2,9,'Online','2024-01-20'),
(12,12,12,1,1,'Store','2024-01-21'),
(13,13,13,2,5,'Store','2024-01-22'),
(14,14,14,1,2,'Online','2024-01-23'),
(15,15,15,3,3,'Store','2024-01-24'),
(16,16,16,1,4,'Store','2024-01-25'),
(17,17,17,2,11,'Online','2024-01-26'),
(18,18,18,1,1,'Store','2024-01-27'),
(19,19,19,2,12,'Store','2024-01-28'),
(20,20,20,1,14,'Online','2024-01-29');

-- Exercise 1
SELECT i.name, co.name, cu.first_name, cu.last_name
FROM customer cu
JOIN orders o ON cu.id = o.customer_id
JOIN store s ON o.store_id = s.id AND s.city = 'Mumbai'
JOIN item i ON o.item_id = i.id AND cu.favorite_color_id = i.color_id
JOIN category ca ON i.category_id = ca.id AND ca.name = 'mens_jeans'
JOIN color co ON i.color_id = co.id
WHERE o.order_channel = 'Store'
ORDER BY co.name;

-- Test customer for Exercise 2
INSERT INTO customer (id, first_name, last_name, favorite_color_id, city)
VALUES (999, 'Test', 'NoOrder', 3, 'Mumbai');

-- Exercise 2
SELECT cu.first_name, cu.last_name, co.name
FROM customer cu
LEFT JOIN orders o ON cu.id = o.customer_id
JOIN color co ON cu.favorite_color_id = co.id
GROUP BY cu.id, cu.first_name, cu.last_name, co.name
HAVING COUNT(o.id) = 0;

-- Exercise 3
SELECT main_cat.name, sub_cat.name
FROM category main_cat
LEFT JOIN category sub_cat ON sub_cat.parent_id = main_cat.id
WHERE main_cat.parent_id IS NULL;

