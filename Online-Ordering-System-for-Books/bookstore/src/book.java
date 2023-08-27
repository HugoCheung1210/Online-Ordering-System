import java.sql.ResultSet;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.*; 
import java.sql.SQLException;

public class book {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    // 2.1: book search
    public static void search_book(Connection con)throws SQLException{
        //Scanner sc= new Scanner(System.in);    //System.in is a standard input stream  
        System.out.print("Search by: \n 1. ISBN\n 2. Book Title\n 3. Author Name\n");  
        Integer method = 0;
        while (true){
            try{
                System.out.print(">>>Please Enter Your Query:");  
                method =Integer.parseInt(in.readLine());
                if (method>=1 & method<=3){
                    break;
                }
                else{
                    System.out.print("Please enter number from 1-3 for searching\n");
                }
            }
            catch(InputMismatchException e){
                System.out.print("Please enter number from 1-3 for searching");
            }
            catch (Exception e) {
                System.out.println("Error when entering value");
            }
        }


        if (method == 1){
            String a = "-1";
            try{
                while(true){
                    System.out.print(">>>Enter ISBN for Searching: ");  
                    a= in.readLine();
                    if (a.length()==0){
                        System.out.print("Please enter a valid input.\n"); 
                        continue;
                    }
                    break;
                }
                String BOOK_QUERY = "SELECT * from Books WHERE ISBN LIKE '"+a+"' "; 
                Statement stmt=con.createStatement();
                ResultSet r=stmt.executeQuery(BOOK_QUERY);
                if (r.next()==false){
                    System.out.println("No result is found");
                }
                else{
                    System.out.println("ISBN: "+r.getString(1));
                    System.out.println("Book Title: "+r.getString(2));
                    System.out.println("Authors: "+r.getString(3));
                    System.out.println("Price: $"+r.getString(4));
                    System.out.println("Inventory Quantity: "+r.getString(5));
                    while(r.next())  {
                        System.out.println("------------------------------");// added
                        System.out.println("ISBN: "+r.getString(1));
                        System.out.println("Book Title: "+r.getString(2));
                        System.out.println("Authors: "+r.getString(3));
                        System.out.println("Price: $"+r.getString(4));
                        System.out.println("Inventory Quantity: "+r.getString(5));
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Error when entering value");
            }
        }
       
        else if (method == 2){
            String a = "-1";
            try{
                while(true){
                    System.out.print(">>>Enter Book Title for Searching: ");  
                    a= in.readLine();
                    if (a.length()==0){
                        System.out.print("Please enter a valid input.\n"); 
                        continue;
                    }
                    break;
                }   
                 
                String BOOK_QUERY = "SELECT * from Books WHERE Title LIKE '%"+a+"%' "; 
                Statement stmt=con.createStatement();
                ResultSet r=stmt.executeQuery(BOOK_QUERY);
                if (r.next()==false){
                    System.out.println("No result is found");
                }
                else{
                    System.out.println("ISBN: "+r.getString(1));
                    System.out.println("Book Title: "+r.getString(2));
                    System.out.println("Authors: "+r.getString(3));
                    System.out.println("Price: $"+r.getString(4));
                    System.out.println("Inventory Quantity: "+r.getString(5));
                    while(r.next())  {
                        System.out.println("------------------------------");// added
                        System.out.println("ISBN: "+r.getString(1));
                        System.out.println("Book Title: "+r.getString(2));
                        System.out.println("Authors: "+r.getString(3));
                        System.out.println("Price: $"+r.getString(4));
                        System.out.println("Inventory Quantity: "+r.getString(5));
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Error when entering value");
            }   
         }
        else if (method == 3){
            String a = "-1";
            try{
                while(true){
                    System.out.print(">>>Enter Author Name for Searching: ");  
                    a= in.readLine();
                    if (a.length()==0){
                        System.out.print("Please enter a valid input.\n"); 
                        continue;
                    }
                    break;
                }

                String BOOK_QUERY = "SELECT * from Books WHERE Authors LIKE '%"+a+"%' "; 
                Statement stmt=con.createStatement();
                ResultSet r=stmt.executeQuery(BOOK_QUERY);
                //sc.close();

                if (r.next()==false){
                    System.out.println("No result is found");
                }
                else{
                    System.out.println("ISBN: "+r.getString(1));
                    System.out.println("Book Title: "+r.getString(2));
                    System.out.println("Authors: "+r.getString(3));
                    System.out.println("Price: $"+r.getString(4));
                    System.out.println("Inventory Quantity: "+r.getString(5));
                    while(r.next())  {
                        System.out.println("------------------------------");// added
                        System.out.println("ISBN: "+r.getString(1));
                        System.out.println("Book Title: "+r.getString(2));
                        System.out.println("Authors: "+r.getString(3));
                        System.out.println("Price: $"+r.getString(4));
                        System.out.println("Inventory Quantity: "+r.getString(5));
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Error when entering value");
            }
        }
    }
    // 3.3: book rank
    public static void rank_book(Connection con)throws SQLException{
        Integer N = -1;
        int TotalItems = -1;
        
        try{ /* 查询数据库books中的总条目数TotalItems。*/
            String BOOKS_COUNT_QUERY = "SELECT COUNT(*) AS COUNT FROM books;"; 
            Statement stmt=con.createStatement();
            ResultSet r1=stmt.executeQuery(BOOKS_COUNT_QUERY);
            r1.next();
            TotalItems = r1.getInt("COUNT");// error here
            //System.out.println(TotalItems);
                 
        }catch (Exception e) {
            System.out.println("Error when returning Total book Items");
        }

        /* 读取需要查询的条目数N。*/
        System.out.print("Search N most popular books:\n");
        while (true){
            try{
                System.out.print(">>>Please Enter N:");  
                Scanner scan = new Scanner(System.in);
                    N = scan.nextInt();
                
                
                if (N <= 0){
                    /* 如果输入的N小于或等于0，则输入不合法。*/
                    System.out.print("Please enter number larger than 0 for searching.\n");
                }
                else if ( N >= TotalItems){
                    /* 如果输入的N大于数据库books中的总条目数，则发出此信息，之后返回等于TotalItems数量的条目。*/
                    System.out.print("The N input is larger than total numbers of books. Will return " + Integer.toString(TotalItems) + " items.\n");
                    break;
                }
                else{
                    /* 正确读取了返回的条目数N。*/
                    break;
                }
            }
            catch(InputMismatchException e){
                System.out.print("Please enter number larger than 0 for searching");
            }
            catch (Exception e) {
                System.out.println("Error when entering value");
            }
        }

        /* 查询N条结果，并返回表格。*/
        try{
            String N_MOST_POPULAR_BOOKS_QUERY = 
                "SELECT ROW_NUMBER() OVER (ORDER BY t.total DESC) AS Ranking, books.* "+
                "FROM books "+
                "JOIN"+ 
                    "(	SELECT ISBN, SUM(Order_quantity) AS total "+
                        "FROM contain "+ 
                        "GROUP BY ISBN "+ 
                        "ORDER BY total DESC "+ 
                        "LIMIT "+ Integer.toString(N) +
                    ") AS t "+ 
                "ON books.ISBN = t.ISBN "+ 
                "ORDER BY t.total DESC"; 
            Statement stmt=con.createStatement();
            ResultSet r=stmt.executeQuery(N_MOST_POPULAR_BOOKS_QUERY);// error here
            if (r.next()==false){
                System.out.println("No result is found");
            }
            else{
                int ranking = r.getInt("Ranking");
                String ISBN = r.getString("ISBN");
                String title = r.getString("Title");
                String author =r.getString("Authors");
                int price = r.getInt("Price");
                int inventory_quantity = r.getInt("Inventory_Quantity");
                System.out.println("Ranking: "+ranking);
                System.out.println("ISBN: "+ISBN);
                System.out.println("Title: "+title);
                System.out.println("Authors: "+author);
                System.out.println("Price: "+price);
                System.out.println("Inventory Quantity: "+inventory_quantity);
                //System.out.printf("| %6d | %-15s | %-15s | %-15s | %5d | %19d |\n", ranking, ISBN, title, author, price, inventory_quantity);
                while(r.next())  {
                    System.out.println("------------------------------");// added
                    ranking = r.getInt("Ranking");
                    ISBN = r.getString("ISBN");
                    title = r.getString("Title");
                    author = r.getString("Authors");
                    price = r.getInt("Price");
                    inventory_quantity = r.getInt("Inventory_Quantity");
                    System.out.println("Ranking: "+ranking);
                    System.out.println("ISBN: "+ISBN);
                    System.out.println("Title: "+title);
                    System.out.println("Authors: "+author);
                    System.out.println("Price: "+price);
                    System.out.println("Inventory Quantity: "+inventory_quantity);
                    //System.out.printf("| %6d | %-15s | %-15s | %-15s | %5d | %19d |\n", ranking, ISBN, title, author, price, inventory_quantity);
                }
                //System.out.println("+--------+-----------------+-----------------+-----------------+-------+---------------------+");
            }
        }
        catch (Exception e) {
            System.out.println("Error when returning N most popular books");
        }
    }
}
