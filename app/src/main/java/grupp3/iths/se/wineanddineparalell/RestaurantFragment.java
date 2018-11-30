package grupp3.iths.se.wineanddineparalell;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class RestaurantFragment extends Fragment {

    public RestaurantFragment() {
        // Required empty public constructor
    }

    private TextView mRestaurantName, mRestaurantAddress, mRestaurantNumber, mRestaurantWeb;
    private CheckBox mDrink, mFood;
    private RatingBar mStarRating, mPriceRating;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private CollectionReference restaurantRef;
    private ReviewAdapter reviewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        //TODO: connect boolean/image values to RestaurantFragment/Checkbox

        //Handle Bundled information from RestaurantAdapter
        final String restName = getArguments().getString("REST_NAME");
        final float starRating = getArguments().getFloat("STAR_RATING");
        final float priceRating = getArguments().getFloat("PRICE_RATING");

        mRestaurantName = view.findViewById(R.id.restuarant_name_tv);
        mRestaurantAddress = view.findViewById(R.id.address_tv);
        mRestaurantNumber = view.findViewById(R.id.phone_nr_tv);
        mRestaurantWeb = view.findViewById(R.id.webbsite_tv);
        mDrink = view.findViewById(R.id.drink_cb);
        mFood = view.findViewById(R.id.food_cb);
        mStarRating = view.findViewById(R.id.average_score_rb);
        mPriceRating = view.findViewById(R.id.average_price_rb);

        restaurantRef = mFireStore.collection("restaurant/" + restName + "/reviews");


        mFireStore.collection("restaurant").document(restName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // OnSuccess, get credentials from database in document field.
                String restaurantName = documentSnapshot.getString("restaurant_name");
                String restaurantAddress = documentSnapshot.getString("restaurant_address");
                String restaurantNumber = documentSnapshot.getString("restaurant_phone_number");
                String restaurantWeb = documentSnapshot.getString("restaurant_website");

                // Print out company user profile from database
                mRestaurantName.append(restaurantName);
                mRestaurantAddress.append(restaurantAddress);
                mRestaurantNumber.append(restaurantNumber);
                mRestaurantWeb.append(restaurantWeb);
                mStarRating.setRating(starRating);
                mPriceRating.setRating(priceRating);


                if (documentSnapshot.getBoolean("restaurant_drink_type").equals(true))
                    mDrink.setChecked(true);

                if (documentSnapshot.getBoolean("restaurant_food_type").equals(true))
                    mFood.setChecked(true);
            }
        });

        //Asks from database in wich order we want to display our reviews
        Query query = restaurantRef.orderBy("user_name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ReviewInfo> options = new FirestoreRecyclerOptions.Builder<ReviewInfo>()
                .setQuery(query, ReviewInfo.class)
                .build();

        reviewAdapter = new ReviewAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.review_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(reviewAdapter);

        return view;
    }
    //Starts to listen for changes in database (added/removed items in database)
    @Override
    public void onStart() {
        super.onStart();
        reviewAdapter.startListening();


    }
    //Stops to listen for changes in database (added/removed items in database)
    @Override
    public void onStop() {
        super.onStop();
        reviewAdapter.stopListening();
    }
}
