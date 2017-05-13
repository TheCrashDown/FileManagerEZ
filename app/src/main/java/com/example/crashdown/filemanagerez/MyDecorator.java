package com.example.crashdown.filemanagerez;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Lev on 12.05.2017.
 */

public class MyDecorator extends RecyclerView.ItemDecoration
{
    private int space;

    public MyDecorator (int space)
    {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
    }


}
