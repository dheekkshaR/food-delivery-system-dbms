import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class deliverysystem {

  private final String serverName = "localhost";
  private final int portNumber = 3306;
  private final String dbName = "dbmsproj";
  Connection conn = null;
  int loc_id;
  int userOrAdmin;

  public Connection getConnection(String userName, String password) throws SQLException {
    Connection conn = null;
    Properties connectionProps = new Properties();
    connectionProps.put("user", userName);
    connectionProps.put("password", password);
    conn = DriverManager.getConnection("jdbc:mysql://"
                    + this.serverName + ":" + this.portNumber + "/" +
                    this.dbName + "?characterEncoding=UTF-8&useSSL=false",
            connectionProps);
    return conn;
  }


  public static void main(String[] args) {
    deliverysystem app = new deliverysystem();
    Scanner inp = new Scanner(System.in);
    System.out.println("Enter MYSQL username :");
    String username = inp.nextLine(); //root
    System.out.println("Enter MYSQL password :");
    String password = inp.nextLine(); //tiger20!
    //login to mysql database
    try {
      app.conn = app.getConnection(username, password);
      System.out.println("Connected to database");
      app.run(app.conn);
    } catch (SQLException e) {
      System.out.println("ERROR: Could not connect to the database");
      System.out.println(e.getMessage());
      return;
    }

    //admin login or user login then go to respective loops
    //take in all user or admin places
    //take and store in lists
  }

  private void run(Connection conn) {
    //say if admin or user
    Scanner inp = new Scanner(System.in);
    while(userOrAdmin!=4) {
      System.out.println("Enter 1 if admin, enter 2 if user, 3 to login as driver, 4 for exit");
      userOrAdmin = inp.nextInt();
      String dummy=inp.nextLine();
      switch (userOrAdmin) {
        case 1:
          adminLogin(conn);
          break;
        case 2:
          System.out.println("Enter 1 for user login, 2 to create new user");
          int ch = inp.nextInt();
          dummy = inp.nextLine();
          if (ch == 1)
            userLogin(conn);
          else if (ch == 2)
            createUser(conn);
          else
            System.out.println("Illegal entry");
          break;
        case 3:
          driverLogin(conn);
          break;
        case 4:
          System.out.println("Exit");
          System.exit(0);
          break;
        default:
          System.out.println("Exit (Invalid user input)!");
          System.exit(0);
          break;
      }
    }
  }

  //inserts new user and displays their location id
  void insertNewUser(String name, String email, String password,String street, String state,String city,String country) {
    CallableStatement ps = null;
    try {
      //SQL EXECUTE QUERY TO ADD the address and get loc id and display
      //ADD to customers table query, get the customer id and display
      //add a mapping entry between cust id and loc id
      ps = conn.prepareCall("{CALL CREATE_NEW_CUSTOMER(?,?,?,?,?,?,?)}");
      ps.setString(1, name);
      ps.setString(2, email);
      ps.setString(3, password);
      ps.setString(4, street);
      ps.setString(5, state);
      ps.setString(6, city);
      ps.setString(7, country);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        int loc=rows.getInt(1);
        int cust=rows.getInt(2);
        System.out.println("Your location id is "+ loc);
        System.out.println("Your new customer id is "+ cust);
        System.out.println("Your new account with name:"+name +" email:" +email+" was successfully created");
      }
    } catch (SQLException e) {
      System.out.println("Could not add new user");
      //throw new RuntimeException(e);
    }
  }

  protected void createUser(Connection conn) {
    Scanner inp=new Scanner(System.in);
    System.out.println("Enter customer name, email, password");
    String name = inp.nextLine();
    String email = inp.nextLine();
    String password = inp.nextLine();
    System.out.println("Enter location address: street, state, city, country");
    String street = inp.nextLine();
    String state = inp.nextLine();
    String city = inp.nextLine();
    String country= inp.nextLine();
    insertNewUser(name, email, password, street, state, city, country);
    System.out.println("User created, login");
    userLogin(conn);
  }

  protected void userLogin(Connection conn) {

    //enter username/password
    //select all user id and pass word form teh table
    Scanner inp = new Scanner(System.in);
    int username1;
    String password1;
    System.out.println("Enter customer id and password");
    username1 = inp.nextInt();
    String dummy=inp.nextLine();
    password1 = inp.nextLine();
    System.out.println(password1);
    boolean login_success = false;
    try {
      Statement st = conn.createStatement();

      CallableStatement login = conn.prepareCall("Select * from customer where customer_id =? and passwordset =? ");
      login.setInt(1, username1);
      login.setString(2, password1);
      CallableStatement counter = conn.prepareCall("SELECT COUNT(*) FROM customer where customer_id =? and passwordset =?");
      counter.setInt(1, username1);
      counter.setString(2, ""+password1);
      ResultSet rsLogin = login.executeQuery();
      ResultSet rsCounter = counter.executeQuery();
      int resultCount = 0;
      while (rsCounter.next()) {
        resultCount = (rsCounter.getInt(1));
      }
      if (resultCount != 1) {
        System.out.println("Credentials invalid - please try again ");
      }


      while (rsLogin.next()) {
        if (rsLogin.getInt(1)==(username1) && rsLogin.getString(4).equals(password1)) {
          System.out.println("Successfully logged in");
          login_success = true;
        } else {
          System.out.println("Credentials invalid - please try again (rows) ");
        }
      }
    } catch (SQLException e) {
      System.out.println(e+" userLogin");
    }

    if (login_success == true) {
      userLoop(conn, username1);
    }
  }

  void displayAddresses(int customer_id) {
    // select statement in location table, where customer id is = custid and print all
    ArrayList<Integer> menus= new ArrayList<>();
      try {
        PreparedStatement ps = conn.prepareStatement("SELECT location.loc_id, latitude, weather, street, city, state, country, surge, rain, temp FROM location JOIN cust_location ON cust_location.loc_id = location.loc_id WHERE cust_location.customer_id = ?;");
        ps.setInt(1, customer_id);
        ResultSet rows = ps.executeQuery();
        while (rows.next()) {
          menus.add(rows.getInt(1));
          System.out.println(rows.getInt(1) + "\t"
                  + rows.getDouble(2)
                  + "\t" + rows.getDouble(3)
                  + "\t" + rows.getString(4)
                  + "\t" + rows.getString(5)
                  + "\t" + rows.getString(6)
                  + "\t" + rows.getString(7)
                  + "\t" + rows.getInt(8)
                  + "\t" + rows.getInt(9)
                  + "\t" + rows.getInt(10)
                  + "\n");
        }
      } catch (SQLException e) {
        System.out.println(e+" displayAddresses");
      }
      Scanner inp=new Scanner(System.in);
    System.out.println("Enter the loc id ");
      loc_id=inp.nextInt();
      String d=inp.nextLine();
    System.out.println("Loc id picked :"+ loc_id);
    if(! menus.contains(loc_id)) {
      if(loc_id==0) {
        System.out.println("Exit from address picker");
        return;
      }
      System.out.println("loc id selected is not in the list enter 0 to exit");
      displayAddresses(customer_id);
    }

  }

  void pastOrdersHistory(int customer_id) {
    CallableStatement ps = null;
    CallableStatement ps1 = null;
    int order_id=0;
    try {
      ps1= conn.prepareCall("{CALL seePastTransactions(?)}");
      ps1.setInt(1,customer_id);
      ResultSet rows1 = ps1.executeQuery();
      while (rows1.next()) {
        order_id = (rows1.getInt(1));
        System.out.println("Order id :"+order_id);

        //SQL EXECUTE QUERY TO ADD the address and get loc id and display
        //ADD to customers table query, get the customer id and display
        //add a mapping entry between cust id and loc id
        ps = conn.prepareCall("{CALL seeItemsGivenOrderId(?)}");
        ps.setInt(1, order_id);
        ResultSet rows = ps.executeQuery();
        while (rows.next()) {
          System.out.println(rows.getString(1));
        }
      }
    } catch (SQLException e) {
      System.out.println("pastOrdersHistory error");
      //throw new RuntimeException(e);
    }
  }

  void displayItemFreq(int item) {

    CallableStatement ps1 = null;
    int f=0;
    try {
      ps1= conn.prepareCall("{CALL getItemCount(?)}");
      ps1.setInt(1,item);
      ResultSet rows1 = ps1.executeQuery();
      while (rows1.next()) {
        f = (rows1.getInt(1));
        System.out.println("Frequency of item "+item+" is :"+f);
      }
    } catch (SQLException e) {
      System.out.println("displayItemFreq error");
      //throw new RuntimeException(e);
    }
  }

  void getPastOrdersAtRestaurant(int customer_id, int restaurant_id) {
    CallableStatement ps = null;
    CallableStatement ps1 = null;
    int order_id=0;
    try {
      ps1= conn.prepareCall("{CALL seePastTransactionsForRestaurant(?,?)}");
      ps1.setInt(1,customer_id);
      ps1.setInt(2,restaurant_id);
      ResultSet rows1 = ps1.executeQuery();
      while (rows1.next()) {
        order_id = (rows1.getInt(1));
        System.out.println("Order id :"+order_id);

        //SQL EXECUTE QUERY TO ADD the address and get loc id and display
        //ADD to customers table query, get the customer id and display
        //add a mapping entry between cust id and loc id
        ps = conn.prepareCall("{CALL seeItemsGivenOrderId(?)}");
        ps.setInt(1, order_id);
        ResultSet rows = ps.executeQuery();
        while (rows.next()) {
          System.out.println(rows.getString(1)+" "+ rows.getInt(2));
        }
      }
    } catch (SQLException e) {
      System.out.println("getPastOrdersAtRestaurant error");
      //throw new RuntimeException(e);
    }
  }

  protected void ordersFromEachRestaurant() {
    CallableStatement ps1 = null;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT restaurant_id, restaurant_name FROM restaurant ");
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        int res_id = rows.getInt(1);
        String name=rows.getString(2);
        ps1 = conn.prepareCall("{CALL countOrdersGivenRestaurant(?)}");
        ps1.setInt(1, res_id);
        ResultSet rows1 = ps1.executeQuery();
        while (rows1.next()) {
          System.out.println(" Restaurant id : "+res_id+" Name :"+name+" orders: " +(rows1.getInt(1)));
        }
      }
    } catch (SQLException e) {
      System.out.println("ordersFromEachRestaurant error ");
      //throw new RuntimeException(e);
    }
  }

  protected void userLoop(Connection conn, int customer_id) {
    System.out.println("Welcome to the food delivery service");
    int choice = 100;
    Scanner inp=new Scanner(System.in);

    int restaurant=0; int menu=0; int item=0;
    while(choice!=0) {
      System.out.println("Enter 1 to order food from a restaurant");
      System.out.println("Enter 2 to view past orders history");
      System.out.println("Enter 3 to view how many times a certain item has been ordered");
      System.out.println("Enter 4 to display all the orders from a certain restaurant");
      System.out.println("Enter 5 to see your frequency at each restaurant");
      System.out.println("Enter 6 to EXIT, 0 to quit");
      choice=inp.nextInt();
      String dum=inp.nextLine();
      switch (choice) {
        case 1:
          //restaurant = displayRestaurants();
          System.out.println("Enter your location id/pincode");
          //existing addresses display
          displayAddresses(customer_id);
          restaurant = displayRestaurants();
          if(restaurant!=0)
          userCartLoop(conn, customer_id, restaurant);
          break;
        case 2:
          pastOrdersHistory(customer_id);//select statement function using customer id
          //view past orders history
          break;
        case 3:
          restaurant = displayRestaurants();
          if(restaurant!=0) {
            menu = displayMenus(restaurant);
            if(menu!=0) {
              item = displayItemsInMenus(restaurant, menu);
              displayItemFreq(item);
            }
          }
          //new func, accept rest id then menu id then item id
          //sum(count)
          //view how many times a certain item has been ordered (using this item id and menu id and rest id)
          break;
        case 4:  //use customer id and rest id to find all the orders and all the items in the orders
          restaurant = displayRestaurants();
          if(restaurant!=0)
          getPastOrdersAtRestaurant(customer_id, restaurant);
          break;
        case 5:// no arguments, just list of restraunts and num of order id's associated with it print
          ordersFromEachRestaurant();
          break;
        case 6:
          System.out.println("Exit");
          run(conn);
          break;

      }
    }
  }

  int displayRestaurants() {
    System.out.println("Pick a restaurant from the list below (Enter restaurant id)");
    ArrayList<Integer> arr= new ArrayList<>();
    try {
      //
      PreparedStatement ps = conn.prepareStatement("SELECT restaurant_id, restaurant_name, rating, phone, street, city, state, country  FROM restaurant JOIN location ON restaurant.loc_id=location.loc_id ;");
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        arr.add(rows.getInt(1));
        System.out.println(rows.getInt(1) + "\t" + rows.getString(2) +
                "\t" + rows.getInt(3) +
                "\t" + rows.getString(4) +
                "\t" + rows.getString(5) +
                "\t" + rows.getString(6) +
                "\t" + rows.getString(7) +
                "\t" + rows.getString(8) + "\n");
      }
    } catch (SQLException e) {
      System.out.println(e+" displayRestaurants");
    }
    Scanner inp=new Scanner(System.in);
    int res= inp.nextInt();
    String dummy=inp.nextLine();
    if(! arr.contains(res)) {
      if(res==0) {
        System.out.println("exiting from restaurant picking");
        return 0;
      }
      System.out.println("Rest id not present, press 0 to exit");
      return displayRestaurants();
    }
    return res;
  }

  int displayMenus(int restaurant_id) {
    System.out.println("Pick a menu from the list below (Enter menu id)");
    int ctr=0;
    ArrayList<Integer> menus= new ArrayList<>();
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT menu_id, type  FROM MENU WHERE restaurant_id = ?;");
      ps.setInt(1, restaurant_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        menus.add(rows.getInt(1));
        System.out.println(rows.getInt(1) + "\t" + rows.getString(2) +"\n");
        ctr++;
      }
    } catch (SQLException e) {
      System.out.println(e+" displayMenus");
    }
    Scanner inp=new Scanner(System.in);
    int res= inp.nextInt();
    if( ! menus.contains(res)) {
      if(res==0) {
        return 0;
      }
      System.out.println("Enter a valid menu id from this restaurant, 0 to exit");
      return displayMenus(restaurant_id);
    }
    String dummy=inp.nextLine();
    return res;
  }

  int displayItemsInMenus(int restaurant_id, int menu_id) {
    System.out.println("Pick an item from this menu (enter item id)");
    ArrayList<Integer> l= new ArrayList<>();
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT item_id, price, item_name  FROM ITEM WHERE restaurant_id = ? AND menu_id= ?;");
      ps.setInt(1, restaurant_id);
      ps.setInt(2, menu_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        l.add(rows.getInt(1) );
        System.out.println(rows.getInt(1) + "\t" + rows.getString(2) +
                "\t" + rows.getString(3) +"\n");
      }
    } catch (SQLException e) {
      System.out.println(e+" displayItemsInMenus");
    }
    System.out.println("---");
    Scanner inp=new Scanner(System.in);
    int res= inp.nextInt();
    //String dummy=inp.nextLine();
    if(! l.contains(res)) {
      if(res==0) {
        return 0;
      }
      System.out.println("Item not present in menu pick again, or enter 0 to quit");
      return displayItemsInMenus(restaurant_id, menu_id);
    }
    return res;
  }

  int getCouponPercent(int coupon) {
    int percent=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT discount_percent  FROM discount WHERE discount_percent_coupon = ? ;");
      ps.setInt(1, coupon);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        percent= (rows.getInt(1));
      }
    } catch (SQLException e) {
      System.out.println(e+" getCouponPercent");
    }
    return percent;
  }

  int getRainWeather() {
    int rain=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT rain  FROM location WHERE loc_id = ? ;");
      ps.setInt(1, loc_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        rain= (rows.getInt(1));
      }
    } catch (SQLException e) {
      System.out.println(e +" getRainWeather");
    }
    return rain;
  }
  int getTemp() {
    int temp=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT temp  FROM location WHERE loc_id = ? ;");
      ps.setInt(1, loc_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        temp= (rows.getInt(1));
      }
    } catch (SQLException e) {
      System.out.println(e+" getTemp");
    }
    return temp;
  }
  int getIncDueToTemp(int temp) {
    //select statemnet use func or procedure here to get temp and see which limit of lower<=tempo<=upper
    // return that value (percent increase in rate)
    int upper=0; int lower=0; int rate=0; int final_rate=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT lower, upper, increase_percent  FROM weather  ;");
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        lower = (rows.getInt(1));
        upper = (rows.getInt(2));
        rate = (rows.getInt(3));
        if(temp>= lower && temp<=upper) {
          final_rate=rate;
        }

      }
    } catch (SQLException e) {
      System.out.println(e+" getIncDueToTemp");
    }
    return final_rate;

  }

  int createNewOrder(int customer_id, int restaurant_id) {
    CallableStatement ps1 = null;
    int order_id=0;
    try {
      ps1 = conn.prepareCall("{CALL createCartFromCustomer(?,?)}");
      ps1.setInt(1, customer_id);
      ps1.setInt(2, restaurant_id);
      ResultSet rows1 = ps1.executeQuery();
      while (rows1.next()) {
        order_id = (rows1.getInt(1));
      }
    } catch (Exception e) {
      System.out.println(" createNewOrder error");
      //throw new RuntimeException(e);
    }
    return order_id;
  }
  void insertOrderItem(int order_id, int item_id){
    CallableStatement ps1 = null;
    try {
      ps1 = conn.prepareCall("{CALL makeOrderItemMapping(?,?)}");
      ps1.setInt(1, order_id);
      ps1.setInt(2, item_id);
      ps1.executeQuery();
      System.out.println("Insert complete");
    } catch (Exception e) {
     System.out.println(e.getMessage());
    }
  }
  void deleteOrderItem(int order_id,int item_id){
    CallableStatement ps1 = null;
    try {
      ps1 = conn.prepareCall("{CALL deleteOrderItemMapping(?,?)}");
      ps1.setInt(1, order_id);
      ps1.setInt(2, item_id);
      ps1.executeQuery();
      System.out.println("Removed item");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
  void showCart(int order_id) {
    System.out.println("Cart contains");
    try {
      CallableStatement ps = conn.prepareCall("{CALL showCart(?)}");
      ps.setInt(1, order_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        //item name and number
        System.out.println(rows.getString(1) + "\t" + rows.getInt(2)  + "\t" + rows.getInt(3) +"\n");
      }
    } catch (SQLException e) {
      System.out.println(e+" showCart");
    }

  }
  int totalCart(int order_id){
    int total=0;
    int price=0;
    int count=0;
    try {
      CallableStatement ps = conn.prepareCall("{CALL showCart(?)}");
      ps.setInt(1, order_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        //item name and number
        price= rows.getInt(3) ;
        count=rows.getInt(2);
        total=total+price*count;
      }
      System.out.println("Total of items:" +total);
    } catch (SQLException e) {
      System.out.println(e +" totalCart");
    }
    return total;
  }
  double getX() {
    double temp=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT latitude  FROM location WHERE loc_id = ? ;");
      ps.setInt(1, loc_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        temp= (rows.getDouble(1));
      }
    } catch (SQLException e) {
      System.out.println(e +" getX");
    }
    return temp;
  }
  double getY() {
    double temp=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT longitude  FROM location WHERE loc_id = ? ;");
      ps.setInt(1, loc_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        temp= (rows.getDouble(1));
      }
    } catch (SQLException e) {
      System.out.println(e+ " getY ");
    }
    return temp;
  }
  double getXofRest(int restaurant_id){
    double temp=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT latitude  FROM location WHERE loc_id = (SELECT loc_id FROM restaurant WHERE restaurant_id =?) ;");
      ps.setInt(1, restaurant_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        temp= (rows.getDouble(1));
      }
    } catch (SQLException e) {
      System.out.println(e+" getXofRest");
    }
    return temp;
  }
  double getYofRest(int restaurant_id){
    double temp=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT longitude  FROM location WHERE loc_id = (SELECT loc_id FROM restaurant WHERE restaurant_id =?) ;");
      ps.setInt(1, restaurant_id);
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        temp= (rows.getDouble(1));
      }
    } catch (SQLException e) {
      System.out.println(e+" getYofRest");
    }
    return temp;
  }
  int getRate(double dist){
    int upper=0; int lower=0; int rate=0; int final_rate=0;
    try {
      PreparedStatement ps = conn.prepareStatement("SELECT lowerdist, upperdist, rateGiven  FROM rate  ;");
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        lower = (rows.getInt(1));
        upper = (rows.getInt(2));
        rate = (rows.getInt(3));
        if(dist>= lower && dist<=upper) {
          final_rate=rate;
        }
      }
    } catch (SQLException e) {
      System.out.println(e+ " getRate");
    }
    return final_rate;
  }
  void totalStoreInPayment(double total, int order_id){

    try {
      PreparedStatement ps = conn.prepareStatement("UPDATE payment SET paid=?  WHERE payment.payment_id =  (SELECT payment_id FROM eachorder WHERE order_id=?) ;");
      ps.setInt(1, (int)total);
      ps.setInt(2, order_id);
      ps.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e+" totalStoreInPayment");
    }

  }

  protected void userCartLoop(Connection conn,int customer_id, int restaurant_id) {
    int menu = 0;
    Scanner inp = new Scanner(System.in);
    int item = 0;

    // query to make a order CART (eachorder)
    // get the order id , add a payment entry, add a delivery id entry blank,
    // get restraunt id from user here
    // create a tuple in each order
    int order_id = createNewOrder(customer_id, restaurant_id);
    int choice = 100;
    while (choice!=0) {
      System.out.println("Enter 1 to add to cart");
      System.out.println("Enter 2 to remove from cart");
      System.out.println("Enter 3 to display cart");
      System.out.println("Enter 4 to proceed with payment");
      System.out.println("Enter 5 to go back to main menu, 0 to quit");
      choice = inp.nextInt();
      String dum = inp.nextLine();
      switch (choice) {
        case 1: //display the menus asscoiated with this restraunt id, return menu picked
          menu = displayMenus(restaurant_id);
          item = displayItemsInMenus(restaurant_id, menu);
          insertOrderItem(order_id, item);
          break;

          //ADD TO TABLE "rel order items"
          // IF already present in table, trigger - update count=count +1
          // send order id, item id
        case 2: //remove from cart
          menu = displayMenus(restaurant_id);
          item = displayItemsInMenus(restaurant_id, menu);
          deleteOrderItem(order_id, item);
          //accept the item id and number from user
          //delete from table if it exists, else use trigger and do count=count-1
          break;
        case 3: //user order id as argument
          showCart(order_id);
          // function call - which displays item name and count, joins between order_items and item table, where order id = order_id
          break;
        case 4: //payment(order_id)- total the order id's all items cost* count
          //calculate total using many select statements
          double total = totalCart(order_id);
          System.out.println(" Total from cart" + total);
          //accept method from user
          System.out.println("Enter method of payment");
          String method = inp.nextLine();
          System.out.println("Discount coupon, enter 0 of no coupon");
          int coupon = inp.nextInt();
          String dummy = inp.nextLine();
          int percentOff = getCouponPercent(coupon);
          int rain = getRainWeather();
          int rainInc = 0;
          if (rain > 50) {
            rainInc = 10;
          }
          int temp = getTemp();
          int incDueToTemp = getIncDueToTemp(temp);
          double xcoor = getXofRest(restaurant_id);
          double ycoor = getYofRest(restaurant_id); // GET x and y cood (lat and long) from restraunt ID - then locaytion table
          double xC = getX();
          double yC = getY();
          ;// SAME but from customer table
          // root of ROOT [(x-x1) sq + (y1-y2) sq]
          //calc dist
          double dist = 0;
          dist = Math.sqrt(((xcoor - xC) * (xcoor - xC)) - ((ycoor - yC) * (ycoor - yC)));
          //FUNCTION to find which interval of RATE table this dist is in and take that rate value returned
          int rate = 0;
          rate = getRate(dist);
          total = total * ((100 - percentOff) / 100) * ((100 + rainInc) / 100) * ((100 + incDueToTemp) / 100);
          System.out.println(" Total after offers and surge pricing effects (weather)" + total);
          total = total + 0.01*(rate * dist);
          System.out.println(" Total plus delivery fee " + total);
          totalStoreInPayment(total, order_id);
          //FUNCTION to add the value of each item ID - pass order ID and it will gen all tuples in REL table bw order and item
          // store total locally
          // add the rates, temp, rain stuff -> store total value in PAID attribute of payment
          // OUT for delivery - driver has to accept,
          break;
        case 5:
          System.out.println("Exit from this restaurant? all progress will be lost");
          restaurant_id = 0;
          userLoop(conn, customer_id);
          break;
        default:
          System.out.println("Invalid input choice");
          break;
      }
  }
  }


  protected void adminLogin(Connection conn) {
    //enter username/password
    //select all user id and pass word form teh table
    Scanner inp = new Scanner(System.in);
    int username1;
    String password1;
    System.out.println("Enter admin id and password");
    username1 = inp.nextInt();
    String dummy=inp.nextLine();
    password1 = inp.nextLine();
    boolean login_success = false;
    try {
      Statement st = conn.createStatement();

      CallableStatement login = conn.prepareCall("Select * from administrator where admin_id =? and passwordSet =? ");
      login.setInt(1, username1);
      login.setString(2, ""+password1);
      CallableStatement counter = conn.prepareCall("Select COUNT(*) from administrator where admin_id =? and passwordSet =?");
      counter.setInt(1, username1);
      counter.setString(2, ""+password1);
      ResultSet rsLogin = login.executeQuery();
      ResultSet rsCounter = counter.executeQuery();
      int resultCount = 0;
      while (rsCounter.next()) {
        resultCount = (rsCounter.getInt(1));
      }
      if (resultCount != 1) {
        System.out.println("Credentials invalid - please try again logged in");
      }


      while (rsLogin.next()) {
        if (rsLogin.getInt(1)==(username1) && rsLogin.getString(2).equals(password1)) {
          System.out.println("Successfully logged in");
          login_success = true;
        } else {
          System.out.println("Credentials invalid - please try again logged in");
        }
      }
    } catch (SQLException e) {
      System.out.println(e+ " adminLogin ");
    }

    if (login_success == true) {
      adminLoop(conn, username1);
    }

  }

  protected void driverLogin(Connection conn) {
    //enter username/password
    //select all user id and pass word form teh table
    Scanner inp = new Scanner(System.in);
    int username1;
    String password1;
    System.out.println("Enter driver id and password");
    username1 = inp.nextInt();
    String dummy=inp.nextLine();
    password1 = inp.nextLine();
    boolean login_success = false;
    try {
      Statement st = conn.createStatement();

      PreparedStatement login = conn.prepareStatement("Select * from driver where driver_id =? and driver_password =? ");
      login.setInt(1, username1);
      login.setString(2, ""+password1);
      PreparedStatement counter = conn.prepareStatement("Select COUNT(*) from driver where driver_id =? and driver_password =?");
      counter.setInt(1, username1);
      counter.setString(2, ""+password1);
      ResultSet rsLogin = login.executeQuery();
      ResultSet rsCounter = counter.executeQuery();
      int resultCount = 0;
      while (rsCounter.next()) {
        resultCount = (rsCounter.getInt(1));
      }
      if (resultCount != 1) {
        System.out.println("Credentials invalid - please try again logged in");
      }


      while (rsLogin.next()) {
        if (rsLogin.getInt(1)==(username1) && rsLogin.getString(2).equals(password1)) {
          System.out.println("Successfully logged in");
          login_success = true;
        } else {
          System.out.println("Credentials invalid - please try again logged in");
        }
      }
    } catch (SQLException e) {
      System.out.println(e +" driverLogin");
    }

    if (login_success == true) {
      driverLoop(conn, username1);
    }

  }

  protected void adminLoop(Connection conn, int admin_id) {
    int flag=0;
    int choice=100;
    Scanner inp=new Scanner(System.in);
      int restaurant = 0;
      while(choice!=0) {
        System.out.println("Enter 1 to add restaurants");
        System.out.println("Enter 2 to add menu in a restaurant");
        System.out.println("Enter 3 to add items in a menu in a restaurants");
        System.out.println("Enter 4 to add discount coupons");
        System.out.println("Enter 5 to recruit a driver");
        System.out.println("Enter 6 to delete a driver");
        System.out.println("Enter 7 to edit rate multiplier info");
        System.out.println("Enter 8 to delete a restaurant");
        System.out.println("Enter 9 to see how many customers each restaurant has");
        System.out.println("Enter 10 to see how many orders each restaurant has");
        System.out.println("Enter 11 to see how many deliveries each driver has done");
        System.out.println("Enter 12 to return to main menu");
        choice = inp.nextInt();
        String dummy = inp.nextLine();
      switch (choice) {
        case 1:
          System.out.println("Enter restaurant name");
          String name = inp.nextLine();
          System.out.println("Enter location address of restaurant:LATITUDE, LONGITUDE,  street, state, city, country");
          double lat = inp.nextDouble();
          double lon = inp.nextDouble();
          String duy= inp.nextLine();
          String street = inp.nextLine();
          String state = inp.nextLine();
          String city = inp.nextLine();
          String country = inp.nextLine();
          createRestaurant(name, lat, lon, street, state, city, country);
          break;
        case 2:
          restaurant = displayRestaurants();
          System.out.println("Enter menu name/type ");
          String menu = inp.nextLine();
          if(restaurant!=0 )
          createMenuForRestaurant(restaurant, menu);
          break;
        case 3:
          restaurant = displayRestaurants();
          int menuId = displayMenus(restaurant);
          System.out.println("Enter Item and its price");
          String item = inp.nextLine();
          int price = inp.nextInt();
          String dumy = inp.nextLine();
          if(restaurant!=0 && menuId!=0)
          addItemToMenu(restaurant, menuId, item, price);
          break;
        case 4:
          System.out.println("Enter new coupon");
          int coupon = inp.nextInt();
          System.out.println("Enter new discount percentage to generate coupon");
          int percent = inp.nextInt();
          String dmy = inp.nextLine();
          addNewDiscountCoupon(coupon, percent);
          break;
        case 5:
          System.out.println("Enter new driver name ");
          String dName = inp.nextLine();
          System.out.println("Enter new driver password ");
          String pass = inp.nextLine();
          System.out.println("Enter new driver govt_id_no ");
          String govt_id_no = inp.nextLine();
          System.out.println("Enter new driver vehicle_no ");
          String vehicle_no = inp.nextLine();
          System.out.println("Enter new driver phone_no ");
          String phone_no = inp.nextLine();
          addNewDriver(dName, pass, govt_id_no, vehicle_no, phone_no);
          break;
        case 6:
          int d = displayDrivers();
          deleteDriver(d);
          break;
        case 7:
          System.out.println("Enter lower limit dist ");
          int l = inp.nextInt();
          System.out.println("Enter upper limit dist  ");
          int u = inp.nextInt();
          System.out.println("Enter new Rate  ");
          int rate = inp.nextInt();
          String du = inp.nextLine();
          updateRate(l, u, rate);
          break;
        case 8:
          restaurant = displayRestaurants();
          if(restaurant!=0)
          deleteRestaurant(restaurant);
          break;
        case 9:
          customersInEachRestaurant();
          break;
        case 10:
          ordersInEachRestaurant();
          break;
        case 11:
          deliveriesByEachDriver();
          break;
        case 12:
          flag = 1;
          run(conn);
          break;
        default:
          System.out.println("Invalid choice");
          break;
      }
    }
      // admin can add restraunts
      // * add menus and items in the menus
      // * he can add drivers
      // * he can delete drivers
      // ** trigger to set driver to 0 and say he quit
      // * can add discounts coupons
      //  can add weather or date multiplier info
      //  can update weather or rate multiplier info
      //* can delete orders- maybe restruants also??- thn update the orders  to have 0 in them -" deleted restraunt"
      // ** trigger to set all ids to 0, null deleted, MENUS< ITEMS, ORDERS set everything to 0
      // extra
      // * can see how many customers/ orders each restraunt has had
      // * can see how many times each item has been ordered
      //*  can see how many deliveries each driver has done
  }
  private void deliveriesByEachDriver() {
    PreparedStatement ps = null;
    CallableStatement ps1 = null;
    int rest=0;
    try {
      ps= conn.prepareStatement("SELECT driver_id, driver_name from driver;");
      ResultSet rows1 = ps.executeQuery();
      while (rows1.next()) {
        rest = (rows1.getInt(1));
        String rest_name = (rows1.getString(2));
        System.out.print("Driver name : "+rest_name+" ");

        //SQL EXECUTE QUERY TO ADD the address and get loc id and display
        //ADD to customers table query, get the customer id and display
        //add a mapping entry between cust id and loc id
        ps1 = conn.prepareCall("{CALL seeNumOfDeliveriesByDriver(?)}");
        ps1.setInt(1, rest);
        ResultSet rows = ps1.executeQuery();
        while (rows.next()) {
          System.out.println(rows.getInt(1));
        }
      }
    } catch (SQLException e) {
      System.out.println("seeNumOfDeliveriesByDriver error");
      //throw new RuntimeException(e);
    }
  }

  private void ordersInEachRestaurant() {
    PreparedStatement ps = null;
    CallableStatement ps1 = null;
    int rest=0;
    try {
      ps= conn.prepareStatement("SELECT restaurant_id, restaurant_name from RESTAURANT;");
      ResultSet rows1 = ps.executeQuery();
      while (rows1.next()) {
        rest = (rows1.getInt(1));
        String rest_name = (rows1.getString(2));
        System.out.print("Restaurant name :"+rest_name+" ");

        //SQL EXECUTE QUERY TO ADD the address and get loc id and display
        //ADD to customers table query, get the customer id and display
        //add a mapping entry between cust id and loc id
        ps1 = conn.prepareCall("{CALL getOrderCountFromRestaurant(?)}");
        ps1.setInt(1, rest);
        ResultSet rows = ps1.executeQuery();
        while (rows.next()) {
          System.out.println(rows.getInt(1));
        }
      }
    } catch (SQLException e) {
      System.out.println("ordersInEachRestaurant error");
      //throw new RuntimeException(e);
    }
  }
  private void customersInEachRestaurant() {
    PreparedStatement ps = null;
    CallableStatement ps1 = null;
    int rest=0;
    try {
      ps= conn.prepareStatement("SELECT restaurant_id, restaurant_name from restaurant;");
      ResultSet rows1 = ps.executeQuery();
      while (rows1.next()) {
        rest = (rows1.getInt(1));
        String rest_name = (rows1.getString(2));
        System.out.print("Restaurant name :"+rest_name+" ");

        //SQL EXECUTE QUERY TO ADD the address and get loc id and display
        //ADD to customers table query, get the customer id and display
        //add a mapping entry between cust id and loc id
        ps1 = conn.prepareCall("{CALL getCustomerCountFromRestaurant(?)}");
        ps1.setInt(1, rest);
        ResultSet rows = ps1.executeQuery();
        while (rows.next()) {
          System.out.println(rows.getInt(1));
        }
      }
    } catch (SQLException e) {
      System.out.println("customersInEachRestaurant error");
      System.out.println(e.getMessage());
      //throw new RuntimeException(e);
    }
  }

  private void deleteRestaurant(int restaurant) {
    PreparedStatement ps1 = null;
    try {
      ps1 = conn.prepareStatement("DELETE FROM restaurant WHERE restaurant_id= ? ");
      ps1.setInt(1, restaurant);
      ps1.executeUpdate();
      System.out.println("restaurant deleted successfully");
    } catch (Exception e) {
      System.out.println("Restaurant deletion error ");
      System.out.println(e.getMessage());
    }
  }

  private void updateRate(int l, int u, int rate) {
    PreparedStatement ps1 = null;
    try {
      ps1 = conn.prepareStatement("UPDATE rate SET rateGiven = ? WHERE lowerdist=? and upperdist=?  ");
      ps1.setInt(1, rate);
      ps1.setInt(2, l);
      ps1.setInt(3, u);
      ps1.executeUpdate();
      System.out.println("Rate updated successfully");
    } catch (Exception e) {
      System.out.println("Rate updation error ");
      System.out.println(e.getMessage());
    }
  }

  private void deleteDriver(int d) {
    CallableStatement ps1 = null;
    try {
      ps1 = conn.prepareCall("{CALL deleteDeliveryDriver(?)}");
      ps1.setInt(1, d);
      ps1.executeQuery();
      System.out.println("Driver deleted successfully");
    } catch (Exception e) {
      System.out.println("Driver not deleted ");
      System.out.println(e.getMessage());
    }
  }

  int displayDrivers() {
    System.out.println("Drivers");
    try {
      //
      PreparedStatement ps = conn.prepareStatement("SELECT driver_id , driver_name, vehicle_no FROM driver;");
      ResultSet rows = ps.executeQuery();
      while (rows.next()) {
        System.out.println(rows.getString(1) + "\t" + rows.getString(2) +
                "\t" + rows.getString(3) + "\n");
      }
    } catch (SQLException e) {
      System.out.println(e+" displayDrivers");
    }
    Scanner inp=new Scanner(System.in);
    int res= inp.nextInt();
    String dummy=inp.nextLine();
    return res;
  }

  private void addNewDriver(String dName, String pass, String govt_id_no, String vehicle_no, String phone_no) {
    CallableStatement ps1 = null; int dId=0;
    try {
      ps1 = conn.prepareCall("{CALL createDeliveryDriver(?,?,?,?,?)}");
      ps1.setString(1, dName);
      ps1.setString(2, pass);
      ps1.setString(3, govt_id_no);
      ps1.setString(4, vehicle_no);
      ps1.setString(5, phone_no);
      ResultSet rsCounter = ps1.executeQuery();
      while (rsCounter.next()) {
        dId =(rsCounter.getInt(1));
      }
      System.out.println("Driver created successfully id = "+ dId);
    } catch (Exception e) {
      System.out.println("Driver not created successfully");
      System.out.println(e.getMessage());
    }
  }

  private void addNewDiscountCoupon(int coupon, int percent) {
    CallableStatement ps1 = null;
    try {
      ps1 = conn.prepareCall("{CALL addDiscountCoupon(?,?)}");
      ps1.setInt(1, coupon);
      ps1.setInt(2, percent);
      ///are both being created in the same function??????????????
    ps1.executeQuery();
      System.out.println("Discount coupon created successfully ");
    } catch (Exception e) {
      System.out.println("Discount not created successfully");
      System.out.println(e.getMessage());
    }
  }

  private void addItemToMenu(int restaurant, int menuId, String item, int price) {
    CallableStatement ps1 = null;
    try {
      ps1 = conn.prepareCall("{CALL addItemToMenu(?,?,?,?)}");
      ps1.setInt(1, restaurant);
      ps1.setInt(2, menuId);
      ps1.setInt(3, price);
      ps1.setString(4, item);

      ///are both being created in the same function??????????????
      ps1.executeQuery();
      System.out.println("Item created successfully");
    } catch (Exception e) {
      System.out.println("Item not created successfully");
      System.out.println(e.getMessage());
    }
  }

  private void createMenuForRestaurant(int restaurant, String menu) {
    CallableStatement ps1 = null;
    try {
      ps1 = conn.prepareCall("{CALL createMenu(?,?)}");
      ps1.setInt(1, restaurant);
      ps1.setString(2, menu);
      ///are both being created in the same function??????????????
      ps1.executeQuery();
      System.out.println("Menu created successfully");
    } catch (Exception e) {
      System.out.println("Menu not created successfully");
      System.out.println(e.getMessage());
    }
  }

  private void createRestaurant(String name,double lat, double lon, String street, String state, String city, String country) {
    CallableStatement ps1 = null;
    try {
      ps1 = conn.prepareCall("{CALL CREATE_NEW_RESTAURANT(?,?,?,?,?,?,?)}");
      ps1.setString(1, name);
      ps1.setDouble(2, lat);
      ps1.setDouble(3, lon);
      ps1.setString(4, street);
      ps1.setString(5, state);
      ps1.setString(6, city);
      ps1.setString(7, country);
      ///are both being created in the same function??????????????
      ps1.executeQuery();
      System.out.println("Restaurant created successfully");
    } catch (Exception e) {
      System.out.println("Restaurant not created successfully");
      System.out.println(e.getMessage());
    }
  }

  protected void driverLoop(Connection conn, int driver_id) {
    // login for delivery driver similar to the user and admin
    // * he can view all the delivery ids and info that have no driver assigned to it for
    // he can pick one
    // * add table entry for the mapping of delivery id and teh driver id
    // * - change the delivery status to assigned
    // * update statement - he can change it to delivered on delivery
    int choice=100;
    Scanner inp=new Scanner(System.in);
    String dummy;
    while(choice!=0) {
      System.out.println("Enter 1 to see all the deliveries you can pick up");
      System.out.println("Enter 2 to pickup a delivery");
      System.out.println("Enter 3 to declare a delivery as delivered");
      System.out.println("Enter 4 to see all the deliveries in your history");
      System.out.println("Enter 5 to logout/exit");
      choice=inp.nextInt();
       dummy=inp.nextLine();
      switch (choice) {
        case 1:
          displayUnassignedDeliveries();
          // find all delivery ids and info that have no driver assigned to it for ( joins with th rel table bw delivery and driver)
          break;
        case 2:
          System.out.println("Enter delivery id");
          int delivery_id = inp.nextInt();
          dummy = inp.nextLine();
          assignDelivery(delivery_id, driver_id);
          // accept a delivery id and make a new entry in rel table
          //change delivery status as on the way
          break;
        case 3:
          System.out.println("Enter delivery id");
          int delivery_id1 = inp.nextInt();
          dummy = inp.nextLine();
          declareDelivered(delivery_id1, driver_id);
          //change delivery table  state as delivered- update the status alone
          break;
        case 4:
          seeDeliveryHistory(driver_id);
          // use driver_id , in rel driver-delivery table print all the deliveries using where statement
          // SELECT deliver_id, deliverystatus FROM delivery JOIN delivery_driver ON delivery_driver.delivery_id = delivery.delivery_id WHERE delivery_driver.driver_id=?
          break;
        case 5:
          System.out.println("Exit driver !");
          run(conn);
          break;

      }
    }


  }

  private void seeDeliveryHistory( int driver_id) {
    CallableStatement ps1 = null; int dId=0;
    try {
      ps1 = conn.prepareCall("{CALL listDeliveriesFromDriver(?)}");
      ps1.setInt(1, driver_id);
      //delivery_id, restid, rest location id and landmark, customer id, name, customer locationid, landmark
      ResultSet rows = ps1.executeQuery();
      while (rows.next()) {
        System.out.println(rows.getInt(1));
      }
      System.out.println("Declared delivered");
      System.out.println();
    } catch (Exception e) {
      System.out.println(" listDeliveriesFromDriver - failed ");
      System.out.println(e.getMessage());
    }
  }

  private void declareDelivered(int delivery_id1, int driver_id) {
    CallableStatement ps1 = null;
    try {
      ps1 = conn.prepareCall("{CALL updateDeliveryStatus(?,? )}");
      ps1.setInt(1, delivery_id1);
      ps1.setString(2, "DELIVERED");
      //delivery_id, restid, rest location id and landmark, customer id, name, customer locationid, landmark
      ps1.executeQuery();
      System.out.println("Declared delivered");
      System.out.println();
    } catch (Exception e) {
      System.out.println("declaring delivered - failed ");
      System.out.println(e.getMessage());
    }
  }

  private void assignDelivery(int delivery_id, int driver_id) {
    CallableStatement ps1 = null;
    CallableStatement ps = null;
    try {
      ps1 = conn.prepareCall("{CALL updateDeliveryStatus(?,? )}");
      ps1.setInt(1, delivery_id);
      ps1.setString(2, "ASSIGNED");
      //delivery_id, restid, rest location id and landmark, customer id, name, customer locationid, landmark
      ps1.executeQuery();
    } catch (Exception e) {
      System.out.println("Mapping failed updateDeliveryStatus - driver and delivery");
      System.out.println(e.getMessage());
    }
    try {
      ps=conn.prepareCall("{CALL assignDriverToDelivery(?,?)}");
      ps.setInt(1, driver_id);
      ps.setInt(2, delivery_id);
      ps.executeQuery();
      System.out.println("Mapping created - driver and delivery");
      System.out.println();
    } catch (Exception e) {
      System.out.println("Mapping failed - driver and delivery");
      System.out.println(e.getMessage());
    }
  }

  private void displayUnassignedDeliveries() {
    CallableStatement ps1 = null; int dId=0;
    try {
      ps1 = conn.prepareCall("{CALL getNewDeliveries()}");
      //delivery_id, restid, rest location id and landmark, customer id, name, customer locationid, landmark
      ResultSet rsCounter = ps1.executeQuery();
      while (rsCounter.next()) {
        System.out.println(rsCounter.getInt(1)+"\t"+rsCounter.getString(2)+"\n");
      }
      System.out.println();
    } catch (Exception e) {
      System.out.println("displayUnassignedDeliveries error");
      System.out.println(e.getMessage());
    }

  }

}

