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
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets.OnSale;
import com.capstone.merkado.R;

import java.util.List;
import java.util.Locale;

public class StoreProductAdapter extends RecyclerView.Adapter<StoreProductAdapter.ViewHolder> {

    List<OnSale> onSaleList;
    Context context;
    Boolean showPrice;

    public StoreProductAdapter(Context context, List<OnSale> onSaleList) {
        this(context, onSaleList, true);
    }

    public StoreProductAdapter(Context context, List<OnSale> onSaleList, Boolean showPrice) {
        this.onSaleList = onSaleList;
        this.context = context;
        this.showPrice = showPrice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gam_sto_stores_product_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(context, onSaleList.get(position), showPrice);
    }

    @Override
    public int getItemCount() {
        return onSaleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView resourceImage;
        public TextView resourcePrice;

        public ViewHolder(View itemView) {
            super(itemView);
            resourceImage = itemView.findViewById(R.id.resource_image);
            resourcePrice = itemView.findViewById(R.id.resource_quantity);
        }

        public void bind(Context context, OnSale onSale, Boolean showPrice) {
            int productImageResource = GameResourceCaller.getResourcesImage(onSale.getResourceId());
            resourceImage.setImageDrawable(ContextCompat.getDrawable(context, productImageResource));
            if (!showPrice) {
                resourcePrice.setVisibility(View.GONE);
                return;
            }
            resourcePrice.setText(String.format(Locale.getDefault(),"P %.2f", onSale.getPrice()));
        }
    }
}

