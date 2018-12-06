package grupp3.iths.se.wineanddineparalell;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomViewHolder> {

    public static final String TAG = "SearchAdapter";

    private List<ItemInfo> mRestaurantList;
    private List<String> mPlaceIds;
    private Context mContext;
    private GeoDataClient mGeoDataClient;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference isFavorite = firebaseFirestore.collection("users")
            .document(user.getUid()).collection("wishlist");

    public SearchAdapter(Context context, List<ItemInfo> restaurantList){
        mRestaurantList = restaurantList;
        mContext = context;
        mGeoDataClient = Places.getGeoDataClient(context);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder customViewHolder, final int i) {
        final ItemInfo restaurantObj = mRestaurantList.get(i);

        customViewHolder.mTextView.setText(restaurantObj.getRestaurant_name());
        customViewHolder.mTextPrice.setRating((float) restaurantObj.getRestaurant_cost_rating());
        customViewHolder.mTextScore.setRating((float) restaurantObj.getRestaurant_star_rating());
        customViewHolder.mImageView.setImageResource(R.drawable.restaurant);

        if(restaurantObj.getRestaurant_place_id() != null){
           setImageViewWithPlaceId(customViewHolder, restaurantObj);
        } else if(restaurantObj.getRestaurant_image_uri() != null){
            setImageViewWithImageUri(customViewHolder, restaurantObj);
        }

        customViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(restaurantObj);
            }
        });

        customViewHolder.mReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview(restaurantObj);
            }
        });

        isFavorite.document(restaurantObj.getRestaurant_name()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if( documentSnapshot.exists())
                    customViewHolder.mFavHeart.setChecked(documentSnapshot.getBoolean("favourite_is_checked"));
                else
                    customViewHolder.mFavHeart.setChecked(false);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //NO such document

            }
        });

        customViewHolder.mFavHeart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( buttonView.isPressed()){
                    if (isChecked) {
                        addFavoriteToWishlist(customViewHolder);
                    } else {
                        removeFromWishlist(customViewHolder);
                    }
                }
            }
        });
    }

    public void addFavoriteToWishlist(final CustomViewHolder customViewHolder) {

        String restaurantName = customViewHolder.mTextView.getText().toString();
        boolean favChecked = true;

        HashMap<String, Object> wishListMap = new HashMap<>();
        wishListMap.put("favourite_restaurant", restaurantName);
        wishListMap.put("favourite_is_checked", favChecked);


        firebaseFirestore.collection("users").document(user.getUid())
                .collection("wishlist").document(restaurantName).set(wishListMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Restaurant added too your Wishlist!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void removeFromWishlist(CustomViewHolder customViewHolder) {

        final String restaurantName = customViewHolder.mTextView.getText().toString();
        boolean favChecked = false;

        HashMap<String, Object> wishListMap = new HashMap<>();
        wishListMap.put("favourite_restaurant", restaurantName);
        wishListMap.put("favourite_is_checked", favChecked);

        DocumentReference docRef = firebaseFirestore.collection("users").document(user.getUid())
                .collection("wishlist").document(restaurantName);
        docRef.delete();
        Toast.makeText(mContext, "Restaurant removed from your Wishlist!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    private Bundle createBundleForRestaurantFrag(ItemInfo restaurant){
            Bundle data = new Bundle();

            data.putString("REST_NAME", restaurant.getRestaurant_name());
            data.putString("REST_ADDRESS", restaurant.getRestaurant_address());
            data.putString("REST_PHONE", restaurant.getRestaurant_phone_number());
            data.putString("REST_WEBBSITE", restaurant.getRestaurant_website());
            data.putFloat("STAR_RATING", (float) restaurant.getRestaurant_star_rating());
            data.putFloat("PRICE_RATING", (float) restaurant.getRestaurant_cost_rating());
            data.putBoolean("REST_FOOD", restaurant.isRestaurant_food_type());
            data.putBoolean("REST_DRINK", restaurant.isRestaurant_drink_type());
//TODO add restaurantimage here as well
            return data;
    }

    private Bundle createBundleForReviewFrag(ItemInfo restaurant){
        Bundle data = new Bundle();
        data.putString("REST_NAME", restaurant.getRestaurant_name());
        return data;
    }

    private void showDetail(ItemInfo restaurantObj){
        Bundle args = createBundleForRestaurantFrag(restaurantObj);
        RestaurantFragment restaurantFragment = new RestaurantFragment();
        restaurantFragment.setArguments(args);
        MainActivity mainActivity = (MainActivity)mContext;
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, restaurantFragment);
        transaction.commit();
    }


    private void addReview(ItemInfo restaurantObj) {
        Bundle args = createBundleForReviewFrag(restaurantObj);
        MakeReviewFragment makeReviewFragment = new MakeReviewFragment();
        makeReviewFragment.setArguments(args);
        MainActivity mainActivity = (MainActivity)mContext;
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, makeReviewFragment);
        transaction.commit();
    }

    private void setImageViewWithPlaceId(final CustomViewHolder customViewHolder, ItemInfo restaurantObj){
        mGeoDataClient.getPlacePhotos(restaurantObj.getRestaurant_place_id())
                .addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
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
                                customViewHolder.mImageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
    }

    private void setImageViewWithImageUri(final CustomViewHolder customViewHolder, ItemInfo restaurantObj){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("Photos/" + restaurantObj.getRestaurant_image_uri() + ".jpg");
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(mContext).load(task.getResult()).into(customViewHolder.mImageView);
            }
        });
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextView;
        private RatingBar mTextPrice;
        private RatingBar mTextScore;
        private ImageView mImageView;
        private CardView mCardView;
        private FloatingActionButton mReviewBtn;

        private ToggleButton mFavHeart;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeViews(itemView);
        }

        void initializeViews(View view){
            mTextView = itemView.findViewById(R.id.rest_name_tv);
            mTextPrice = itemView.findViewById(R.id.avr_price_rb);
            mTextScore = itemView.findViewById(R.id.avr_score_rb);
            mImageView = itemView.findViewById(R.id.image_view);
            mCardView = itemView.findViewById(R.id.restaurang_cv);
            mReviewBtn = itemView.findViewById(R.id.review_btn);
            mFavHeart = itemView.findViewById(R.id.favourite_heart_img);
        }
    }
}
