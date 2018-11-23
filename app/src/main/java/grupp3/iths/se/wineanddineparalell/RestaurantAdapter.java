package grupp3.iths.se.wineanddineparalell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<ItemInfo, RestaurantAdapter.RestaurantHolder> {
    private int expandedPosition = -1;
    private RecyclerView reviewList;
    private Context mContext;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<ItemInfo> options) {
        super(options);
    }

//    public class ReviewViewHolder extends RecyclerView.ViewHolder {
//        private ImageView profile;
//        private TextView userName;
//        private TextView comment;
//        private TextView date;
//        private RatingBar rating;

//        private ReviewAdapter reviewAdapter;
//
//        public ReviewViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            profile = itemView.findViewById(R.id.user_pic_iv);
//            userName = itemView.findViewById(R.id.user_name_tv);
//            comment = itemView.findViewById(R.id.comment_tv);
//            date = itemView.findViewById(R.id.date_stamp_tv);
//            rating = itemView.findViewById(R.id.user_score_rb);
//
//            FirestoreRecyclerOptions<ReviewInfo> options = new FirestoreRecyclerOptions.Builder<ReviewInfo>()
//                    .build();
//
//            reviewList = itemView.findViewById(R.id.review_recycler_view);
//            reviewList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
//            reviewAdapter = new ReviewAdapter(options);
//            reviewList.setAdapter(reviewAdapter);
//        }
//    }

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
        holder.textName.setText(model.getName());
        holder.textDistance.setText(model.getDistance());
        holder.textPrice.setText(model.getCost());
        holder.textScore.setRating(model.getStar());

        holder.reviewAdapter.setData(model.get(position).getTags()); // List of Strings
        holder.reviewAdapter.setRowIndex(position);

//        final boolean isExpanded = position==expandedPosition;
//        holder.expandCard.setVisibility(isExpanded?View.VISIBLE:View.GONE);
//        holder.itemView.setActivated(isExpanded);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                expandedPosition = isExpanded ? -1:position;
//                notifyItemChanged(position);
//            }
//        });
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
        public ImageView imgView;
        public TextView textName;
        public TextView textDistance;
        public TextView textPrice;
        public RatingBar textScore;

        private ReviewAdapter reviewAdapter;

//        public ConstraintLayout expandCard;


        public RestaurantHolder(@NonNull View itemView) {
            super(itemView);

            // connect fields in cardview
            imgView = itemView.findViewById(R.id.image_view);
            textName = itemView.findViewById(R.id.rest_name_tv);
            textDistance = itemView.findViewById(R.id.distance_tv);
            textPrice = itemView.findViewById(R.id.avr_price_tv);
            textScore = itemView.findViewById(R.id.avr_score_rb);

            FirestoreRecyclerOptions<ReviewInfo> options = new FirestoreRecyclerOptions.Builder<ReviewInfo>()
                    .build();

            reviewList = itemView.findViewById(R.id.review_recycler_view);
            reviewList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            reviewAdapter = new ReviewAdapter(options);
            reviewList.setAdapter(reviewAdapter);

           // expandCard = itemView.findViewById(R.id.more_info_expand_constraintlayout);

        }
    }
}
