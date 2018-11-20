package grupp3.iths.se.wineanddineparalell;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class WishListFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference restaurantRef = db.collection("restaurant");

    private RestaurantAdapter adapter;

   // List<ItemInfo> itemList;
   // private RecyclerViewAdapter adapter;


  //  public WishListFragment() {
  //  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        Query query = restaurantRef.orderBy("distance",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ItemInfo> options = new FirestoreRecyclerOptions.Builder<ItemInfo>()
                .setQuery(query, ItemInfo.class)
                .build();

       // recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new RestaurantAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
//
//        adapter = new RecyclerViewAdapter(getContext(),itemList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(adapter);
//
//        SwipeToDeleteCallback swipeHandler = new SwipeToDeleteCallback(getContext()) {
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//                adapter.removeItem(viewHolder.getAdapterPosition());
//            }
//        };
//        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeHandler);
//        touchHelper.attachToRecyclerView(recyclerView);
//
//        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {



    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        itemList = new ArrayList<>();
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));
//        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
//                "0,6km","$$$$$", "*"));





    }


}

