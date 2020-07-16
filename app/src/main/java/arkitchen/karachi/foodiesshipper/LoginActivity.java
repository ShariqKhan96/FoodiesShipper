package arkitchen.karachi.foodiesshipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import arkitchen.karachi.foodiesshipper.model.Shipper;
import arkitchen.karachi.foodiesshipper.utils.PrefUtils;
import arkitchen.karachi.foodiesshipper.utils.Utils;

import info.hoang8f.widget.FButton;

public class LoginActivity extends AppCompatActivity {

    // @BindView(R.id.phoneNumber)
    MaterialEditText phoneNumber;
    //@BindView(R.id.password)
    MaterialEditText password;
    //@BindView(R.id.signIn)
    FButton signIn;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneNumber = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);

        databaseReference = FirebaseDatabase.getInstance().getReference("Shippers");

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.getInstance().anyFieldEmpty(new String[]{password.getText().toString(), phoneNumber.getText().toString()})) {

                    Utils.getInstance().showLoader(LoginActivity.this);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Utils.getInstance().dismissLoader();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Shipper shipper = data.getValue(Shipper.class);
                                if (shipper.phone.equals(phoneNumber.getText().toString()))
                                    if (shipper.password.equals(password.getText().toString())) {

                                        PrefUtils.putString("login", "true", LoginActivity.this);
                                        PrefUtils.putString("phone", phoneNumber.getText().toString(), LoginActivity.this);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        break;
                                    }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Utils.getInstance().dismissLoader();
                            Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    Toast.makeText(LoginActivity.this, "Some field(s) empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}