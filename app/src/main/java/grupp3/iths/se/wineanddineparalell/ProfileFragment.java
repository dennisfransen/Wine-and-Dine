package grupp3.iths.se.wineanddineparalell;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

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


    private static final int GALLERY_INTENT = 2;
    private String mUserId;

    private StorageReference storageReference;
    private static final int IMAGE_REUQEST = 1;
    private Uri imageUri;
    private StorageTask uplaodTask;


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


        // Creating instance of StorageReference
        storageReference = FirebaseStorage.getInstance().getReference("UserPhotos");

        // Bind mProfilePicture to image view on profile_fragment.xml
        mProfilePicture = view.findViewById(R.id.user_avatar_im);
        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        return view;
    }


    // https://www.youtube.com/watch?v=ZHmVNst_Rnc

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, IMAGE_REUQEST);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uplaodTask = fileReference.getFile(imageUri);
            uplaodTask.continueWith(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();


                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image_url", mUri);
                        mFireStore.collection("users").document(user.getUid()).update(map);
                        // TODO: Fix something here 9.53

                        pd.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });

        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REUQEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uplaodTask != null && uplaodTask.isInProgress()) {
                Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }


//    // Upload file that are picked in gallery to storage database under UserPhotos location.
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
//
//            Uri uri = data.getData();
//
//            // Get user Id and place in imageName String variable.
//            String imageName = user.getUid();
//
//            // Upload image to UserPhotos collection and name the file the same as current user id.
//            final StorageReference filepath = mStorageRef.child("UserPhotos").child(imageName);
//            final Context context = getContext();
//            final ImageView profilePicture = mProfilePicture;
//
//            // Check if file was uploaded successfully. Prompt the user with a Toast message saying: "Upload successful".
//            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Glide.with(context).load(filepath).into(profilePicture).onLoadFailed(context.getDrawable(R.drawable.app_logo));
//                    Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();
//                }
//
//                // If the upload fails. Prompt the user with a Toast message saying: "Error: + error message type".
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getActivity(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    private void openGalleryOnPhone() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, GALLERY_INTENT);
//    }


}




