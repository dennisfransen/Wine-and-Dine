package grupp3.iths.se.wineanddineparalell;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WishListFragment extends Fragment {

    private List<ItemInfo> favoriteslist;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference wishListRef = db.collection("users").document(mAuth.getUid()).collection("wishlist");

    private SearchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);


        setUpRecyclerView(view);
        populateWishlistList();

        return view;
    }

    private void setUpRecyclerView(View view) {

        favoriteslist = new ArrayList<ItemInfo>();
        adapter = new SearchAdapter(getActivity(),favoriteslist);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    void populateWishlistList(){
        wishListRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot snapshot : task.getResult()) {

                    String wishlisttName = snapshot.getString("favourite_restaurant");
                    DocumentReference restaurantRef = db.collection("restaurant").document(wishlisttName);

                    restaurantRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                              ItemInfo itemInfo = documentSnapshot.toObject(ItemInfo.class);
                              favoriteslist.add(itemInfo);
                              adapter.notifyDataSetChanged();
                        }

                    });
                }
            }
        });
    }
}

