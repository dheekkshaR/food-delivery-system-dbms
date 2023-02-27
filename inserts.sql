USE dbmsproj;


insert into administrator(admin_id,passwordSet) values(123456, "123456");
insert into administrator(admin_id,passwordSet) values(234567, "234567");
insert into administrator(admin_id,passwordSet) values(345678, "345678");


    
INSERT INTO location(landmark,latitude,longitude,weather,city,state,country,surge,rain,temp) values ("Near Elephant A", 186, 156, "RAINY","HOBOKEN","NJ","USA", 0, 35, 13);
INSERT INTO location(landmark,latitude,longitude,weather,city,state,country,surge,rain,temp) values ("Near Statue B", 112, 132, "SUNNY","BOSTON","MA","USA", 0, 0, 23);
INSERT INTO location(landmark,latitude,longitude,weather,city,state,country,surge,rain,temp) values ("Next to Coolidge Bar", 156, 101, "SUNNY","REDDING","PA","USA", 0, 0, 35);
INSERT INTO location(landmark,latitude,longitude,weather,city,state,country,surge,rain,temp) values ("Next to the Metro", 121, 88, "SUNNY","IRVINE","CA","USA", 0, 0, 28);
INSERT INTO location(landmark,latitude,longitude,weather,city,state,country,surge,rain,temp) values ("On the tarmac road", 109, 110, "CLOUDY","BOSTON","MA","USA", 0, 18, 12);
INSERT INTO location(landmark,latitude,longitude,weather,city,state,country,surge,rain,temp) values ("Before Max Theatre", 121, 123, "SUNNY","BOSTON","MA","USA", 0, 0, 25);
INSERT INTO location(landmark,latitude,longitude,weather,city,state,country,surge,rain,temp) values ("On the way to the post office", 123, 121, "RAINY","SEATTLE","WA","USA", 0, 35, 10);

    
insert into customer(cust_name,email, passwordset, phone, joined_date) values ("Rao","rao@.com","rao1123",3421112, "27-12-1997"); 
insert into customer(cust_name,email, passwordset, phone, joined_date) values ("Jim","jim@.com","jim1123",7438743, "27-01-1997"); 
insert into customer(cust_name,email, passwordset, phone, joined_date) values ("Karry","karry@.com","karry1123",1932032, "27-02-1991"); 
insert into customer(cust_name,email, passwordset, phone, joined_date) values ("Dheeksha","dheeksha@.com","dheek1123",93120321, "29-01-2002"); 
insert into customer(cust_name,email, passwordset, phone, joined_date) values ("Lif","lif@.com","lif1123",49309090, "27-02-1993"); 
insert into customer(cust_name,email, passwordset, phone, joined_date) values ("John","john@.com","john1123",1212433, "01-01-1992"); 
    




insert into cust_location(customer_id, loc_id) values (2, 3);
insert into cust_location(customer_id, loc_id) values (1, 5);
insert into cust_location(customer_id, loc_id) values (3, 2);
insert into cust_location(customer_id, loc_id) values (4, 7);
insert into cust_location(customer_id, loc_id) values (5, 6);
    


insert into menu(type, resturant_id) values ("Chinese Veg", 1);
insert into menu(type, resturant_id) values ("Chinese Non - Veg", 1);
insert into menu(type, resturant_id) values ("American - Veg", 2);
insert into menu(type, resturant_id) values ("American - Non Veg", 2);
/*insert into menu(type, resturant_id) values ("Chinese Veg", 1);
insert into menu(type, resturant_id) values ("Chinese Veg", 1);
insert into menu(type, resturant_id) values ("Chinese Veg", 1);*/



insert into item(price, item_name, restaurant_id, menu_id) values(10, "Veg Noodles", 1, 1);
insert into item(price, item_name, restaurant_id, menu_id) values(7, "Wonton Soup", 1, 1);
insert into item(price, item_name, restaurant_id, menu_id) values(3, "Sesame Sticks", 1, 1);
insert into item(price, item_name, restaurant_id, menu_id) values(4, "Fries", 2, 3);
insert into item(price, item_name, restaurant_id, menu_id) values(4, "Thick Shake", 2, 3);




insert into driver(driver_password,driver_name,govt_id_no,vehicle_no,phone_no) values ("1234", "Sid", 132222, 45545,8881454232);
insert into driver(driver_password,driver_name,govt_id_no,vehicle_no,phone_no) values ("2345", "Singh", 345522, 57565,4929299212);
insert into driver(driver_password,driver_name,govt_id_no,vehicle_no,phone_no) values ("a453", "Joe", 2143222, 81818,393090031);
insert into driver(driver_password,driver_name,govt_id_no,vehicle_no,phone_no) values ("b345", "Denise", 1874554, 12345,123999399);



insert into delivery(deliverystatus) values("NOT ASSIGNED");
insert into delivery(deliverystatus) values("NOT ASSIGNED");
insert into delivery(deliverystatus) values("NOT ASSIGNED");
insert into delivery(deliverystatus) values("NOT ASSIGNED");
insert into delivery(deliverystatus) values("NOT ASSIGNED");




insert into delivery_driver(delivery_id,driver_id) values (1,2);
insert into delivery_driver(delivery_id,driver_id) values (2,3);
insert into delivery_driver(delivery_id,driver_id) values (3,1);
insert into delivery_driver(delivery_id,driver_id) values (4,1);




insert into payment(method,paid) values ("Gpay", 0);
insert into payment(method,paid) values ("Apple Pay", 0);
insert into payment(method,paid) values("Cash", 0); 

insert into eachorder(order_status, restaurant_id, customer_id, delivery_id, payment_id) values ("INCOMPLETE", 1,  2, 1,1);
insert into eachorder(order_status, restaurant_id, customer_id, delivery_id, payment_id) values ("DELIVERED", 1,  2, 2,2);
insert into eachorder(order_status, restaurant_id, customer_id, delivery_id, payment_id) values ("DELIVERED", 1,  2, 3,3);




CALL makeOrderItemMapping(1,1);
CALL makeOrderItemMapping(1,1);
CALL makeOrderItemMapping(2,2);
CALL makeOrderItemMapping(2,3);



insert into discount(discount_percent_coupon, discount_percent) values (12345, 10);
insert into discount(discount_percent_coupon, discount_percent) values (23456, 15);
insert into discount(discount_percent_coupon, discount_percent) values (34567, 20);





insert into discount_payment(payment_id, discount_percent_coupon) values(1, 123456);
insert into discount_payment(payment_id, discount_percent_coupon) values(2,  234567);

insert into rate(lowerdist, upperdist, rateGiven) values(0,50,10);
insert into rate(lowerdist, upperdist, rateGiven) values(51,100,25);
insert into rate(lowerdist, upperdist, rateGiven) values(101,2000,50);


insert into weather(lower, upper, increase_percent) values(0,25,10);
insert into weather(lower, upper, increase_percent) values(26,51,20);
insert into weather(lower, upper, increase_percent) values(52,100,40);




























