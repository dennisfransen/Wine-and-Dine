package grupp3.iths.se.wineanddineparalell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mUserName, mFirstName, mLastName, mPhoneNumber;
    private Button mFinish;
    //private Number mPhoneNumber;

    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirestore = FirebaseFirestore.getInstance();

        mUserName = findViewById(R.id.user_name_pt);
        mFirstName = findViewById(R.id.first_name_pt);
        mLastName = findViewById(R.id.last_name_pt);
        mPhoneNumber = findViewById(R.id.phone_number_et);
        mFinish = findViewById(R.id.finish_btn);

        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mUserName.getText().toString();
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();

                Map<String, String> userMap = new HashMap<>();
                userMap.put("username", userName);
                userMap.put("firstname", firstName);
                userMap.put("lastname", lastName);
                userMap.put("phonenumber", phoneNumber);

                mFirestore.collection("users").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

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
                }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        // Send end user to MainActivity.
                        Intent gotoMainActivity = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(gotoMainActivity);
                    }
                });
            }
        });

    }
}
