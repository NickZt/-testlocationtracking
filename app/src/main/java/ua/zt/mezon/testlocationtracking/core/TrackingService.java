package ua.zt.mezon.testlocationtracking.core;

import java.util.ArrayList;
import java.util.List;

import ua.zt.mezon.testlocationtracking.Json.TrackPoint;

/**
 * Created by MezM on 26.05.2016.
 */
public class TrackingService extends BaseService {


    private List<TrackPoint> points = new ArrayList<>();


    public void newPoint(final long trackId, final double latitude, final double longitude,final double elevation) {
        TrackPoint trackPoint = new TrackPoint( trackId,  latitude,  longitude,elevation);
        points.add(trackPoint);
    }

    public List<TrackPoint> getPoints() {
        return points;
    }

}
