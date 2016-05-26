package ua.zt.mezon.testlocationtracking.Json;

import java.io.Serializable;

/**
 * Created by MezM on 26.05.2016.
 */


public class TrackPoint implements Serializable {


    private long trackId;

    private double latitude;


    private double longitude;


    private double elevation;

    public TrackPoint(final long trackId, final double latitude, final double longitude,final double elevation) {
        this.trackId = trackId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }


//    public TrackPoint(final long trackId, final Marker coordinate) {
//        this.trackId = trackId;
//        this.latitude = coordinate.getLatitude();
//        this.longitude = coordinate.getLongitude();
//    }

    public TrackPoint() {

    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(final long trackId) {
        this.trackId = trackId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getelevation() {
        return longitude;
    }

    public void setelevation(final double elevation) {
        this.elevation = elevation;
    }
}
