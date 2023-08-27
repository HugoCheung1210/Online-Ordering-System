import java.sql.ResultSet;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class order {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    // 2.2: place a new order
        public static void add_order(Connection con){
        String c_uid = "";
        
        try{
            // UID input and check
            Boolean flagU=false;
            while(!flagU){
                System.out.println("Please input your user ID (UID): ");
                c_uid = in.readLine();
                String check_uid="select * FROM Customers WHERE UID=?;";
                PreparedStatement uidStmt=con.prepareStatement(check_uid);
                uidStmt.setString(1, c_uid);
                ResultSet uidrs=uidStmt.executeQuery();
                if(!uidrs.next()){
                    System.out.println("The customer ID does not exist.");
                }else{
                    flagU=true;
                }
            }
            // auto generate Order_date, OID, status 
            Statement stmt = con.createStatement();
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            String oid ="";
            String get_greatest_oid="SELECT OID FROM Orders ORDER BY OID DESC LIMIT 1";
            ResultSet rs=stmt.executeQuery(get_greatest_oid);
            int i_oid=0;
            while(rs.next()){
                String s_oid=rs.getString(1);
                i_oid=Integer.valueOf(s_oid);
                i_oid+=1;
                oid=String.format("%08d",i_oid);
            }
            String status = "ordered";
            // create order record
            String insertD="insert into Orders(OID,Order_date,Shipping_status) values(?,?,?)";
            PreparedStatement insertStmt=con.prepareStatement(insertD);
            insertStmt.setString(1, oid);
            insertStmt.setDate(2, sqlDate);
            insertStmt.setString(3, status);
            insertStmt.executeUpdate();
            // create place record
            insertD="insert into Place(OID,UID) values(?,?)";
            insertStmt=con.prepareStatement(insertD);
            insertStmt.setString(1, oid);
            insertStmt.setString(2, c_uid);
            insertStmt.executeUpdate();
            // create contain record: OID,ISBN,Order_quantity
            Boolean flag=false;
            String isbn="";
            String quatity="";
            String choice="";
            int q=0;
            while(!flag){
                System.out.println("Please input book ISBN: ");
                isbn=in.readLine();
                //System.out.println("isbn input "+isbn);
                /* need to check whether the book exists */
                String checkBook="SELECT * FROM Books WHERE ISBN=?;";
                PreparedStatement checkB=con.prepareStatement(checkBook);
                checkB.setString(1, isbn);
                ResultSet checkrs=checkB.executeQuery();
                if(!checkrs.next()){
                    System.out.println("Book with ISBN as "+isbn+" is not exist.");
                }else{
                    Boolean flag2=false;// specify the quantity of book isbn
                    while(!flag2){
                        System.out.println("Please input book quantity: ");
                        quatity=in.readLine();
                        try{
                            q=Integer.parseInt(quatity);
                        }catch(Exception e){
                         continue;
                        }
                        if(q<=0){
                            System.out.println("Quantity of book must be positive.");
                        }else{
                            String checkQuantity="SELECT Inventory_Quantity FROM Books WHERE ISBN=?;";
                            checkB=con.prepareStatement(checkQuantity);
                            checkB.setString(1, isbn);
                            ResultSet quantityrs=checkB.executeQuery();
                            while(quantityrs.next()){
                                int inventory=quantityrs.getInt(1);
                                if(q>inventory){// no enough inventory
                                    System.out.println("Sorry, there is only "+inventory+" books in our Bookstore.");
                                }else{
                                    //create contain record: OID,ISBN,Order_quantity
                                    insertD="insert into Contain(OID,ISBN,Order_quantity) values(?,?,?)";
                                    insertStmt=con.prepareStatement(insertD);
                                    insertStmt.setString(1, oid);
                                    insertStmt.setString(2, isbn);
                                    insertStmt.setInt(3, q);
                                    insertStmt.executeUpdate();
                                    // update book inventory
                                    insertD="update Books set Inventory_Quantity=? where ISBN=?";
                                    insertStmt=con.prepareStatement(insertD);
                                    insertStmt.setInt(1, inventory-q);
                                    insertStmt.setString(2, isbn);
                                    insertStmt.executeUpdate();
                                    flag2=true;

                                    System.out.println("End adding books? [y/n]");
                                    choice=in.readLine();
                                    if(choice.charAt(0)=='y'){
                                        flag=true;
                                        System.out.println("Your order has been placed.");
                                    }
                                }
                            }                          
                        }
                    }
                }
                
            } 
        }catch(Exception e){
            System.out.println("error when adding order.");
        }
    }

    // 2.3: customer: search order by UID
    public static void customer_order(Connection con)throws SQLException {
        String user = "-1";
        Integer len = 0;
        while(true){
            try {
                System.out.print(">>>Please enter User ID for checking history orders:");  
                user= in.readLine();
                Integer intValue = Integer.parseInt(user);

                String check = "SELECT * from Customers WHERE UID = '"+user+"'" ; 
                Statement stmt=con.createStatement();
                ResultSet r=stmt.executeQuery(check);
                if (r.next()==false){
                    System.out.println("The User ID is invalid");
                }
                else{
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a numerical value.");
            }
            catch (Exception e) {
                System.out.println("Error when entering value");
            }
        }
        
        String ORDER_QUERY = "SELECT * from Orders WHERE OID IN (SELECT OID from Place WHERE UID = '"+user+"')" ; 
        Statement stmt1=con.createStatement();
        ResultSet rs=stmt1.executeQuery(ORDER_QUERY);
        if (rs.next()==false){
            System.out.println("No result is found");
        }
        else {
            System.out.println("Order ID: "+rs.getString(1));
            System.out.println("Order Date: "+rs.getString(2));
            System.out.println("Shipping Staus: "+rs.getString(3));
            while(rs.next())  {
                System.out.println("------------------------------");// added
                System.out.println("Order ID: "+rs.getString(1));
                System.out.println("Order Date: "+rs.getString(2));
                System.out.println("Shipping Staus: "+rs.getString(3));
            }
        }
    }

    // 3.1: bookstore: change shipping status
    public static void update_order(Connection con)throws SQLException{
        String order = "-1";
        Integer len = 0;
        String input = "-1";
        while(true){
            try {
                System.out.print(">>>Please enter the ID of the order you would like to update:");
                order= in.readLine();
                len = order.length();
                Integer intValue = Integer.parseInt(order);
                input = Integer.toString(intValue);
                for (len = input.length();len<8;len++){
                    input = "0"+input;
                }

                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a numerical value.");
            }
            catch (Exception e) {
                System.out.println("Error when entering value");
            }
        }

        String UPDATE_QUERY = "SELECT * from Orders WHERE OID = '"+input+"'";
        Statement stmt=con.createStatement();
        ResultSet r=stmt.executeQuery(UPDATE_QUERY);
        String cur_status = "-1";
        if (r.next()==false){
            System.out.println("This order ID cannot be found.");
            return;
        }
        else{
            System.out.println("The current shipping status for this order is: "+r.getString(3));
            cur_status = r.getString(3);
            if (cur_status.equals("received")){
                System.out.println("Notice that the shipping status is already received. Please update another order.");
                return;
            }
        }

        String update = "-1";
        while (true){
            System.out.println(">>>Please enter the update for the shipping status or the command (quit) to go back to the last page:");
            try{
                update = in.readLine();
            }
            catch (Exception e) {
                System.out.println("Error when entering value");
            }
            if (update.equals("quit")){
                System.out.println("Quitting...");
                return;
            }

            if (!(update.equals("received") || update.equals("shipped") || update.equals("ordered"))){
                System.out.println("Please enter a valid update.");
            }
            
            else if (cur_status.equals(update)){
                System.out.println("The shipping status is already "+cur_status);
            }
            
            else if (cur_status.equals("shipped") & update.equals("ordered")){
                System.out.println("The shipping status is already shipped");
            }
            // Quiting command will be implemented later
            else {
                break;
            }
        }
        String UPDATE = "UPDATE Orders SET Shipping_status = '" + update + "' WHERE OID = '"+input+"'";
        Statement stmt1=con.createStatement();
        int rs=stmt1.executeUpdate(UPDATE);
        System.out.println("Successfully updated the shipping status to "+ update + ".");
    }
    
    // 3.2: bookstore: list order by shipping status
    public static void check_order(Connection con)throws SQLException{
        /* 此处定义了7种查询：1.ordered; 2.shipped, 3.received, 4.Not ordered, 5.Not shipped, 6.Not received, 7.All.
         * 每种查询结果均先按照Shipping_Status以'ordered''shipped''received'的顺序排列，然后按照时间先后排列。
        */
        String[] QueryStr = new String[]{
            "SELECT * FROM orders WHERE Shipping_Status = 'ordered' ORDER BY Order_date ASC;",
            "SELECT * FROM orders WHERE Shipping_Status = 'shipped' ORDER BY Order_date ASC;",
            "SELECT * FROM orders WHERE Shipping_Status = 'received' ORDER BY Order_date ASC;",
            "SELECT * FROM orders WHERE Shipping_Status != 'ordered' ORDER BY CASE WHEN Shipping_Status = 'shipped' THEN 0 ELSE 1 END, Order_date ASC;",
            "SELECT * FROM orders WHERE Shipping_Status != 'shipped' ORDER BY CASE WHEN Shipping_Status = 'ordered' THEN 0 ELSE 1 END, Order_date ASC;",
            "SELECT * FROM orders WHERE Shipping_Status != 'received' ORDER BY CASE WHEN Shipping_Status = 'ordered' THEN 0 ELSE 1 END, Order_date ASC;",
            "SELECT * FROM orders ORDER BY CASE WHEN Shipping_Status = 'ordered' THEN 0 WHEN Shipping_Status = 'shipped' THEN 1 ELSE 2 END, Order_date ASC;"};
        
        /*输入查询的种类。*/
        System.out.print("Check by:\n"+
                         "  1.ordered      2.shipped      3.received,\n"+
                         "  4.Not ordered  5.Not shipped  6.Not received\n"+
                         "  7.All\n");  
        int Type = 0;
        while (true){
            try{
                System.out.print(">>>Please Enter number 1-7 :");
                Scanner scan = new Scanner(System.in);
                    Type = scan.nextInt();
                
                
                if (Type>=1 && Type<=7){
                    break;
                }
                else{
                    System.out.print("Please enter number from 1-7 for Order Query.\n");
                }
            }
            catch(InputMismatchException e){
                System.out.print("Please enter number from 1-7 for Order Query.");
            }
            catch (Exception e) {
                System.out.println("Error when entering value.");
            }
        }

        /*输出查询结果。*/
        try{
            String ORDER_QUERY = QueryStr[Type-1];
            Statement stmt=con.createStatement();
            ResultSet r=stmt.executeQuery(ORDER_QUERY);
            if (r.next()==false){
                System.out.println("No result is found.");
            }
            else{
                int oid = r.getInt("OID");
                String orderDate = r.getString("Order_date");
                String shippingStatus = r.getString("Shipping_Status");

                System.out.println("OID: "+oid);
                System.out.println("Order Date: "+orderDate);
                System.out.println("Shipping status: "+shippingStatus);
                //System.out.printf("| %3d | %10s | %14s |\n", oid, orderDate, shippingStatus);
                while(r.next())  {
                    System.out.println("------------------------------");// added
                    oid = r.getInt("OID");
                    orderDate = r.getString("Order_date");
                    shippingStatus = r.getString("Shipping_Status");
                    System.out.println("OID: "+oid);
                    System.out.println("Order Date: "+orderDate);
                    System.out.println("Shipping status: "+shippingStatus);
                    //System.out.printf("| %3d | %10s | %14s |\n", oid, orderDate, shippingStatus);
                }
                //System.out.println("+-----+------------+----------------+");
            }
        }
        catch (Exception e) {
            System.out.println("Error when returning order query");
        }

    }
    
}
