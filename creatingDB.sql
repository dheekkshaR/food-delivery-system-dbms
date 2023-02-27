DROP DATABASE IF EXISTS dbmsproj;
CREATE DATABASE IF NOT EXISTS dbmsproj;
USE dbmsproj;

-- table for LOCATION 
CREATE TABLE location( loc_id INT AUTO_INCREMENT PRIMARY KEY,
	landmark varchar(50),
    latitude decimal NOT NULL DEFAULT 0 ,
    longitude decimal NOT NULL DEFAULT 0 ,
    weather varchar(50),
	street varchar(50) NOT NULL DEFAULT " ",
    city VARCHAR(20) NOT NULL DEFAULT " ",
    state VARCHAR(20) NOT NULL DEFAULT " ",
    country VARCHAR(20) NOT NULL DEFAULT " ",
    surge INT NOT NULL DEFAULT 0,
    rain INT NOT NULL DEFAULT 0, 
    temp INT NOT NULL DEFAULT 0 );
    
    
-- table for customer
DROP TABLE IF EXISTS customer;

CREATE TABLE customer( customer_id INT AUTO_INCREMENT PRIMARY KEY,
	cust_name varchar(50) NOT NULL DEFAULT "DHEEKKSHA",
    email varchar(50) NOT NULL DEFAULT "abc@gmail.com",
    passwordset varchar(50) NOT NULL,
    phone varchar(10),
    joined_date DATE);
    
-- rel between cust and location
CREATE TABLE cust_location(
customer_id INT,
loc_id INT,
PRIMARY KEY (customer_id, loc_id),
FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (loc_id) REFERENCES location(loc_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- table for Restraunt
CREATE TABLE restaurant( restaurant_id INT AUTO_INCREMENT PRIMARY KEY,
	restaurant_name varchar(50) NOT NULL DEFAULT "Kenmore",
    rating INT NOT NULL DEFAULT 5,
    phone varchar(10),
    loc_id INT,
    FOREIGN KEY (loc_id) REFERENCES location(loc_id) ON UPDATE CASCADE ON DELETE SET NULL
    );

-- table for menu
CREATE TABLE menu( menu_id INT AUTO_INCREMENT PRIMARY KEY,
type varchar(50),
restaurant_id INT,
FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id) ON UPDATE CASCADE ON DELETE SET NULL
);

-- table for items
CREATE TABLE item( item_id INT AUTO_INCREMENT PRIMARY KEY,
price INT NOT NULL DEFAULT 0,
item_name VARCHAR(50) NOT NULL DEFAULT "item",
restaurant_id INT,
menu_id INT,
FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id) ON UPDATE CASCADE ON DELETE SET NULL,
FOREIGN KEY (menu_id) REFERENCES menu(menu_id) ON UPDATE CASCADE ON DELETE SET NULL);

-- table for payment
CREATE TABLE payment( payment_id INT AUTO_INCREMENT PRIMARY KEY,
method varchar(50),
paid INT );


-- table for delivery driver
CREATE TABLE driver( driver_id INT AUTO_INCREMENT PRIMARY KEY,
driver_password varchar(50) NOT NULL DEFAULT " ",
driver_name varchar(50) NOT NULL DEFAULT " ",
govt_id_no varchar(50) NOT NULL DEFAULT " ",
vehicle_no varchar(50) NOT NULL DEFAULT " ",
phone_no varchar(10) NOT NULL DEFAULT " ");


-- table for Delivery
CREATE TABLE delivery( delivery_id INT AUTO_INCREMENT PRIMARY KEY,
timetaken TIME,
starttime TIMESTAMP,
endtime TIMESTAMP,
deliveryrating INT,
deliverystatus varchar(50) DEFAULT "NOT ASSIGNED"
);

-- rel between delivery driver and delivery 
CREATE TABLE delivery_driver (
delivery_id INT,
driver_id INT,
PRIMARY KEY (delivery_id, driver_id),
FOREIGN KEY (driver_id) REFERENCES driver(driver_id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (delivery_id) REFERENCES delivery(delivery_id) ON UPDATE CASCADE ON DELETE CASCADE );


-- table for ORDERS
CREATE TABLE eachorder( order_id INT AUTO_INCREMENT PRIMARY KEY,
order_status varchar(50) DEFAULT "INCOMPLETE",
restaurant_id INT,
customer_id INT,
delivery_id INT,
payment_id INT,
FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id) ON UPDATE CASCADE ON DELETE SET NULL,
FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (delivery_id) REFERENCES delivery(delivery_id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (payment_id) REFERENCES payment(payment_id) ON UPDATE CASCADE ON DELETE CASCADE
);


-- rel between order and items
CREATE TABLE order_items(
order_id INT,
item_id INT,
item_count INT DEFAULT 1,
PRIMARY KEY (order_id, item_id),
FOREIGN KEY (order_id) REFERENCES eachorder(order_id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (item_id) REFERENCES item(item_id) ON UPDATE CASCADE ON DELETE CASCADE
);


-- table for discounts
CREATE TABLE discount( discount_percent_coupon INT PRIMARY KEY,
discount_percent INT NOT NULL DEFAULT 0
);

-- table for discount payment mapping
-- seperate table even with 1 to many mapping as some discountscan eixst without payments corr existing 
CREATE TABLE discount_payment( discount_percent INT ,
payment_id INT,
discount_percent_coupon INT ,
FOREIGN KEY (discount_percent_coupon) REFERENCES discount(discount_percent_coupon) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (payment_id) REFERENCES payment(payment_id) ON UPDATE CASCADE ON DELETE CASCADE);

-- table for rate
CREATE TABLE rate( lowerdist INT,
upperdist INT,
rateGiven INT );

-- table for weather
-- DROP TABLE IF EXISTS weather;
CREATE TABLE weather( lower INT,
upper INT,
PRIMARY KEY(lower, upper), 
increase_percent INT );

-- admin
CREATE TABLE administrator( admin_id INT,
passwordSet varchar(50) );