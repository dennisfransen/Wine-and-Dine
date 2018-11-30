package grupp3.iths.se.wineanddineparalell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference restaurantRef = db.collection("restaurant");

    public static final String TAG = "SearchFragment";

    private EditText mSearchEditText;

    private RestaurantAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchEditText = view.findViewById(R.id.editText_search);

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(TAG, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Query query = restaurantRef.orderBy("restaurant_name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ItemInfo> options = new FirestoreRecyclerOptions.Builder<ItemInfo>()
                .setQuery(query, ItemInfo.class)
                .build();

        adapter = new RestaurantAdapter(options, getActivity().getSupportFragmentManager());

        RecyclerView recyclerView = view.findViewById(R.id.listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        return view;
    }

    //Starts to listen for changes in database (added/removed items in database)
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    //Stops listening for changes in database (added/removed items in database)
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}