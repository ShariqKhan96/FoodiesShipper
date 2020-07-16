package arkitchen.karachi.foodiesshipper.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

import arkitchen.karachi.foodiesshipper.R;
import arkitchen.karachi.foodiesshipper.dataprovider.MyLocationProvider;
import arkitchen.karachi.foodiesshipper.utils.PrefUtils;
import arkitchen.karachi.foodiesshipper.utils.Utils;

public class TrackingService extends Service {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setContentTitle("FOODIES")
                .setContentText("Tracking...")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .build();
        startForeground(1, notification);

        startGeoFencing();
        return START_NOT_STICKY;


    }

    private void startGeoFencing() {
        fusedLocationProviderClient = Utils.getInstance().getFusedClient(this);
        MyLocationProvider.getMyLocationProvider().getUserLocation(this, fusedLocationProviderClient, (locationResult, callback) -> {
            firebaseLogger(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
            Log.e("newLocation", locationResult.getLastLocation().toString());
            this.locationCallback = callback;
        });
    }

    private void firebaseLogger(double latitude, double longitude) {
        FirebaseDatabase.getInstance().getReference("ShipperLocation").child(PrefUtils.getString("phone", this)).setValue(new LatLng(latitude, longitude));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLocationProvider.getMyLocationProvider().stopUpdates(fusedLocationProviderClient, locationCallback);
    }
}