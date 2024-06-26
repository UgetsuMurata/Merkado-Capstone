package com.capstone.merkado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Objects.StoresDataObjects.StoreViewObjects.StoreViewConsumerObject;
import com.capstone.merkado.R;

import java.util.List;

public class StoreViewConsumerAdapter extends RecyclerView.Adapter<StoreViewConsumerAdapter.StoreViewHolder> {

    private Context context;
    private List<StoreViewConsumerObject> storeViewConsumerList;

    public StoreViewConsumerAdapter(Context context, List<StoreViewConsumerObject> storeViewConsumerList) {
        this.context = context;
        this.storeViewConsumerList = storeViewConsumerList;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store_view_comsumer, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        StoreViewConsumerObject currentItem = storeViewConsumerList.get(position);

        holder.itemImage.setImageResource(currentItem.getImageResource());
        holder.itemName.setText(currentItem.getItemName());
        holder.itemDesc.setText(currentItem.getItemPrice());
        holder.itemQty.setText(String.valueOf(currentItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return storeViewConsumerList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImage;
        public TextView itemName;
        public TextView itemDesc;
        public TextView itemQty;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_store_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemDesc = itemView.findViewById(R.id.item_price);
            itemQty = itemView.findViewById(R.id.item_qty);
        }
    }
}
