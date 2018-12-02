package grupp3.iths.se.wineanddineparalell;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public void onBindViewHolder(@NonNull final CustomViewHolder customViewHolder, int i) {
        final ItemInfo restaurantObj = mRestaurantList.get(i);

        customViewHolder.mTextView.setText(restaurantObj.getRestaurant_name());
        customViewHolder.mTextPrice.setRating(restaurantObj.getRestaurant_cost_rating());
        customViewHolder.mTextScore.setRating(restaurantObj.getRestaurant_star_rating());
        customViewHolder.mImageView.setImageResource(R.drawable.restaurant);

        customViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = createBundle(restaurantObj);
                RestaurantFragment restaurantFragment = new RestaurantFragment();
                restaurantFragment.setArguments(args);
                MainActivity mainActivity = (MainActivity)mContext;
                FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, restaurantFragment);
                transaction.commit();
            }
        });


        customViewHolder.mFavHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(customViewHolder.mFavHeart.getTag() == null || customViewHolder.mFavHeart.getTag().toString().equals("ic_favorite_border")){
                    customViewHolder.mFavHeart.setImageResource(R.drawable.ic_favorite_full);
                    customViewHolder.mFavHeart.setTag("ic_favorite_full");

                    String favRestaurant = customViewHolder.mTextView.getText().toString();

                    Map<String, String> userFavourite = new HashMap<>();
                    userFavourite.put("fav_restaurant", favRestaurant);

                    firebaseFirestore.collection("users").document(user.getUid())
                            .collection("users favourites").document().
                            set(userFavourite).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Restaurant added too your Wishlist!", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    customViewHolder.mFavHeart.setImageResource(R.drawable.ic_favorite_border);
                    Toast.makeText(v.getContext(), "Restaurant removed from your Wishlist!", Toast.LENGTH_SHORT).show();
                    customViewHolder.mFavHeart.setTag("ic_favorite_border");

                    Toast.makeText(v.getContext(), "Restaurant removed from your Wishlist!", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    private Bundle createBundle(ItemInfo restaurant){
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

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextView;
        private RatingBar mTextPrice;
        private RatingBar mTextScore;
        private ImageView mImageView;
        private CardView mCardView;

        private ImageView mFavHeart;

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
            mFavHeart = itemView.findViewById(R.id.favourite_heart_img);
        }
    }
}
