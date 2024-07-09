package com.capstone.merkado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    Context context;
    LinkedHashMap<Integer, Inventory> inventoryLinkedHashMap;
    OnClickListener onClickListener;

    public InventoryAdapter(Context context, LinkedHashMap<Integer, Inventory> inventoryLinkedHashMap) {
        this.context = context;
        this.inventoryLinkedHashMap = inventoryLinkedHashMap;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gam_inv_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Map.Entry<Integer, Inventory> entry = inventoryLinkedHashMap.entrySet().iterator().next();
        holder.bind(context, entry.getValue(), entry.getKey(), onClickListener);
    }

    @Override
    public int getItemCount() {
        return inventoryLinkedHashMap.size();
    }


    public static class InventoryViewHolder extends RecyclerView.ViewHolder {

        ImageView resourceImage;
        TextView resourceQuantity;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            resourceImage = itemView.findViewById(R.id.resource_image);
            resourceQuantity = itemView.findViewById(R.id.resource_quantity);
        }

        public void bind(Context context, Inventory inventory, Integer index, OnClickListener onClickListener) {
            resourceImage.setImageDrawable(ContextCompat.getDrawable(context, GameResourceCaller.getResourcesImage(inventory.getResourceId())));
            resourceQuantity.setText(String.valueOf(inventory.getQuantity()));
            itemView.setOnClickListener(v -> {
                onClickListener.onClick(inventory, index);
            });
        }

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(Inventory inventory, Integer index);
    }
}
