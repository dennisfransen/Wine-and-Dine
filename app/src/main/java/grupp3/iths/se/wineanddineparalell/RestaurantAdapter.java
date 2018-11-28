package grupp3.iths.se.wineanddineparalell;


import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

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
        holder.textPrice.setRating(model.getRestaurant_cost_rating());
        holder.textScore.setRating(model.getRestaurant_star_rating());

        holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeReviewFragment makeReviewFragment = new MakeReviewFragment();

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

                //TODO: connect boolean/Image values from firestore to RestaurantFragments checkbox

                restaurantName = holder.textName.getText().toString();
                //TODO: Check Address getter, doesn't work!
                restaurantAddress = model.getRestaurant_address();
                restaurantPhoneNumber = model.getRestaurant_phone_number();
                restaurantWebbbsite = model.getRestaurant_website();

                restaurantAvrStar = holder.textPrice.getRating();
                restaurangAvrPrice = holder.textScore.getRating();


                //Bundles information from adapter and so it can be sent to new Fragment
                Bundle data = new Bundle();

                data.putString("REST_NAME", restaurantName);
                data.putString("REST_ADDRESS", restaurantAddress);
                data.putString("REST_PHONE", restaurantPhoneNumber);
                data.putString("REST_WEBBSITE", restaurantWebbbsite);

                data.putFloat("REST_RATING_STAR", restaurantAvrStar);
                data.putFloat("REST_RATING_DOLLAR", restaurangAvrPrice);


                restaurantFragment.setArguments(data);

                //Transaction to RestaurantFragment on card click
                FragmentTransaction fragmentTransaction = holder.mcontext.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, restaurantFragment);
                fragmentTransaction.commit();
            }
        });
    }

    //Inflates a new item in recyclerview
    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);

        return new RestaurantHolder(view, mcontext);
    }


    //Method that deletes item in recyclerlist by swiping
    /*
    public void removeItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    } */


    //Helps to connect textview in item_list.xml with recyclerview
    class RestaurantHolder extends RecyclerView.ViewHolder {

        //fields for textviews in recyclerview SearchFragment
        private ImageView imgView;
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

            textName = itemView.findViewById(R.id.rest_name_tv);
            textPrice = itemView.findViewById(R.id.avr_price_rb);
            textScore = itemView.findViewById(R.id.avr_score_rb);

            reviewBtn = itemView.findViewById(R.id.review_btn);
            cardview = itemView.findViewById(R.id.restaurang_cv);
        }
    }
}
