package org.example.models;

import java.util.List;

public class VrsAir {
    private int    id;
    private String icao;
    private String registration;
    private String callsign;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double track;
    private List<Double> longitudes; // значения долготы за последние 24 часа
    private List<Double> latitudes; // значения широты за последние 24 часа

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcao() {
        return icao;
    }

    public double getTrack() {
        return track;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public String getRegistration() {
        return registration;
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

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setTrack(double track) {
        this.track = track;
    }

    public List<Double> getLongitudes() {
        return longitudes;
    }

    public List<Double> getLatitudes() {
        return latitudes;
    }

    public void setLongitudes(List<Double> longitudes) {
        this.longitudes = longitudes;
    }

    public void setLatitudes(List<Double> latitudes) {
        this.latitudes = latitudes;
    }
}
