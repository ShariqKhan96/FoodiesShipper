package arkitchen.karachi.foodiesshipper.interfaces;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public interface ILocationListener {
    void onLocationReceived(LocationResult locationResult, LocationCallback callback);
}
