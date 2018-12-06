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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference restaurantRef = db.collection("restaurant");

    public static final String TAG = "SearchFragment";

    private EditText mSearchEditText;

    private RestaurantAdapter adapter;
    private SearchAdapter mSearchAdapter;
    private List<ItemInfo> mRestaurantList;
    private List<ItemInfo> mFilteredList;
    private RecyclerView recyclerView;

    public SearchFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchEditText = view.findViewById(R.id.editText_search);

        setUpRecyclerView(view);
        populateRestaurantList();
        setUpFiltering();

        return view;
    }

    void setUpRecyclerView(View view){
        mFilteredList = new ArrayList<ItemInfo>();
        mSearchAdapter = new SearchAdapter(getActivity(), mFilteredList);
        recyclerView = view.findViewById(R.id.listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mSearchAdapter);
    }

    void populateRestaurantList(){
        mRestaurantList = new ArrayList<>();
        final List<String> placeIds = new ArrayList<>();
        restaurantRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    ItemInfo itemInfo = documentSnapshot.toObject(ItemInfo.class);
                    mRestaurantList.add(itemInfo);
                    mFilteredList.add(itemInfo);
                }
                mSearchAdapter.notifyDataSetChanged();
            }
        });
    }

    void setUpFiltering(){
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFilteredList.clear();
                for (ItemInfo itemInfo : mRestaurantList) {
                    if(itemInfo.getRestaurant_name().toLowerCase().startsWith(s.toString())){
                        mFilteredList.add(itemInfo);
                    }
                }
                mSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}