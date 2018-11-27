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
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.EXTRA_OUTPUT;

public class AddFragment extends Fragment {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int IMAGE_GALLERY_REQUEST = 2;

    private ImageView mImageRestaurant;
    private FloatingActionButton mCaptureBtn;
    private FloatingActionButton mUploadBtn;
    private FloatingActionButton mCheckInBtn;
    private FloatingActionButton mImageGalleryBtn;
    private ProgressBar mProgress;

    private EditText mNameRestaurant;

    private Uri mImageUri = null;
    String mCurrentPhotoPath;

    FirebaseStorage storage;
    StorageReference mStorageRef;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Bound Variables to CapturePictureActivity class.
        mImageRestaurant = view.findViewById(R.id.image_restaurant_IV);

        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();

        // Setup captureBtn to check if device has a camera.
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
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

        // Setup UploadBtn to redirect to upload to Firebase Storage.
        mUploadBtn = view.findViewById(R.id.save_btn);
        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upLoadImage();
            }
        });

        // TODO Setup CheckInBtn if time.
        mCheckInBtn = view.findViewById(R.id.check_in_btn);
        mCheckInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Checkin' in;", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup ImageView to redirect to chosen Method to get image from Photo gallery.
        mImageGalleryBtn = view.findViewById(R.id.gallery_btn);
        mImageGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFileChooser();
            }
        });

        mProgress = view.findViewById(R.id.progressBar);
        mProgress.setVisibility(View.GONE);

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
    private void OpenFileChooser() {
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
}