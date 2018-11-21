package grupp3.iths.se.wineanddineparalell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private ImageView mUserAvatar;
    private EditText mUserName, mFirstName, mLastName, mPhoneNumber;
    private Button mFinish;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseFirestore = FirebaseFirestore.getInstance();

        mUserAvatar = findViewById(R.id.user_avatar_im);
        mUserName = findViewById(R.id.user_name_pt);
        mFirstName = findViewById(R.id.first_name_pt);
        mLastName = findViewById(R.id.last_name_pt);
        mPhoneNumber = findViewById(R.id.phone_number_et);

        // Pressing finish will add username, firstname, lastname, phonenumber to an uniqe database id that will be saved in the firestore database.
        mFinish = findViewById(R.id.finish_btn);
        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Check if username is already in use.
                String userName = mUserName.getText().toString();
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();

                Map<String, String> userMap = new HashMap<>();
                userMap.put("username", userName);
                userMap.put("firstname", firstName);
                userMap.put("lastname", lastName);
                userMap.put("phonenumber", phoneNumber);

                // Add Document to database under users collection. Set document id to current user id
                firebaseFirestore.collection("users").document(user.getUid()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Welcome the end user. Registration complete.
                        Toast.makeText(RegistrationActivity.this, "You successfully added your account, welcome!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Get error message from application and display in toast for end user.
                        String error = e.getMessage();
                        Toast.makeText(RegistrationActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Send end user to MainActivity.
                        Intent gotoMainActivity = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(gotoMainActivity);
                    }
                });
            }
        });

    }
}
