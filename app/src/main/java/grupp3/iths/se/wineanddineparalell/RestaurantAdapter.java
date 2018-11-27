package grupp3.iths.se.wineanddineparalell;


import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<ItemInfo, RestaurantAdapter.RestaurantHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<ItemInfo> options) {
        super(options);
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

        final ReviewFragment reviewFragment = new ReviewFragment();
        final FragmentManager fragmentManager;

        holder.imgView.setImageResource(R.drawable.restaurant);
        holder.textName.setText(model.getName());
        holder.textDistance.setText(model.getDistance());
        holder.textPrice.setText(model.getCost());
        holder.textScore.setRating(model.getStar());
        holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "CLICK WORKS!", Toast.LENGTH_SHORT).show();
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

        return new RestaurantHolder(view);
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
        private TextView textDistance;
        private TextView textPrice;
        private RatingBar textScore;
        private Button reviewBtn;

        ReviewFragment reviewFragment;
        

        public RestaurantHolder(@NonNull View itemView) {
            super(itemView);

            // connect fields in cardview
            imgView = itemView.findViewById(R.id.image_view);
            textName = itemView.findViewById(R.id.rest_name_tv);
            textDistance = itemView.findViewById(R.id.distance_tv);
            textPrice = itemView.findViewById(R.id.avr_price_tv);
            textScore = itemView.findViewById(R.id.avr_score_rb);
            reviewBtn = itemView.findViewById(R.id.review_btn);

//            reviewFragment = new ReviewFragment();
//
//            reviewBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.main_frame, reviewFragment);
//                    fragmentTransaction.commit();
//                }
//            });


        }
    }

}
