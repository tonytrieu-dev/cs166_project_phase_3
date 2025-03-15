/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


 import java.sql.DriverManager;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.io.File;
 import java.io.FileReader;
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.util.List;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.sql.Timestamp;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Scanner;
 
 /**
  * This class defines a simple embedded SQL utility class that is designed to
  * work with PostgreSQL JDBC drivers.
  *
  */
 public class PizzaStore {
 
    // reference to physical database connection.
    private Connection _connection = null;
    
    // Global variable to store the current logged-in user
    static String currentUser = null;
    
    // Global variable to store the current user role
    static String currentRole = null;
 
    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(
                                 new InputStreamReader(System.in));
 
    /**
     * Creates a new instance of PizzaStore
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {
 
       System.out.print("Connecting to database...");
       try{
          // constructs the connection URL
          String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
          System.out.println ("Connection URL: " + url + "\n");
 
          // obtain a physical connection
          this._connection = DriverManager.getConnection(url, user, passwd);
          System.out.println("Done");
       }catch (Exception e){
          System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
          System.out.println("Make sure you started postgres on this machine");
          System.exit(-1);
       }//end catch
    }//end PizzaStore
 
    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate (String sql) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the update instruction
       stmt.executeUpdate (sql);
 
       // close the instruction
       stmt.close ();
    }//end executeUpdate
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;
 
       // iterates through the result set and output them to standard out.
       boolean outputHeader = true;
       while (rs.next()){
        if(outputHeader){
          for(int i = 1; i <= numCol; i++){
          System.out.print(rsmd.getColumnName(i) + "\t");
          }
          System.out.println();
          outputHeader = false;
        }
          for (int i=1; i<=numCol; ++i)
             System.out.print (rs.getString (i) + "\t");
          System.out.println ();
          ++rowCount;
       }//end while
       stmt.close();
       return rowCount;
    }//end executeQuery
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the results as
     * a list of records. Each record in turn is a list of attribute values
     *
     * @param query the input query string
     * @return the query result as a list of records
     * @throws java.sql.SQLException when failed to execute the query
     */
    public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;
 
       // iterates through the result set and saves the data returned by the query.
       boolean outputHeader = false;
       List<List<String>> result  = new ArrayList<List<String>>();
       while (rs.next()){
         List<String> record = new ArrayList<String>();
       for (int i=1; i<=numCol; ++i)
          record.add(rs.getString (i));
         result.add(record);
       }//end while
       stmt.close ();
       return result;
    }//end executeQueryAndReturnResult
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();
 
        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);
 
        int rowCount = 0;
 
        // iterates through the result set and count nuber of results.
        while (rs.next()){
           rowCount++;
        }//end while
        stmt.close ();
        return rowCount;
    }
 
    /**
     * Method to fetch the last value from sequence. This
     * method issues the query to the DBMS and returns the current
     * value of sequence used for autogenerated keys
     *
     * @param sequence name of the DB sequence
     * @return current value of a sequence
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int getCurrSeqVal(String sequence) throws SQLException {
    Statement stmt = this._connection.createStatement ();
 
    ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
    if (rs.next())
       return rs.getInt(1);
    return -1;
    }
 
    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup(){
       try{
          if (this._connection != null){
             this._connection.close ();
          }//end if
       }catch (SQLException e){
          // ignored.
       }//end try
    }//end cleanup
 
    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
    public static void main (String[] args) {
       if (args.length != 3) {
          System.err.println (
             "Usage: " +
             "java [-classpath <classpath>] " +
             PizzaStore.class.getName () +
             " <dbname> <port> <user>");
          return;
       }//end if
 
       Greeting();
       PizzaStore esql = null;
       try{
          // use postgres JDBC driver.
          Class.forName ("org.postgresql.Driver").newInstance ();
          // instantiate the PizzaStore object and creates a physical
          // connection.
          String dbname = args[0];
          String dbport = args[1];
          String user = args[2];
          esql = new PizzaStore (dbname, dbport, user, "");
 
          boolean keepon = true;
          while(keepon) {
             // These are sample SQL statements
             System.out.println("MAIN MENU");
             System.out.println("---------");
             System.out.println("1. Create user");
             System.out.println("2. Log in");
             System.out.println("9. < EXIT");
             String authorisedUser = null;
             switch (readChoice()){
                case 1: CreateUser(esql); break;
                case 2: authorisedUser = LogIn(esql); break;
                case 9: keepon = false; break;
                default : System.out.println("Unrecognized choice!"); break;
             }//end switch
             if (authorisedUser != null) {
               boolean usermenu = true;
               while(usermenu) {
                 System.out.println("MAIN MENU");
                 System.out.println("---------");
                 System.out.println("1. View Profile");
                 System.out.println("2. Update Profile");
                 System.out.println("3. View Menu");
                 System.out.println("4. Place Order"); //make sure user specifies which store
                 System.out.println("5. View Full Order ID History");
                 System.out.println("6. View Past 5 Order IDs");
                 System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                 System.out.println("8. View Stores"); 
 
                 //**the following functionalities should only be able to be used by drivers & managers**
                  System.out.println("9. Update Order Status");
                 //**the following functionalities should only be able to be used by managers**
                  System.out.println("10. Update Menu");
                  System.out.println("11. Update User");
                 System.out.println(".........................");
                 System.out.println("20. Log out");
                 switch (readChoice()){
                    case 1: viewProfile(esql); break;
                    case 2: updateProfile(esql); break;
                    case 3: viewMenu(esql); break;
                    case 4: placeOrder(esql); break;
                    case 5: viewAllOrders(esql); break;
                    case 6: viewRecentOrders(esql); break;
                    case 7: viewOrderInfo(esql); break;
                    case 8: viewStores(esql); break;
                    case 9: updateOrderStatus(esql); break;
                    case 10: updateMenu(esql); break;
                    case 11: updateUser(esql); break;
 
                    case 20: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                 }
               }
             }
          }//end while
       }catch(Exception e) {
          System.err.println (e.getMessage ());
       }finally{
          // make sure to cleanup the created table and close the connection.
          try{
             if(esql != null) {
                System.out.print("Disconnecting from database...");
                esql.cleanup ();
                System.out.println("Done\n\nBye !");
             }//end if
          }catch (Exception e) {
             // ignored.
          }//end try
       }//end try
    }//end main
 
    public static void Greeting(){
       System.out.println(
          "\n\n*******************************************************\n" +
          "              User Interface      	               \n" +
          "*******************************************************\n");
    }//end Greeting
 
    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
       int input;
       // returns only if a correct value is given.
       do {
          System.out.print("Please make your choice: ");
          try { // read the integer, parse it and break.
             input = Integer.parseInt(in.readLine());
             break;
          }catch (Exception e) {
             System.out.println("Your input is invalid!");
             continue;
          }//end try
       }while (true);
       return input;
    }//end readChoice
 
    /*
     * Creates a new user
     **/
    public static void CreateUser(PizzaStore esql) {
      try {
         System.out.println("\n=== USER REGISTRATION ===");
         System.out.print("Enter your login: ");
         String login = in.readLine().trim();
         System.out.print("Enter your password: ");
         String password = in.readLine().trim();
         System.out.print("Enter your phone number: ");
         String phoneNum = in.readLine().trim();
         
         if (login.isEmpty() || password.isEmpty() || phoneNum.isEmpty()) {
            System.out.println("Error: All fields must be filled out.");
            return;
         }

         if (!phoneNum.matches("\\d+")) {
            System.out.println("Error: Phone number must contain only numeric digits (0-9).");
            return;
         }

         String checkQuery = "SELECT login FROM Users WHERE login = '" + login.replace("'", "''") + "'";
         List<List<String>> result = esql.executeQueryAndReturnResult(checkQuery);
         if (!result.isEmpty()) {
            System.out.println("Error: This username already exists. Please choose another one.");
            return;
         }
         // Sets the default role for new users
         String role = "customer";
         String query = "INSERT INTO Users (login, password, role, favoriteItems, phoneNum) VALUES ('" 
                     + login + "', '" 
                     + password + "', '" 
                     + role + "', NULL, '" 
                     + phoneNum + "')";
         esql.executeUpdate(query);
         System.out.println("\nSuccess! User '" + login + "' has been registered as a customer.");
         System.out.println("You can now log in with your credentials.");
         
      } catch (SQLException e) {
         System.err.println("Database error during user creation: " + e.getMessage());
      } catch (Exception e) {
         System.err.println("Error processing your request: " + e.getMessage());
      }
    }
 
 
    /*
     * Check log in credentials for an existing user
     * @return User login or null is the user does not exist
     **/
      public static String LogIn(PizzaStore esql) {
         try {
            System.out.println("Login");
            System.out.println("-----");
            System.out.print("Enter username: ");
            String login = in.readLine();
            System.out.print("Enter password: ");
            String password = in.readLine();

            String query = String.format("SELECT * FROM Users WHERE login = '%s' AND password = '%s'", login, password);
            int userCount = esql.executeQuery(query);
            if (userCount == 1) {
               // Get the user's role for permission checks later
               String roleQuery = String.format("SELECT role FROM Users WHERE login = '%s'", login);
               List<List<String>> result = esql.executeQueryAndReturnResult(roleQuery);
               currentRole = result.get(0).get(0);
               currentUser = login;
               System.out.println("Login successful!");
               System.out.println("Welcome, " + login + "! (Role: " + currentRole + ")");
               return login;
            } else {
               System.out.println("Error: Invalid username or password.");
               return null;
            }
         } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
         }
      }//end
 
    /*
     * View user profile information
     **/
      public static void viewProfile(PizzaStore esql) {
         try {
            System.out.println("User Profile");
            System.out.println("-----------");

            if (currentUser == null) {
               System.out.println("Error: No user is currently logged in.");
               return;
            }
               
            String query = String.format("SELECT login, role, favoriteItems, phoneNum FROM Users WHERE login = '%s'", currentUser);
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            if (result.size() > 0) {
               List<String> user = result.get(0);
               System.out.println("Username: " + user.get(0));
               System.out.println("Role: " + user.get(1));
               System.out.println("Favorite Items: " + (user.get(2) == null || user.get(2).isEmpty() ? "None" : user.get(2)));
               System.out.println("Phone Number: " + user.get(3));
               } else {
                  System.out.println("Error: Could not retrieve user profile information.");
               }
         } catch (Exception e) {
            System.err.println("Error viewing profile: " + e.getMessage());
         }
      }
      
      /*
      * Update user profile information
      **/
      public static void updateProfile(PizzaStore esql) {
         try {
            if (currentUser == null) {
               System.out.println("Error: You must be logged in to update your profile.");
               return;
            }
            System.out.println("\nUpdate Profile");
            System.out.println("1. Password  2. Favorite Items  3. Phone Number  4. Go back");
               
            switch (readChoice()) {
               case 1: // Password
                  System.out.print("Current password: ");
                  String currentPassword = in.readLine();
                  String verifyQuery = String.format("SELECT login FROM Users WHERE login = '%s' AND password = '%s'", currentUser, currentPassword);
                  List<List<String>> result = esql.executeQueryAndReturnResult(verifyQuery);
                     
                  if (result.isEmpty()) {
                     System.out.println("Error: Incorrect password.");
                     return;
                  }
                     
                  System.out.print("New password (it has to be three characters or longer): ");
                  String newPassword = in.readLine();
                  if (newPassword.length() < 3) {
                     System.out.println("Error: Password too short.");
                     return;
                  }
                     
                  String updateQuery = String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", newPassword, currentUser);
                  esql.executeUpdate(updateQuery);
                  System.out.println("Password updated successfully.");
                  break;

                  case 2: // Favorite items
                     System.out.print("Enter your favorite item: ");
                     String favoriteItem = in.readLine();
                     // Check if the item exists
                     String checkItemQuery = String.format("SELECT itemName FROM Items WHERE itemName = '%s'", favoriteItem);
                     result = esql.executeQueryAndReturnResult(checkItemQuery);
                     if (result.isEmpty()) {
                        System.out.println("Error: Item not found in menu.");
                        return;
                     }

                     updateQuery = String.format("UPDATE Users SET favoriteItems = '%s' WHERE login = '%s'", 
                        favoriteItem, currentUser);
                     esql.executeUpdate(updateQuery);
                     System.out.println("Favorite item updated successfully.");
                     break;
                     
                  case 3: // Phone number
                     System.out.print("New phone number: ");
                     String phoneNum = in.readLine();
                     
                     if (phoneNum.length() < 10) {
                        System.out.println("Error: Enter a valid phone number that's 10 digits or longer.");
                        return;
                     }
                     // Update phone number
                     updateQuery = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s'", phoneNum, currentUser);
                     esql.executeUpdate(updateQuery);
                     System.out.println("Phone number updated successfully.");
                     break;
                     
                  case 4: // Go back
                     return;
                  default:
                     System.out.println("Not a valid choice.");
               }
         } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
         }
      }
    
    /*
     * View the restaurant menu with filtering and sorting options
     **/
      public static void viewMenu(PizzaStore esql) {
         try {
               System.out.println("Menu Viewing Options");
               System.out.println("-------------------");
               System.out.println("1. View full menu");
               System.out.println("2. Filter by item type");
               System.out.println("3. Filter by price range");
               System.out.println("4. Sort by price (low to high)");
               System.out.println("5. Sort by price (high to low)");
               System.out.println("6. Go back!");
               
               switch (readChoice()) {
                  case 1: 
                     String query = "SELECT itemName, typeOfItem, price, description FROM Items ORDER BY typeOfItem, itemName";
                     System.out.println("\n===== FULL MENU =====");
                     esql.executeQueryAndPrintResult(query);
                     break;

                  case 2: // Filter by item type
                     System.out.println("Available item types:");
                     // Get all distinct item types - show them exactly as stored
                     String typesQuery = "SELECT DISTINCT typeOfItem FROM Items ORDER BY typeOfItem";
                     esql.executeQueryAndPrintResult(typesQuery);
                     System.out.print("Enter type to filter by: ");
                     String type = in.readLine();
                     String filteredQuery = String.format(
                        "SELECT itemName, typeOfItem, price, description FROM Items " +
                        "WHERE TRIM(LOWER(typeOfItem)) LIKE LOWER('%%%s%%') " +
                        "ORDER BY itemName", 
                        type);
                        
                     System.out.println("\n===== FILTERED MENU BY TYPE =====");
                     int count = esql.executeQueryAndPrintResult(filteredQuery);
                     if (count == 0) {
                        System.out.println("No items found with the specified type.");
                        // Additional debugging to show what types exist
                        //System.out.println("\nDebug - All existing types:");
                        //esql.executeQueryAndPrintResult("SELECT DISTINCT typeOfItem FROM Items");
                     }
                     break;
                     
                  case 3: // Filter by price range
                     System.out.print("Enter minimum price: ");
                     float minPrice = Float.parseFloat(in.readLine());
                     System.out.print("Enter maximum price: ");
                     float maxPrice = Float.parseFloat(in.readLine());
                     String priceQuery = String.format("SELECT itemName, typeOfItem, price, description FROM Items WHERE price >= %.2f AND price <= %.2f ORDER BY price", minPrice, maxPrice);
                     System.out.println("\n===== FILTERED MENU BY PRICE RANGE =====");
                     count = esql.executeQueryAndPrintResult(priceQuery);
                     if (count == 0) {
                        System.out.println("No items found in the specified price range.");
                     }
                     break;
                     
                  case 4: // Sort by price (low to high)
                     String ascendingQuery = "SELECT itemName, typeOfItem, price, description FROM Items ORDER BY price ASC, itemName";
                     System.out.println("\n===== MENU SORTED BY PRICE (LOW TO HIGH) =====");
                     esql.executeQueryAndPrintResult(ascendingQuery);
                     break;
                     
                  case 5: // Sort by price (high to low)
                     String descendingQuery = "SELECT itemName, typeOfItem, price, description FROM Items ORDER BY price DESC, itemName";
                     System.out.println("\n===== MENU SORTED BY PRICE (HIGH TO LOW) =====");
                     esql.executeQueryAndPrintResult(descendingQuery);
                     break;
                     
                  case 6: // Go back
                     return;
                     
                  default:
                     System.out.println("Not a valid choice!");
                     break;
               }
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
      }
      
      /*
      * Place a new food order
      **/
      public static void placeOrder(PizzaStore esql) {
         try {
            if (currentUser == null) {
               System.out.println("Error: You must be logged in to place an order.");
               return;
            }

            System.out.println("Place New Order");
            System.out.println("--------------");
            System.out.println("Available stores:");
            String storeQuery = "SELECT storeID, address, city, state, isOpen FROM Store";
            esql.executeQueryAndPrintResult(storeQuery);
            System.out.print("Enter the store ID you want to order from: ");
            int storeID = Integer.parseInt(in.readLine());
            
            // Verify if the store exists
            String storeCheckQuery = String.format("SELECT storeID, isOpen FROM Store WHERE storeID = %d", storeID);
            List<List<String>> storeResult = esql.executeQueryAndReturnResult(storeCheckQuery);
            if (storeResult.isEmpty()) {
               System.out.println("Error: Invalid store selection.");
               return;
            }
            
            String isOpenStatus = storeResult.get(0).get(1);
            System.out.println("Store open status: " + isOpenStatus);
            if (isOpenStatus.equalsIgnoreCase("false") || isOpenStatus.equalsIgnoreCase("no") || isOpenStatus.equalsIgnoreCase("closed") || isOpenStatus.equals("0")) {
               System.out.println("WARNING: This store appears to be closed. Do you still want to place an order? Type yes or no.");
               String userChoice = in.readLine();
               if (!userChoice.equalsIgnoreCase("yes")) {
                  System.out.println("Order cancelled.");
                  return;
               }
            }
            
            int orderID = 0; // Default if no orders exist
            String orderIDQuery = "SELECT MAX(orderID) FROM FoodOrder";
            List<List<String>> result = esql.executeQueryAndReturnResult(orderIDQuery);
            
            if (result.size() > 0 && result.get(0).get(0) != null) {
               orderID = Integer.parseInt(result.get(0).get(0)) + 1;
            }
            
            float totalPrice = 0.0f;
            ArrayList<String> orderedItems = new ArrayList<>();
            ArrayList<Integer> itemQuantities = new ArrayList<>();
            boolean addingItems = true;
            while (addingItems) {
               // Display menu
               System.out.println("\nMenu:");
               String menuQuery = "SELECT itemName, price FROM Items ORDER BY itemName";
               esql.executeQueryAndPrintResult(menuQuery);
               
               System.out.print("Enter item name (or type 'done' to finish): ");
               String itemName = in.readLine();
               if (itemName.equalsIgnoreCase("done")) {
                  addingItems = false;
                  continue;
               }
               
               String itemCheckQuery = String.format("SELECT price FROM Items WHERE itemName = '%s'", itemName);
               List<List<String>> itemCheck = esql.executeQueryAndReturnResult(itemCheckQuery);
               if (itemCheck.isEmpty()) {
                  System.out.println("Error: Item not found on menu.");
                  continue;
               }
               
               System.out.print("Enter quantity: ");
               int quantity = Integer.parseInt(in.readLine());
               if (quantity <= 0) {
                  System.out.println("Error: Quantity must be greater than zero.");
                  continue;
               }
               
               orderedItems.add(itemName);
               itemQuantities.add(quantity);
               float itemPrice = Float.parseFloat(itemCheck.get(0).get(0));
               totalPrice += (itemPrice * quantity);
               System.out.println("Item added. Current total: $" + String.format("%.2f", totalPrice));
               System.out.print("Add another item? Type yes or no: ");
               String anotherUserItem = in.readLine();
               if (!anotherUserItem.equalsIgnoreCase("yes")) {
                  addingItems = false;
               }
            }
            
            if (orderedItems.isEmpty()) {
               System.out.println("Order cancelled - no items selected.");
               return;
            }
            
            java.util.Date date = new java.util.Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedTimestamp = sdf.format(timestamp);
            String orderQuery = String.format(
               "INSERT INTO FoodOrder (orderID, login, storeID, totalPrice, orderTimestamp, orderStatus) " +
               "VALUES (%d, '%s', %d, %.2f, '%s', 'Placed')",
               orderID, currentUser, storeID, totalPrice, formattedTimestamp);
            esql.executeUpdate(orderQuery);
            
            // Add items to order
            for (int i = 0; i < orderedItems.size(); i++) {
               String itemOrderQuery = String.format(
                  "INSERT INTO ItemsInOrder (orderID, itemName, quantity) VALUES (%d, '%s', %d)",
                  orderID, orderedItems.get(i), itemQuantities.get(i));
               esql.executeUpdate(itemOrderQuery);
            }
            System.out.println("\nOrder placed successfully!");
            System.out.println("Order ID: " + orderID);
            System.out.println("Total: $" + String.format("%.2f", totalPrice));
            System.out.println("Status: Placed");
            
         } catch (Exception e) {
            System.err.println("Error placing order: " + e.getMessage());
            // Print the complete stack trace for debugging
            e.printStackTrace();
         }
      }

      /*
      * View all order history for the logged-in user
      **/
      public static void viewAllOrders(PizzaStore esql) {
         try {
            System.out.println("Order History");
            System.out.println("------------");
            String query;

            if (currentRole.trim().equalsIgnoreCase("Manager") || currentRole.trim().equalsIgnoreCase("Driver")) {
               System.out.println("1. View all orders in the system");
               System.out.println("2. View only my orders");
               System.out.print("Enter choice: ");

               int choice = Integer.parseInt(in.readLine());
               if (choice == 1) {
                  query = "SELECT orderID, login, storeID, totalPrice, orderTimestamp, orderStatus FROM FoodOrder ORDER BY orderTimestamp DESC";
                  System.out.println("\n===== ALL ORDERS IN SYSTEM =====");
               } else {
                  query = String.format("SELECT orderID, storeID, totalPrice, orderTimestamp, orderStatus FROM FoodOrder WHERE login = '%s' ORDER BY orderTimestamp DESC", currentUser);
                  System.out.println("\n===== YOUR ORDERS =====");
               }
               } else {
                  // Customers can only see their own orders
                  query = String.format("SELECT orderID, storeID, totalPrice, orderTimestamp, orderStatus FROM FoodOrder WHERE login = '%s' ORDER BY orderTimestamp DESC", currentUser);
                  System.out.println("\n===== YOUR ORDERS =====");
               }
               
               int result = esql.executeQueryAndPrintResult(query);
               if (result == 0) {
                  System.out.println("No orders found.");
               } else {
                  System.out.println("\nTotal orders: " + result);
               }
            } 
            catch (Exception e) {
               System.err.println(e.getMessage());
            }
      }

       public static void viewRecentOrders(PizzaStore esql) {
         try {
            if (currentUser == null) {
               System.out.print("Error: You must be logged in to view recent orders.");
               return;
            }
            System.out.println("\n===== Your 5 Most recent orders =====");
            String query = String.format(
               "SELECT orderID, storeID, totalPrice, orderTimestamp, orderStatus " +
               "FROM FoodOrder WHERE login = '%s' ORDER BY orderTimestamp DESC LIMIT 5",
               currentUser
            );

            int resultCount = esql.executeQueryAndPrintResult(query);
            if (resultCount == 0) {
               System.out.println("No recent orders found.");
            }
         }
         catch (Exception e) {
               System.err.println("Error retrieving recent orders: " + e.getMessage());
         }
       }

public static void viewOrderInfo(PizzaStore esql) {
    try {
        if (currentUser == null) {
            System.out.println("Error: You must be logged in to view order information.");
            return;
        }
        
        //System.out.println("Debug - Current user: " + currentUser);
        //System.out.println("Debug - Current role: '" + currentRole + "'");
        
        System.out.print("Enter the Order ID to look up: ");
        int orderID = Integer.parseInt(in.readLine());
        
        // First check if the order exists at all
        String checkOrderQuery = String.format(
            "SELECT COUNT(*) FROM FoodOrder WHERE orderID = %d", orderID);
        int orderCount = Integer.parseInt(esql.executeQueryAndReturnResult(checkOrderQuery).get(0).get(0));
        
        if (orderCount == 0) {
            System.out.println("Error: Order ID " + orderID + " does not exist in the database.");
            return;
        }
        
        String query;
        // Trim the role to remove any possible whitespace
        String role = currentRole.trim();
        
        if (role.equalsIgnoreCase("manager") || role.equalsIgnoreCase("driver")) {
            // Managers & Drivers can see all orders
            query = String.format(
                "SELECT orderTimestamp, totalPrice, orderStatus FROM FoodOrder WHERE orderID = %d", 
                orderID);
            //System.out.println("Debug - Using manager/driver query");
        } else {
            // Customers can only see their own orders
            query = String.format(
                "SELECT orderTimestamp, totalPrice, orderStatus FROM FoodOrder WHERE orderID = %d AND login = '%s'", 
                orderID, currentUser);
            //System.out.println("Debug - Using customer query");
        }
        
        //System.out.println("Debug - Query: " + query);
        
        List<List<String>> orderDetails = esql.executeQueryAndReturnResult(query);
        
        if (orderDetails.isEmpty()) {
            if (role.equalsIgnoreCase("customer")) {
                System.out.println("Error: You do not have permission to view this order.");
            } else {
                System.out.println("Error: Could not retrieve order details. Please contact technical support.");
            }
            return;
        }
        
        // Display Order Info
        List<String> order = orderDetails.get(0);
        System.out.println("\n===== Order Details =====");
        System.out.println("Timestamp: " + order.get(0));
        System.out.println("Total Price: $" + order.get(1));
        System.out.println("Status: " + order.get(2).trim()); // Trim to remove padding on char fields
        
        // Retrieve order items
        String itemsQuery = String.format(
            "SELECT itemName, quantity FROM ItemsInOrder WHERE orderID = %d", 
            orderID);
        
        System.out.println("\n===== Order Items =====");
        int itemCount = esql.executeQueryAndPrintResult(itemsQuery);
        
        if (itemCount == 0) {
            System.out.println("No items found for this order.");
        }
    } catch (NumberFormatException e) {
        System.err.println("Error: Invalid order ID format. Please enter a numeric value.");
    } catch (Exception e) {
        System.err.println("Error retrieving order information: " + e.getMessage());
        e.printStackTrace(); // Print full stack trace for debugging
    }
}

   

       // Shows all store information
      public static void viewStores(PizzaStore esql) {
         try {
            System.out.println("\n===== STORES =====");
            String query = "SELECT * FROM Store ORDER BY storeID";
            int count = esql.executeQueryAndPrintResult(query);
            
            if (count == 0) System.out.println("No stores found.");
         } catch (Exception e) {
            System.err.println(e.getMessage());
         }
      }

      // Updates an order's status (drivers & managers only)
      public static void updateOrderStatus(PizzaStore esql) {
    try {
        if (currentUser == null) {
            System.out.println("You must be logged in to update order status.");
            return;
        }

        // Fixed permission check - only allows managers and drivers
        if (currentRole.trim().equalsIgnoreCase("customer")) {
            System.out.println("Permission denied. Only managers and drivers can update order status.");
            return;
        }

        System.out.println("\nCurrent orders:");
        esql.executeQueryAndPrintResult("SELECT orderID, login, orderStatus FROM FoodOrder ORDER BY orderTimestamp DESC LIMIT 10");

        System.out.print("Enter order ID: ");
        int orderID = Integer.parseInt(in.readLine());

        System.out.println("Status options: 1-Placed, 2-Preparing, 3-Ready, 4-Delivering, 5-Delivered");
        System.out.print("New status (1-5): ");
        int choice = Integer.parseInt(in.readLine());

        String[] statuses = {"Placed", "Preparing", "Ready", "Delivering", "Delivered"};

        if (choice < 1 || choice > 5) {
            System.out.println("Invalid status.");
            return;
        }

        String query = String.format("UPDATE FoodOrder SET orderStatus = '%s' WHERE orderID = %d", 
                                    statuses[choice-1], orderID);

        // Changed this line - don't try to capture a return value
        esql.executeUpdate(query);

        // Simply report success since we can't check the number of rows affected
        System.out.println("Status updated successfully.");
    } catch (Exception e) {
        System.err.println("Error updating order status: " + e.getMessage());
    }
}

      // Updates menu items (managers only)
      public static void updateMenu(PizzaStore esql) {
         try {
            if (currentUser == null || !currentRole.trim().equalsIgnoreCase("manager")) {
               System.out.println("Permission denied.");
               return;
            }
            
            System.out.println("1. Add item");
            System.out.println("2. Update item");
            System.out.println("3. Delete item");
            
            switch(readChoice()) {
               case 1: // Add
                  System.out.print("Name: ");
                  String name = in.readLine();
                  System.out.print("Type: ");
                  String type = in.readLine();
                  System.out.print("Ingredients: ");
                  String ingredients = in.readLine();
                  System.out.print("Price: ");
                  float price = Float.parseFloat(in.readLine());
                  System.out.print("Description: ");
                  String desc = in.readLine();
                  
                  String query = String.format("INSERT INTO Items VALUES ('%s', '%s', '%s', %f, '%s')", 
                                          name, ingredients, type, price, desc);
                  esql.executeUpdate(query);
                  System.out.println("Item added.");
                  break;
                  
               case 2: // Update
                  System.out.println("Current menu:");
                  esql.executeQueryAndPrintResult("SELECT itemName, price FROM Items");
                  
                  System.out.print("Item to update: ");
                  String item = in.readLine();
                  System.out.print("New price: ");
                  float newPrice = Float.parseFloat(in.readLine());
                  
                  esql.executeUpdate(String.format("UPDATE Items SET price = %f WHERE itemName = '%s'", newPrice, item));
                  System.out.println("Price updated.");
                  break;
                  
               case 3: // Delete
                  System.out.println("Current menu:");
                  esql.executeQueryAndPrintResult("SELECT itemName FROM Items");
                  
                  System.out.print("Item to delete: ");
                  String delItem = in.readLine();
                  
                  System.out.print("Confirm delete Type yes or no: ");
                  if (in.readLine().equalsIgnoreCase("yes")) {
                     esql.executeUpdate(String.format("DELETE FROM Items WHERE itemName = '%s'", delItem));
                     System.out.println("Item deleted.");
                  }
                  break;
            }
         } 
         catch (Exception e) {
            System.err.println(e.getMessage());
         }
      }

      // Updates user info (managers only)
      public static void updateUser(PizzaStore esql) {
         try {
            if (currentUser == null || !currentRole.trim().equalsIgnoreCase("manager")) {
               System.out.println("Permission denied.");
               return;
            }
            
            System.out.println("Users:");
            esql.executeQueryAndPrintResult("SELECT login, role FROM Users");
            
            System.out.print("Username to modify: ");
            String user = in.readLine();
            
            System.out.println("1. Change role");
            System.out.println("2. Reset password");
            
            switch(readChoice()) {
               case 1:
                  System.out.print("New role (customer/driver/manager): ");
                  String role = in.readLine();
                  
                  if (role.equalsIgnoreCase("customer") || 
                     role.equalsIgnoreCase("driver") || 
                     role.equalsIgnoreCase("manager")) {
                     
                     esql.executeUpdate(String.format("UPDATE Users SET role = '%s' WHERE login = '%s'", role, user));
                     System.out.println("Role updated.");
                  } else {
                     System.out.println("Invalid role.");
                  }
                  break;
                  
               case 2:
                  System.out.print("New password: ");
                  String pass = in.readLine();
                  
                  esql.executeUpdate(String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", pass, user));
                  System.out.println("Password reset.");
                  break;
            }
         } 
         catch (Exception e) {
            System.err.println(e.getMessage());
         }
      }
   }