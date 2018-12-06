package grupp3.iths.se.wineanddineparalell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView mUsername, mFirstName, mLastName, mPhoneNumber, mEmail;
    private ImageView mProfilePicture;
    private Button mSaveChanges, mChangePassword;
    private ImageButton mChangeAvatar;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore;

    private String mUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        // Connect profile_fragment Text views with ProfileFragment
        mUsername = view.findViewById(R.id.username_display_et);
        mFirstName = view.findViewById(R.id.firstname_display_et);
        mLastName = view.findViewById(R.id.lastname_display_et);
        mPhoneNumber = view.findViewById(R.id.phone_number_display_et);
        mEmail = view.findViewById(R.id.email_display_et);

        // Get current end user ID
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFireStore.collection("users").document(mUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // OnSuccess, get credentials from database in document field.
                String userName = documentSnapshot.getString("username");
                String firstName = documentSnapshot.getString("firstname");
                String lastName = documentSnapshot.getString("lastname");
                String phoneNumber = documentSnapshot.getString("phonenumber");
                // Fetch email data directly from current user.
                String email = user.getEmail();

                // Print out end user credentials in profile_fragment text views.
                mUsername.setText(userName);
                mFirstName.setText(firstName);
                mLastName.setText(lastName);
                mPhoneNumber.setText(phoneNumber);
                mEmail.setText(email);
            }
        });

        // Save changes in profile to database.
        mSaveChanges = view.findViewById(R.id.save_changes_btn);
        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mUsername.getText().toString();
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
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
                        user.updateProfile(profileUpdates);
                        Toast.makeText(getActivity(), "You successfully updated your profile", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Get error message from application and display in toast for end user.
                        String error = e.getMessage();
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

        // Change password button. Send end user a "Change password" email.
        mChangePassword = view.findViewById(R.id.change_pw_btn);
        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(user.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Check your email to change your password", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


        // Bind mProfilePicture to image view on profile_fragment.xml
        mProfilePicture = view.findViewById(R.id.user_avatar_im);
        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return view;
    }

}




