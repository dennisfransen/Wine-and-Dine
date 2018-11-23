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

public class ReviewAdapter extends FirestoreRecyclerAdapter <ReviewInfo, ReviewAdapter.ReviewHolder>{

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ReviewAdapter(@NonNull FirestoreRecyclerOptions<ReviewInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewAdapter.ReviewHolder holder, int position, @NonNull ReviewInfo model) {
        holder.userName.setText("Herr Nilsson");
        holder.comment.setText("Kunde inte beställa banener! Dålig restaurang..");
        holder.date.setText("12/12/17");
        holder.rating.setNumStars(1);
        holder.profile.setImageResource(R.drawable.test_profile_picture);

    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_list, viewGroup,false);

        return new ReviewHolder(view);
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        private ImageView profile;
        private TextView userName;
        private TextView comment;
        private TextView date;
        private RatingBar rating;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.user_pic_iv);
            userName = itemView.findViewById(R.id.user_name_tv);
            comment = itemView.findViewById(R.id.comment_tv);
            date = itemView.findViewById(R.id.date_stamp_tv);
            rating = itemView.findViewById(R.id.user_score_rb);
        }
    }
}
