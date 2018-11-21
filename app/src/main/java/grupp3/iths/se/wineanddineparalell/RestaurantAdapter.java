package grupp3.iths.se.wineanddineparalell;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RestaurantAdapter extends FirestoreRecyclerAdapter <ItemInfo, RestaurantAdapter.RestaurantHolder> {


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
     * @param holder fields in item_list.xml
     * @param position where data will be set
     * @param model helper from FirestoreRecyclerAdapter to get info from fields in database
     */
    @Override
    protected void onBindViewHolder(@NonNull RestaurantHolder holder, int position, @NonNull ItemInfo model) {

        holder.imgView.setImageResource(R.drawable.restaurant);
        holder.textName.setText(model.getName());
        holder.textDistance.setText(model.getDistance());
        holder.textPrice.setText(model.getCost());
        holder.textScore.setText(model.getStar());
    }

    /**
     * Inflates a new item in recyclerview
     * @param viewGroup
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list,viewGroup,false);


        return new RestaurantHolder(view);
    }

    /**
     * Method that deltes item in recyclerlist by swiping
     * @param position
     */
    public void removeItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    /**
     * Helps to connect textview in item_list.xml with recyclerview
     */
    class RestaurantHolder extends RecyclerView.ViewHolder {

        public ImageView imgView;
        public TextView textName;
        public TextView textDistance;
        public TextView textPrice;
        public TextView textScore;

        public RestaurantHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.image_view);
            textName = itemView.findViewById(R.id.rest_name_tv);
            textDistance = itemView.findViewById(R.id.distance_tv);
            textPrice = itemView.findViewById(R.id.avr_price_tv);
            textScore = itemView.findViewById(R.id.avr_score_tv);
        }
    }
}
