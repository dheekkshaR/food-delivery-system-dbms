USE dbmsproj;

DROP PROCEDURE IF EXISTS CREATE_NEW_CUSTOMER;

DELIMITER //
CREATE PROCEDURE CREATE_NEW_CUSTOMER(
customer_name_p VARCHAR(50),
email_p VARCHAR(50),
paswd_p VARCHAR(50),
street_p VARCHAR(50),
state_p VARCHAR(50),
city_p VARCHAR(50),
country_p VARCHAR(50)
)
BEGIN
DECLARE cust_id_new INT;
DECLARE loc_id_new INT;
INSERT INTO customer(cust_name, email, passwordset) values(customer_name_p, email_p,paswd_p);
SELECT customer_id FROM customer ORDER BY customer_id DESC LIMIT 1 INTO cust_id_new;
INSERT INTO location(street, state, city, country) values (street_p, state_p, city_p, country_p);
SELECT loc_id FROM location ORDER BY loc_id DESC LIMIT 1 INTO loc_id_new;
INSERT INTO cust_location(customer_id, loc_id) values (cust_id_new, loc_id_new);


SELECT loc_id, cust_id_new from location where loc_id=loc_id_new LIMIT 1; 


END //
DELIMITER ;    
    

DROP TRIGGER IF EXISTS updateStatus;
DELIMITER // 
CREATE TRIGGER updateStatus
  AFTER UPDATE ON delivery 
  FOR EACH ROW 
BEGIN 
DECLARE a varchar(50) DEFAULT " ";
DECLARE b int DEFAULT 0;
SELECT NEW.deliverystatus INTO a FROM delivery;  
SELECT eachorder.order_id INTO b FROM eachorder JOIN delivery WHERE NEW.delivery_id= eachorder.delivery_id;
UPDATE eachorder SET order_status= a WHERE order_id=b;
END//



    

CALL CREATE_NEW_CUSTOMER("Dick","gggg.com","bcdf","343 Blvd","NY","Brooklyn","USA");
CALL CREATE_NEW_CUSTOMER("Eric","qqqq@fjfj.com","ghik","341 Blvd","CA","Irvine","USA");
CALL CREATE_NEW_CUSTOMER("Adit","abc@fjfj.com","lokp","345 Blvd","MA","Watertown","USA");

DROP procedure IF EXISTS CREATE_NEW_RESTAURANT;


DELIMITER //
CREATE PROCEDURE CREATE_NEW_RESTAURANT(
restaurant_name_p VARCHAR(50),
lat_p DOUBLE,
long_p DOUBLE, 
street_p VARCHAR(50),
state_p VARCHAR(10),
city_p VARCHAR(20),
country_p VARCHAR(20)
)
BEGIN
DECLARE new_loc_id INT;
INSERT INTO location(latitude, longitude, street,city, state,country) values (lat_p, long_p, street_p,city_p,state_p,country_p);
SELECT loc_id from location ORDER BY loc_id DESC limit 1 INTO new_loc_id;
INSERT INTO restaurant(restaurant_name,loc_id) VALUES(restaurant_name_p, new_loc_id); 
END //
DELIMITER ; 

CALL CREATE_NEW_RESTAURANT("Deli",152.234,145.233,"East 23","MA", "Boston","USA");
CALL CREATE_NEW_RESTAURANT("Dinos",151.234,147.233,"East 28","MA", "Boston","USA");
CALL CREATE_NEW_RESTAURANT("Chocos",158.234,147.233,"East 45","MA", "Boston","USA");
CALL CREATE_NEW_RESTAURANT("Steve's",159.234,147.233,"East 27","MA", "Boston","USA");

DROP PROCEDURE IF EXISTS createMenu;

DELIMITER //
CREATE PROCEDURE createMenu(
restaurant_id_p INT,
menu_type_p VARCHAR(50)
)
BEGIN
INSERT INTO menu(type,restaurant_id) VALUES(menu_type_p, restaurant_id_p);
END //
DELIMITER ; 

CALL createMenu(3,"Veg - Japanese");
CALL createMenu(2,"Non Veg - Chinese");
CALL createMenu(3,"Veg - Chinese");



DROP PROCEDURE IF EXISTS addItemToMenu;

DELIMITER //
CREATE PROCEDURE addItemToMenu(
restaurant_id_p INT,
menu_id_p INT,
price_p INT,
item_name_p VARCHAR(50)
)
BEGIN
INSERT INTO item(price,item_name,restaurant_id,menu_id) VALUES(price_p, item_name_p , restaurant_id_p, menu_id_p);
END //
DELIMITER ; 

CALL addItemToMenu(2,2,50,"Chicken Teriyaki");
CALL addItemToMenu(2,2,70,"Chicken Manchurian");
CALL addItemToMenu(3,1,100,"Veg Ramen");
CALL addItemToMenu(3,1,60,"Veg Balls In Broth");

DROP PROCEDURE IF EXISTS createCartFromCustomer;

DELIMITER //
CREATE PROCEDURE createCartFromCustomer(
customer_id_p INT,
restaurant_id_p INT
)
BEGIN
DECLARE payment_id_new INT;
DECLARE delivery_id_new INT;
DECLARE order_id_new INT;
INSERT INTO payment(paid) VALUES(0);
SELECT payment_id from payment ORDER BY payment_id DESC LIMIT 1 INTO  payment_id_new;
INSERT INTO delivery(deliveryrating) VALUES (0); 
SELECT DELIVERY_ID from delivery ORDER BY DELIVERY_ID DESC LIMIT 1 INTO  delivery_id_new;
INSERT INTO eachorder(restaurant_id, customer_id, delivery_id, payment_id) VALUES (restaurant_id_p, customer_id_p, delivery_id_new, payment_id_new);
SELECT order_id FROM eachorder ORDER BY order_id DESC LIMIT 1;


END //
DELIMITER ;

CALL createCartFromCustomer(2,2);
CALL createCartFromCustomer(2,3);
CALL createCartFromCustomer(3,2);

DROP PROCEDURE IF EXISTS makeOrderItemMapping;

DELIMITER //
CREATE PROCEDURE makeOrderItemMapping(

order_id_p INT,
item_id_p INT

)
BEGIN
declare temp int default 0;
select item_count into temp from order_items where order_id=order_id_p and item_id=item_id_p;
IF temp > 0 THEN
	UPDATE order_items set item_count = temp+1 where order_id = order_id_p and item_id = item_id_p;
ELSE
	INSERT INTO order_items(order_id, item_id,item_count) VALUES (order_id_p, item_id_p,temp+1);
END IF;
END //
DELIMITER ;

CALL makeOrderItemMapping(2,2);
CALL makeOrderItemMapping(2,1);

CALL makeOrderItemMapping(3,3);
CALL makeOrderItemMapping(3,4);
CALL makeOrderItemMapping(3,4);

DROP PROCEDURE IF EXISTS deleteOrderItemMapping;
DELIMITER //
CREATE PROCEDURE deleteOrderItemMapping(

order_id_p INT,
item_id_p INT

)
BEGIN
declare temp int default 0;
select item_count into temp from order_items where order_id=order_id_p and item_id=item_id_p;
IF temp = 1 THEN
	DELETE FROM order_items where order_id = order_id_p AND item_id = item_id_p;
ELSE
	
	UPDATE order_items set item_count = temp-1 where order_id = order_id_p and item_id = item_id_p;

END IF;
END //
DELIMITER ;

CALL deleteOrderItemMapping(3,4); 



DROP PROCEDURE IF EXISTS createDeliveryDriver;

DELIMITER //
CREATE PROCEDURE createDeliveryDriver(
driver_name_p VARCHAR(50),
passwd_p VARCHAR(50),
govt_id_p INT,
vehicle_no_p INT,
phone_no_p INT
)
BEGIN
DECLARE new_driver_id INT;
INSERT INTO driver(driver_name,driver_password,govt_id_no,phone_no,vehicle_no) VALUES(driver_name_p, passwd_p,govt_id_p, phone_no_p,vehicle_no_p);
SELECT driver_id FROM driver ORDER BY driver_id DESC LIMIT 1 ;

END //
DELIMITER ; 


CALL createDeliveryDriver("Joe","Joe",123456,857468756,121233);
CALL createDeliveryDriver("Dave","dave",123457,85746333,121345);
CALL createDeliveryDriver("Singh","sing",123458,85746111,121287);
CALL createDeliveryDriver("Mary","Mar",123459,85799491,121213);

DROP PROCEDURE IF EXISTS deleteDeliveryDriver;

DELIMITER //
CREATE PROCEDURE deleteDeliveryDriver(
driver_id_p INT
)
BEGIN
DELETE FROM driver WHERE driver_id = driver_id_p;
END //
DELIMITER ; 

CALL deleteDeliveryDriver(4);


DROP PROCEDURE IF EXISTS addWeatherMultiplier;
DELIMITER //
CREATE PROCEDURE addWeatherMultiplier(

RANGE_LOW_P INT,
RANGE_HIGH_P INT, 
INCREASE_PERCENT_P INT
)
BEGIN
INSERT INTO weather(lower,upper,increase_percent) VALUES (RANGE_LOW_P, RANGE_HIGH_P, INCREASE_PERCENT_P);
END //
DELIMITER ;

CALL addWeatherMultiplier(0,10,50);
CALL addWeatherMultiplier(11,20,30);
CALL addWeatherMultiplier(21,30,1);

DROP PROCEDURE IF EXISTS updateWeatherMultiplier;
DELIMITER //
CREATE PROCEDURE updateWeatherMultiplier(
RANGE_LOW_P INT,
RANGE_HIGH_P INT, 
INCREASE_PERCENT_P INT
)
BEGIN
UPDATE weather SET increase_percent = INCREASE_PERCENT_P WHERE lower = RANGE_LOW_P AND upper = RANGE_HIGH_P;
END //
DELIMITER ;

CALL updateWeatherMultiplier(11,20,40);

DROP PROCEDURE IF EXISTS addDiscountCoupon;
DELIMITER //
CREATE PROCEDURE addDiscountCoupon(
coupon_code_p INT,
discount_percent_p INT
)
BEGIN
INSERT INTO discount(discount_percent_coupon, discount_percent) VALUES(coupon_code_p,discount_percent_p);
END //
DELIMITER ; 

CALL addDiscountCoupon(123456,30);
CALL addDiscountCoupon(234567,15);


DROP PROCEDURE IF EXISTS deleteRestaurant;

DELIMITER //
CREATE PROCEDURE deleteRestaurant(
restaurant_id_p INT
)
BEGIN
DELETE FROM restaurant WHERE restaurant_id = restaurant_id_p;
END //
DELIMITER ; 

CALL deleteRestaurant(5);

DROP PROCEDURE IF EXISTS getCustomerCountFromRestaurant;

DELIMITER //
CREATE PROCEDURE getCustomerCountFromRestaurant(
restaurant_id_p INT
)
BEGIN
SELECT COUNT(customer_id) from eachorder WHERE restaurant_id = restaurant_id_p GROUP BY restaurant_id;

END //
DELIMITER ;    

CALL getCustomerCountFromRestaurant(2);

DELIMITER //
CREATE PROCEDURE getOrderCountFromRestaurant(
restaurant_id_p INT
)
BEGIN
SELECT COUNT(order_id) from eachorder WHERE restaurant_id = restaurant_id_p GROUP BY restaurant_id;

END //
DELIMITER ; 

CALL getOrderCountFromRestaurant(2);


DROP PROCEDURE IF EXISTS getDeliveriesFromDriver;
DELIMITER //
CREATE PROCEDURE getDeliveriesFromDriver (
driver_id_p INT
)
BEGIN

SELECT COUNT(delivery_id) from delivery_driver WHERE driver_id = driver_id_p GROUP BY driver_id;

END //
DELIMITER ; 

CALL getDeliveriesFromDriver(2);

DROP PROCEDURE IF EXISTS listDeliveriesFromDriver;
DELIMITER //
CREATE PROCEDURE listDeliveriesFromDriver (
driver_id_p INT
)
BEGIN

SELECT delivery_id from delivery_driver WHERE driver_id = driver_id_p;
END //
DELIMITER ; 

CALL listDeliveriesFromDriver(1);




DROP PROCEDURE IF EXISTS getNewDeliveries;

DELIMITER //

CREATE PROCEDURE getNewDeliveries(
)
BEGIN
SELECT delivery_id,deliverystatus FROM delivery WHERE deliverystatus = "NOT ASSIGNED";
END //
DELIMITER ; 

CALL getNewDeliveries();

DROP PROCEDURE IF EXISTS assignDriverToDelivery;

DELIMITER //
CREATE PROCEDURE assignDriverToDelivery(
driver_id_p INT,
delivery_id_p INT
)
BEGIN
INSERT INTO delivery_driver(driver_id,delivery_id) VALUES(driver_id_p, delivery_id_p);
END //
DELIMITER ; 

CALL assignDriverToDelivery(2,1);

DROP PROCEDURE IF EXISTS getDeliveriesFromDriver;
DELIMITER //
CREATE PROCEDURE getDeliveriesFromDriver(
driver_id_p INT
)
BEGIN
SELECT delivery_driver.delivery_id, delivery.deliverystatus FROM delivery JOIN delivery_driver ON delivery_driver.delivery_id = delivery.delivery_id WHERE delivery_driver.driver_id=driver_id_p;
END //
DELIMITER ; 


CALL getDeliveriesFromDriver(1);



DROP PROCEDURE IF EXISTS updateDeliveryStatus;

DELIMITER //
CREATE PROCEDURE updateDeliveryStatus(
delivery_id_p INT,
status VARCHAR(20)
)
BEGIN
UPDATE delivery SET deliverystatus = status WHERE delivery_id = delivery_id_p;
UPDATE eachorder SET order_status = status WHERE eachorder.delivery_id = delivery_id_p;
END //
DELIMITER ; 


CALL updateDeliveryStatus(2,"ASSIGNED");


DROP PROCEDURE IF EXISTS updatepaymentgivenorderid;

DELIMITER //
CREATE PROCEDURE updatepaymentgivenorderid(
method_p VARCHAR(20), 
amount_p INT,
order_id_p INT
)

BEGIN
DECLARE payment_id_p INT;
SELECT payment_id from eachorder WHERE order_id = order_id_p INTO payment_id_p;
UPDATE payment SET paid = amount_p , method = method_p WHERE payment_id = payment_id_p;

END //
DELIMITER ;
DROP TRIGGER IF EXISTS updateStatus;

CALL updatepaymentgivenorderid("UPI",20,2);

DROP PROCEDURE IF EXISTS seePastTransactions;

DELIMITER //
CREATE PROCEDURE seePastTransactions(
customer_id_p INT
)
BEGIN
SELECT e.order_id from eachorder e
INNER JOIN customer c on c.customer_id = e.customer_id
WHERE c.customer_id = customer_id_p AND e.order_status = "DELIVERED";
END //
DELIMITER ;


CALL seePastTransactions(2);


DROP PROCEDURE IF EXISTS seePastTransactionsForRestaurant;

DELIMITER //
CREATE PROCEDURE seePastTransactionsForRestaurant(
customer_id_p INT,
restaurant_id_p INT
)
BEGIN
SELECT e.order_id from eachorder e
INNER JOIN customer c on c.customer_id = e.customer_id
WHERE c.customer_id = customer_id_p AND e.order_status = "DELIVERED" AND e.restaurant_id = restaurant_id_p;
END //
DELIMITER ;


CALL seePastTransactionsForRestaurant(2,2);



DROP PROCEDURE IF EXISTS seeItemsGivenOrderId;

DELIMITER //
CREATE PROCEDURE seeItemsGivenOrderId(
order_id_p INT
)
BEGIN
SELECT DISTINCT i.item_name, o.item_count from item i
JOIN order_items o using(item_id)
WHERE o.order_id = order_id_p; 
END //
DELIMITER ;


CALL seeItemsGivenOrderId(2);

DROP PROCEDURE IF EXISTS getItemCount;

DELIMITER //
CREATE PROCEDURE getItemCount(
item_id_p INT
)
BEGIN

select sum(item_count) from order_items where item_id = item_id_p group by(item_id);
END //
DELIMITER ;

CALL getItemCount(2);


DROP PROCEDURE IF EXISTS countOrdersGivenRestaurant;

DELIMITER //
CREATE PROCEDURE countOrdersGivenRestaurant(

restaurant_id_p INT
)

BEGIN
SELECT COUNT(order_id) FROM eachorder WHERE restaurant_id = restaurant_id_p group by restaurant_id;

END //
DELIMITER ;

CALL countOrdersGivenRestaurant(2);



DROP PROCEDURE IF EXISTS showCart;

DELIMITER //
CREATE PROCEDURE showCart(

order_id_p INT


)
BEGIN
SELECT i.item_name, o.item_count,i.price from order_items o
INNER JOIN item i on i.item_id = o.item_id
WHERE o.order_id = order_id_p; 
END //
DELIMITER ;

CALL showCart(2);

DROP PROCEDURE IF EXISTS seeNumOfDeliveriesByDriver;
DELIMITER //
CREATE PROCEDURE seeNumOfDeliveriesByDriver(
driver_id_p INT
)
BEGIN
SELECT COUNT(delivery_id) from delivery_driver WHERE driver_id = driver_id_p GROUP BY driver_id;
END //
DELIMITER ; 


CALL seeNumOfDeliveriesByDriver(1);


DROP PROCEDURE IF EXISTS getOrderCountFromRestaurant;
DELIMITER //
CREATE PROCEDURE getOrderCountFromRestaurant(
restaurant_id_p INT
)
BEGIN
SELECT COUNT(order_id) from eachorder WHERE restaurant_id = restaurant_id_p GROUP BY restaurant_id;
END //
DELIMITER ; 

CALL getOrderCountFromRestaurant(2);


DROP FUNCTION IF EXISTS ADMIN_DELETE_ORDERS;
DELIMITER $$
CREATE FUNCTION ADMIN_DELETE_ORDERS(
order_id INT
)
RETURNS INT DETERMINISTIC READS SQL DATA
BEGIN
	DECLARE is_success INT;
    	DECLARE selected_id INT;
    DELETE FROM eachorder WHERE order_id = selected_id;
    SET is_success = 1;
    RETURN (is_success);
    
END $$
DELIMITER ;

DROP FUNCTION IF EXISTS ADMIN_ADD_RESTAURANTS;

DELIMITER $$

CREATE FUNCTION ADMIN_ADD_RESTAURANTS(
restaurant_name VARCHAR(20),
rating INT,
phone_num INT,
location_id INT
)
RETURNS INT DETERMINISTIC READS SQL DATA
BEGIN
	DECLARE is_success INT;
  
    INSERT INTO RESTAURANT VALUES(NULL, restaurant_name, rating, phone_num, location_id);
    SET is_success = 1;
    RETURN (is_success);
    
    
END $$
DELIMITER ;
