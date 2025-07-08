package org.example.data_base;

import org.example.models.VrsAir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConditionDao {
    static private final String url = "jdbc:postgresql://localhost:5432/Radar";
    static private final String user = "postgres";
    static private final String password = "ourproga";


    public static void addCondition(VrsAir air, long time, int timePosition, int lastContact, boolean onGround,
            double verticalRate, double geoAltitude, String squawk, boolean spi, int positionSource) {
        String sql = """
        INSERT INTO conditions(
        time,
        aircrafts_icao24,
        callsign,
        time_position,
        last_contact,
        longitude,
        latitude,
        baro_altitude,
        on_ground,
        velocity,
        true_track,
        vertical_rate,
        geo_altitude,
        squawk,
        spi,
        position_source
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setLong(1, time);
             pstmt.setString(2, air.getIcao());
             pstmt.setString(3, air.getCallsign());
             pstmt.setInt(4, timePosition);
             pstmt.setInt(5, lastContact);
             pstmt.setDouble(6, air.getLongitude());
             pstmt.setDouble(7, air.getLatitude());
             pstmt.setDouble(8, air.getAltitude());
             pstmt.setBoolean(9, onGround);
             pstmt.setDouble(10, air.getSpeed());
             pstmt.setDouble(11, air.getTrack());
             pstmt.setDouble(12, verticalRate);
             pstmt.setDouble(13, geoAltitude);
             pstmt.setString(14, squawk);
             pstmt.setBoolean(15, spi);
             pstmt.setInt(16, positionSource);
             pstmt.executeUpdate();
             System.out.println("Добавлено состояние самолета icao = " + air.getIcao());
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("[ошибка condition]" + e.getMessage());
           // return false;
        }
    }


}
