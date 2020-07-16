package arkitchen.karachi.foodiesshipper.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class Utils {


    private static final Utils ourInstance = new Utils();
    private static ProgressDialog TutorBayDialog;

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {
    }

    public boolean anyFieldEmpty(String[] strings) {
        boolean isEmpty = false;
        for (int i = 0; i < strings.length; i++) {
            if (TextUtils.isEmpty(strings[i])) {
                isEmpty = true;
                break;

            }
        }
        return isEmpty;
    }

    public AlertDialog.Builder getAlertDialog(Context context, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        return builder;
    }


    public void openMapForDirection(Context context, String latitude1, String longitude1, String latitude2, String longitude2) {
//        boolean isEnable = Common.CheckEnableGPS(context);
//        if (!isEnable) {
//            Toast.makeText(context, "Turn on your location services!", Toast.LENGTH_SHORT).show();
//            return;
//        }
        StringBuilder builder = new StringBuilder();
        builder.append("http://maps.google.com/maps?saddr=");
        builder.append(latitude1);
        builder.append(",");
        builder.append(longitude1);
        builder.append("&daddr=");
        builder.append(latitude2);
        builder.append(",");
        builder.append(longitude2);
        String query = builder.toString();
        Log.e("query", query);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(query));
        context.startActivity(intent);
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    public void showLoader(Context context) {
        TutorBayDialog = new ProgressDialog(context);
        TutorBayDialog.setMessage("Please Wait..");
        TutorBayDialog.setTitle("");
        TutorBayDialog.show();
    }

    public FusedLocationProviderClient getFusedClient(Context context) {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        return client;
    }


    public void dismissLoader() {
        if (TutorBayDialog != null)
            TutorBayDialog.dismiss();
    }

    public String convertcodeToStatus(String status) {
        if (status.equals("0"))
            return "Order Pending";
        if (status.equals("1"))
            return "On My Way";
        if (status.equals("2"))
            return "Shipped";
        return "null";
    }
}
