package com.example.robo.tvshows.ui.show;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.networking.APIClient;

import java.util.List;


public class TVShowAdapter extends RecyclerView.Adapter<TVShowAdapter.ViewHolder> {

    //Constants
    private static final int LIST_ITEM = 0;
    private static final int GRID_ITEM = 1;

    private ITVShow listener;
    private List<TVShow> shows;
    private boolean isSwitchView = false;
    private FloatingActionButton btnSwitchView;

    TVShowAdapter(List<TVShow> shows, ITVShow listener, FloatingActionButton btnSwitchView) {
        this.listener = listener;
        this.shows = shows;
        this.btnSwitchView = btnSwitchView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        //Switch between list and grid view
        if (viewType == LIST_ITEM){
            itemView = LayoutInflater.from(parent.getContext()).inflate( R.layout.recycle_view_show_item_list, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_show_item_grid, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        TextView showTitle = holder.itemView.findViewById(R.id.item_textView);
        ImageView showImage = holder.itemView.findViewById(R.id.item_show_image);
        final TVShow show = shows.get(position);

        showTitle.setText(show.getTitle());

        //Display image with rounded corners
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(20));

        String imageUrl = APIClient.getFullImageUrl(show.getImageURL());
        Glide.with(holder.itemView)
                .load(imageUrl)
                .apply(requestOptions)
                .into(showImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.callShowDetailsActivity(show.getID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return shows.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType (int position) {
        if (isSwitchView){
            return LIST_ITEM;
        }else{
            return GRID_ITEM;
        }
    }

    public void setShows(List<TVShow> shows) {
        this.shows = shows;
        notifyDataSetChanged();
    }

    public boolean toggleItemViewType () {
        isSwitchView = !isSwitchView;

        //Change FAB button icon for switching views list <-> grid
        int i = getItemViewType(0);
        btnSwitchView.setImageResource((i == LIST_ITEM) ? R.drawable.ic_grid_view : R.drawable.ic_list_view);

        return isSwitchView;
    }
}
