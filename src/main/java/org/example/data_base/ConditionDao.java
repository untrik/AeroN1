package org.example.data_base;

import org.example.models.VrsAir;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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


    public static void updateLastCoordinates(VrsAir air) {
        String sql = """
                SELECT DISTINCT longitude, latitude
                FROM conditions
                WHERE NOW() - to_timestamp(time) < INTERVAL '24 hours'
                AND aircrafts_icao24 = ?;
                """;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, air.getIcao());
             ResultSet rs = pstmt.executeQuery();
             List<Double> longitudes = new ArrayList<>();
             List<Double> latitudes = new ArrayList<>();
             while (rs.next()) {
                 longitudes.add(rs.getDouble("longitude"));
                 latitudes.add(rs.getDouble("latitude"));
             }
             air.setLatitudes(latitudes);
             air.setLongitudes(longitudes);
             System.out.printf("Для самолета %s получены его координаты за последние сутки\n", air.getIcao());
             System.out.println(air.getLatitudes());
             System.out.println(air.getLongitudes());
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.printf("Не удалось получить координат для %s за последние 24 часа\n", air.getIcao());
            System.out.println(e.getMessage());
        }
    }
}
