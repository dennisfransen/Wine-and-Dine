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

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class WishListFragment extends Fragment {

    List<ItemInfo> itemList;
    private RecyclerViewAdapter adapter;

    public WishListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(getContext(),itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        SwipeToDeleteCallback swipeHandler = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                adapter.removeItem(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeHandler);
        touchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemList = new ArrayList<>();
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));
        itemList.add(new ItemInfo(R.drawable.restaurant, "Le pain francaise",
                "0,6km","$$$$$", "*"));



    }
}

