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
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceDisplayMode;
import com.capstone.merkado.Objects.StoresDataObjects.MarketPrice;
import com.capstone.merkado.R;

import java.util.List;
import java.util.stream.Collectors;

public class SupplyDemandDisplayAdapter extends RecyclerView.Adapter<SupplyDemandDisplayAdapter.QASAdapterViewer> {
    Context context;
    List<MarketPrice> marketDataList;
    List<MarketPrice> marketDataListFiltered;
    Display display;

    public SupplyDemandDisplayAdapter(Context context, List<MarketPrice> marketDataList, Display display) {
        this.context = context;
        this.marketDataList = marketDataList;
        this.display = display;
        changeDisplayedList(ResourceDisplayMode.EDIBLES);
    }

    @NonNull
    @Override
    public QASAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_market_data_window, parent, false);
        return new QASAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QASAdapterViewer holder, int position) {
        MarketPrice marketItem = marketDataListFiltered.get(position);
        holder.bind(context, marketItem, display);
    }

    @Override
    public int getItemCount() {
        return marketDataListFiltered.size();
    }

    public static class QASAdapterViewer extends RecyclerView.ViewHolder {
        ImageView resourceImage;
        TextView resourceName;
        TextView resourceQuantity;

        public QASAdapterViewer(@NonNull View itemView) {
            super(itemView);
            resourceImage = itemView.findViewById(R.id.resource_image);
            resourceName = itemView.findViewById(R.id.resource_name);
            resourceQuantity = itemView.findViewById(R.id.resource_quantity);
        }

        public void bind(Context context, MarketPrice marketItem, Display display) {
            resourceImage.setImageDrawable(ContextCompat.getDrawable(context,
                    GameResourceCaller.getResourcesImage(marketItem.getResourceId())));
            resourceName.setText(InternalDataFunctions.getResourceData(marketItem.getResourceId()).getName());
            resourceQuantity.setText(
                    String.valueOf(
                            Display.SUPPLY.equals(display) ?
                                    marketItem.getMetadata().getSupply() != null ?
                                            marketItem.getMetadata().getSupply()
                                            :
                                            0
                                    :
                                    marketItem.getMetadata().getDemand().getHour1()
                    )
            );
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeDisplayedList(ResourceDisplayMode resourceDisplayMode) {
        this.marketDataListFiltered = marketDataList.stream()
                .filter(marketItem -> resourceDisplayMode.toString().equals(
                        InternalDataFunctions.getResourceData(marketItem.getResourceId()).getType())
                )
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public enum Display {
        SUPPLY, DEMAND
    }
}
