package grupp3.iths.se.wineanddineparalell;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomViewHolder> {

    private List<ItemInfo> mRestaurantList;
    private Context mContext;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public SearchAdapter(Context context, List<ItemInfo> restaurantList){
        mRestaurantList = restaurantList;
        mContext = context;
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
        customViewHolder.mTextPrice.setRating(restaurantObj.getRestaurant_cost_rating());
        customViewHolder.mTextScore.setRating(restaurantObj.getRestaurant_star_rating());
        customViewHolder.mImageView.setImageResource(R.drawable.restaurant);

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

        // Onclicklistener for the heart in restaurant item so user can add/remove restaurant to wishlist
        customViewHolder.mFavHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                
                //If heart don't have a tag or have the tag ic_favorite_border
                // (that heart image is not filled) we want to add restaurant too wishlist with click
                if(customViewHolder.mFavHeart.getTag() == null || customViewHolder.mFavHeart.getTag().toString().equals("not added")){
                    addFavoriteToWishlist(customViewHolder, v);

                    // If the heart already has been clicked and saved to wishlist, then we want the
                    // click to delete restaurant from wishlist
                } else {
                    removeFromWishlist(customViewHolder, v);

                }
            }
        });
    }

    public void addFavoriteToWishlist(final CustomViewHolder customViewHolder, final View v){

        //Change tag to added and change image to filled heart
        customViewHolder.mFavHeart.setTag("added");
        customViewHolder.mFavHeart.setChecked(true);
        String restaurantName = customViewHolder.mTextView.getText().toString();
        boolean favChecked = true;

        HashMap<String, Object> wishListMap = new HashMap<>();
        wishListMap.put("favourite_restaurant", restaurantName);
        wishListMap.put("favourite_is_checked", favChecked);

        firebaseFirestore.collection("users").document(user.getUid())
                .collection("wishlist").document(restaurantName).set(wishListMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(v.getContext(), "Restaurant added too your Wishlist!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void removeFromWishlist(CustomViewHolder customViewHolder, View v) {

        //Change tag to added and change image to filled heart
        customViewHolder.mFavHeart.setTag("not added");
        customViewHolder.mFavHeart.setChecked(false);
        final String restaurantName = customViewHolder.mTextView.getText().toString();
        boolean favChecked = false;

        HashMap<String, Object> wishListMap = new HashMap<>();
        wishListMap.put("favourite_restaurant", restaurantName);
        wishListMap.put("favourite_is_checked", favChecked);

        DocumentReference docRef = firebaseFirestore.collection("users").document(user.getUid())
                .collection("wishlist").document(restaurantName);
        docRef.delete();
        Toast.makeText(v.getContext(), "Restaurant removed from your Wishlist!", Toast.LENGTH_SHORT).show();
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
            data.putFloat("STAR_RATING", restaurant.getRestaurant_star_rating());
            data.putFloat("PRICE_RATING", restaurant.getRestaurant_cost_rating());
            data.putBoolean("REST_FOOD", restaurant.isRestaurant_food_type());
            data.putBoolean("REST_DRINK", restaurant.isRestaurant_drink_type());

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
