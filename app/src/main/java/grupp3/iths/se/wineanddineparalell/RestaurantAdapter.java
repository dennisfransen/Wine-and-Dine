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

    @Override
    protected void onBindViewHolder(@NonNull RestaurantHolder holder, int position, @NonNull ItemInfo model) {

        holder.imgView.setImageResource(R.drawable.restaurant);
        holder.textName.setText(String.valueOf(model.getNameOfRest()));
        holder.textDistance.setText(String.valueOf(model.getDist()));
        holder.textPrice.setText(String.valueOf(model.getPrice()));
        holder.textScore.setText(String.valueOf(model.getScore()));
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list,viewGroup,false);


        return new RestaurantHolder(view);
    }

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
