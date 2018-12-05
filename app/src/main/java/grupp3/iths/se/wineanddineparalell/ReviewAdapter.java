package grupp3.iths.se.wineanddineparalell;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ReviewAdapter extends FirestoreRecyclerAdapter<ReviewInfo, ReviewAdapter.ReviewHolder> {

    public ReviewAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewHolder holder, int position, @NonNull ReviewInfo model) {

        holder.ratingCost.setRating(model.getUser_cost());
        holder.ratingStar.setRating(model.getUser_rating());
        holder.userName.setText(model.getUser_name());
        holder.commet.setText(model.getUser_review());

    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_review, viewGroup, false);

        return new ReviewAdapter.ReviewHolder(view);
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePic;
        private RatingBar ratingStar, ratingCost;
        private TextView userName, commet, commetDate;


        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            ratingCost = itemView.findViewById(R.id.user_price_rb);
            ratingStar = itemView.findViewById(R.id.user_score_rb);
            userName = itemView.findViewById(R.id.username_review_tv);
            commet = itemView.findViewById(R.id.review_tv);
        }
    }
}
