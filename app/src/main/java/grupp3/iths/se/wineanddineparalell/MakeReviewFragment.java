package grupp3.iths.se.wineanddineparalell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MakeReviewFragment extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firebaseFirestore;

    private RatingBar mStar;
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

        // Saving user input into database collection: reviews under restaurants collection.
        mSendReviewBtn = view.findViewById(R.id.send_review_btn);
        mSendReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float star = mStar.getRating();
                String review = mReview.getText().toString();

                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("user_name", user.getEmail());
                reviewMap.put("user_rating", star);
                reviewMap.put("user_review", review);

                String restName = getArguments().getString("REST_NAME");

                firebaseFirestore.collection("restaurant").document(restName)
                        .collection("reviews").document().set(reviewMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Review successfully added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }

}
