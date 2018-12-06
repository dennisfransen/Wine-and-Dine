package grupp3.iths.se.wineanddineparalell;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<ItemInfo, RestaurantAdapter.RestaurantHolder> {
    private FragmentManager mcontext;

    // Variables to send into RestaurantFragment via bundles
    private ImageView restaurantImage;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPhoneNumber;
    private String restaurantWebbbsite;
    private float restaurantAvrStar;
    private float restaurangAvrPrice;
    private boolean restaurantFoodCB;
    private boolean restaurantDrinkCB;

    // private GeoDataClient mGeoDataClient;



    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     * @param context
     */
    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<ItemInfo> options, FragmentManager context) {
        super(options);
        mcontext = context;

    }


    // Connects database to recyclerview
    // holder: fields in item_list.xml
    // position: where data will be set in recyclerview
    // model: helper from FirestoreRecyclerAdapter to get info from fields in database
    @Override
    protected void onBindViewHolder(@NonNull final RestaurantHolder holder, final int position, @NonNull final ItemInfo model) {

        //Holders for cardview in SearchFragment
        holder.imgView.setImageResource(R.drawable.restaurant);
        holder.textName.setText(model.getRestaurant_name());
        holder.textPrice.setRating((float) model.getRestaurant_cost_rating());
        holder.textScore.setRating((float) model.getRestaurant_star_rating());


        //TODO Images don't change, get from Google or firebase Storage
//        if(model.getRestaurant_place_id() != null){
//            setImageViewWithPlaceId(holder.imgView, model);
//        } else if(model.getRestaurant_image_uri() != null){
//            setImageViewWithImageUri(holder, model);
//        }

        holder.favHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.favHeart.setImageResource(R.drawable.ic_favorite_full);
                Toast.makeText(v.getContext(), "Restaurant added too your Wishlist!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeReviewFragment makeReviewFragment = new MakeReviewFragment();

                //Bundles restaurant name from adapter and so it can be sent to new Fragment
                Bundle data = new Bundle();

                //Get the name of restaurant your are on when clicking revview btn
                restaurantName = holder.textName.getText().toString();
                data.putString("REST_NAME", restaurantName);

                makeReviewFragment.setArguments(data);

                //Transaction to MakeReviewFragment on button click
                FragmentTransaction fragmentTransaction = holder.mcontext.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, makeReviewFragment);
                fragmentTransaction.commit();

            }
        });

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantFragment restaurantFragment = new RestaurantFragment();

                restaurantName = holder.textName.getText().toString();
                restaurantAddress = model.getRestaurant_address();
                restaurantPhoneNumber = model.getRestaurant_phone_number();
                restaurantWebbbsite = model.getRestaurant_website();

                restaurantAvrStar = holder.textPrice.getRating();
                restaurangAvrPrice = holder.textScore.getRating();
                restaurantFoodCB = model.isRestaurant_food_type();
                restaurantDrinkCB = model.isRestaurant_drink_type();


                //Bundles information from adapter and so it can be sent to new Fragment
                Bundle data = new Bundle();

                data.putString("REST_NAME", restaurantName);
                data.putString("REST_ADDRESS", restaurantAddress);
                data.putString("REST_PHONE", restaurantPhoneNumber);
                data.putString("REST_WEBBSITE", restaurantWebbbsite);

                data.putFloat("STAR_RATING", restaurantAvrStar);
                data.putFloat("PRICE_RATING", restaurangAvrPrice);
                data.putBoolean("REST_FOOD", restaurantFoodCB);
                data.putBoolean("REST_DRINK", restaurantDrinkCB);

                restaurantFragment.setArguments(data);

                //Transaction to RestaurantFragment on card click
                FragmentTransaction fragmentTransaction = holder.mcontext.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, restaurantFragment);
                fragmentTransaction.commit();
            }
        });
    }

//    private void setImageViewWithPlaceId(final RestaurantAdapter customViewHolder, ItemInfo restaurantObj){
//        mGeoDataClient.getPlacePhotos(restaurantObj.getRestaurant_place_id())
//                .addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
//                        PlacePhotoMetadataResponse photos =  task.getResult();
//                        PlacePhotoMetadataBuffer placePhotoMetadataBuffer = photos.getPhotoMetadata();
//                        PlacePhotoMetadata metadata = placePhotoMetadataBuffer.get(0);
//                        mGeoDataClient.getPhoto(metadata).addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
//                            @Override
//                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
//                                PlacePhotoResponse response = task.getResult();
//                                Bitmap bitmap = response.getBitmap();
//                                customViewHolder.restaurantImage.setImageBitmap(bitmap);
//                            }
//                        });
//                    }
//                });
//    }
//
//    private void setImageViewWithImageUri(final RestaurantAdapter customViewHolder, ItemInfo restaurantObj){
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageReference = storage.getReference().child("Photos/" + restaurantObj.getRestaurant_image_uri() + ".jpg");
//        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                Glide.with(mcontext).load(task.getResult()).into(customViewHolder.restaurantImage);
//            }
//        });
//    }



    //Inflates a new item in recyclerview
    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);

        return new RestaurantHolder(view, mcontext);
    }

    //Helps to connect textview in item_list.xml with recyclerview
    class RestaurantHolder extends RecyclerView.ViewHolder {

        //fields for textviews in recyclerview SearchFragment
        private ImageView imgView;
        private ImageView favHeart;
        private TextView textName;
        private RatingBar textPrice;
        private RatingBar textScore;
        private Button reviewBtn;
        private FragmentManager mcontext;
        private CardView cardview;


        public RestaurantHolder(@NonNull View itemView, FragmentManager context) {
            super(itemView);
            mcontext = context;

            // connect fields in cardview/SearchFragment
            imgView = itemView.findViewById(R.id.image_view);
            favHeart = itemView.findViewById(R.id.favourite_heart_img);

            textName = itemView.findViewById(R.id.rest_name_tv);
            textPrice = itemView.findViewById(R.id.avr_price_rb);
            textScore = itemView.findViewById(R.id.avr_score_rb);

            reviewBtn = itemView.findViewById(R.id.review_btn);
            cardview = itemView.findViewById(R.id.restaurang_cv);
        }
    }
}
