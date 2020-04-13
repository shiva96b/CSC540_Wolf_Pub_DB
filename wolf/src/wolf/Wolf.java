/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolf;

import java.sql.*;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.swing.*;

public class Wolf {

    // Update your user info alone here
    private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/mravind";

    // Update your user and password info here!
    private static final String user = "mravind";
    private static final String password = "200314451";

    // Menu to get the Table Names for SELECT, UPDATE and DELETE Operations
    public static String getTableNames() {
        return JOptionPane.showInputDialog("Type table name for respective table\n"
                + "1. ARTICLEHAS\n"
                + "2. ARTICLES\n"
                + "3. AUTHORS\n"
                + "4. BOOK\n"
                + "5. BOOKHAS\n"
                + "6. CHAPTER\n"
                + "7. DISTRIBUTOR\n"
                + "8. DISTRIBUTORPAYS\n"
                + "9. EDITORS\n"
                + "10. ISSUE\n"
                + "11. ORDERS\n"
                + "12. OWES\n"
                + "13. PAYSAUTHOR\n"
                + "14. PAYSEDITOR\n"
                + "15. PUBLICATION\n"
                + "16. PUBLICATIONHAS\n"
                + "17. PUBLISHINGHOUSE\n"
                + "18. SENDS");
    }

    // Main Menu Options
    public static String getMainMenu() {
        return JOptionPane.showInputDialog("Input number for respective mainMenu \n"
                + "1. SELECT\n"
                + "2. INSERT\n"
                + "3. UPDATE\n"
                + "4. DELETE\n"
                + "5. GENERATE\n"
                + "6. ORDER CREATION WITH DISTRIBUTOR\n"
                + "7. RECORD DISTRIBUTOR PAYMENT\n"
                + "8. VIEW EDITOR PUBLICATIONS\n"
                + "0. QUIT\n");
    }

    // Generate Report Options
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

            while (!mainMenu.equals("0")) {

                try {
                    // Connecting to the Maria Database
                    connection = DriverManager.getConnection(jdbcURL, user, password);
                    statement = connection.createStatement();

                    mainMenu = getMainMenu();

                    // QUIT
                    if (mainMenu.equals("0")) {
                        return;
                    }

                    // SELECT, INSERT, UPDATE, DELETE
                    if (Integer.parseInt(mainMenu) <= 4) {
                        tableName = getTableNames();

                        // Executing the Describe Table query to get the column details
                        result = statement.executeQuery("DESC " + tableName);
                        int count = 0;

                        while (result.next()) {
                            String name = result.getString("Field");
                            String type = result.getString("Type");
                            System.out.println(name + " " + type);
                            count = count + 1;
                        }

                        switch (mainMenu) {
                            case "2":
                                // Insert into Table
                                String insquery = JOptionPane.showInputDialog("Enter the new row details");
                                ResultSet success = statement.executeQuery("INSERT INTO " + tableName + " VALUES (" + insquery + ");");
                                System.out.println(success);
                                JOptionPane.showMessageDialog(null, "Inserted successfully");
                                break;

                            case "3":
                                // Update rows in Table
                                String updquery = JOptionPane.showInputDialog("Finish the remaining \nUPDATE " + tableName + " SET ");
                                result = statement.executeQuery("UPDATE " + tableName + " SET " + updquery);
                                JOptionPane.showMessageDialog(null, "Updated successfully");
                                break;

                            case "4":
                                // Delete from Table
                                String colname = JOptionPane.showInputDialog("Enter the column name followed by the value to delete");
                                String vall = JOptionPane.showInputDialog("");
                                result = statement.executeQuery("DELETE FROM " + tableName + " WHERE " + colname + "= " + vall);
                                JOptionPane.showMessageDialog(null, "Deleted successfully");
                                System.out.print("DELETE FROM " + tableName + " WHERE " + colname + "= " + vall);
                                break;

                            case "1":
                                // Select Statement execution
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
                    } // VIEWING EDITOR'S PUBLICATIONS
                    else if (Integer.parseInt(mainMenu) == 8) {
                        String value = JOptionPane.showInputDialog("Input the editor by which you want to select\n");
                        System.out.println("PUBID");
                        result = statement.executeQuery("SELECT PUBID FROM PUBLICATIONHAS WHERE EDITORSSN=" + Integer.parseInt(value) + ";");
                        String pubIds = "";
                        ResultSetMetaData meta = result.getMetaData();

                        while (result.next()) {
                            for (int i = 1; i <= meta.getColumnCount(); i++) {
                                pubIds += result.getString(i) + ",";
                            }
                        }

                        if (pubIds == "") {
                            JOptionPane.showMessageDialog(null, "The editor doesn't have any records");
                        } else {
                            int count = 0;
                            result = statement.executeQuery("DESC PUBLICATION");
                            while (result.next()) {
                                String name = result.getString("Field");
                                System.out.print(name + "\t\t");
                                count = count + 1;
                            }
                            System.out.print("\n");

                            pubIds = pubIds.substring(0, pubIds.length() - 1);
                            result = statement.executeQuery("select * from PUBLICATION where PUBID in (" + pubIds + ");");

                            while (result.next()) {
                                for (int i = 1; i <= count; i++) {
                                    System.out.print(result.getString(i) + "\t\t");
                                }
                                System.out.println();
                            }

                        }

                    } // GENERATING MONTHLY REPORTS
                    else if (Integer.parseInt(mainMenu) == 5) {
                        // Generate Report Query Execution with sub Operations
                        String reportOption;
                        reportOption = getReportOption();

                        if (reportOption.equals("1") || reportOption.equals("2")) {
                            String by = JOptionPane.showInputDialog("Input the attribute by which you want to select\n"
                                    + "1. TOPIC\n"
                                    + "2. DATE\n"
                                    + "3. AUTHOR");

                            if (reportOption.equals("1")) {
                                switch (by) {
                                    // Find Book by Topic
                                    case "1":
                                        String value = JOptionPane.showInputDialog("Input the topic by which you want to select\n");
                                        result = statement.executeQuery("DESC BOOK");
                                        while (result.next()) {
                                            String name = result.getString("Field");
                                            System.out.print(name + "\t");
                                        }
                                        System.out.println();
                                        result = statement.executeQuery("SELECT * FROM BOOK B WHERE B.PUBID = (SELECT P.PUBID FROM PUBLICATION P WHERE P.GENRE = '" + value + "');");
                                        ResultSetMetaData meta = result.getMetaData();

                                        while (result.next()) {
                                            for (int i = 1; i <= meta.getColumnCount(); i++) {
                                                System.out.print(result.getString(i) + "\t");
                                            }
                                            System.out.println();
                                        }
                                        break;
                                    // Find Book by Date
                                    case "2":
                                        value = JOptionPane.showInputDialog("Input the date(yyyy-mm-dd) by which you want to select\n");
                                        result = statement.executeQuery("DESC BOOK");
                                        while (result.next()) {
                                            String name = result.getString("Field");
                                            System.out.print(name + "\t");
                                        }
                                        System.out.println();
                                        result = statement.executeQuery("SELECT * FROM BOOK B WHERE B.PUBID = (SELECT PUBID FROM PUBLICATION P WHERE P.DATE = '" + value + "');");
                                        meta = result.getMetaData();

                                        while (result.next()) {
                                            for (int i = 1; i <= meta.getColumnCount(); i++) {
                                                System.out.print(result.getString(i) + "\t");
                                            }
                                            System.out.println();
                                        }
                                        break;
                                    // Find Book by Author
                                    case "3":
                                        value = JOptionPane.showInputDialog("Input the author by which you want to select\n");
                                        result = statement.executeQuery("DESC BOOK");
                                        while (result.next()) {
                                            String name = result.getString("Field");
                                            System.out.print(name + "\t");
                                        }
                                        System.out.println();
                                        result = statement.executeQuery("SELECT B.PUBID, B.ISBN, B.EDITIONNO FROM BOOK B, BOOKHAS BH, AUTHORS A WHERE B.PUBID = BH.PUBID "
                                                + "AND BH.AUTHORSSN = A.AUTHORSSN AND A.NAME = '" + value + "';");
                                        meta = result.getMetaData();

                                        while (result.next()) {
                                            for (int i = 1; i <= meta.getColumnCount(); i++) {
                                                System.out.print(result.getString(i) + "\t");
                                            }
                                            System.out.println();
                                        }
                                        break;
                                }
                            } else if (reportOption.equals("2")) {
                                switch (by) {
                                    // Find Article by Topic
                                    case "1":
                                        String value = JOptionPane.showInputDialog("Input the topic by which you want to select\n");
                                        result = statement.executeQuery("DESC ARTICLES");
                                        while (result.next()) {
                                            String name = result.getString("Field");
                                            System.out.print(name + "\t");
                                        }
                                        System.out.println();
                                        result = statement.executeQuery("SELECT * FROM ARTICLES A WHERE A.PUBID = (SELECT P.PUBID FROM PUBLICATION P WHERE P.GENRE = '" + value + "');");
                                        ResultSetMetaData meta = result.getMetaData();

                                        while (result.next()) {
                                            for (int i = 1; i <= meta.getColumnCount(); i++) {
                                                System.out.print(result.getString(i) + "\t");
                                            }
                                            System.out.println();
                                        }
                                        break;
                                    // Find Article by Date
                                    case "2":
                                        value = JOptionPane.showInputDialog("Input the date(yyyy/mm/dd) by which you want to select\n");
                                        result = statement.executeQuery("DESC ARTICLES");
                                        while (result.next()) {
                                            String name = result.getString("Field");
                                            System.out.print(name + "\t");
                                        }
                                        System.out.println();
                                        result = statement.executeQuery("SELECT * FROM ARTICLES A WHERE A.PUBID = (SELECT PUBID FROM PUBLICATION P WHERE P.DATE = '" + value + "');");
                                        meta = result.getMetaData();

                                        while (result.next()) {
                                            for (int i = 1; i <= meta.getColumnCount(); i++) {
                                                System.out.print(result.getString(i) + "\t");
                                            }
                                            System.out.println();
                                        }
                                        break;
                                    // Find Article by Author
                                    case "3":
                                        value = JOptionPane.showInputDialog("Input the author by which you want to select\n");
                                        System.out.println("ARTICLEID \t TITLE \t TEXT");
                                        result = statement.executeQuery("SELECT A.ARTICLEID, A.TITLE, A.TEXT FROM ARTICLES A, ARTICLEHAS AH, AUTHORS AU WHERE A.ARTICLEID = AH.ARTICLEID "
                                                + "AND AH.AUTHORSSN = AU.AUTHORSSN AND AU.NAME = '" + value + "';");
                                        meta = result.getMetaData();

                                        while (result.next()) {
                                            for (int i = 1; i <= meta.getColumnCount(); i++) {
                                                System.out.print(result.getString(i) + "\t");
                                            }
                                            System.out.println();
                                        }
                                        break;
                                }
                            }
                        }
                        switch (reportOption) {
                            // Generate Balance
                            case "3":
                                String value = JOptionPane.showInputDialog("Input the distributor ID\n");
                                System.out.println("DISTID \t BALANCE");
                                result = statement.executeQuery("SELECT DISTID, ROUND(SUM(BALANCE),2) AS BALANCE FROM OWES WHERE DISTID = " + value + ";");
                                ResultSetMetaData meta = result.getMetaData();

                                while (result.next()) {
                                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                                        System.out.print(result.getString(i) + "\t");
                                    }
                                    System.out.println();
                                }
                                break;
                            // Number and total price of each publication bought per distributor per month
                            case "4":
                                result = statement.executeQuery("SELECT B.PUBID, B.DISTID, MONTH(A.ORDERDATE) AS PERMONTH, YEAR(A.ORDERDATE) AS PERYEAR, "
                                        + "ROUND(SUM(A.PRICE), 2) AS TOTALPRICE, SUM(NOOFCOPIES) AS NOOFCOPIES FROM ORDERS A, SENDS B WHERE A.ORDERID=B.ORDERID"
                                        + " GROUP BY B.PUBID, B.DISTID, MONTH(A.ORDERDATE), YEAR(A.ORDERDATE);");
                                meta = result.getMetaData();
                                System.out.println("PUBID \t DISTID \t PERMONTH \t PERYEAR \t TOTALPRICE \t NOOFCOPIES");
                                while (result.next()) {
                                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                                        System.out.print(result.getString(i) + "\t\t");
                                    }
                                    System.out.println();
                                }
                                break;
                            // Total revenue of each publishing house
                            case "5":
                                result = statement.executeQuery("SELECT P.PUBHOUSEID, MONTH(D.PAYDATE) AS MONTH, YEAR(D.PAYDATE) AS YEAR, "
                                        + "ROUND(SUM(AMOUNT), 2) AS REVENUE FROM DISTRIBUTORPAYS D, PUBLISHINGHOUSE P WHERE D.PUBHOUSEID = P.PUBHOUSEID "
                                        + "GROUP BY P.PUBHOUSEID, MONTH(D.PAYDATE), YEAR(D.PAYDATE)");
                                meta = result.getMetaData();
                                System.out.println("PUBHOUSEID \t MONTH \t YEAR \t REVENUE");
                                while (result.next()) {
                                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                                        System.out.print(result.getString(i) + "\t\t");
                                    }
                                    System.out.println();
                                }
                                break;
                            // Total expenses of each publishing house
                            case "6":
                                result = statement.executeQuery("SELECT PUBHOUSEID,  MONTH, YEAR, ROUND(SUM(COST),2) AS EXPENSE "
                                        + "FROM "
                                        + "(SELECT P.PUBHOUSEID, ROUND(SUM(O.SHIPPINGCOST),2) AS COST, MONTH(O.ORDERDATE) AS MONTH, YEAR(O.ORDERDATE) AS YEAR "
                                        + "FROM PUBLICATION P, ORDERS O, SENDS S "
                                        + "WHERE O.ORDERID = S.ORDERID AND S.PUBID = P.PUBID "
                                        + "GROUP BY P.PUBHOUSEID, MONTH(O.ORDERDATE), YEAR(O.ORDERDATE) "
                                        + "UNION ALL "
                                        + "SELECT PUBHOUSEID, ROUND(SUM(AMOUNT),2) AS COST, MONTH(PAYDATE) AS MONTH, YEAR(PAYDATE) AS YEAR "
                                        + "FROM PAYSAUTHOR GROUP BY PUBHOUSEID, MONTH(PAYDATE), YEAR(PAYDATE) "
                                        + "UNION ALL "
                                        + "SELECT PUBHOUSEID, ROUND(SUM(AMOUNT),2) AS COST, MONTH(PAYDATE) AS MONTH, YEAR(PAYDATE) AS YEAR "
                                        + "FROM PAYSEDITOR GROUP BY PUBHOUSEID, MONTH(PAYDATE), YEAR(PAYDATE)) AS DER "
                                        + "GROUP BY DER.PUBHOUSEID, DER.MONTH, DER.YEAR;");
                                meta = result.getMetaData();
                                System.out.println("PUBHOUSEID \t MONTH \t YEAR \t EXPENSE");
                                while (result.next()) {
                                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                                        System.out.print(result.getString(i) + "\t\t");
                                    }
                                    System.out.println();
                                }
                                break;
                            // Total current number of distributors for each publishing house
                            case "7":
                                result = statement.executeQuery("SELECT P.NAME, COUNT(*) AS NOOFDISTRIBUTORS FROM OWES O, PUBLISHINGHOUSE P WHERE "
                                        + "O.PUBHOUSEID = P.PUBHOUSEID GROUP BY P.NAME;");
                                meta = result.getMetaData();
                                System.out.println("NAME \t\t NOOFDISTRIBUTORS");
                                while (result.next()) {
                                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                                        System.out.print(result.getString(i) + "\t\t");
                                    }
                                    System.out.println();
                                }
                                break;
                            // Total revenue
                            case "8":
                                String by = JOptionPane.showInputDialog("Input the attribute by which you want to calculate revenue for\n"
                                        + "1. CITY\n"
                                        + "2. DISTRIBUTOR\n"
                                        + "3. LOCATION");
                                // BY CITY
                                if (by.equals("1")) {
                                    result = statement.executeQuery("SELECT P.NAME, P.CITY, ROUND(SUM(D.AMOUNT),2) AS REVENUE FROM DISTRIBUTORPAYS D, PUBLISHINGHOUSE P "
                                            + "WHERE P.PUBHOUSEID = D.PUBHOUSEID GROUP BY P.NAME, P.CITY;");
                                    meta = result.getMetaData();
                                    System.out.println("NAME \t\t CITY \t\t REVENUE");
                                    while (result.next()) {
                                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                                            System.out.print(result.getString(i) + "\t\t");
                                        }
                                        System.out.println();
                                    }
                                } // BY DISTRIBUTOR
                                else if (by.equals("2")) {
                                    result = statement.executeQuery("SELECT P.NAME, D.DISTID, ROUND(SUM(D.AMOUNT),2) AS REVENUE FROM DISTRIBUTORPAYS D, PUBLISHINGHOUSE P "
                                            + "WHERE P.PUBHOUSEID = D.PUBHOUSEID GROUP BY P.NAME, D.DISTID;");
                                    meta = result.getMetaData();
                                    System.out.println("NAME \t\t DISTID \t\t REVENUE");
                                    while (result.next()) {
                                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                                            System.out.print(result.getString(i) + "\t\t");
                                        }
                                        System.out.println();
                                    }
                                } // BY LOCATION
                                else {
                                    result = statement.executeQuery("SELECT P.NAME, P.LOCATION, ROUND(SUM(D.AMOUNT),2) AS REVENUE FROM DISTRIBUTORPAYS D, PUBLISHINGHOUSE P "
                                            + "WHERE P.PUBHOUSEID = D.PUBHOUSEID GROUP BY P.NAME, P.LOCATION;");
                                    meta = result.getMetaData();
                                    System.out.println("NAME \t\t LOCATION \t\t REVENUE");
                                    while (result.next()) {
                                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                                            System.out.print(result.getString(i) + "\t\t");
                                        }
                                        System.out.println();
                                    }
                                }
                                break;
                            // Total payments to editors and authors per work type
                            case "9":
                                by = JOptionPane.showInputDialog("Input the attribute by which you want to calculate payment for\n"
                                        + "1. WORK TYPE\n"
                                        + "2. TIME PERIOD\n");
                                // BY WORK TYPE
                                if (by.equals("1")) {
                                    System.out.println("Editors: ");
                                    ResultSet result1 = statement.executeQuery("SELECT TYPE, ROUND(SUM(AMOUNT), 2) AS PAYMENT FROM PAYSEDITOR GROUP BY TYPE;");
                                    meta = result1.getMetaData();
                                    System.out.println("TYPE \t PAYMENT");
                                    while (result1.next()) {
                                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                                            System.out.print(result1.getString(i) + "\t\t");
                                        }
                                        System.out.println();
                                    }

                                    System.out.println("Authors: ");
                                    ResultSet result2 = statement.executeQuery("SELECT TYPE, ROUND(SUM(AMOUNT), 2) AS PAYMENT FROM PAYSAUTHOR GROUP BY TYPE;");
                                    meta = result2.getMetaData();
                                    System.out.println("TYPE \t PAYMENT");
                                    while (result2.next()) {
                                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                                            System.out.print(result2.getString(i) + "\t\t");
                                        }
                                        System.out.println();
                                    }
                                } // BY TIME PERIOD
                                else {
                                    System.out.println("Editors: ");
                                    ResultSet result1 = statement.executeQuery("SELECT MONTH(PAYDATE), YEAR(PAYDATE), ROUND(SUM(AMOUNT), 2) AS PAYMENT FROM PAYSEDITOR "
                                            + "GROUP BY MONTH(PAYDATE), YEAR(PAYDATE);");
                                    meta = result1.getMetaData();
                                    System.out.println("MONTH \t YEAR \t PAYMENT");
                                    while (result1.next()) {
                                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                                            System.out.print(result1.getString(i) + "\t\t");
                                        }
                                        System.out.println();
                                    }

                                    System.out.println("Authors: ");
                                    ResultSet result2 = statement.executeQuery("SELECT MONTH(PAYDATE), YEAR(PAYDATE), ROUND(SUM(AMOUNT), 2) AS PAYMENT FROM PAYSAUTHOR "
                                            + "GROUP BY MONTH(PAYDATE), YEAR(PAYDATE);");
                                    meta = result2.getMetaData();
                                    System.out.println("MONTH \t YEAR \t PAYMENT");
                                    while (result2.next()) {
                                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                                            System.out.print(result2.getString(i) + "\t\t");
                                        }
                                        System.out.println();
                                    }
                                }
                        }

                    } // TRANSACTIONS
                    // ORDER CREATION WHICH INSERTS INTO ORDERS, SENDS AND OWES
                    else if (Integer.parseInt(mainMenu) == 6) {
                        try {
                            // Start of DB Transaction
                            connection.setAutoCommit(false);
                            String insquery = JOptionPane.showInputDialog("Enter the ORDER details: distributorID, publicationID, orderID, No of Copies, Order Date, Delivery Date, Price, Shipping Cost");

                            String[] itemsArray;
                            itemsArray = insquery.split(",");

                            Float costOfOrder = Float.parseFloat(itemsArray[3]) * Float.parseFloat(itemsArray[6]) + Float.parseFloat(itemsArray[7]);

                            // Insert Record into ORDERS TABLE
                            String sqlStatement = "INSERT INTO ORDERS VALUES (";
                            for (int i = 2; i < itemsArray.length; i++) {
                                sqlStatement += itemsArray[i];
                                if (i != itemsArray.length - 1) {
                                    sqlStatement += ",";
                                }
                            }
                            sqlStatement += ");";
                            System.out.println(sqlStatement);
                            ResultSet success = statement.executeQuery(sqlStatement);
                            System.out.println(success);

                            //Constant Publishing House ID
                            sqlStatement = "INSERT INTO SENDS VALUES (" + itemsArray[2] + "," + itemsArray[1] + "," + itemsArray[0] + ");";

                            success = statement.executeQuery(sqlStatement);

                            System.out.println(success);

                            // Retrieve the current balance for the given Publication House and Distributor
                            ResultSet rs = statement.executeQuery("select BALANCE from OWES where DISTID = '" + itemsArray[0] + "' and PUBHOUSEID = '1'");

                            if (rs.next()) {
                                // If a balance exists
                                Float currentBalance = Float.parseFloat(rs.getString("BALANCE"));
                                currentBalance += costOfOrder;
                                sqlStatement = "UPDATE OWES SET BALANCE =" + currentBalance + " where DISTID = '" + itemsArray[0] + "' and PUBHOUSEID = '1';";
                                success = statement.executeQuery(sqlStatement);
                                System.out.println(success);

                            } else {
                                // If this is the first transaction between the given Distributor and Publication
                                sqlStatement = "INSERT INTO OWES VALUES (" + itemsArray[0] + "," + "1" + "," + Float.toString(costOfOrder) + ")";
                            }

                            success = statement.executeQuery(sqlStatement);
                            System.out.println(success);

                            JOptionPane.showMessageDialog(null, "Inserted successfully");
                            connection.commit();

                        } catch (SQLException e) {
                            // Rollback if any of the above queries fail
                            connection.rollback();
                            e.printStackTrace();
                        } finally {
                            connection.setAutoCommit(true);
                        }
                    } // RECORDING PAYMENTS BY DISTRIBUTORS
                    else if (Integer.parseInt(mainMenu) == 7) {
                        try {
                            // Start of DB Transaction
                            connection.setAutoCommit(false);
                            String insquery = JOptionPane.showInputDialog("Enter the PAYMENT details: Order ID, Date, Amount");

                            String[] itemsArray;
                            itemsArray = insquery.split(",");

                            String selectQuerry = "Select DISTID from SENDS where ORDERID = " + itemsArray[0];
                            ResultSet success = statement.executeQuery(selectQuerry);
                            System.out.println(success);
                            Integer distID = 0;

                            if (success.next()) {
                                distID = Integer.parseInt(success.getString("DISTID"));
                            }

                            Float payment = Float.parseFloat(itemsArray[2]);

                            String sqlStatement = "INSERT INTO DISTRIBUTORPAYS VALUES (";
                            sqlStatement += distID + ",1," + itemsArray[1] + "," + itemsArray[2] + ");";

                            System.out.println(sqlStatement);
                            success = statement.executeQuery(sqlStatement);
                            System.out.println(success);

                            //Constant Publishing House ID
                            // Retrieve the current balance for the given Publication House and Distributor
                            ResultSet rs = statement.executeQuery("select BALANCE from OWES where DISTID = '" + distID + "' and PUBHOUSEID = 1");

                            if (rs.next()) {
                                // If a balance exists
                                Float currentBalance = Float.parseFloat(rs.getString("BALANCE"));
                                currentBalance -= payment;
                                sqlStatement = "UPDATE OWES SET BALANCE =" + currentBalance + " where DISTID = '" + distID + "' and PUBHOUSEID = 1;";
                                success = statement.executeQuery(sqlStatement);
                                System.out.println(success);

                            } else {
                                //Constant Publishing House ID
                                // If this is the first transaction between the given Distributor and Publication
                                payment *= -1;
                                sqlStatement = "INSERT INTO OWES VALUES (" + distID + ",1," + Float.toString(payment) + ")";
                            }

                            success = statement.executeQuery(sqlStatement);
                            System.out.println(success);

                            JOptionPane.showMessageDialog(null, "Inserted successfully");
                            connection.commit();

                        } catch (SQLException e) {
                            // Rollback if any of the above queries fail
                            connection.rollback();
                            e.printStackTrace();
                        } finally {
                            connection.setAutoCommit(true);
                        }
                    }

                } catch (Throwable oops) {
                    // Catch any error thrown by the application
                    JOptionPane.showMessageDialog(null, "There was an error while executing the query");
                    oops.printStackTrace();
                } finally {
                    // Closing Connection
                    close(result);
                    close(statement);
                    close(connection);
                }
            }
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    // Function Overloading for closing the connection
    static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable whatever) {
            }
        }
    }

    static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Throwable whatever) {
            }
        }
    }

    static void close(ResultSet result) {
        if (result != null) {
            try {
                result.close();
            } catch (Throwable whatever) {
            }
        }
    }
}
