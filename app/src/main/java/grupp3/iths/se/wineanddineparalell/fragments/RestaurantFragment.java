package grupp3.iths.se.wineanddineparalell.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import grupp3.iths.se.wineanddineparalell.R;
import grupp3.iths.se.wineanddineparalell.adapter.ReviewAdapter;
import grupp3.iths.se.wineanddineparalell.models.ItemInfo;
import grupp3.iths.se.wineanddineparalell.models.ReviewInfo;

public class RestaurantFragment extends Fragment {

    public RestaurantFragment() {
        // Required empty public constructor
    }

    private TextView mRestaurantName, mRestaurantAddress, mRestaurantNumber, mRestaurantWeb;
    private CheckBox mDrink, mFood;
    private RatingBar mStarRating, mPriceRating;
    private ImageView mImageView;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private CollectionReference restaurantRef;
    private ReviewAdapter reviewAdapter;
    private GeoDataClient mGeoDataClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        mGeoDataClient = Places.getGeoDataClient(getActivity());

        //Handle Bundled information from RestaurantAdapter
        final String restName = getArguments().getString("REST_NAME");
        final float starRating = getArguments().getFloat("STAR_RATING");
        final float priceRating = getArguments().getFloat("PRICE_RATING");


        mRestaurantName = view.findViewById(R.id.restuarant_name_tv);
        mRestaurantAddress = view.findViewById(R.id.address_tv);
        mRestaurantNumber = view.findViewById(R.id.phone_nr_tv);
        mRestaurantWeb = view.findViewById(R.id.webbsite_tv);
        mDrink = view.findViewById(R.id.drink_cb);
        mFood = view.findViewById(R.id.food_cb);
        mStarRating = view.findViewById(R.id.average_score_rb);
        mPriceRating = view.findViewById(R.id.average_price_rb);
        mImageView = view.findViewById(R.id.img_view);

        view.findViewById(R.id.address_tv).setOnClickListener(this::addressClick);

        restaurantRef = mFireStore.collection("restaurant/" + restName + "/reviews");


        mFireStore.collection("restaurant").document(restName).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    ItemInfo itemInfo = documentSnapshot.toObject(ItemInfo.class);
                    setUpDataInViews(itemInfo);
                }
            }
        });

        //Asks from database in wich order we want to display our reviews
        Query query = restaurantRef.orderBy("user_name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ReviewInfo> options = new FirestoreRecyclerOptions.Builder<ReviewInfo>()
                .setQuery(query, ReviewInfo.class)
                .build();

        reviewAdapter = new ReviewAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.review_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(reviewAdapter);

        return view;



    }
    //Starts to listen for changes in database (added/removed items in database)
    @Override
    public void onStart() {
        super.onStart();
        reviewAdapter.startListening();


    }
    //Stops to listen for changes in database (added/removed items in database)
    @Override
    public void onStop() {
        super.onStop();
        reviewAdapter.stopListening();
    }

    public void addressClick(View view){
        Uri addr = Uri.parse("geo:0,0?q="+((TextView)view).getText());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, addr);
        mapIntent.setPackage("com.google.android.apps.maps");
        if(mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivity(mapIntent);
        }
    }

    private void setUpDataInViews(ItemInfo itemInfo){

        mRestaurantName.setText(itemInfo.getRestaurant_name());
        mRestaurantAddress.setText(itemInfo.getRestaurant_address());
        mRestaurantNumber.setText(itemInfo.getRestaurant_phone_number());
        mRestaurantWeb.setText(itemInfo.getRestaurant_website());

        if(itemInfo.isRestaurant_drink_type()){
            mDrink.setChecked(true);
        }
        if(itemInfo.isRestaurant_food_type()){
            mFood.setChecked(true);
        }

        mStarRating.setRating((float) itemInfo.getRestaurant_star_rating());
        mPriceRating.setRating((float) itemInfo.getRestaurant_cost_rating());

        if(itemInfo.getRestaurant_place_id() != null){

            mGeoDataClient.getPlacePhotos(itemInfo.getRestaurant_place_id()).addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    PlacePhotoMetadataResponse photos =  task.getResult();
                    PlacePhotoMetadataBuffer placePhotoMetadataBuffer = photos.getPhotoMetadata();
                    PlacePhotoMetadata metadata = placePhotoMetadataBuffer.get(0);
                    mGeoDataClient.getPhoto(metadata).addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse response = task.getResult();
                            Bitmap bitmap = response.getBitmap();
                            mImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            });
        } else if(itemInfo.getRestaurant_image_uri() != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("Photos/" + itemInfo.getRestaurant_image_uri() + ".jpg");
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(getActivity()).load(task.getResult()).into(mImageView);
                }
            });
        }
    }
}
