package grupp3.iths.se.wineanddineparalell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomViewHolder> {

    private List<ItemInfo> mRestaurantList;
    private Context mContext;

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
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        ItemInfo restaurantObj = mRestaurantList.get(i);
        customViewHolder.mTextView.setText(restaurantObj.getRestaurant_name());
        customViewHolder.mTextPrice.setRating(restaurantObj.getRestaurant_cost_rating());
        customViewHolder.mTextScore.setRating(restaurantObj.getRestaurant_star_rating());
        customViewHolder.mImageView.setImageResource(R.drawable.restaurant);
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextView;
        private RatingBar mTextPrice;
        private RatingBar mTextScore;
        private ImageView mImageView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.rest_name_tv);
            mTextPrice = itemView.findViewById(R.id.avr_price_rb);
            mTextScore = itemView.findViewById(R.id.avr_score_rb);
            mImageView = itemView.findViewById(R.id.image_view);
        }
    }
}
