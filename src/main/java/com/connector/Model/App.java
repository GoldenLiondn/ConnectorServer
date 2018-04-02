package com.connector.Model;

import com.connector.View.Server_frame;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static boolean calling = false;
    public static boolean dialerConnected = false;
    public static String callingNumber;


    private static final String hostname = "localhost";
    private static final String databasename = "mydatabase";
    private static final String databaseusername = "root";
    private static final String databasepassword = "1234";
    public static Connection connection = null;

    public static void main(String[] args) throws UnknownHostException {


        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"+ hostname +":3306/"+ databasename +"?useUnicode=true&characterEncoding=UTF-8",databaseusername,databasepassword);
            System.out.println("connected mysql");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new Server_frame();

    }




}
