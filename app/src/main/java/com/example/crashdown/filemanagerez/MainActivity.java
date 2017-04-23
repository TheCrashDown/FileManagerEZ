package com.example.crashdown.filemanagerez;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private RecyclerView recyclerView2;

    private File currentDir;
    private File currentDir1;
    private File[] files;
    private File[] files1;
    private List<FileObject> strings = new ArrayList<FileObject>();
    private List<FileObject> strings1 = new ArrayList<FileObject>();
    private boolean InMainDirectory = false;
    private boolean InMainDirectory2 = false;

    private boolean inSelectMode = false;
    private boolean inSelectMode2 = false;
    private List<String> selected = new ArrayList<String>();
    private List<String> selected2 = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        currentDir = new File("/mnt/sdcard");
        strings.add(new FileObject(currentDir));
        if (currentDir.isDirectory())
        {
            files = currentDir.listFiles();
            for(int i = 0; i < currentDir.list().length; i ++)
                Log.d("EPTA", currentDir.list()[i]);
            for (int i = 0; i < files.length; i++)
            {
                strings.add(new FileObject(files[i]));
            }
        }


        currentDir1 = new File("/mnt/sdcard2");
        strings1.add(new FileObject(currentDir1));
        if (currentDir1.isDirectory())
        {
            files1 = currentDir1.listFiles();
            for (int i = 0; i < files1.length; i++) {
                strings1.add(new FileObject(files1[i]));
            }
        }


        recyclerView = (RecyclerView) findViewById(R.id.list1);
        final ListAdapter listAdapter = new ListAdapter(this, strings, currentDir, selected);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(listAdapter);
        /////
        /////
        recyclerView2 = (RecyclerView) findViewById(R.id.list2);
        final ListAdapter2 listAdapter2 = new ListAdapter2(this, strings1, currentDir1);

        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView2.setAdapter(listAdapter2);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("EPTAhui", "click------------------");
                        if(!inSelectMode)
                        {
                            if (strings.get(position).getName() == "../turn_back/..")
                            {
                                MoverArgument result = MoveToParent(strings, currentDir);
                                strings = result.getStrings();
                                currentDir = result.getCurrentDir();
                                listAdapter.notifyDataSetChanged();
                            } else if ((strings.get(position).getFile().getAbsolutePath().equals("/mnt/sdcard") && !InMainDirectory) || (strings.get(position).getFile().getAbsolutePath().equals("/mnt/sdcard2") && !InMainDirectory)) {
                                for (int i = 0; i < strings.size(); ) strings.remove(0);
                                  strings.add(new FileObject(new File("/mnt/sdcard")));
                                strings.add(new FileObject(new File("/mnt/sdcard2")));
                                InMainDirectory = true;
                                listAdapter.notifyDataSetChanged();
                            } else if (InMainDirectory) {
                                if (position == 0) {
                                    currentDir = new File("/mnt/sdcard");
                                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                                    strings.add(new FileObject(currentDir));
                                    if (currentDir.isDirectory()) {
                                        files = currentDir.listFiles();
                                        for (int i = 0; i < files.length; i++) {
                                            strings.add(new FileObject(files[i]));
                                        }
                                    }
                                }
                                if (position == 1) {
                                    currentDir = new File("/mnt/sdcard2");
                                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                                    strings.add(new FileObject(currentDir));
                                    if (currentDir.isDirectory()) {
                                        files = currentDir.listFiles();
                                        for (int i = 0; i < files.length; i++) {
                                            strings.add(new FileObject(files[i]));
                                        }
                                    }
                                }
                                InMainDirectory = false;
                                listAdapter.notifyDataSetChanged();
                            } else {
                                MoverArgument result = MoveToChild(strings, currentDir, position);
                                strings = result.getStrings();
                                currentDir = result.getCurrentDir();
                                listAdapter.notifyDataSetChanged();
                            }

                    }
                    else
                        {
                            if(!selected.contains(strings.get(position).getName())) selected.add(strings.get(position).getName());
                            else selected.remove(strings.get(position).getName());
                            Log.d("EPTAhui", strings.get(position).getName());
                            listAdapter.notifyDataSetChanged();
                        }
                }

                    @Override
                    public void onItemLongClick(View view, int position)
                    {
                        Log.d("EPTAhui", "longlonglongclick------------------");
                        Toast.makeText(getApplicationContext(),strings.get(position).getName(), Toast.LENGTH_SHORT).show();
                        if(!inSelectMode)
                        {
                            inSelectMode = !inSelectMode;
                            selected.add(strings.get(position).getName());
                        }
                        else
                        {
                            inSelectMode = !inSelectMode;
                            while(selected.size() > 0) selected.remove(0);
                        }

                        listAdapter.notifyDataSetChanged();

                    }

                })
        );




        recyclerView2.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView2, new RecyclerItemClickListener.OnItemClickListener() {


                    @Override
                    public void onItemClick(View view, int position)
                    {
                        if(strings1.get(position).getName() == "../turn_back/..")
                        {
                            MoverArgument result = MoveToParent(strings1, currentDir1);
                            strings1 = result.getStrings();
                            currentDir1 = result.getCurrentDir();
                            listAdapter2.notifyDataSetChanged();
                        }
                        else if((strings1.get(position).getFile().getAbsolutePath().equals("/mnt/sdcard") && !InMainDirectory2) || (strings1.get(position).getFile().getAbsolutePath().equals("/mnt/sdcard2") && !InMainDirectory2))
                        {
                            for(int i = 0; i < strings1.size();) strings1.remove(0);
                            strings1.add(new FileObject(new File("/mnt/sdcard")));
                            strings1.add(new FileObject(new File("/mnt/sdcard2")));
                            InMainDirectory2 = true;
                            listAdapter2.notifyDataSetChanged();
                        }
                        else if(InMainDirectory2)
                        {
                            if(position == 0)
                            {
                                currentDir1 = new File("/mnt/sdcard");
                                for(int i = 0; i < strings1.size();) strings1.remove(0);
                                strings1.add(new FileObject(currentDir1));
                                if (currentDir1.isDirectory())
                                {
                                    files1 = currentDir1.listFiles();
                                    for (int i = 0; i < files1.length; i++)
                                    {
                                        strings1.add(new FileObject(files1[i]));
                                    }
                                }
                            }
                            if(position == 1)
                            {
                                currentDir1 = new File("/mnt/sdcard2");
                                for(int i = 0; i < strings1.size();) strings1.remove(0);
                                strings1.add(new FileObject(currentDir1));
                                if (currentDir1.isDirectory())
                                {
                                    files1 = currentDir1.listFiles();
                                    for (int i = 0; i < files1.length; i++)
                                    {
                                        strings1.add(new FileObject(files1[i]));
                                    }
                                }
                            }
                            InMainDirectory2 = false;
                            listAdapter2.notifyDataSetChanged();
                        }
                        else
                        {
                            MoverArgument result = MoveToChild(strings1, currentDir1, position);
                            strings1 = result.getStrings();
                            currentDir1 = result.getCurrentDir();
                            listAdapter2.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position)
                    {
                        Toast.makeText(getApplicationContext(),strings1.get(position).getName(), Toast.LENGTH_SHORT).show();
                    }
                })
        );


    }
    public MoverArgument MoveToParent(List<FileObject> strings, File currentDir)
    {
        File[] files;
        currentDir = currentDir.getParentFile();
        for(int i = 0; i < strings.size();)
        {
            strings.remove(0);
        }
        files = currentDir.listFiles();
        if (currentDir.getAbsolutePath().endsWith("/mnt/sdcard"))
        {
            strings.add(new FileObject(new File("/mnt/sdcard")));
        }
        else if (currentDir.getAbsolutePath().endsWith("/mnt/sdcard2"))
        {
            strings.add(new FileObject(new File("/mnt/sdcard2")));
        }
        else strings.add(new FileObject("../turn_back/.."));
        for (int i = 0; i < files.length; i++)
        {
            strings.add(new FileObject(files[i]));
        }
        MoverArgument result = new MoverArgument();
        result.setStrings(strings);
        result.setCurrentDir(currentDir);
        return result;
    }
    public MoverArgument MoveToChild (List<FileObject> strings, File currentDir, int position)
    {
        File[] files;
        currentDir = new File(currentDir.getAbsolutePath() + "/" + strings.get(position).getName());
        if(currentDir.isDirectory())
        {
            for(int i = 0; i < strings.size();)
            {
                strings.remove(0);
            }
            files = currentDir.listFiles();
            strings.add(new FileObject("../turn_back/.."));
            for (int i = 0; i < files.length; i++)
            {
                strings.add(new FileObject(files[i]));
            }
        }
        else currentDir = currentDir.getParentFile();
        MoverArgument result = new MoverArgument();
        result.setStrings(strings);
        result.setCurrentDir(currentDir);
        return result;
    }
}