package grupp3.iths.se.wineanddineparalell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView mUsername, mFirstName, mLastName, mPhoneNumber, mEmail;
    private ImageView mProfilePicture;
    private Button mSignOut, mChangePassword;
    private ImageButton mChangeAvatar;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private StorageReference mStorageRef;

    private static final int GALLERY_INTENT = 2;

    private String mUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Connect profile_fragment Text views with ProfileFragment
        mUsername = view.findViewById(R.id.username_display_tv);
        mFirstName = view.findViewById(R.id.firstname_display_tv);
        mLastName = view.findViewById(R.id.lastname_display_tv);
        mPhoneNumber = view.findViewById(R.id.phone_number_display_tv);
        mEmail = view.findViewById(R.id.email_display_tv);

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
                mUsername.append(" "  + userName);
                mFirstName.append(" " + firstName);
                mLastName.append(" " + lastName);
                mPhoneNumber.append(" " + phoneNumber);
                mEmail.append(" " + email);
            }
        });

        // Sign out button. Send end user to LoginActivity if button is pressed.
        mSignOut = view.findViewById(R.id.signout_btn);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
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

        // Creating instance of StorageReference
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Bind mProfilePicture to image view on profile_fragment.xml
        mProfilePicture = view.findViewById(R.id.user_avatar_im);

        // Open gallery on phone to pick user profile picture that you want to upload to database.
        mChangeAvatar = view.findViewById(R.id.change_avatar_btn);
        mChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryOnPhone();
            }
        });

        return view;
    }


    // Upload file that are picked in gallery to storage database under UserPhotos location.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            Uri uri = data.getData();

            // Get user Id and place in imageName String variable.
            String imageName = user.getUid();

            // Upload image to UserPhotos collection and name the file the same as current user id.
            final StorageReference filepath = mStorageRef.child("UserPhotos").child(imageName + ".jpg");
            final Context context = getContext();
            final ImageView imageView = mProfilePicture;

            // Check if file was uploaded successfully. Prompt the user with a Toast message saying: "Upload successful".
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(context).load(filepath).into(imageView).onLoadFailed(context.getDrawable(R.drawable.app_logo));
                    Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();
                }

                // If the upload fails. Prompt the user with a Toast message saying: "Error: + error message type".
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String error = e.getMessage();
                    Toast.makeText(getActivity(), "Error" + error, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void openGalleryOnPhone() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }
}




