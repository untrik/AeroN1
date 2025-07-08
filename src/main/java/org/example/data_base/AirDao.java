package org.example.data_base;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirDao {
    static private final String url = "jdbc:postgresql://localhost:5432/Radar";
    static private final String user = "postgres";
    static private final String password = "ourproga";

    static private List<String> allAirs = getAllAirs();

    public static List<String> getAllAirs() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT icao24 FROM aircrafts";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String value = rs.getString("icao24");
                result.add(value);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return result;
    }


    public static boolean addAir(String icao24, String country) {
        String sql = "INSERT INTO aircrafts(icao24, country) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, icao24);
             pstmt.setString(2, country);
             pstmt.executeUpdate();
        } catch (SQLException e) {
           // e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }
        allAirs.add(icao24);
        return true;
    }


    public static boolean isThereAir(String icao24) {
        return allAirs.contains(icao24);
    }
}
