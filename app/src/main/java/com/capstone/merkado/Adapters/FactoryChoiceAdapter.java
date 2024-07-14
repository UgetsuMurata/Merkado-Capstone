package com.capstone.merkado.Adapters;

import static com.capstone.merkado.Adapters.FactoryChoiceAdapter.ChoiceHolder.Mode.IDLE;
import static com.capstone.merkado.Adapters.FactoryChoiceAdapter.ChoiceHolder.Mode.LOCKED;
import static com.capstone.merkado.Adapters.FactoryChoiceAdapter.ChoiceHolder.Mode.SELECTED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData.FactoryDetails;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("NotifyDataSetChanged")
public class FactoryChoiceAdapter extends RecyclerView.Adapter<FactoryChoiceAdapter.ChoiceHolder> {

    Context context;
    List<ResourceData> resourceData;
    Long proficiency;
    Integer onProductionId;
    OnChoiceSet onChoiceSet;

    public FactoryChoiceAdapter(Context context) {
        this(context, new ArrayList<>(), 0L, -1);
    }

    public FactoryChoiceAdapter(Context context, List<ResourceData> resourceData, @Nullable Long proficiency, @Nullable Integer onProductionId) {
        this.context = context;
        this.resourceData = resourceData;
        this.proficiency = proficiency == null ? 0 : proficiency;
        this.onProductionId = onProductionId == null ? -1 : onProductionId;
    }

    @NonNull
    @Override
    public ChoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gam_sec_agricultural_sector_factory, parent, false);
        return new ChoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceHolder holder, int position) {
        holder.bind(context, resourceData.get(position), proficiency, onProductionId, onChoiceSet);
    }

    @Override
    public int getItemCount() {
        return resourceData.size();
    }

    public static class ChoiceHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardViewStroke;
        ImageView resourceImage, itemLock, itemLockFilter;

        public ChoiceHolder(@NonNull View itemView) {
            super(itemView);
            cardViewStroke = itemView.findViewById(R.id.card_view_stroke);
            resourceImage = itemView.findViewById(R.id.item_image);
            itemLock = itemView.findViewById(R.id.item_lock);
            itemLockFilter = itemView.findViewById(R.id.item_lock_filter);
        }

        public void bind(Context context, ResourceData resourceData, Long proficiency, Integer onProductionId, OnChoiceSet onChoiceSet) {
            // Set up on click listener
            itemView.setOnClickListener(v -> {
                if (onChoiceSet == null) return;
                if (proficiency < resourceData.getFactoryDefaults().getProficiencyRequirement()) {
                    onChoiceSet.setChoice(ReturnChoiceStatus.NOT_ENOUGH_PROFICIENCY, null);
                    return;
                }
                onChoiceSet.setChoice(ReturnChoiceStatus.SUCCESS, resourceData);
            });

            // set up view status
            if (proficiency < resourceData.getFactoryDefaults().getProficiencyRequirement()) {
                changeMode(LOCKED);
            } else if (Objects.equals(onProductionId, resourceData.getResourceId())) {
                changeMode(SELECTED);
                if (onChoiceSet != null)
                    onChoiceSet.setChoice(ReturnChoiceStatus.CREATED, resourceData);
            } else {
                changeMode(IDLE);
            }

            // change image resource
            resourceImage.setImageDrawable(ContextCompat.getDrawable(context,
                    GameResourceCaller.getResourcesImage(resourceData.getResourceId())));
        }

        private void changeMode(Mode mode) {
            itemLock.setVisibility(mode == LOCKED ? View.VISIBLE : View.GONE);
            itemLockFilter.setVisibility(mode == LOCKED ? View.VISIBLE : View.GONE);
            cardViewStroke.setStrokeWidth(mode == SELECTED ? 4 : 0);
        }

        public enum Mode {
            LOCKED, SELECTED, IDLE
        }
    }

    public void setOnChoiceSet(OnChoiceSet onChoiceSet) {
        this.onChoiceSet = onChoiceSet;
    }

    public void updateFactoryDetails(Long proficiency, Integer onProductionId) {
        this.proficiency = proficiency;
        this.onProductionId = onProductionId;
        notifyDataSetChanged();
    }

    public void updateResourceData(List<ResourceData> resourceDataList) {
        this.resourceData = resourceDataList;
        notifyDataSetChanged();
    }

    public interface OnChoiceSet {
        void setChoice(ReturnChoiceStatus returnChoiceStatus, @Nullable ResourceData resourceData);
    }

    public enum ReturnChoiceStatus {
        NOT_ENOUGH_PROFICIENCY, GENERAL_ERROR, SUCCESS, CREATED
    }
}
