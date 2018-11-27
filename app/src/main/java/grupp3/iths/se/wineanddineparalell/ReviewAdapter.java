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

        holder.profilePic.setImageResource(R.drawable.profile);
        holder.ratingStar.setRating(model.getRatingStar());
        holder.userName.setText(model.getUserName());
        holder.commet.setText(model.getCommet());
        holder.commetDate.setText(model.getCommetDate());

    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_review, viewGroup, false);

        return new ReviewAdapter.ReviewHolder(view);
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePic;
        private RatingBar ratingStar;
        private TextView userName, commet, commetDate;


        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profile_pic_img);
            ratingStar = itemView.findViewById(R.id.user_score_rb);
            userName = itemView.findViewById(R.id.username_review_tv);
            commet = itemView.findViewById(R.id.review_tv);
            commetDate = itemView.findViewById(R.id.date_tv);
        }
    }
}
