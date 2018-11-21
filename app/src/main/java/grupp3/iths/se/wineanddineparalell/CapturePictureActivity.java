package grupp3.iths.se.wineanddineparalell;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.EXTRA_OUTPUT;


public class CapturePictureActivity extends AppCompatActivity {


    static final int REQUEST_PICTURE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;

    private ImageView mImageRestaurant;
    private Button mCaptureBtn;
    private Button mUploadBtn;

    private String nameRestaurant;
    private int distance;
    private int ratings;
    private int pricerange;
    private TextView comments;

    private Uri photoURI;
    String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_picture);

        // Bound ImageRestaurant, captureBtn,  to CapturePictureActivity class.
        mImageRestaurant = findViewById(R.id.image_restaurant_IV);
        mCaptureBtn = findViewById(R.id.capture_btn);
        mUploadBtn = findViewById(R.id.upload_btn);

        // Setup all buttons to redirect to chosen Method.
        mCaptureBtn.setOnClickListener(capturePicture);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getApplicationContext(), "This device does not have a camera", Toast.LENGTH_SHORT).show();
            mCaptureBtn.setEnabled(false);
        }
    }

    private View.OnClickListener capturePicture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                mCaptureBtn.setEnabled(true);
                dispatchTakeThumbNailPictureIntent();
            }
        }
    };

    private void dispatchTakeThumbNailPictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_PICTURE_CAPTURE);
        }
    }

    private void dispatchTakeFullPictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure that there's a cameraActivity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_PICTURE_CAPTURE);
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
                photoURI = FileProvider.getUriForFile(this, "grupp3.iths.se.wineanddineparalell.fileprovider", pictureFile);
            takePictureIntent.putExtra(EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_PICTURE_CAPTURE);
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //TODO FirebaseAutherisation by username?
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(mCurrentPhotoPath);
            if (imgFile.exists()) {
                mImageRestaurant.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

}

