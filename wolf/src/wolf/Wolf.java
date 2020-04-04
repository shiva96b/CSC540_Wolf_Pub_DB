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
        return JOptionPane.showInputDialog("Type tablename for respective table\n" +
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
                + "4. Generate "
                + "5. QUIT\n");
    }

    public static void main(String[] args) {

        try {

            String mainMenu = "";
            String tableName = "";

            Class.forName("org.mariadb.jdbc.Driver");
            Connection connection = null;
            Statement statement = null;
            ResultSet result = null;

            while (!mainMenu.equals("QUIT")) {

                try {
                    connection = DriverManager.getConnection(jdbcURL, user, password);
                    statement = connection.createStatement();

                    mainMenu = getMainMenu();

                    if (Integer.parseInt(mainMenu) < 4)
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
                            JOptionPane.showMessageDialog(null, "Succesfull");
                            break;
                        case "2":
                            String updquery = JOptionPane.showInputDialog("Finish the remaining \nUPDATE " + tableName + " SET ");
                            result = statement.executeQuery("UPDATE " + tableName + " SET " + updquery);
                            System.out.print("updated");
                            break;
                        case "3":
                            String colname = JOptionPane.showInputDialog("Enter the column name followed by the value to delete");
                            String vall = JOptionPane.showInputDialog("");
                            result = statement.executeQuery("DELETE FROM " + tableName + " WHERE " + colname + "= " + vall);
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
//                            String name = result.getString("Field");
//                            String type = result.getString("Type");
                                for (int i = 1; i <= count; i++) {
                                    System.out.print(result.getString(i) + "\t\t");
                                }
                                System.out.println();
                            }
                    }

                } catch (Throwable oops) {
                    JOptionPane.showMessageDialog(null, "There was an error while executing the querry");
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
