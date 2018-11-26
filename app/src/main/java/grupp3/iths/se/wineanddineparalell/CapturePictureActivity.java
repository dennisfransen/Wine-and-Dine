package grupp3.iths.se.wineanddineparalell;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.EXTRA_OUTPUT;

public class CapturePictureActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int IMAGE_GALLERY_REQUEST = 2;

    private ImageView mImageRestaurant;
    private FloatingActionButton mCaptureBtn;
    private FloatingActionButton mUploadBtn;
    private FloatingActionButton mCheckInBtn;
    private FloatingActionButton mAddToFavoritesBtn;
    private ProgressBar mProgress;

    //TODO Store in Firestore Database?
    private EditText mNameRestaurant;
    private EditText distance;
    private int ratings;
    private int pricerange;
    private TextView comments;

    private Uri mImageUri = null;
    String mCurrentPhotoPath;

    FirebaseStorage storage;
    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_picture);

        // Bound Variables to CapturePictureActivity class.
        mImageRestaurant = findViewById(R.id.image_restaurant_IV);

        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();

        // Setup captureBtn to check if device has a camera.
        mCaptureBtn = findViewById(R.id.capture_btn);
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Toast.makeText(getApplicationContext(), "This device does not have a camera", Toast.LENGTH_SHORT).show();
                    mCaptureBtn.setEnabled(false);
                } else
                    mCaptureBtn.setEnabled(true);
                dispatchTakePictureIntent();
            }
        });

        // Setup UploadBtn to redirect to upload to Firebase Storage.
        mUploadBtn = findViewById(R.id.upload_btn);
        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upLoadImage();
            }
        });

        // TODO Setup CheckInBtn.
        mCheckInBtn = findViewById(R.id.check_in_btn);
        mCheckInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Checkin' in;", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO Setup adding to favorites?
        mAddToFavoritesBtn = findViewById(R.id.add_to_favorites_btn);
        mAddToFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "My favorite place", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO Not visible for the users yet that they can choose a picture from gallery
        // Setup ImageView to redirect to chosen Method to get image from Photo gallery.
        mImageRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFileChooser();
            }
        });
    }

    /**
     * Take picture with camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure that there's a cameraActivity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            // Create the File where the photo should go
            File pictureFile = null;

            try {
                pictureFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Continue only if the File was successfully created
            if (pictureFile != null)
                mImageUri = FileProvider.getUriForFile(this, "grupp3.iths.se.wineanddineparalell.fileprovider", pictureFile);
            takePictureIntent.putExtra(EXTRA_OUTPUT, mImageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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
/*
    private void upLoadImage() {
        Uri file = Uri.fromFile(new File("path/to/images/uploads.jpg"));

        StorageReference imagesRef = mStorageRef.child("images");

        StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = imagesRef.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
                Toast.makeText(CapturePictureActivity.this,
                        "Failed to upload picture to cloud storage",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = imagesRef.getDownloadUrl();
                Toast.makeText(CapturePictureActivity.this,
                        "Image has been uploaded to cloud storage",
                        Toast.LENGTH_SHORT).show();
                // Get a URL to the uploaded content

            }
        });
    }



    private void uploadImage() {

        if(mImageUri != null)
        {
            final ProgressBar progressBar = new ProgressBar(this);
            progressBar.setVisibility(View.VISIBLE);

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        */
    }

