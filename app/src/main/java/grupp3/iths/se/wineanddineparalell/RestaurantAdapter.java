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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<ItemInfo, RestaurantAdapter.RestaurantHolder> {
    private FragmentManager mcontext;

    private String name;


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

    /**
     * Connects database to recyclerview
     *
     * @param holder   fields in item_list.xml
     * @param position where data will be set
     * @param model    helper from FirestoreRecyclerAdapter to get info from fields in database
     */
    @Override
    protected void onBindViewHolder(@NonNull final RestaurantHolder holder, final int position, @NonNull final ItemInfo model) {

        holder.imgView.setImageResource(R.drawable.restaurant);
        holder.textName.setText(model.getRestaurant_name());
        holder.textPrice.setRating(model.getRestaurant_cost_rating());
        holder.textScore.setRating(model.getRestaurant_star_rating());

        holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeReviewFragment makeReviewFragment = new MakeReviewFragment();

                FragmentTransaction fragmentTransaction = holder.mcontext.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, makeReviewFragment);
                fragmentTransaction.commit();
                //Toast.makeText(v.getContext(), "CLICK WORKS!", Toast.LENGTH_SHORT).show();

            }
        });

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantFragment restaurantFragment = new RestaurantFragment();
                name = holder.textName.getText().toString();

                Bundle data = new Bundle();
                data.putString("REST_NAME", name);
                restaurantFragment.setArguments(data);

                FragmentTransaction fragmentTransaction = holder.mcontext.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, restaurantFragment);
                fragmentTransaction.commit();
                //Toast.makeText(v.getContext(), "CLICK WORKS!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * Inflates a new item in recyclerview
     *
     * @param viewGroup
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);

        return new RestaurantHolder(view, mcontext);
    }

    /**
     * Method that deltes item in recyclerlist by swiping
     *
     * @param position
     */
    public void removeItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    /**
     * Helps to connect textview in item_list.xml with recyclerview
     */
    class RestaurantHolder extends RecyclerView.ViewHolder {

        //fields for textviews in cardview
        private ImageView imgView;
        private TextView textName;
        private RatingBar textPrice;
        private RatingBar textScore;
        private Button reviewBtn;
        private FragmentManager mcontext;
        private CardView cardview;

        RestaurantFragment restaurantFragment;

        //fields to fill in RestaurantFragment
        private ImageView restaurantImg;

        private TextView restaurantName;
        private TextView restaurantAddress;
        private TextView phoneNumber;
        private TextView webbsite;

        private RatingBar ratingStar;
        private RatingBar ratingDollar;

        private CheckBox foodCB;
        private CheckBox drinkCB;


        public RestaurantHolder(@NonNull View itemView, FragmentManager context) {
            super(itemView);

            // connect fields in cardview
            imgView = itemView.findViewById(R.id.image_view);
            textName = itemView.findViewById(R.id.rest_name_tv);
            textPrice = itemView.findViewById(R.id.avr_price_rb);
            textScore = itemView.findViewById(R.id.avr_score_rb);

            reviewBtn = itemView.findViewById(R.id.review_btn);
            mcontext = context;
            cardview = itemView.findViewById(R.id.restaurang_cv);

            restaurantFragment = new RestaurantFragment();

            //Connect fields in RestaurantFragment

            restaurantImg = itemView.findViewById(R.id.img_view);

            restaurantName = itemView.findViewById(R.id.rest_name_tv);
            restaurantAddress = itemView.findViewById(R.id.address_tv);
            phoneNumber = itemView.findViewById(R.id.phone_nr_tv);
            webbsite = itemView.findViewById(R.id.webbsite_tv);

            ratingStar = itemView.findViewById(R.id.avr_score_rb);
            ratingDollar = itemView.findViewById(R.id.avr_price_rb);

            foodCB = itemView.findViewById(R.id.food_cb);
            drinkCB = itemView.findViewById(R.id.drink_cb);

        }
    }

    public String getName() {
        return this.name;
    }
}
