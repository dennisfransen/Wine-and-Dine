package grupp3.iths.se.wineanddineparalell;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class ReviewFragment extends Fragment {

    public ReviewFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private ReviewAdapter reviewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        final RecyclerView reviewRecyclerView = view.findViewById(R.id.review_rv);


        mFireStore.collection("restaurant").document("0OdsOgY7wAh0mAYwgyMR").collection("reviews")
                .document("0OdsOgY7wAh0mAYwgyMR").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FirestoreRecyclerOptions<ReviewInfo> options = new FirestoreRecyclerOptions.Builder<ReviewInfo>().build();
                reviewAdapter = new ReviewAdapter(options);

                reviewRecyclerView.setHasFixedSize(true);
                reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                reviewRecyclerView.setAdapter(reviewAdapter);
            }
        });



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        reviewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        reviewAdapter.stopListening();
    }
}
