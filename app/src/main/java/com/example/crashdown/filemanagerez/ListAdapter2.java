package com.example.crashdown.filemanagerez;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;


public class ListAdapter2 extends RecyclerView.Adapter<ListAdapter2.MyViewHolder2> {

    Context context;
    List<FileObject> arrayList;
    File currentdir;

    public ListAdapter2(Context context,List<FileObject> arrayList, File currentdir)
    {
        this.context = context;
        this.arrayList = arrayList;
        this.currentdir = currentdir;
    }


    @Override
    public ListAdapter2.MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item2, parent, false);
        return new MyViewHolder2(v);
    }

    @Override
    public void onBindViewHolder(ListAdapter2.MyViewHolder2 holder, int position)
    {
        String string = arrayList.get(position).getName();
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

    public class MyViewHolder2 extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView textView;
        LinearLayout linearLayout;

        public MyViewHolder2 (View itemview)
        {
            super(itemview);
            linearLayout = (LinearLayout) itemview.findViewById(R.id.list_item2);
            imageView = (ImageView) itemview.findViewById(R.id.list_item2_image);
            textView = (TextView) itemview.findViewById(R.id.list_item2_textView);
        }
    }
}