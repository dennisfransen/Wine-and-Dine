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

    private RestaurantAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Query query = restaurantRef.orderBy("restaurant_name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ItemInfo> options = new FirestoreRecyclerOptions.Builder<ItemInfo>()
                .setQuery(query, ItemInfo.class)
                .build();

        adapter = new RestaurantAdapter(options, getActivity().getSupportFragmentManager());

        RecyclerView recyclerView = view.findViewById(R.id.listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //ItemTouchHelper sets witch direction deletefunction will be, and helps us get the
        // position(item) that are being deleted
/*        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                adapter.removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);*/

//
//        mEditText = view.findViewById(R.id.editText);
//        mListView = view.findViewById(R.id.listView);
//        mListOfPredictions = new ArrayList<>();
//        mListOfPredictions.add("HELLO WORLD");
//
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mListOfPredictions);
//        mListView.setAdapter(arrayAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                AutocompletePrediction MyAutocompletePrediction=mAutocompletePredictions.get(position);
//
//                mGeoDataClient.getPlaceById(MyAutocompletePrediction.getPlaceId()).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
//                    @Override
//                    public void onSuccess(PlaceBufferResponse places) {
//                        Log.d("MY LOG","" + places.toString());
//
//
//                    }
//                });
//
//            }
//        });
//
//        mGeoDataClient = Places.getGeoDataClient(getActivity());
//        final AutocompleteFilter countryFilter = new AutocompleteFilter.Builder().setCountry("SE").build();
//
//        mEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mGeoDataClient.getAutocompletePredictions(s.toString(), null, countryFilter).addOnSuccessListener(new OnSuccessListener<AutocompletePredictionBufferResponse>() {
//                    @Override
//                    public void onSuccess(AutocompletePredictionBufferResponse autocompletePredictions) {
//                        mListOfPredictions.clear();
//                        mAutocompletePredictions.clear();
//                        for (int i = 0; i < autocompletePredictions.getCount(); i++) {
//                            mAutocompletePredictions.add(autocompletePredictions.get(i));
//
//                            mListOfPredictions.add(autocompletePredictions.get(i).getFullText(null).toString());
//                        }
//                        arrayAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//            }
//    });

        return view;
    }

    //Starts to listen for changes in database
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    //Stops listening for changes in database
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}