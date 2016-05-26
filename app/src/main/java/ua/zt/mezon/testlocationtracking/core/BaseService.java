package ua.zt.mezon.testlocationtracking.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import ua.zt.mezon.testlocationtracking.MainActivity;
import ua.zt.mezon.testlocationtracking.R;

/**
 * Created by MezM on 26.05.2016.
 */
public class BaseService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Test loc app")
                .setContentText("Doing watch your movement...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return new ServiceConnector.ServiceBinder<>(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public static <T extends Service> ServiceConnector<T> bindService(final Class<T> serviceClazz, final Context context, final ServiceConnector.ServiceListener<T> listener) {
        ServiceConnector<T> serviceConnection = new ServiceConnector<T>(listener);
        Intent intent = new Intent(context, serviceClazz);
        Intent startServiceIntent = new Intent(context, serviceClazz);
        context.startService(startServiceIntent);
        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        return serviceConnection;
    }


}
