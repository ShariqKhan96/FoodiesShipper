package arkitchen.karachi.foodiesshipper.dataprovider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import arkitchen.karachi.foodiesshipper.interfaces.ILocationListener;
import arkitchen.karachi.foodiesshipper.utils.Utils;


public class MyLocationProvider {
    public static MyLocationProvider myLocationProvider = null;

    public static MyLocationProvider getMyLocationProvider() {
        if (myLocationProvider == null)
            myLocationProvider = new MyLocationProvider();
        return myLocationProvider;
    }

    public void getUserLocation(final Context c, FusedLocationProviderClient client, final ILocationListener locationListener) {
        LocationRequest request;
        request = new LocationRequest();
        request.setInterval(5000);
        request.setFastestInterval(4000);
        request.setSmallestDisplacement(1);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        int permission = ContextCompat.checkSelfPermission(c,
                android.Manifest.permission.ACCESS_FINE_LOCATION);


        if (permission == PackageManager.PERMISSION_GRANTED && Utils.isGPSEnabled(c)) {

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    LocationCallback callback = this;
                    locationListener.onLocationReceived(locationResult, callback);
                }
            }, Looper.myLooper());
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    AlertDialog.Builder builder = Utils.getInstance().getAlertDialog(c, "Turn on the device location from device settings and enable permissions", "Error");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            c.startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }, 1000);

        }


    }

    public void stopUpdates(FusedLocationProviderClient client, LocationCallback locationCallback) {
        client.removeLocationUpdates(locationCallback);
    }
}
