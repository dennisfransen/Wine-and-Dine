package grupp3.iths.se.wineanddineparalell;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.EXTRA_OUTPUT;

public class AddFragment extends Fragment {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int IMAGE_GALLERY_REQUEST = 2;

    private ImageView mImageRestaurant;
    private EditText mNameRestaurant, mAddress, mPhoneNumber, mWebsite, mReview;
    private RatingBar mCost, mStar;
    private CheckBox mFood, mDrink;
    private FloatingActionButton mCaptureBtn, mSaveBtn, mImageGalleryBtn;

    private Uri mImageUri = null;
    private String mCurrentPhotoPath;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;

    SearchFragment searchFragment;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        //storage = FirebaseStorage.getInstance();
        //mStorageRef = storage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        searchFragment = new SearchFragment();

        // Bound Variables from AddFragment
        mImageRestaurant = view.findViewById(R.id.image_restaurant_IV);
        mNameRestaurant = view.findViewById(R.id.name_restaurant_ET);
        mAddress = view.findViewById(R.id.location_ET);
        mPhoneNumber = view.findViewById(R.id.phone_number_ET);
        mWebsite = view.findViewById(R.id.website_ET);
        mCost = view.findViewById(R.id.ratingBar_price);
        mStar = view.findViewById(R.id.ratingBar_stars);
        mFood = view.findViewById(R.id.food_cb);
        mDrink = view.findViewById(R.id.drink_cb);
        mReview = view.findViewById(R.id.review);

        // Saving user input into database collection: reviews under restaurants collection.
        mSaveBtn = view.findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // TODO: Check if username is already in use.
                String restaurantName = mNameRestaurant.getText().toString();
                String restaurantAdress = mAddress.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();
                String webSite = mWebsite.getText().toString();
                int star = mStar.getNumStars(); // TODO: Fix the number so it does not set as 5 in database
                int cost = mCost.getNumStars();
                boolean food = mFood.isChecked();
                boolean drink = mDrink.isChecked();
                String review = mReview.getText().toString();

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("restaurant_name", restaurantName);
                userMap.put("restaurant_adress", restaurantAdress);
                userMap.put("restaurant_phone_number", phoneNumber);
                userMap.put("restaurant_website", webSite);
                userMap.put("restaurant_star_rating", star);
                userMap.put("restaurant_cost_rating", cost);
                userMap.put("restaurant_food_type", food);
                userMap.put("restaurant_drink_type", drink);
                userMap.put("user_name", user.getEmail());
                userMap.put("user_review", review);

                firebaseFirestore.collection("restaurant").document(restaurantName)
                        .collection("reviews").document().set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // TODO: Send user to SearchFragment
                        Toast.makeText(getActivity(), "Added restaurant successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Setup captureBtn to check if device has a camera.
        Context context = getActivity();
        //PackageManager packageManager = context.getPackageManager();
        mCaptureBtn = view.findViewById(R.id.capture_btn);
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Toast.makeText(getActivity(), "This device does not have a camera", Toast.LENGTH_SHORT).show();
                    mCaptureBtn.setEnabled(false);
                } else
                    mCaptureBtn.setEnabled(true);
                dispatchTakePictureIntent();
            }
        });

        // Setup ImageView to redirect to chosen Method to get image from phone Photo gallery.
        mImageGalleryBtn = view.findViewById(R.id.gallery_btn);
        mImageGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneGallery();
            }
        });

        return view;
    }


    /**
     * Take picture with camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure that there's a cameraActivity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            // Create the File where the photo should go
            File pictureFile = null;

            try {
                pictureFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getActivity(),
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Continue only if the File was successfully created
            if (pictureFile != null)
                mImageUri = FileProvider.getUriForFile(getActivity(), "grupp3.iths.se.wineanddineparalell.fileprovider", pictureFile);
            takePictureIntent.putExtra(EXTRA_OUTPUT, mImageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * The dimensions of the bitmap
     */
    private void setPic() {

        int targetW = mImageRestaurant.getWidth();
        int targetH = mImageRestaurant.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageRestaurant.setImageBitmap(bitmap);

    }

    /**
     * Choose picture from gallery when clicked on mImageRestaurant
     */
    private void openPhoneGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            //Picasso.with(this).load(mImageUri).into(mImageRestaurant);
            mImageRestaurant.setImageURI(mImageUri);
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(mCurrentPhotoPath);
            if (imgFile.exists()) {
                mImageRestaurant.setImageURI(mImageUri);

            }
        }
    }

    private void upLoadImage() {
        Uri file = Uri.fromFile(new File("path/to/images/uploads.jpg"));

        StorageReference imagesRef = mStorageRef.child("images");

        StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = imagesRef.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(),
                        "Failed to upload picture to cloud storage",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(),
                        "Image has been uploaded to cloud storage",
                        Toast.LENGTH_SHORT).show();
                // Get a URL to the uploaded content

            }
        });
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}