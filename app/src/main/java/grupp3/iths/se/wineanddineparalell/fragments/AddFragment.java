package grupp3.iths.se.wineanddineparalell.fragments;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import grupp3.iths.se.wineanddineparalell.R;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.EXTRA_OUTPUT;

public class AddFragment extends Fragment {

    static final String TAG = "AddFragment";

    //constant fields
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int IMAGE_GALLERY_REQUEST = 2;
    static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;

    private ImageView mImageRestaurant, mLogoApp;
    private EditText mNameRestaurant, mAddress, mPhoneNumber, mWebsite;
    private CheckBox mFood, mDrink;
    private FloatingActionButton mGooglePlacesBtn, mCaptureBtn, mSaveBtn, mImageGalleryBtn;

    private Uri mImageUri = null;
    private String mPlaceIdForImage;
    private String mCurrentPhotoPath;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference mStorageRef;

    SearchFragment searchFragment;
    private GeoDataClient mGeoDataClient;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mGeoDataClient = Places.getGeoDataClient(getActivity());

        searchFragment = new SearchFragment();

        // Bound Variables from AddFragment
        mImageRestaurant = view.findViewById(R.id.image_restaurant_IV);
        mLogoApp = view.findViewById(R.id.logo_IV);
        mNameRestaurant = view.findViewById(R.id.name_restaurant_ET);
        mAddress = view.findViewById(R.id.location_ET);
        mPhoneNumber = view.findViewById(R.id.phone_number_ET);
        mWebsite = view.findViewById(R.id.website_ET);
        mFood = view.findViewById(R.id.food_cb);
        mDrink = view.findViewById(R.id.drink_cb);

        // Saving user input into database collection: reviews under restaurants collection.
        mSaveBtn = view.findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restaurantName = mNameRestaurant.getText().toString();
                String restaurantAdress = mAddress.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();
                String webSite = mWebsite.getText().toString();
                boolean food = mFood.isChecked();
                boolean drink = mDrink.isChecked();

                Map<String, Object> restaurantMap = new HashMap<>();
                restaurantMap.put("restaurant_name", restaurantName);
                restaurantMap.put("restaurant_address", restaurantAdress);
                restaurantMap.put("restaurant_phone_number", phoneNumber);
                restaurantMap.put("restaurant_website", webSite);
                restaurantMap.put("restaurant_food_type", food);
                restaurantMap.put("restaurant_drink_type", drink);

                if(mImageUri == null){
                    restaurantMap.put("restaurant_place_id", mPlaceIdForImage);
                } else if (mImageUri != null){
                    restaurantMap.put("restaurant_image_uri", mImageUri.getLastPathSegment());
                }

                firebaseFirestore.collection("restaurant").document(restaurantName).set(restaurantMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mNameRestaurant.setText("Name");
                        mAddress.setText("Address ");
                        mPhoneNumber.setText("Phone number");
                        mWebsite.setText("Website");
                        mFood.setChecked(false);
                        mDrink.setChecked(false);

                        switchFragment(searchFragment);

                        Toast.makeText(getActivity(), "Added restaurant successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });


                if(mImageUri != null){

                    Log.d(TAG, "Image name is " + mImageUri.getLastPathSegment());
                    StorageReference storageReference = mStorageRef.child("Photos").child(mImageUri.getLastPathSegment() + ".jpg");
                    storageReference.putFile(mImageUri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.d(TAG, task.toString());
                                    }
                                }
                            });
                }
            }
        });

        // Setup captureBtn to check if device has a camera, if camera is available redirect to take picture with camera.
        mCaptureBtn = view.findViewById(R.id.capture_btn);
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Toast.makeText(getActivity(), "This device does not have a camera", Toast.LENGTH_SHORT).show();
                    mCaptureBtn.setEnabled(false);
                } else
                    mCaptureBtn.setEnabled(true);
                takePicture();
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

        mGooglePlacesBtn = view.findViewById(R.id.fab_google_places);
        mGooglePlacesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGooglePlaces();
            }
        });

        return view;
    }

    //Method to return to searchfragment after adding a new review
    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Take picture with camera
     */
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure that there's a cameraActivity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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

    /*
    Get the dimensions of the picture in the Imageview and save size of picture
     */
    private void setPic() {
        //TODO  hela koden skulle sättas i Jobscheduler för att undvika jobba hela grafiska gränsnittet

        int targetW = mImageRestaurant.getWidth();
        int targetH = mImageRestaurant.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageRestaurant.setImageBitmap(bitmap);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, bos);
        try {
            FileOutputStream fos = new FileOutputStream(mCurrentPhotoPath);
            fos.write(bos.toByteArray());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * Upload file that are picked in gallery or direct from camera to show in Imageview.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mImageRestaurant.setImageURI(mImageUri);
            mLogoApp.setVisibility(View.INVISIBLE);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                setPic();
                //mImageRestaurant.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                mImageUri = Uri.fromFile(new File(mCurrentPhotoPath));
                mLogoApp.setVisibility(View.INVISIBLE);
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(getActivity(), data);

            if (place.getPlaceTypes().contains(79)) {

                mNameRestaurant.setText(place.getName());
                mAddress.setText(place.getAddress());
                mPhoneNumber.setText(place.getPhoneNumber());
                mWebsite.setText(place.getWebsiteUri().toString());
                mImageRestaurant.setImageResource(R.drawable.restaurant);

                mGeoDataClient.getPlacePhotos(place.getId()).addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        if(task.isSuccessful()){
                            PlacePhotoMetadataResponse photos = task.getResult();
                            PlacePhotoMetadataBuffer placePhotoMetadataBuffer = photos.getPhotoMetadata();
                            PlacePhotoMetadata metadata = placePhotoMetadataBuffer.get(0);
                            mGeoDataClient.getPhoto(metadata).addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                    PlacePhotoResponse response = task.getResult();
                                    Bitmap bitmap = response.getBitmap();
                                    mImageRestaurant.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }
                });

                mPlaceIdForImage = place.getId();
                mLogoApp.setVisibility(View.INVISIBLE);

            } else {
                Toast.makeText(getActivity(), "Please select a restaurant!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void uploadImagesToFirebase() {

        // TODO: Check if username is already in use.


    }


    void openGooglePlaces() {
        try {
            AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("SE").build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(getActivity());

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageUri = null; 
    }
}