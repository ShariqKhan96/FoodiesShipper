package arkitchen.karachi.foodiesshipper.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import arkitchen.karachi.foodiesshipper.LoginActivity;
import arkitchen.karachi.foodiesshipper.MainActivity;
import arkitchen.karachi.foodiesshipper.R;
import arkitchen.karachi.foodiesshipper.Remote.APIService;
import arkitchen.karachi.foodiesshipper.Remote.RetrofitClient;
import arkitchen.karachi.foodiesshipper.Splash;
import arkitchen.karachi.foodiesshipper.common.Constants;
import arkitchen.karachi.foodiesshipper.dataprovider.MyLocationProvider;
import arkitchen.karachi.foodiesshipper.interfaces.ILocationListener;
import arkitchen.karachi.foodiesshipper.model.MyResponse;
import arkitchen.karachi.foodiesshipper.model.Notification;
import arkitchen.karachi.foodiesshipper.model.Order;
import arkitchen.karachi.foodiesshipper.model.Request;
import arkitchen.karachi.foodiesshipper.model.Sender;
import arkitchen.karachi.foodiesshipper.model.Token;
import arkitchen.karachi.foodiesshipper.model.User;
import arkitchen.karachi.foodiesshipper.services.TrackingService;
import arkitchen.karachi.foodiesshipper.utils.PrefUtils;
import arkitchen.karachi.foodiesshipper.utils.Utils;
import arkitchen.karachi.foodiesshipper.viewholders.OrderViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> firebaseRecyclerAdapter;
    Context context;
    DatabaseReference databaseReference;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = root.findViewById(R.id.menu_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        databaseReference = FirebaseDatabase.getInstance().getReference("Shippers").child(PrefUtils.getString("phone", context)).child("assignments");
        loadData();
        updateToken();
        return root;
    }

    private void updateToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Token token = new Token(instanceIdResult.getToken(), false);
                FirebaseDatabase.getInstance().getReference("Shippers").child(PrefUtils.getString("phone", context)).child("token").setValue(token);
            }
        });
    }

    private void loadData() {
        Log.e("hello", "hellojee");
        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(databaseReference, Request.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {
                viewHolder.orderName.setText("#" + model.orderId);
                viewHolder.orderPhone.setText(model.phone);

//                if(model.delivery_time!=null)
//                {
//
//                }
                Long time = Long.valueOf(model.orderId);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.getTime());

                viewHolder.deliver_at.setText("Deliver At: " + date);
                viewHolder.orderStatus.setText(model.name);
                viewHolder.orderAddress.setText(model.address);

                if (model.punch_status.equals("2")) {
                    viewHolder.start.setVisibility(View.GONE);
                    viewHolder.end.setVisibility(View.GONE);
                    viewHolder.directions.setVisibility(View.GONE);
                    viewHolder.call.setVisibility(View.GONE);
                } else if (model.punch_status.equals("1")) {
                    viewHolder.start.setVisibility(View.GONE);
                    viewHolder.end.setVisibility(View.VISIBLE);
                    viewHolder.directions.setVisibility(View.VISIBLE);
                    viewHolder.call.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.start.setVisibility(View.VISIBLE);
                    viewHolder.end.setVisibility(View.GONE);
                    viewHolder.directions.setVisibility(View.VISIBLE);
                    viewHolder.call.setVisibility(View.VISIBLE);
                }

                viewHolder.start.setOnClickListener(v -> {
                    changeAssignmentStatus(model, "start");
                });
                viewHolder.end.setOnClickListener(v -> {
                    changeAssignmentStatus(model, "end");
                });

                viewHolder.directions.setOnClickListener(v -> {
                    Utils.getInstance().showLoader(context);
                    Dexter.withActivity(getActivity())
                            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    //login
                                    FusedLocationProviderClient client = Utils.getInstance().getFusedClient(context);
                                    MyLocationProvider.getMyLocationProvider().getUserLocation(context, client, new ILocationListener() {
                                        @Override
                                        public void onLocationReceived(LocationResult locationResult, LocationCallback callback) {
                                            Utils.getInstance().dismissLoader();
                                            MyLocationProvider.getMyLocationProvider().stopUpdates(client, callback);

                                            Utils.getInstance().openMapForDirection(context, locationResult.getLastLocation().getLatitude() + "", locationResult.getLastLocation().getLongitude() + "", model.latLng.split(",")[0], model.latLng.split(",")[1]);
                                        }
                                    });

                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    Toast.makeText(context, "Enable permission to continue!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).onSameThread().check();
                });

                viewHolder.call.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + model.phone));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Cannot place call", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false));
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);


//        final List<Request> orders = new ArrayList<>();
//        FirebaseDatabase.getInstance().getReference("Shippers").child(PrefUtils.getString("phone", context)).child("assignments").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Request request = data.getValue(Request.class);
//                    if (!request.status.equals("2")) {
//                        orders.add(request);
//                    }
////                    Long time = Long.getLong(request.orderId);
////                    Calendar calendar = Calendar.getInstance();
////                    calendar.setTimeInMillis(time);
////                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    private void changeAssignmentStatus(Request model, String status) {
        if (status.equals("start")) {
            Utils.getInstance().showLoader(context);
            FirebaseDatabase.getInstance().getReference("Requests")
                    .child(model.orderId)
                    .child("punch_status").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Utils.getInstance().dismissLoader();
                    FirebaseDatabase.getInstance().getReference("Requests")
                            .child(model.orderId)
                            .child("status").setValue("3").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                Retrofit retrofit = RetrofitClient.getClient(Constants.FCM_BASE_URL);
                                APIService apiService = retrofit.create(APIService.class);
                                FirebaseDatabase.getInstance().getReference("User")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot data : snapshot.getChildren()) {
                                                    User user = data.getValue(User.class);
                                                    if (user.Phone.equals(model.phone)) {

                                                        if (user.token != null) {
                                                            String message = "Tracking of your order # " + model.orderId + " is available now!";
                                                            String title = "Tracking";
                                                            apiService.sendNotification(new Sender(user.token.token, new Notification(message, title)))
                                                                    .enqueue(new Callback<MyResponse>() {
                                                                        @Override
                                                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                                            Log.e("code", response.code() + "");
                                                                            if (response.isSuccessful()) {
                                                                                //Toast.makeText(context, "Promotion sent successfully!", Toast.LENGTH_SHORT).show();

                                                                            }
//                                                                            else
//                                                                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        @Override
                                                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                                                        }
                                                                    });
                                                        }

                                                    } else {

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                FirebaseDatabase.getInstance().getReference("User")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot data : snapshot.getChildren()) {
                                                    User user = data.getValue(User.class);
                                                    if (user.isStaff.equals("true")) {

                                                        if (user.token != null) {
                                                            String message = "Tracking of order # " + model.orderId + " is available now!";
                                                            String title = "Tracking";
                                                            apiService.sendNotification(new Sender(user.token.token, new Notification(message, title)))
                                                                    .enqueue(new Callback<MyResponse>() {
                                                                        @Override
                                                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                                            Log.e("code", response.code() + "");
                                                                            if (response.isSuccessful()) {
                                                                                //Toast.makeText(context, "Promotion sent successfully!", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                            //else
                                                                            // Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        @Override
                                                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                                                        }
                                                                    });
                                                        }

                                                    } else {

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                //apiService.sendNotification(new Sender())

                                FirebaseDatabase.getInstance().getReference("Shippers").child(PrefUtils.getString("phone", context)).child("assignments")
                                        .child(model.orderId)
                                        .child("status").setValue("3");


                                FirebaseDatabase.getInstance().getReference("Shippers").child(PrefUtils.getString("phone", context)).child("assignments")
                                        .child(model.orderId)
                                        .child("punch_status").setValue("1");

//                                FirebaseDatabase.getInstance().getReference("Shippers").child(PrefUtils.getString("phone", context)).child("assignments")
//                                        .child(model.orderId)
//                                        .child("status").setValue("4");


                                Toast.makeText(context, "Shipping Started!", Toast.LENGTH_SHORT).show();
                                ContextCompat.startForegroundService(context, new Intent(context, TrackingService.class));
                            } else
                                Toast.makeText(context, "Shipping failed to start!", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });
        } else {
            model.punch_status = "2";
            model.delivery_time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime());
//            FirebaseDatabase.getInstance().getReference("Requests")
//                    .child(model.orderId)
//                    .setValue(model).addOnCompleteListener(task -> {

            FirebaseDatabase.getInstance().getReference("Shippers").child(PrefUtils.getString("phone", context)).child("assignments")
                    .child(model.orderId)
                    .setValue(model);

            FirebaseDatabase.getInstance().getReference("Shippers").child(PrefUtils.getString("phone", context)).child("assignments")
                    .child(model.orderId)
                    .child("status").setValue("4");

            FirebaseDatabase.getInstance().getReference("Requests")
                    .child(model.orderId)
                    .child("status").setValue("4");

            FirebaseDatabase.getInstance().getReference("Requests")
                    .child(model.orderId)
                    .child("punch_status").setValue("2");

            Retrofit retrofit = RetrofitClient.getClient(Constants.FCM_BASE_URL);
            APIService apiService = retrofit.create(APIService.class);
            FirebaseDatabase.getInstance().getReference("User")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User user = data.getValue(User.class);
                                if (user.Phone.equals(model.phone)) {

                                    if (user.token != null) {
                                        String message = "Your order # " + model.orderId + " has been delivered!";
                                        String title = "DELIVERY ALERT";
                                        apiService.sendNotification(new Sender(user.token.token, new Notification(message, title)))
                                                .enqueue(new Callback<MyResponse>() {
                                                    @Override
                                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                        Log.e("code", response.code() + "");
                                                        if (response.isSuccessful()) {
                                                            //Toast.makeText(context, "Promotion sent successfully!", Toast.LENGTH_SHORT).show();

                                                        }
//                                                                            else
//                                                                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                                    }
                                                });
                                    }

                                } else {

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference("User")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User user = data.getValue(User.class);
                                if (user.isStaff.equals("true")) {

                                    if (user.token != null) {
                                        String message = "Order # " + model.orderId + " has been delivered to user!";
                                        String title = "DELIVERY CONFIRMATION";
                                        apiService.sendNotification(new Sender(user.token.token, new Notification(message, title)))
                                                .enqueue(new Callback<MyResponse>() {
                                                    @Override
                                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                        Log.e("code", response.code() + "");
                                                        if (response.isSuccessful()) {
                                                            //Toast.makeText(context, "Promotion sent successfully!", Toast.LENGTH_SHORT).show();

                                                        }
                                                        //else
                                                        // Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                                    }
                                                });
                                    }

                                } else {

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


//            if (task.isSuccessful())
            Toast.makeText(context, "Shipping Completed!", Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(context, "Shipping Completion Failed!", Toast.LENGTH_SHORT).show();

            context.stopService(new Intent(context, TrackingService.class));
//            });
        }

        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}