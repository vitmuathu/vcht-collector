package com.example.vchtcollector.queryDB;

import com.example.vchtcollector.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class executeUpdate {
    public static void main(String[] args) throws SQLException {
        try (
                Connection con = ConnectionUtils.openConnection();
                Statement st = con.createStatement();
        ) {
            // Insert
            String sqlInsert = "INSERT INTO user(username, password, createdDate) "
                    + " VALUE('user1', '123', now());";
            int numberRowsAffected = st.executeUpdate(sqlInsert);
            System.out.println("Affected rows after inserted: "+ numberRowsAffected);
        }
    }
}
