package com.capstone.merkado.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets.OnSale;
import com.capstone.merkado.R;

import java.util.List;
import java.util.Locale;

public class StoreViewAdapter extends RecyclerView.Adapter<StoreViewAdapter.StoreViewHolder> {

    Activity activity;
    Context context;
    List<OnSale> onSaleList;
    OnClickListener onClickListener;

    public StoreViewAdapter(Activity activity, List<OnSale> onSaleList) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.onSaleList = onSaleList;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store_view_comsumer, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        holder.bind(activity, onSaleList.get(position), onClickListener);
    }

    @Override
    public int getItemCount() {
        return onSaleList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImage;
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemQty;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_store_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemQty = itemView.findViewById(R.id.item_qty);
        }

        public void bind(Activity activity, OnSale onSale, OnClickListener onClickListener) {
            DataFunctions.getResourceDataById(onSale.getResourceId()).thenAccept(resourceData -> {
                activity.runOnUiThread(() -> itemName.setText(resourceData.getName()));
            });

            int itemImageResource = GameResourceCaller.getResourcesImage(onSale.getResourceId());
            itemImage.setImageDrawable(ContextCompat.getDrawable(activity.getApplicationContext(), itemImageResource));
            itemPrice.setText(String.format(Locale.getDefault(), "P %.2f", onSale.getPrice()));
            itemQty.setText(String.valueOf(onSale.getQuantity()));
            itemView.setOnClickListener(v -> onClickListener.onClick(onSale));
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(OnSale onSale);
    }
}
