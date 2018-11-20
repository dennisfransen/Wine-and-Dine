package grupp3.iths.se.wineanddineparalell;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgView;
    public TextView textName;
    public TextView textDistance;
    public TextView textPrice;
    public TextView textScore;

    /**
     * Constructor that sets fields to correct textview and imageview
     * @param itemView
     */
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        imgView = itemView.findViewById(R.id.image_view);
        textName = itemView.findViewById(R.id.rest_name_tv);
        textDistance = itemView.findViewById(R.id.distance_tv);
        textPrice = itemView.findViewById(R.id.avr_price_tv);
        textScore = itemView.findViewById(R.id.avr_score_tv);
    }

}

