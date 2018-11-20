package grupp3.iths.se.wineanddineparalell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;



public class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private Context myContext;
    private List<ItemInfo> list;

    /**
     * Constructor for Recyclerview, takes context and list of items to be displayed
     * @param myContext
     * @param list
     */
    public RecyclerViewAdapter(Context myContext, List<ItemInfo> list) {
        this.myContext = myContext;
        this.list = list;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(myContext).inflate(R.layout.item_list,viewGroup,false);

        ItemViewHolder itemHolder = new ItemViewHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {

        itemViewHolder.imgView.setImageResource(list.get(i).getImage());
        itemViewHolder.textName.setText(list.get(i).getNameOfRest());
        itemViewHolder.textDistance.setText(list.get(i).getDist());
        itemViewHolder.textPrice.setText(list.get(i).getPrice());
        itemViewHolder.textScore.setText(list.get(i).getScore());
    }

    /**
     * Method that returns length of list item
     * @return
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Method that removes a listitem in recyclerview
     * @param index
     */
    public void removeItem(int index){
        if( index > 0 && index < list.size()) {
            list.remove(index);
            this.notifyItemRemoved(index);
        }
    }

}

