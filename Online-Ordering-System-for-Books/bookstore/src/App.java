import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;     


public class App {
    
    private static boolean leave = false;
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static String username="root";
    private static String password="vera2104";

    //--- create database if bookstore not exists.
    public static void initDatabase() throws SQLException{
        String initDbQuery = "CREATE DATABASE IF NOT EXISTS Bookstore";
        Statement st = null;
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", username, password);
        st = con.createStatement();
        st.execute(initDbQuery);
    }

    // initialize the tables and data in bookstore db.
    public static void loadData(Connection con){
        //--- create tables if not exist
        try{
            //--- create customers table
            Statement CTst = con.createStatement();
            String createCSql="CREATE TABLE IF NOT EXISTS Customers("
            +"UID VARCHAR(10),"
            +"Name VARCHAR(50) NOT NULL,"
            +"Address VARCHAR(200) NOT NULL,"
            +"PRIMARY KEY(UID))";
            CTst.executeUpdate(createCSql);
            //--- create orders table
            String createOSql="CREATE TABLE IF NOT EXISTS Orders("
            //+"id_auto INT NOT NULL AUTO_INCREMENT primary key,"
            //+"OID AS (RIGHT('00000000'+CAST(id_auto AS VARCHAR(8)),8)),"
            +"OID VARCHAR(8) NOT NULL,"
            +"Order_date DATE NOT NULL,"
            +"Shipping_status VARCHAR(20) NOT NULL,"
            +"PRIMARY KEY(OID))";
            //+"PRIMARY KEY(id_auto))";
            CTst.executeUpdate(createOSql);
            //--- create books table
            String createBSql="CREATE TABLE IF NOT EXISTS Books("
            +"ISBN VARCHAR(13),"
            +"Title VARCHAR(100) NOT NULL,"
            +"Authors VARCHAR(50) NOT NULL,"
            +"Price INT NOT NULL,"
            +"Inventory_Quantity INT NOT NULL,"
            +"PRIMARY KEY(ISBN))";
            CTst.executeUpdate(createBSql);
            //--- create place table
            String createPSql="CREATE TABLE IF NOT EXISTS Place("
            +"OID VARCHAR(8) NOT NULL,"
            +"UID VARCHAR(10) NOT NULL,"
            +"PRIMARY KEY(OID,UID),"
            +"FOREIGN KEY(OID) REFERENCES Orders(OID) ON DELETE CASCADE,"
            +"FOREIGN KEY(UID) REFERENCES Customers(UID) ON DELETE CASCADE)";
            CTst.executeUpdate(createPSql);
            //--- create contain table
            String createConSql="CREATE TABLE IF NOT EXISTS Contain("
            +"OID VARCHAR(8) NOT NULL,"
            +"ISBN VARCHAR(13) NOT NULL,"
            +"Order_quantity INT NOT NULL,"
            +"FOREIGN KEY(OID) REFERENCES Orders(OID) ON DELETE CASCADE,"
            +"FOREIGN KEY(ISBN) REFERENCES Books(ISBN) ON DELETE CASCADE)";
            CTst.executeUpdate(createConSql);
        }catch(Exception e){
            System.out.println("error when creating tables.");
            System.out.println(e);
        }
        //--- clear data in all tables
        try{
            Statement Clearst = con.createStatement();
            String ClearSql="DELETE FROM Orders";
            Clearst.executeUpdate(ClearSql);
            ClearSql="DELETE FROM Books";
            Clearst.executeUpdate(ClearSql);
            ClearSql="DELETE FROM Customers";
            Clearst.executeUpdate(ClearSql);
            ClearSql="DELETE FROM Place";
            Clearst.executeUpdate(ClearSql);
            ClearSql="DELETE FROM Contain";
            Clearst.executeUpdate(ClearSql);
        }catch(Exception e){
            System.out.println("error when clearing data in tables.");
            System.out.println(e);
        }
        //--- load Customers data from csv
        try{
            String filePath="customers.csv";
            String loadCSql="insert into Customers(UID,Name,Address) values(?,?,?)";
            PreparedStatement loadCStmt=con.prepareStatement(loadCSql);
            BufferedReader lineReader1=new BufferedReader(new FileReader(filePath));
            String lineText1=null;
            lineReader1.readLine();

            while((lineText1=lineReader1.readLine())!=null){
               String[] data1=lineText1.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
               String UID=data1[0];
               String Name=data1[1];
               String Address=data1[2];
               loadCStmt.setString(1, UID);
               loadCStmt.setString(2, Name);
               loadCStmt.setString(3, Address);
               loadCStmt.executeUpdate();
            }
            lineReader1.close();
        }catch(Exception e){
            System.out.println("error when loading Customers data.");
            System.out.println(e);
        }
        //--- load Books data from csv
        try{
            String filePath="books.csv";
            String loadBSql="insert into Books(ISBN,Title,Authors,Price,Inventory_Quantity) values(?,?,?,?,?)";
            PreparedStatement loadBStmt=con.prepareStatement(loadBSql);
            BufferedReader lineReader2=new BufferedReader(new FileReader(filePath));
            String lineText2=null;
            lineReader2.readLine();

            while((lineText2=lineReader2.readLine())!=null){
               String[] data2=lineText2.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
               String ISBN=data2[0];
               String Title=data2[1];
               String Authors=data2[2];
               String Price=data2[3];
               String Inventory_Quantity=data2[4];

               loadBStmt.setString(1, ISBN);
               loadBStmt.setString(2, Title);
               loadBStmt.setString(3, Authors);
               loadBStmt.setInt(4,Integer.parseInt(Price));
               loadBStmt.setInt(5,Integer.parseInt(Inventory_Quantity));
               loadBStmt.executeUpdate();
            }
            lineReader2.close();

        }catch(Exception e){
            System.out.println("error when loading Books data.");
            System.out.println(e);
        }
        //--- load Orders data from csv
        try{
             String filePath="orders.csv";
             String loadDataSql="insert into Orders(OID,Order_date,Shipping_status) values(?,?,?)";
             PreparedStatement loadDataStmt=con.prepareStatement(loadDataSql);
             BufferedReader lineReader=new BufferedReader(new FileReader(filePath));
             String lineText=null;
             lineReader.readLine();

             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
             java.util.Date javaDate = null;

             while((lineText=lineReader.readLine())!=null){
                String[] data=lineText.split(",");
                String OID=data[0];
                String Order_date=data[1];
                String Shipping_status=data[2];

                loadDataStmt.setString(1, OID);
                javaDate=sdf.parse(Order_date);
                java.sql.Date mySQLDate = new java.sql.Date(javaDate.getTime());
                loadDataStmt.setDate(2, mySQLDate);
                loadDataStmt.setString(3, Shipping_status);
                loadDataStmt.executeUpdate();
             }
             lineReader.close();
        }catch(Exception e){
            System.out.println("error when loading Orders data.");
            System.out.println(e);
        }
        //--- load place data from csv
        try{
            String filePath="place.csv";
            String loadDataSql="insert into Place(OID,UID) values(?,?)";
            PreparedStatement loadDataStmt=con.prepareStatement(loadDataSql);
            BufferedReader lineReader=new BufferedReader(new FileReader(filePath));
            String lineText=null;
            lineReader.readLine();

            while((lineText=lineReader.readLine())!=null){
               String[] data=lineText.split(",");
               String OID=data[0];
               String UID=data[1];

               loadDataStmt.setString(1, OID);
               loadDataStmt.setString(2, UID);
               loadDataStmt.executeUpdate();
            }
            lineReader.close();
        }catch(Exception e){
           System.out.println("error when loading place data.");
           System.out.println(e);
        }
        // load contain data from csv
        try{
            String filePath="contain.csv";
            String loadDataSql="insert into Contain(OID,ISBN,Order_quantity) values(?,?,?)";
            PreparedStatement loadDataStmt=con.prepareStatement(loadDataSql);
            BufferedReader lineReader=new BufferedReader(new FileReader(filePath));
            String lineText=null;
            lineReader.readLine();

            while((lineText=lineReader.readLine())!=null){
               String[] data=lineText.split(",");
               String OID=data[0];
               String ISBN=data[1];
               String Order_quantity=data[2];

               loadDataStmt.setString(1, OID);
               loadDataStmt.setString(2, ISBN);
               loadDataStmt.setInt(3,Integer.parseInt(Order_quantity));
               loadDataStmt.executeUpdate();
            }
            lineReader.close();
        }catch(Exception e){
           System.out.println("error when loading contain data.");
           System.out.println(e);
        }

    }

    private static int get_record_number(Connection con,String dbName)throws SQLException{
        String sql="select count(*) from "+dbName;
        Statement st=con.createStatement();
        int count = 0;
        ResultSet rs = st.executeQuery(sql);
        rs.next();
        count = rs.getInt(1);
        return count;
    }
    
    private static void display_welcome(Connection con) throws SQLException {
        int books_num=App.get_record_number(con,"Books");
        int customers_num=App.get_record_number(con,"Customers");
        int orders_num=App.get_record_number(con,"Orders");
        System.out.println("===== Welcome to Book Ordering Management System =====");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  
        System.out.println("+ System date: "+dtf.format(now));
        System.out.println("+ Database Records: Books("+books_num+"), Customers("+customers_num+"), Orders("+orders_num+").");
        System.out.println("------------------------------------------------------");
        System.out.println("1.Database Initialization");
        System.out.println("2.Customer Operation");
        System.out.println("3.Bookstore Operation");
        System.out.println("4.Quit");
        System.out.println(">>>Please Enter Your Query: ");
    }

    private static void display_customer() {
        System.out.println("===== Customer Operation =====");
        System.out.println("------------------------------");
        System.out.println("1.Book search");
        System.out.println("2.Place order");
        System.out.println("3.Check order");
        System.out.println("4.Back to main menu");
        System.out.println(">>>Please Enter Your Query: ");
    }

    private static void display_store() {
        System.out.println("===== Bookstore Operation =====");
        System.out.println("-------------------------------");
        System.out.println("1.Update order");
        System.out.println("2.Check order");
        System.out.println("3.Book rank");
        System.out.println("4.Back to main menu");
        System.out.println(">>>Please Enter Your Query: ");
    }

    public static void main(String[] args) throws Exception {
        
        try{
            initDatabase();
            // connect to the mysql db
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Bookstore", username, password);

            loadData(con);
            while(!leave){
                display_welcome(con);
                String choice = "";
                int c = 0;
                while((choice=in.readLine())==null || choice.length()==0);
                try{
                    c=Integer.parseInt(choice);
                }catch(Exception e){
                    continue;
                }
                // choice not exist
                if(c>4 || c<1) continue;
                // choose 1.Database Initialization
                if(c == 1){
                    initDatabase();
                    loadData(con);
                    System.out.println("database initialization completed.");
                }else if(c == 2){ // choose 2.Customer Operation
                    Boolean flag2 = false;
                    while(!flag2){
                        display_customer();
                        while((choice=in.readLine())==null || choice.length()==0);
                        try{
                            c=Integer.parseInt(choice);
                        }catch(Exception e){
                            continue;
                        }
                        if(c>4 || c<1) continue;// choice not exist
                        if(c==1){//1.Book search
                            book.search_book(con);
                        }else if(c==2){//2.Place order
                            order.add_order(con);
                        }else if(c==3){//3.Check order
                            order.customer_order(con);
                        }else{//4.Back to main menu
                            flag2 = true;
                        }
                    }
                }else if(c == 3){ // choose 3.Bookstore Operation
                    Boolean flag3 = false;
                    while(!flag3){
                        display_store();
                        while((choice=in.readLine())==null || choice.length()==0);
                        try{
                            c=Integer.parseInt(choice);
                        }catch(Exception e){
                            continue;
                        }
                        if(c>4 || c<1) continue;// choice not exist
                        if(c==1){//1.update order
                            order.update_order(con);
                        }else if(c==2){//2.check order
                            order.check_order(con);
                        }else if(c==3){//3.book rank
                            book.rank_book(con);
                        }else{//4.Back to main menu
                            flag3 = true;
                        }
                    }
                }else{
                    leave = true;
                }                
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
