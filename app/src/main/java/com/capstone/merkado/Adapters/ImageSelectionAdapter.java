package com.capstone.merkado.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Economy.CreateEconomy;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ImageSelectionAdapter extends RecyclerView.Adapter<ImageSelectionAdapter.ImageSelectionAdapterViewer> {
    Context context;
    List<CreateEconomy.ImageIdPair> images;
    Integer selected;
    OnClickListener onClickListener;

    public ImageSelectionAdapter(@NonNull Context context,
                                 @NonNull List<CreateEconomy.ImageIdPair> images,
                                 @NonNull Integer selected) {
        this.context = context;
        this.images = images;
        this.selected = selected;
    }

    @NonNull
    @Override
    public ImageSelectionAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_selection, parent, false);
        return new ImageSelectionAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSelectionAdapterViewer holder, int position) {
        CreateEconomy.ImageIdPair src = images.get(position);
        holder.bind(context, src, selected, onClickListener);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageSelectionAdapterViewer extends RecyclerView.ViewHolder {
        MaterialCardView border;
        ImageView image;

        public ImageSelectionAdapterViewer(@NonNull View itemView) {
            super(itemView);
            border = itemView.findViewById(R.id.image_border);
            image = itemView.findViewById(R.id.image);
        }

        @SuppressLint("NotifyDataSetChanged")
        public void bind(@NonNull Context context, @NonNull CreateEconomy.ImageIdPair src, Integer selected, OnClickListener onClickListener) {
            image.setImageDrawable(ContextCompat.getDrawable(context, src.getImage()));
            border.setStrokeColor(ContextCompat.getColor(context,
                    selected.equals(src.getId()) ? R.color.merkado_orange : R.color.black));
            border.setOnClickListener(v -> {
                ImageSelectionAdapter.this.selected = src.getId();
                notifyDataSetChanged();
                if (onClickListener != null) onClickListener.onClick(src);
            });
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(CreateEconomy.ImageIdPair src);
    }
}
