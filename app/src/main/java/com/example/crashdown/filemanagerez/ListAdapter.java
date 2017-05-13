package com.example.crashdown.filemanagerez;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    Context context;
    List<FileObject> arrayList;
    File currentdir;
    List<String> selected;

    public ListAdapter(Context context, List<FileObject> arrayList, File currentDir, List<String> selected)
    {
        this.context = context;
        this.arrayList = arrayList;
        this.currentdir = currentDir;
        this.selected = selected;
    }


    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item1, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ListAdapter.MyViewHolder holder, int position)
    {

        String string = arrayList.get(position).getName();
        int color = Color.WHITE;
        //if(selected.size()==0) holder.checkbox.setMaxWidth(0);
        if(selected.contains(arrayList.get(position).getName()))
        {
            color = Color.parseColor("#bed2dc");
            holder.checkbox.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.item_checked));
        }
        //holder.checkbox.r
        else if(selected.size()!=0) holder.checkbox.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.item_unchecked));
        if(selected.size()==0) holder.checkbox.setVisibility(View.GONE); else holder.checkbox.setVisibility(View.VISIBLE);
        holder.itemView.setBackgroundColor(color);
        if (selected.contains(arrayList.get(position).getName())) Log.d("EPTAhui2.2","true"); else Log.d("EPTAhui2.2","false");
        holder.textView.setText(string);




        if(string.equals("../turn_back/.."))
        {
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.turnback_image));
        }
        else if(arrayList.get(position).getFile().getAbsolutePath().equals("/mnt/sdcard") || arrayList.get(position).getFile().getAbsolutePath().equals("/mnt/sdcard2"))
        {
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher));
        }
        else if (arrayList.get(position).getFile() != null && arrayList.get(position).getFile().isDirectory())
        {
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.folder_image));
        }
        else holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.document_image));



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView checkbox;
        ImageView imageView;
        TextView textView;
        LinearLayout linearLayout;



        public MyViewHolder(View itemview) {
            super(itemview);

            linearLayout = (LinearLayout) itemview.findViewById(R.id.list_item1);
            checkbox = (ImageView) itemview.findViewById(R.id.list_item1_checkbox);
            imageView = (ImageView) itemview.findViewById(R.id.list_item1_image);
            textView = (TextView) itemview.findViewById(R.id.list_item1_textView);


        }

    }

}
