/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolf;

// This example is created by Seokyong Hong
// modified by Shrikanth N C to support MySQL(MariaDB)

// Relpace all $USER$ with your unity id and $PASSWORD$ with your 9 digit student id or updated password (if changed)

import java.sql.*;
import javax.swing.*;

public class Wolf {


    // Update your user info alone here
    private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/mravind"; // Using SERVICE_NAME

// Update your user and password info here!

    private static final String user = "mravind";
    private static final String password = "200314451";

    public static String getTableNames(){
        return JOptionPane.showInputDialog("Type table name for respective table\n" +
                "1. ARTICLEHAS\n" +
                "2. ARTICLES\n" +
                "3. AUTHORS\n" +
                "4. BOOK\n" +
                "5. BOOKHAS\n" +
                "6. CHAPTER\n" +
                "7. DISTRIBUTOR\n" +
                "8. DISTRIBUTORPAYS\n" +
                "9. EDITORS\n" +
                "10. ISSUE\n" +
                "11. ORDERS\n" +
                "12. OWES\n" +
                "13. PAYSAUTHOR\n" +
                "14. PAYSEDITOR\n" +
                "15. PUBLICATION\n" +
                "16. PUBLICATIONHAS\n" +
                "17. PUBLISHINGHOUSE\n" +
                "18. SENDS");
    }

    public static String getMainMenu(){
        return JOptionPane.showInputDialog("Input number for respective mainMenu \n"
                + "0. SELECT\n"
                + "1. INSERT\n"
                + "2. UPDATE\n"
                + "3. DELETE\n"
                + "4. GENERATE\n"
                + "5. Order Creation for Distributor\n"
                + "6. QUIT\n");
    }
    
    public static String getReportOption() {
        return JOptionPane.showInputDialog("Input number for respective report generation\n"
                + "1. Find book\n"
                + "2. Find article\n"
                + "3. Check balance\n"
                + "4. Number and total price of each publication bought per distributor per month\n"
                + "5. Total revenue of each publishing house\n"
                + "6. Total expenses of each publishing house\n"
                + "7. Total current number of distributors for each publishing house\n"
                + "8. Total revenue\n"
                + "9. Total payments to editors and authors\n");
    }

    public static void main(String[] args) {

        try {

            String mainMenu = "";
            String tableName = "";

            Class.forName("org.mariadb.jdbc.Driver");
            Connection connection = null;
            Statement statement = null;
            ResultSet result = null;
            if(mainMenu == "6")
                return;
            while (!mainMenu.equals("6")) {

                try {
                    connection = DriverManager.getConnection(jdbcURL, user, password);
                    statement = connection.createStatement();

                    mainMenu = getMainMenu();

                    if (Integer.parseInt(mainMenu) < 4) {
                        tableName = getTableNames();
                        result = statement.executeQuery("DESC " + tableName);
                        int count = 0;
                        while (result.next()) {
                            String name = result.getString("Field");
                            String type = result.getString("Type");
                            System.out.println(name + " " + type);
                            count = count + 1;
                        }
                        switch (mainMenu) {
                            case "1":
                                String insquery = JOptionPane.showInputDialog("Enter the new row details");
                                ResultSet success = statement.executeQuery("INSERT INTO " + tableName + " VALUES (" + insquery + ");");
                                System.out.println(success);
                                JOptionPane.showMessageDialog(null, "Inserted successfully");
                                break;
                            case "2":
                                String updquery = JOptionPane.showInputDialog("Finish the remaining \nUPDATE " + tableName + " SET ");
                                result = statement.executeQuery("UPDATE " + tableName + " SET " + updquery);
                                JOptionPane.showMessageDialog(null, "Updated successfully");
                                break;
                            case "3":
                                String colname = JOptionPane.showInputDialog("Enter the column name followed by the value to delete");
                                String vall = JOptionPane.showInputDialog("");
                                result = statement.executeQuery("DELETE FROM " + tableName + " WHERE " + colname + "= " + vall);
                                JOptionPane.showMessageDialog(null, "Deleted successfully");
                                System.out.print("DELETE FROM " + tableName + " WHERE " + colname + "= " + vall);
                                break;
                            case "0":
                                result = statement.executeQuery("DESC " + tableName);
                                while (result.next()) {
                                    String name = result.getString("Field");
                                    System.out.print(name + "\t\t");
                                }
                                System.out.print("\n");
                                result = statement.executeQuery("SELECT * FROM " + tableName);
                                while (result.next()) {
                                    for (int i = 1; i <= count; i++) {
                                        System.out.print(result.getString(i) + "\t\t");
                                    }
                                    System.out.println();
                                }
                        }
                    }
                    else if(Integer.parseInt(mainMenu) == 4) {
                        String reportOption;
                        reportOption = getReportOption();
                        if(reportOption.equals("1") || reportOption.equals("2")) {
                            String by = JOptionPane.showInputDialog("Input the attribute by which you want to select\n"
                                    + "1. TOPIC\n"
                                    + "2. DATE\n"
                                    + "3. AUTHOR");
                            
                            if(reportOption.equals("1")) {
                                switch(by) {
                                case "1": String value = JOptionPane.showInputDialog("Input the topic by which you want to select\n");
                                              result = statement.executeQuery("SELECT * FROM BOOK B WHERE B.PUBID = (SELECT P.PUBID FROM PUBLICATION P WHERE P.GENRE = '" + value + "');");
                                              ResultSetMetaData meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                                case "2": value = JOptionPane.showInputDialog("Input the date(yyyy/mm/dd) by which you want to select\n");
                                              result = statement.executeQuery("SELECT * FROM BOOK B WHERE B.PUBID = (SELECT PUBID FROM PUBLICATION P WHERE P.DATE = '" + value + "');");
                                              meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                                case "3": value = JOptionPane.showInputDialog("Input the author by which you want to select\n");
                                              result = statement.executeQuery("SELECT B.PUBID, B.ISBN, B.EDITIONNO FROM BOOK B, BOOKHAS BH, AUTHORS A WHERE B.PUBID = BH.PUBID "
                                                      + "AND BH.AUTHORSSN = A.AUTHORSSN AND A.NAME = '" + value + "';");
                                              meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                            }
                        }
                            else if(reportOption.equals("2")) {
                                switch(by) {
                                case "1": String value = JOptionPane.showInputDialog("Input the topic by which you want to select\n");
                                              result = statement.executeQuery("SELECT * FROM ARTICLES A WHERE A.PUBID = (SELECT P.PUBID FROM PUBLICATION P WHERE P.GENRE = '" + value + "');");
                                              ResultSetMetaData meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                                case "2": value = JOptionPane.showInputDialog("Input the date(yyyy/mm/dd) by which you want to select\n");
                                              result = statement.executeQuery("SELECT * FROM ARTICLES A WHERE A.PUBID = (SELECT PUBID FROM PUBLICATION P WHERE P.DATE = '" + value + "');");
                                              meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                                case "3": value = JOptionPane.showInputDialog("Input the author by which you want to select\n");
                                              result = statement.executeQuery("SELECT A.ARTICLEID, A.TITLE, A.TEXT FROM ARTICLES A, ARTICLEHAS AH, AUTHORS AU WHERE A.ARTICLEID = AH.ARTICLEID "
                                                      + "AND AH.AUTHORSSN = AU.AUTHORSSN AND AU.NAME = '" + value + "';");
                                              meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                            }
                        }
                    }
                        switch(reportOption) {
                            case "3": String value = JOptionPane.showInputDialog("Input the distributor ID\n");
                                      result = statement.executeQuery("SELECT DISTID, ROUND(SUM(BALANCE),2) AS BALANCE FROM OWES WHERE DISTID = " + value + ";");
                                      ResultSetMetaData meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                            case "4": result = statement.executeQuery("SELECT B.PUBID, B.DISTID, MONTH(A.ORDERDATE) AS PERMONTH, YEAR(A.ORDERDATE) AS PERYEAR, "
                                    + "ROUND(SUM(A.PRICE), 2) AS TOTALPRICE, SUM(NOOFCOPIES) AS NOOFCOPIES FROM ORDERS A, SENDS B WHERE A.ORDERID=B.ORDERID"
                                    + " GROUP BY B.PUBID, B.DISTID, MONTH(A.ORDERDATE), YEAR(A.ORDERDATE);");
                                            meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                            case "5": result = statement.executeQuery("SELECT P.PUBHOUSEID, MONTH(D.PAYDATE) AS MONTH, YEAR(D.PAYDATE) AS YEAR, "
                                    + "ROUND(SUM(AMOUNT), 2) AS REVENUE FROM DISTRIBUTORPAYS D, PUBLISHINGHOUSE P WHERE D.PUBHOUSEID = P.PUBHOUSEID "
                                    + "GROUP BY P.PUBHOUSEID, MONTH(D.PAYDATE), YEAR(D.PAYDATE)");
                                            meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                            case "6": result = statement.executeQuery("SELECT PUBHOUSEID,  MONTH, YEAR, ROUND(SUM(COST),2) AS EXPENSE " +
                                                "FROM " +
                                                "(SELECT P.PUBHOUSEID, ROUND(SUM(O.SHIPPINGCOST),2) AS COST, MONTH(O.ORDERDATE) AS MONTH, YEAR(O.ORDERDATE) AS YEAR " +
                                                "FROM PUBLICATION P, ORDERS O, SENDS S " +
                                                "WHERE O.ORDERID = S.ORDERID AND S.PUBID = P.PUBID " +
                                                "GROUP BY P.PUBHOUSEID, MONTH(O.ORDERDATE), YEAR(O.ORDERDATE) " +
                                                "UNION ALL " +
                                                "SELECT PUBHOUSEID, ROUND(SUM(AMOUNT),2) AS COST, MONTH(PAYDATE) AS MONTH, YEAR(PAYDATE) AS YEAR " +
                                                "FROM PAYSAUTHOR GROUP BY PUBHOUSEID, MONTH(PAYDATE), YEAR(PAYDATE) " +
                                                "UNION ALL " +
                                                "SELECT PUBHOUSEID, ROUND(SUM(AMOUNT),2) AS COST, MONTH(PAYDATE) AS MONTH, YEAR(PAYDATE) AS YEAR " +
                                                "FROM PAYSEDITOR GROUP BY PUBHOUSEID, MONTH(PAYDATE), YEAR(PAYDATE)) AS DER " +
                                                "GROUP BY DER.PUBHOUSEID, DER.MONTH, DER.YEAR;");
                                            meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                            case "7": result = statement.executeQuery("SELECT P.NAME, COUNT(*) AS NOOFDISTRIBUTORS FROM OWES O, PUBLISHINGHOUSE P WHERE "
                                    + "O.PUBHOUSEID = P.PUBHOUSEID GROUP BY P.NAME;");
                                            meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                              break;
                            case "8": String by = JOptionPane.showInputDialog("Input the attribute by which you want to calculate revenue for\n"
                                    + "1. CITY\n"
                                    + "2. DISTRIBUTOR\n"
                                    + "3. LOCATION");
                                    if(by.equals("1")) {
                                        result = statement.executeQuery("SELECT P.NAME, P.CITY, ROUND(SUM(D.AMOUNT),2) AS REVENUE FROM DISTRIBUTORPAYS D, PUBLISHINGHOUSE P "
                                                + "WHERE P.PUBHOUSEID = D.PUBHOUSEID GROUP BY P.NAME, P.CITY;");
                                              meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                    }
                                    else if(by.equals("2")) {
                                        result = statement.executeQuery("SELECT P.NAME, D.DISTID, ROUND(SUM(D.AMOUNT),2) AS REVENUE FROM DISTRIBUTORPAYS D, PUBLISHINGHOUSE P "
                                                + "WHERE P.PUBHOUSEID = D.PUBHOUSEID GROUP BY P.NAME, D.DISTID;");
                                              meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                    }
                                    else {
                                        result = statement.executeQuery("SELECT P.NAME, P.LOCATION, ROUND(SUM(D.AMOUNT),2) AS REVENUE FROM DISTRIBUTORPAYS D, PUBLISHINGHOUSE P "
                                                + "WHERE P.PUBHOUSEID = D.PUBHOUSEID GROUP BY P.NAME, P.LOCATION;");
                                              meta = result.getMetaData();
                                              
                                              while(result.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                    }
                                    break;
                            case "9": by = JOptionPane.showInputDialog("Input the attribute by which you want to calculate payment for\n"
                                    + "1. WORK TYPE\n"
                                    + "2. TIME PERIOD\n");
                                    if(by.equals("1")) {
                                        System.out.println("Editors: ");
                                        ResultSet result1 = statement.executeQuery("SELECT TYPE, ROUND(SUM(AMOUNT), 2) AS PAYMENT FROM PAYSEDITOR GROUP BY TYPE;");
                                              meta = result1.getMetaData();
                                              
                                              while(result1.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result1.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                        
                                        System.out.println("Authors: ");
                                        ResultSet result2 = statement.executeQuery("SELECT TYPE, ROUND(SUM(AMOUNT), 2) AS PAYMENT FROM PAYSAUTHOR GROUP BY TYPE;");
                                        meta = result2.getMetaData();

                                        while(result2.next()) {
                                            for(int i=1; i<=meta.getColumnCount(); i++)
                                              System.out.print(result2.getString(i) + "\t");
                                            System.out.println();
                                        }
                                    }
                                    else {
                                        System.out.println("Editors: ");
                                        ResultSet result1 = statement.executeQuery("SELECT MONTH(PAYDATE), YEAR(PAYDATE), ROUND(SUM(AMOUNT), 2) AS PAYMENT FROM PAYSEDITOR "
                                                + "GROUP BY MONTH(PAYDATE), YEAR(PAYDATE);");
                                              meta = result1.getMetaData();
                                              
                                              while(result1.next()) {
                                                  for(int i=1; i<=meta.getColumnCount(); i++)
                                                    System.out.print(result1.getString(i) + "\t");
                                                  System.out.println();
                                              }
                                        
                                        System.out.println("Authors: ");
                                        ResultSet result2 = statement.executeQuery("SELECT MONTH(PAYDATE), YEAR(PAYDATE), ROUND(SUM(AMOUNT), 2) AS PAYMENT FROM PAYSAUTHOR "
                                                + "GROUP BY MONTH(PAYDATE), YEAR(PAYDATE);");
                                        meta = result2.getMetaData();

                                        while(result2.next()) {
                                            for(int i=1; i<=meta.getColumnCount(); i++)
                                              System.out.print(result2.getString(i) + "\t");
                                            System.out.println();
                                        }
                                    }
                        }
                        
                    }
                    else if(Integer.parseInt(mainMenu) == 5)
                    {
                        try {
                            connection.setAutoCommit(false);
                            String insquery = JOptionPane.showInputDialog("Enter the ORDER details: distributorID, publicationID, orderID, No of Copies, Order Date, Delivery Date, Price, Shipping Cost");

                            String[] itemsArray;
                            itemsArray = insquery.split(",");

                            Float costOfOrder = Float.parseFloat(itemsArray[3]) * Float.parseFloat(itemsArray[6]) + Float.parseFloat(itemsArray[7]);

                            String sqlStatement = "INSERT INTO ORDERS VALUES (";
                            for(int i=2; i<itemsArray.length; i++)
                            {
                                sqlStatement += itemsArray[i] ;
                                if (i != itemsArray.length-1){
                                    sqlStatement += ",";
                                }
                            }
                            sqlStatement += ");";
                            System.out.println(sqlStatement);
                            ResultSet success = statement.executeQuery(sqlStatement);
                            System.out.println(success);

                            //Constant Publishing House ID
                            sqlStatement = "INSERT INTO SENDS VALUES (" + itemsArray[2] + ",1," + itemsArray[0] +");";

                            success = statement.executeQuery(sqlStatement);
                            System.out.println(success);

                            ResultSet rs = statement.executeQuery("select BALANCE from OWES where DISTID = '" + itemsArray[0] + "' and PUBHOUSEID = '1'");

                            if(rs.next()){
                                Float currentBalance = Float.parseFloat(rs.getString("BALANCE"));
                                currentBalance += costOfOrder;
                                sqlStatement = "UPDATE OWES SET BALANCE =" + currentBalance +" where DISTID = '" + itemsArray[0] + "' and PUBHOUSEID = '1';";
                                success = statement.executeQuery(sqlStatement);
                                System.out.println(success);

                            }
                            else
                            {
                                //Constant Publishing House ID
                                sqlStatement = "INSERT INTO OWES VALUES ("+itemsArray[0]+","+"1"+","+ Float.toString(costOfOrder)+")";
                            }

                            success = statement.executeQuery(sqlStatement);
                            System.out.println(success);

                            JOptionPane.showMessageDialog(null, "Inserted successfully");
                            connection.commit();

                        } catch (SQLException e) {
                            connection.rollback();
                            e.printStackTrace();
                        } finally {
                            connection.setAutoCommit(true);
                        }
                    }

                } catch (Throwable oops) {
                    JOptionPane.showMessageDialog(null, "There was an error while executing the query");
                    oops.printStackTrace();
                } finally {
                    close(result);
                    close(statement);
                    close(connection);
                }
            }
        }catch(Throwable oops) {
            oops.printStackTrace();
        }
    }
    static void close(Connection connection) {
        if(connection != null) {
            try {
                connection.close();
            } catch(Throwable whatever) {}
        }
    }
    static void close(Statement statement) {
        if(statement != null) {
            try {
                statement.close();
            } catch(Throwable whatever) {}
        }
    }
    static void close(ResultSet result) {
        if(result != null) {
            try {
                result.close();
            } catch(Throwable whatever) {}
        }
    }
}