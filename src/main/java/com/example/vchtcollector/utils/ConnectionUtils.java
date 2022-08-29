package com.example.vchtcollector.utils;

import com.example.vchtcollector.configs.DBConfig;
import com.example.vchtcollector.configs.LoadConfig;
import com.mysql.cj.jdbc.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class ConnectionUtils {

    private static final String hostName = "127.0.0.1";
    private static final String dbName = "counter";
    private static final String username = "counter";
    private static final String password = "counter1123";
    private static final String connectionURL = "jdbc:mysql://" + hostName + ":3306/" + dbName;

    private  static DBConfig dbConfig;

    @Autowired
    public void init(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    static LoadConfig conf = new LoadConfig("file-config/info.yml");
    public static Connection openConnection() throws  SQLException{
        System.out.println();
        DriverManager.registerDriver(new Driver());
        return DriverManager.getConnection(connectionURL, username, password);
    }


    public static Connection oracleConnection() throws SQLException, ClassNotFoundException {
        conf.loadConfig();
        System.out.println(dbConfig.getEvtpUsername()+"------------------------------------------");
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPassword());
    }
}
