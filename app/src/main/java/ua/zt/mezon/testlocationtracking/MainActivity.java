package ua.zt.mezon.testlocationtracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ua.zt.mezon.testlocationtracking.Json.TrackPoint;
import ua.zt.mezon.testlocationtracking.core.ServiceConnector;
import ua.zt.mezon.testlocationtracking.core.TrackingService;

public class MainActivity extends AppCompatActivity implements LocationListener, ServiceConnector.ServiceListener<TrackingService> {

    public static final String TAG = "testlocationtracking";
    private static final boolean SHOW_DEBUG = true;
    static int minTimeUpdateSeconds = 30;
    static float minDistanceUpdateMeters = 5;
    static String xmlHeader = "<?xml version='1.0' encoding='Utf-8' standalone='yes' ?>";
    static String gpxTrackHeader = "<gpx xmlns=\"http://www.topografix.com/GPX/1/0\" version=\"1.0\" creator=\"org.yriarte.tracklogger\">\n<trk>\n<trkseg>\n";
    static String gpxTrackFooter = "\n</trkseg>\n</trk>\n</gpx>\n";
    protected CountDownTimer timer;
    LocationManager mLocationManager;
    Location mLocation;
    Boolean servONduty = false;
    FileWriter gpxLogWriter;


    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            mLocation = location;

            if (SHOW_DEBUG) Log.d(TAG, "Latitude: " + String.valueOf(mLocation.getLatitude()));
            if (SHOW_DEBUG) Log.d(TAG, "Longitude: " + String.valueOf(mLocation.getLongitude()));
            if (SHOW_DEBUG) Log.d(TAG, "Altitude: " + String.valueOf(mLocation.getAltitude()));


        }

        public void onProviderDisabled(String arg0) {

        }

        public void onProviderEnabled(String arg0) {

        }

        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        }
    };
    private TrackingService service;
    private List<TrackPoint> pointsGUI = new ArrayList<>();
    private TextView tvlineServ, tvTimer, tvlineTimer;

    public void startLogging() {
        if (gpxLogWriter != null)
            stopLogging();
        if (SHOW_DEBUG) Log.d(TAG, "startLogging: " + String.valueOf((gpxLogWriter)));
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            if (SHOW_DEBUG) Log.d(TAG, "Stop Logging: " + String.valueOf((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))));
            return;
        }
        if (SHOW_DEBUG) Log.d(TAG, "startLogging: file str>" + Environment.getExternalStorageDirectory().getAbsolutePath());
        if (SHOW_DEBUG) Log.d(TAG, "startLogging: file str>" +Environment.getExternalStorageDirectory() + "/"
                + getString(R.string.app_name) + "_" + String.valueOf(Calendar.getInstance().getTime().getTime())
                + ".gpx");
        try {
            gpxLogWriter = new FileWriter(Environment.getExternalStorageDirectory() + "/"
                    + getString(R.string.app_name) + "_" + String.valueOf(Calendar.getInstance().getTime().getTime())
                    + ".gpx"
            );

            if (SHOW_DEBUG) Log.d(TAG, "startLogging: file obj>" + String.valueOf((gpxLogWriter)));
            gpxLogWriter.write(xmlHeader + gpxTrackHeader);
        } catch (Exception e) {
           // gpxLogWriter = null;
        }
        if (SHOW_DEBUG) Log.d(TAG, "startLogging: " + String.valueOf((gpxLogWriter)));
    }

    public void stopLogging() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.removeUpdates(this);
        if (gpxLogWriter == null)
            return;
        try {
            gpxLogWriter.append(gpxTrackFooter);
            gpxLogWriter.append(gpxTrackFooter);
            gpxLogWriter.close();
        } catch (Exception e) {
        }
        gpxLogWriter = null;

    }

    @Override
    public void onServiceConnected(final TrackingService service) {
        this.service = service;

        //  service.getPoints();
    }

    @Override
    public void onServiceDisconnected(final TrackingService service) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
//
//        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (mLocation == null)
//            return;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //                notification
                    if (servONduty) {
                        servONduty = false;
                        Snackbar.make(view, " stopService", Snackbar.LENGTH_LONG)
                                .setAction("Stop", null).show();
                        stopService(
                                new Intent(MainActivity.this, TrackingService.class));
                    } else {
                        servONduty = true;
                        Snackbar.make(view, " START", Snackbar.LENGTH_LONG)
                                .setAction("Stop", null).show();
                        startService(
                                new Intent(MainActivity.this, TrackingService.class));
                        TrackingService.bindService(TrackingService.class, MainActivity.this, MainActivity.this);
                    }





                }
            });
        }
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvlineServ = (TextView) findViewById(R.id.tvlineServ);
        tvlineTimer = (TextView) findViewById(R.id.tvlineTimer);
        timer = new MyCounter(minTimeUpdateSeconds * 1000, 1000);
        timer.start();
//        startService(
//                new Intent(MainActivity.this, MyIntentService.class));
        startLogging();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateServTpoint() throws IOException {

        List<TrackPoint> points = service.getPoints();
        tvlineServ.setText("Points List");
        for (TrackPoint point : points) {
            tvlineServ.append(Double.toString(point.getLatitude()) + "Long>" + Double.toString(point.getLongitude()));
        }
        if (points.size() == 99) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("ss", points);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FileWriter file = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + getString(R.string.app_name) + "_" + String.valueOf(Calendar.getInstance().getTime().getTime())
                    + ".json");
            try {
                try {
                    file.append(obj.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                file.close();

            } finally {
                file.close();
            }
        }
    }

    public String gpxTrackPoint(double lat, double lon, double ele, long time) {
        String trkpt = "<trkpt";
        trkpt += " lon=\"" + Double.valueOf(lon).toString() + "\"";
        trkpt += " lat=\"" + Double.valueOf(lat).toString() + "\"";
        trkpt += ">\n  <ele>" + Double.valueOf(ele).toString() + "</ele>\n";
        byte timebytes[] = new Timestamp(time).toString().getBytes();
        timebytes[10] = 'T';
        timebytes[19] = 'Z';
        trkpt += "  <time>" + new String(timebytes).substring(0, 20) + "</time>\n";
        trkpt += "</trkpt>\n";
        return trkpt;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//     // TODO: 26.05.2016 Later
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO: 26.05.2016 Later
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO: 26.05.2016 Later
    }

    public class MyCounter extends CountDownTimer {

        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (mLocation == null) {
                 mLocation.setLatitude(42);
                mLocation.setLongitude(42);
            }
            TrackPoint tmp = new TrackPoint(12, mLocation.getLatitude(), mLocation.getLongitude(), 0);
            if (servONduty){
                            try {
                updateServTpoint();
            } catch (IOException e) {
                e.printStackTrace();
            }
            }

            pointsGUI.add(tmp);
            tvlineTimer.append(gpxTrackPoint(tmp.getLatitude(), tmp.getLongitude(), 0, mLocation.getTime()));
            try {
                gpxLogWriter.append(gpxTrackPoint(mLocation.getLatitude(), mLocation.getLongitude(), mLocation.getAltitude(), mLocation.getTime()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Double.toString((tmp.getLatitude())+"Long>" + Double.toString( (tmp.getLongitude()));
            if (pointsGUI.size() >= 99) {
                tvlineServ.setText(R.string.TimerString);

                stopLogging();
                timer.cancel();
            } else {
                tvTimer.setText("Поехали!");
                timer.start();

            }


        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvTimer.setText((String.valueOf(millisUntilFinished / 1000)));
        }
    }


}
