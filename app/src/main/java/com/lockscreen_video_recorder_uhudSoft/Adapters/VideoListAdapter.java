package com.lockscreen_video_recorder_uhudSoft.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lockscreen_video_recorder_uhudSoft.Models.VideoModel;
import com.lockscreen_video_recorder_uhudSoft.R;

import java.io.File;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder>{

    private List<VideoModel> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public VideoListAdapter(Context context, List<VideoModel> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.itemview_video, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(mData.get(position).getPath()).into(holder.imageView);
        holder.title.setText(new File(mData.get(position).getPath()).getName());

        long lengthKB = new File(mData.get(position).getPath()).length();
        lengthKB = lengthKB / 1024;

        long remainingKB = lengthKB % 1024;
        long n1 = remainingKB, n2 = remainingKB; // n2 will hold the first two digits.
        while (remainingKB > 100) {
            n2 = n1;
            n1 = remainingKB;
            remainingKB /= 10;
        }

        long lengthMB = lengthKB / 1024;
        holder.size.setText(lengthMB + "." + remainingKB + "MB"); // Video size

        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(mData.get(position).getPath()));
            holder.duration.setText(getDuration(mediaPlayer.getDuration()));
        } catch (Exception e) {
            e.printStackTrace();
            holder.duration.setText("--:--");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, mData.get(position).getPath());
            }
        });
    }

    private String getDuration(long duration) {
        String videoDuration = null;

        int dur = (int) duration;
        int hrs = (dur / 3600000);
        int min = (dur / 60000) % 6000;
        int sec = (dur % 60000 / 1000);

        if (hrs > 0) {
            min = min % 60;
            videoDuration = String.format("%02d hrs, %02d min, %02d sec", hrs, min, sec);
        } else if (min > 0) videoDuration = String.format("%02d min, %02d sec", min, sec);
        else videoDuration = String.format("%02d sec", sec);

        return videoDuration;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration, size;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.list_item_title);
            duration = itemView.findViewById(R.id.list_item_duration);
            size = itemView.findViewById(R.id.list_item_size);
            imageView = itemView.findViewById(R.id.list_item_image);
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, String path);
    }
}
