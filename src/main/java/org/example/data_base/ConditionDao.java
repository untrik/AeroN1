package org.example.data_base;

import org.example.models.VrsAir;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConditionDao {
    static private final String url = "jdbc:postgresql://localhost:5432/Radar";
    static private final String user = "postgres";
    static private final String password = "ourproga";

    private String icao;
    private String callsign;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double track;
    private boolean onGround;
    private String squawk;
    private String originCountry;
    private double verticalRate;
    private long   lastSeen;
    int            timePosition;
    double         geoAltitude;
    boolean        spi;
    int            positionSource;
    long           time;

    public static void addConditions(List<ConditionDao> conditions) {
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
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
             conn.setAutoCommit(false);
             try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 for (ConditionDao condition : conditions) {
                     pstmt.setLong(1, condition.getTime());
                     pstmt.setString(2, condition.getIcao());
                     pstmt.setString(3, condition.getCallsign());
                     pstmt.setInt(4, condition.getTimePosition());
                     pstmt.setLong(5, condition.getLastSeen());
                     pstmt.setDouble(6, condition.getLongitude());
                     pstmt.setDouble(7, condition.getLatitude());
                     pstmt.setDouble(8, condition.getAltitude());
                     pstmt.setBoolean(9, condition.isOnGround());
                     pstmt.setDouble(10, condition.getSpeed());
                     pstmt.setDouble(11, condition.getTrack());
                     pstmt.setDouble(12, condition.getVerticalRate());
                     pstmt.setDouble(13, condition.getGeoAltitude());
                     pstmt.setString(14, condition.getSquawk());
                     pstmt.setBoolean(15, condition.isSpi());
                     pstmt.setInt(16, condition.getPositionSource());
                     pstmt.addBatch();
                 }
                 pstmt.executeBatch();
                 conn.commit();
                 System.out.printf("Добавлены %s состояний самолетов:\n", conditions.size());
                 for (ConditionDao conditionDao : conditions) System.out.println(conditionDao);
             } catch (SQLException e) {
                 System.out.printf("Произошла ошибка %s во время добавления состояния: %s");
             }
        } catch (SQLException e) {
            System.out.println("Ошибка при подключении к БД: " + e.getMessage());
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
             //System.out.println("Широта: " + air.getLatitudes());
             //System.out.println("Долгота: " + air.getLongitudes());
        } catch (SQLException e) {
            System.out.printf("Не удалось получить координат для %s за последние 24 часа\n", air.getIcao());
            System.out.println(e.getMessage());
        }
    }

    public ConditionDao(VrsAir air, int timePosition, double geoAltitude, boolean spi, int positionSource, long time) {
        icao = air.getIcao();
        callsign = air.getCallsign();
        latitude = air.getLatitude();
        longitude = air.getLongitude();
        altitude = air.getAltitude();
        speed = air.getSpeed();
        track = air.getTrack();
        onGround = air.isOnGround();
        squawk = air.getSquawk();
        originCountry = air.getOriginCountry();
        verticalRate = air.getVerticalRate();
        lastSeen = air.getLastSeen();
        this.timePosition = timePosition;
        this.geoAltitude = geoAltitude;
        this.spi = spi;
        this.positionSource = positionSource;
        this.time = time;
    }

    public String getIcao() {
        return icao;
    }

    public String getCallsign() {
        return callsign;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public double getTrack() {
        return track;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public String getSquawk() {
        return squawk;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public double getVerticalRate() {
        return verticalRate;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public int getTimePosition() {
        return timePosition;
    }

    public double getGeoAltitude() {
        return geoAltitude;
    }

    public boolean isSpi() {
        return spi;
    }

    public int getPositionSource() {
        return positionSource;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return String.format("icao = %s, callsign = %s, longitude = %s, latitude = %s, speed = %s", icao, callsign, longitude, latitude, speed);
    }
}
