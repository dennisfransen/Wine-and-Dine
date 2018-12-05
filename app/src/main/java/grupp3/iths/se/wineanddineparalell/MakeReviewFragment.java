package grupp3.iths.se.wineanddineparalell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class MakeReviewFragment extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firebaseFirestore;

    private RatingBar mStar, mCost;
    private EditText mReview;
    private Button mSendReviewBtn;

    public MakeReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_make_review, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        // Bound Variables from MakeReviewFragment
        mReview = view.findViewById(R.id.write_review_et);
        mStar = view.findViewById(R.id.leave_rating_rb);
        mCost = view.findViewById(R.id.leave_cost_rb);

        // Saving user input into database collection: reviews under restaurants collection.
        mSendReviewBtn = view.findViewById(R.id.send_review_btn);
        mSendReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float star = mStar.getRating();
                float cost = mCost.getRating();
                String review = mReview.getText().toString();

                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("user_name", user.getDisplayName());
                reviewMap.put("user_rating", star);
                reviewMap.put("user_cost_rating", cost);
                reviewMap.put("user_review", review);

                String restName = getArguments().getString("REST_NAME");

                firebaseFirestore.collection("restaurant").document(restName)
                        .collection("reviews").document().set(reviewMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SearchFragment searchFragment = new SearchFragment();
                        switchFragment(searchFragment);
                        Toast.makeText(getActivity(), "Review successfully added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

                DocumentReference doc = firebaseFirestore.collection("restaurant").document(restName);

                addStarAvgReview(doc, star);
                addCostAvgReview(doc, cost);
            }
        });

        return view;
    }

    //Method to return to searchfragment after adding a new review
    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    private Task<Void> addCostAvgReview(final DocumentReference restaurantRef, final float rating) {
// Create reference for new rating, for use inside the transaction

        final DocumentReference ratingRef = restaurantRef.collection("reviews").document();


        // In a transaction, add the new rating and update the aggregate totals
        return firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                ItemInfo itemInfo = new ItemInfo();

                itemInfo = transaction.get(restaurantRef).toObject(ItemInfo.class);

                // Compute new number of ratings
                int newNumRatings = itemInfo.restaurant_number_of_cost_reviews + 1;

                // Compute new average rating
                double oldRatingTotal = itemInfo.restaurant_cost_rating * itemInfo.restaurant_number_of_cost_reviews;
                double newAvgRating = (oldRatingTotal + rating) / newNumRatings;

                // Set new restaurant info
                itemInfo.restaurant_number_of_cost_reviews = newNumRatings;
                itemInfo.restaurant_cost_rating = newAvgRating;

                // Update restaurant
                transaction.set(restaurantRef, itemInfo);

                // Update rating
                Map<String, Object> data = new HashMap<>();
                data.put("rating", rating);
                transaction.set(ratingRef, data, SetOptions.merge());

                return null;
            }
        });
    }

    private Task<Void> addStarAvgReview(final DocumentReference restaurantRef, final float rating) {
// Create reference for new rating, for use inside the transaction

        final DocumentReference ratingRef = restaurantRef.collection("reviews").document();


        // In a transaction, add the new rating and update the aggregate totals
        return firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                ItemInfo itemInfo = new ItemInfo();

                itemInfo = transaction.get(restaurantRef).toObject(ItemInfo.class);

                // Compute new number of ratings
                int newNumRatings = itemInfo.restaurant_number_of_reviews + 1;

                // Compute new average rating
                double oldRatingTotal = itemInfo.restaurant_star_rating * itemInfo.restaurant_number_of_reviews;
                double newAvgRating = (oldRatingTotal + rating) / newNumRatings;

                // Set new restaurant info
                itemInfo.restaurant_number_of_reviews = newNumRatings;
                itemInfo.restaurant_star_rating = newAvgRating;

                // Update restaurant
                transaction.set(restaurantRef, itemInfo);

                // Update rating
                Map<String, Object> data = new HashMap<>();
                data.put("rating", rating);
                transaction.set(ratingRef, data, SetOptions.merge());

                return null;
            }
        });
    }

}
