package grupp3.iths.se.wineanddineparalell;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CapturePictureActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;

    //Layoutfields, to be saved in Firebase
    private ImageView imageRestaurant;
    private String nameRestaurant;
    private int distance;
    private int ratings;
    private int pricerange;
    private TextView comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_picture);

        imageRestaurant = (ImageView) findViewById(R.id.image_restaurant_IV);
        Button capturePicture = (Button) findViewById(R.id.capture_picture_btn);
        capturePicture.setOnClickListener(capture);

        if (!PackageManager().set )

    //Buttons
            // startActivityForResult method

    }
}
