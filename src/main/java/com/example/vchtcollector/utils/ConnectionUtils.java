package com.example.vchtcollector.utils;

import com.example.vchtcollector.configs.DBConfig;
import com.mysql.cj.jdbc.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtils {

    private static final String hostName = "127.0.0.1";
    private static final String dbName = "counter";
    private static final String username = "counter";
    private static final String password = "counter1123";
    private static final String connectionURL = "jdbc:mysql://" + hostName + ":3306/" + dbName;

    @Autowired
    private static DBConfig dbConfig;
    public static Connection openConnection() throws  SQLException{
        DriverManager.registerDriver(new Driver());
        return DriverManager.getConnection(connectionURL, username, password);
    }


    public static Connection oracleConnection() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        System.out.println(dbConfig.getEvtpPassword()+"===================================================");
        return DriverManager.getConnection("jdbc:oracle:thin:@10.60.117.73:1521:evtp","vtp","Cntt#2018#");
    }
}
