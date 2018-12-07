package grupp3.iths.se.wineanddineparalell.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import grupp3.iths.se.wineanddineparalell.R;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    static final String TAG = "ProfileFragment";
    static final int IMAGE_GALLERY_REQUEST = 1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView mUsername, mFirstName, mLastName, mPhoneNumber, mEmail;
    private ImageView mProfilePicture;
    private Button mSaveChanges, mChangePassword;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mStorageReference;

    private String mUserId;
    private Uri mImageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mFirebaseFirestore = FirebaseFirestore.getInstance();

        // Connect profile_fragment Text views with ProfileFragment
        mUsername = view.findViewById(R.id.username_display_et);
        mFirstName = view.findViewById(R.id.firstname_display_et);
        mLastName = view.findViewById(R.id.lastname_display_et);
        mPhoneNumber = view.findViewById(R.id.phone_number_display_et);
        mEmail = view.findViewById(R.id.email_display_et);
        mProfilePicture = (ImageView) view.findViewById(R.id.user_avatar_im);


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
                String imageUriString;
                if (documentSnapshot.contains("profile_picture_image_uri")){
                    imageUriString = documentSnapshot.getString("profile_picture_image_uri");
                    Log.d(TAG, "document for user Ibrahim has this image uri " + imageUriString);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference().child("ProfilePictures/" + imageUriString + ".jpg");
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Glide.with(getActivity()).load(task.getResult()).into(mProfilePicture);
                            } else {
                                Log.d(TAG, "could not load the image");
                            }
                        }
                    });
                }


            }
        });

        // Save changes in profile to database.
        mSaveChanges = view.findViewById(R.id.save_changes_btn);
        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = mUsername.getText().toString();
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();

                Map<String, String> userMap = new HashMap<>();
                userMap.put("username", userName);
                userMap.put("firstname", firstName);
                userMap.put("lastname", lastName);
                userMap.put("phonenumber", phoneNumber);

                if (mImageUri != null){
                    userMap.put("profile_picture_image_uri", mImageUri.getLastPathSegment());
                }

                // Add Document to database under users collection. Set document id to current user id
                mFirebaseFirestore.collection("users").document(user.getUid()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            if (mImageUri != null){
               Log.d(TAG, "mImageUri is not empty and is " + mImageUri.getLastPathSegment());
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference().child("ProfilePictures/" + mImageUri.getLastPathSegment() + ".jpg");
                storageReference.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "image saved in firebase storage");
                        } else {
                            Log.d(TAG, "image could not be saved in firebase storage");
                        }
                    }
                });
            }

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
        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Log.d(TAG, "data received in onactivityresult method is " + mImageUri.getLastPathSegment());
            mProfilePicture.setImageURI(mImageUri);
        }
    }
}