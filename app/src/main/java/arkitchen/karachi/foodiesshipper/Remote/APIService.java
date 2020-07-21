package arkitchen.karachi.foodiesshipper.Remote;





import arkitchen.karachi.foodiesshipper.model.MyResponse;
import arkitchen.karachi.foodiesshipper.model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by hp on 3/7/2018.
 */

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAZ83mPOI:APA91bFdLCDQWZkA_bgzQExTqlDztKJw8_FwguqqRHCN8WpN2ORcE_gDqIoOck4vsW8b8KSwGRM_HkLnqa114Q3JBHvJ_tCth-MP8PJJ9Nr72_4uM4nLOy0t_XGaRCGBEtuXoowSyfA4"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
