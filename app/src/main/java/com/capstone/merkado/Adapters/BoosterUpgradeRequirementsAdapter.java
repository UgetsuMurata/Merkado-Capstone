package com.capstone.merkado.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.DataManager.DataFunctionPackage.InternalDataFunctions;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Helpers.InventoryHelper;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceCount;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class BoosterUpgradeRequirementsAdapter extends RecyclerView.Adapter<BoosterUpgradeRequirementsAdapter.BoosterUpgradeReqViewer> {

    Context context;
    List<ResourceCount> resourceCountList;
    List<Inventory> inventoryContents;
    OnEvent onEvent;

    public BoosterUpgradeRequirementsAdapter(Context context) {
        this(context, new ArrayList<>(), new ArrayList<>());
    }

    public BoosterUpgradeRequirementsAdapter(Context context, List<ResourceCount> resourceCountList, List<Inventory> inventoryContents) {
        this.context = context;
        this.resourceCountList = resourceCountList;
        this.inventoryContents = inventoryContents;
    }

    @NonNull
    @Override
    public BoosterUpgradeRequirementsAdapter.BoosterUpgradeReqViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gam_sec_factory_booster_upgrade, parent, false);
        return new BoosterUpgradeRequirementsAdapter.BoosterUpgradeReqViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoosterUpgradeReqViewer holder, int position) {
        final ResourceCount resourceCount = resourceCountList.get(position);
        final Inventory inventoryItem = InventoryHelper.finder(inventoryContents, resourceCount.getResourceId());
        holder.bind(context, resourceCount, inventoryItem, onEvent);
    }

    @Override
    public int getItemCount() {
        return this.resourceCountList.size();
    }

    public void setResourceCountList(List<ResourceCount> resourceCountList) {
        this.resourceCountList = resourceCountList;
        notifyDataSetChanged();
    }

    public void setInventoryContents(List<Inventory> inventoryContents) {
        this.inventoryContents = inventoryContents;
        notifyDataSetChanged();
    }

    public void setOnEvent(OnEvent onEvent) {
        this.onEvent = onEvent;
    }

    public static class BoosterUpgradeReqViewer extends RecyclerView.ViewHolder {

        ImageView resourceImg;
        TextView resourceName, resourceQuantity, currentQuantity, quantitySeparator;

        public BoosterUpgradeReqViewer(@NonNull View itemView) {
            super(itemView);
            resourceImg = itemView.findViewById(R.id.resource_img);
            resourceName = itemView.findViewById(R.id.resource_name);
            resourceQuantity = itemView.findViewById(R.id.resource_quantity);
            quantitySeparator = itemView.findViewById(R.id.quantity_separator);
            currentQuantity = itemView.findViewById(R.id.current_quantity);
        }

        public void bind(Context context, ResourceCount resourceCount, Inventory inventoryItem, OnEvent onEvent) {
            ResourceData resourceData = InternalDataFunctions.getResourceData(resourceCount.getResourceId());
            resourceImg.setImageDrawable(ContextCompat.getDrawable(context, GameResourceCaller.getResourcesImage(resourceCount.getResourceId())));
            resourceName.setText(resourceData.getName());
            resourceQuantity.setText(String.valueOf(resourceCount.getQuantity()));

            Integer currentQty = 0;
            if (inventoryItem != null) currentQty = inventoryItem.getQuantity();
            currentQuantity.setText(String.valueOf(currentQty));
            if (currentQty < resourceCount.getQuantity()) {
                red(context);
                if (onEvent != null) onEvent.onNotEnoughItems(resourceCount.getResourceId());
            } else black(context);
        }

        public void red(Context context) {
            resourceQuantity.setTextColor(ContextCompat.getColor(context, R.color.red));
            quantitySeparator.setTextColor(ContextCompat.getColor(context, R.color.red));
            currentQuantity.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        public void black(Context context) {
            resourceQuantity.setTextColor(ContextCompat.getColor(context, R.color.black));
            quantitySeparator.setTextColor(ContextCompat.getColor(context, R.color.black));
            currentQuantity.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    public interface OnEvent {
        void onNotEnoughItems(Integer resourceId);
    }
}
