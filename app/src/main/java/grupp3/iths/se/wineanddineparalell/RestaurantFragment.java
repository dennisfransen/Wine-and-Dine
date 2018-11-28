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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RestaurantFragment extends Fragment {
    private ImageView restaurantImg;

    private TextView restaurantName;
    private TextView restaurantAddress;
    private TextView phoneNumber;
    private TextView webbsite;

    private RatingBar ratingStar;
    private RatingBar ratingDollar;

    private CheckBox foodCB;
    private CheckBox drinkCB;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private CollectionReference restaurantRef = mFireStore.collection("reviews");
    private ReviewAdapter reviewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        //TODO: connect boolean/image values to RestaurantFragment/Checkbox

        //Handle Bundled information from RestaurantAdapter
        String restName = getArguments().getString("REST_NAME");
        String resAdr = getArguments().getString("REST_ADDRESS");
        String phone = getArguments().getString("REST_PHONE");
        String webSite = getArguments().getString("REST_WEBBSITE");

        float ratStar = getArguments().getFloat("REST_RATING_STAR");
        float ratDollar = getArguments().getFloat("REST_RATING_DOLLAR");


        //All fields in RestaurantFragment connected to xml fields
        restaurantImg = view.findViewById(R.id.img_view);

        restaurantName = view.findViewById(R.id.restuarant_name_tv);
        restaurantAddress = view.findViewById(R.id.address_tv);
        phoneNumber = view.findViewById(R.id.phone_nr_tv);
        webbsite = view.findViewById(R.id.webbsite_tv);

        ratingStar = view.findViewById(R.id.average_score_rb);
        ratingDollar = view.findViewById(R.id.average_price_rb);

        foodCB = view.findViewById(R.id.food_cb);
        drinkCB = view.findViewById(R.id.drink_cb);

        //Set data to the different fields in Fragment_review.xml
        restaurantName.setText(restName);
        //TODO: Check Address getter, doesn't work!
        restaurantAddress.setText(resAdr);
        phoneNumber.setText(phone);
        webbsite.setText(webSite);

        ratingStar.setRating(ratStar);
        ratingDollar.setRating(ratDollar);

        //Asks from database in wich order we want to display our reviews
        Query query = restaurantRef.orderBy("ratingStar", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ReviewInfo> options = new FirestoreRecyclerOptions.Builder<ReviewInfo>()
                .setQuery(query, ReviewInfo.class)
                .build();

        reviewAdapter = new ReviewAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.review_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(reviewAdapter);



//        mFireStore.collection("restaurant").document("0OdsOgY7wAh0mAYwgyMR").collection("reviews")
//                .document("0OdsOgY7wAh0mAYwgyMR").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                FirestoreRecyclerOptions<ReviewInfo> options = new FirestoreRecyclerOptions.Builder<ReviewInfo>().build();
//
//                reviewAdapter = new ReviewAdapter(options);
//
//                RecyclerView reviewRecyclerView = view.findViewById(R.id.review_rv);
//
//                reviewRecyclerView.setHasFixedSize(true);
//                reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                reviewRecyclerView.setAdapter(reviewAdapter);
//            }
//        });

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
