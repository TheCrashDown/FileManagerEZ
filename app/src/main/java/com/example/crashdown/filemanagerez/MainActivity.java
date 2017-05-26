package com.example.crashdown.filemanagerez;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final int NORMAL_MODE = 0;
    private static final int SELECT_MODE = 1;
    private static final int COPY_MODE = 2;
    private static final int MOVETO_MODE = 3;
    public static final FileObject STORAGE_LOCATION = new FileObject(Environment.getExternalStorageDirectory(), "Storage");
    public static final FileObject SDCARD_LOCATION = new FileObject(new File(System.getenv("SECONDARY_STORAGE")), "SD-Card");
    public static final String TURN_BACK_BUTTON = "../turn_back/..";

    private static final int SORT_BY_NAME = 0;
    private static final int SORT_BY_SIZE = 1;

    private int sortType;

    private File currentDir;
    private File currentDir1;

    private File[] files;
    private File[] files1;

    private List<FileObject> strings = new ArrayList<>();
    private List<FileObject> strings1 = new ArrayList<>();

    private boolean InMainDirectory = false;
    private boolean InMainDirectory2 = false;

    private static int currentMode = 0;
    private static int currentMode1 = 0;

    private List<FileObject> selected = new ArrayList<>();
    private List<FileObject> selected1 = new ArrayList<>();
    private List<FileObject> selectedForCopy = new ArrayList<>();
    private List<FileObject> selectedForCopy1 = new ArrayList<>();

    private static int lastRecyclerAction = 0;

    private static long lastCloseClicked = 0;

    private List<Integer> scrollHistory = new ArrayList<>();
    private List<Integer> scrollHistory1 = new ArrayList<>();

    private String destinationCopy = "";

    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    final ListAdapter listAdapter = new ListAdapter(this, strings, currentDir, selected);
    final ListAdapter listAdapter2 = new ListAdapter(this, strings1, currentDir1, selected1);

    private SeekBar copySwapper;
    private boolean copySwapperVisible = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("FileManagerEz");
        copySwapper = (SeekBar) findViewById(R.id.copySwapper);
        copySwapper.setOnSeekBarChangeListener(this);
        sortType = SORT_BY_NAME;


        currentDir = STORAGE_LOCATION.getFile();
        strings.add(STORAGE_LOCATION);
        if (currentDir.isDirectory()) {
            files = currentDir.listFiles();
            for (int i = 0; i < currentDir.list().length; i++)
                Log.d("EPTA", currentDir.list()[i]);
            for (int i = 0; i < files.length; i++) {
                strings.add(new FileObject(files[i]));
            }
        }
        sortListsBy(sortType);


        currentDir1 = SDCARD_LOCATION.getFile();
        strings1.add(SDCARD_LOCATION);
        if (currentDir1.isDirectory())
        {
            files1 = currentDir1.listFiles();
            for (int i = 0; i < files1.length; i++) {
                strings1.add(new FileObject(files1[i]));
            }
        }

        scrollHistory.add(0);
        scrollHistory1.add(0);


        recyclerView = (RecyclerView) findViewById(R.id.list1);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(listAdapter);
        recyclerView.addItemDecoration(new MyDecorator(3));
        /////
        /////
        /////
        /////
        recyclerView2 = (RecyclerView) findViewById(R.id.list2);


        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView2.setAdapter(listAdapter2);
        recyclerView2.addItemDecoration(new MyDecorator(3));


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        Log.d("EPTAhui", "click------------------");
                        if (currentMode != SELECT_MODE)
                        {
                            lastRecyclerAction = 1;
                            if (strings.get(position).getName().equals(TURN_BACK_BUTTON)) {
                                MoverArgument result = MoveToParent(strings, currentDir);
                                strings = result.getStrings();
                                currentDir = result.getCurrentDir();
                                recyclerView.scrollToPosition(scrollHistory.get(scrollHistory.size()-1));
                                scrollHistory.remove(scrollHistory.size()-1);
                                listAdapter.notifyDataSetChanged();
                            } else if ((strings.get(position).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory) ||
                                    (strings.get(position).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory)) {
                                for (int i = 0; i < strings.size(); ) strings.remove(0);
                                strings.add(STORAGE_LOCATION);
                                strings.add(SDCARD_LOCATION);
                                recyclerView.scrollToPosition(scrollHistory.get(scrollHistory.size()-1));
                                scrollHistory.remove(scrollHistory.size()-1);
                                InMainDirectory = true;
                                listAdapter.notifyDataSetChanged();
                            } else if (InMainDirectory)
                            {
                                if (position == 0) {
                                    scrollHistory.add(position);
                                    currentDir = STORAGE_LOCATION.getFile();
                                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                                    strings.add(STORAGE_LOCATION);
                                    if (currentDir.isDirectory()) {
                                        files = currentDir.listFiles();
                                        for (int i = 0; i < files.length; i++) {
                                            strings.add(new FileObject(files[i]));
                                        }
                                    }
                                }
                                if (position == 1) {
                                    scrollHistory.add(position);
                                    currentDir = SDCARD_LOCATION.getFile();
                                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                                    strings.add(SDCARD_LOCATION);
                                    if (currentDir.isDirectory()) {
                                        files = currentDir.listFiles();
                                        for (int i = 0; i < files.length; i++) {
                                            strings.add(new FileObject(files[i]));
                                        }
                                    }
                                }
                                InMainDirectory = false;
                                listAdapter.notifyDataSetChanged();
                            } else if(strings.get(position).getFile().isDirectory()) {
                                scrollHistory.add(position);
                                MoverArgument result = MoveToChild(strings, currentDir, position);
                                strings = result.getStrings();
                                currentDir = result.getCurrentDir();
                                recyclerView.scrollToPosition(0);
                                listAdapter.notifyDataSetChanged();
                            }
                            //new
                            else if(strings.get(position).getFile().isFile())
                            {
                                try
                                {
                                    openFileInDefault(strings.get(position).getFile());
                                }
                                catch (IOException e){}

                            }

                        } else if (currentMode == SELECT_MODE && position != 0 && !InMainDirectory) {
                            if (!selected.contains(strings.get(position)))
                                selected.add(strings.get(position));
                            else selected.remove(strings.get(position));
                            if (selected.size() == 0)
                            {
                                currentMode = NORMAL_MODE;
                                setTitle("FileManagerEZ");
                            }
                            Log.d("EPTAhui", "----" + strings.get(position).getName());
                            Log.d("EPTAhui", "----" + selected);
                            listAdapter.notifyDataSetChanged();
                        }
                        sortListsBy(sortType);
                        listAdapter.notifyDataSetChanged();
                        listAdapter2.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemLongClick(View view, final int position) {
                        lastRecyclerAction = 1;
                        Log.d("EPTAhui", "longlonglongclick------------------");
                        //Toast.makeText(getApplicationContext(),strings.get(position).getName(), Toast.LENGTH_SHORT).show();
                        if (currentMode == NORMAL_MODE && position != 0 && !InMainDirectory) {
                            currentMode = SELECT_MODE;
                            setTitle("Selecting");
                            selected.add(strings.get(position));
                        } else if (currentMode == SELECT_MODE) {
                            currentMode = NORMAL_MODE;
                            setTitle("FileManagerEZ");
                            while (selected.size() > 0) selected.remove(0);
                        } else if (currentMode == COPY_MODE || currentMode1 == COPY_MODE)
                        {
                            CopyDialog(strings.get(position));
                            if (selectedForCopy.size() == 0 && selectedForCopy1.size() == 0)
                            {
                                currentMode = NORMAL_MODE;
                                currentMode1 = NORMAL_MODE;
                                setTitle("FileManagerEZ");
                            }
                        } else if (currentMode == MOVETO_MODE || currentMode1 == MOVETO_MODE)
                        {
                            MovetoDialog(strings.get(position));
                            if (selectedForCopy.size() == 0 && selectedForCopy1.size() == 0)
                            {
                                currentMode = NORMAL_MODE;
                                currentMode1 = NORMAL_MODE;
                                setTitle("FileManagerEZ");
                            }
                        }


                        listAdapter.notifyDataSetChanged();
                        listAdapter2.notifyDataSetChanged();

                    }

                })
        );


        recyclerView2.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView2, new RecyclerItemClickListener.OnItemClickListener() {


                    @Override
                    public void onItemClick(View view, int position)
                    {
                        if (currentMode1 != SELECT_MODE) {
                            lastRecyclerAction = 2;
                            if (strings1.get(position).getName().equals(TURN_BACK_BUTTON)) {
                                MoverArgument result = MoveToParent(strings1, currentDir1);
                                strings1 = result.getStrings();
                                currentDir1 = result.getCurrentDir();
                                recyclerView2.scrollToPosition(scrollHistory1.get(scrollHistory1.size()-1));
                                scrollHistory1.remove(scrollHistory1.get(scrollHistory1.size()-1));
                                listAdapter2.notifyDataSetChanged();
                            } else if ((strings1.get(position).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2) ||
                                    (strings1.get(position).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2)) {
                                for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                                strings1.add(STORAGE_LOCATION);
                                strings1.add(SDCARD_LOCATION);
                                InMainDirectory2 = true;
                                recyclerView2.scrollToPosition(scrollHistory1.get(scrollHistory1.size()-1));
                                scrollHistory1.remove(scrollHistory1.get(scrollHistory1.size()-1));
                                listAdapter2.notifyDataSetChanged();
                            } else if (InMainDirectory2) {
                                if (position == 0) {
                                    scrollHistory1.add(position);
                                    currentDir1 = STORAGE_LOCATION.getFile();
                                    for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                                    strings1.add(STORAGE_LOCATION);
                                    if (currentDir1.isDirectory()) {
                                        files1 = currentDir1.listFiles();
                                        for (int i = 0; i < files1.length; i++) {
                                            strings1.add(new FileObject(files1[i]));
                                        }
                                    }
                                }
                                if (position == 1) {
                                    scrollHistory1.add(position);
                                    currentDir1 = SDCARD_LOCATION.getFile();
                                    for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                                    strings1.add(SDCARD_LOCATION);
                                    if (currentDir1.isDirectory()) {
                                        files1 = currentDir1.listFiles();
                                        for (int i = 0; i < files1.length; i++) {
                                            strings1.add(new FileObject(files1[i]));
                                        }
                                    }
                                }
                                InMainDirectory2 = false;
                                listAdapter2.notifyDataSetChanged();
                            } else if(strings1.get(position).getFile().isDirectory()){
                                scrollHistory1.add(position);
                                MoverArgument result = MoveToChild(strings1, currentDir1, position);
                                strings1 = result.getStrings();
                                currentDir1 = result.getCurrentDir();
                                recyclerView2.scrollToPosition(0);
                                listAdapter2.notifyDataSetChanged();
                            }
                            else if(strings1.get(position).getFile().isFile())
                            {
                                try
                                {
                                    openFileInDefault(strings1.get(position).getFile());
                                }
                                catch (IOException e){}
                            }


                        } else if (currentMode1 == SELECT_MODE && position != 0 && !InMainDirectory2) {
                            if (!selected1.contains(strings1.get(position)))
                                selected1.add(strings1.get(position));
                            else selected1.remove(strings1.get(position));
                            if (selected1.size() == 0)
                            {
                                currentMode1 = NORMAL_MODE;
                                setTitle("FileManagerEZ");
                            }
                            Log.d("EPTAhui", "----" + strings1.get(position).getName());
                            Log.d("EPTAhui", "----" + selected1);
                            listAdapter2.notifyDataSetChanged();

                        }
                        sortListsBy(sortType);
                        listAdapter.notifyDataSetChanged();
                        listAdapter2.notifyDataSetChanged();


                    }

                    @Override
                    public void onItemLongClick(View view, final int position) {
                        lastRecyclerAction = 2;
                        Log.d("EPTAhui", "longlonglongclick------------------");
                        //Toast.makeText(getApplicationContext(),strings1.get(position).getName(), Toast.LENGTH_SHORT).show();
                        if (currentMode1 == NORMAL_MODE && position != 0 && !InMainDirectory2) {
                            currentMode1 = SELECT_MODE;
                            setTitle("Selecting");
                            selected1.add(strings1.get(position));
                        } else if (currentMode1 == SELECT_MODE) {
                            currentMode1 = NORMAL_MODE;
                            setTitle("FileManagerEZ");
                            while (selected1.size() > 0) selected1.remove(0);
                        } else if (currentMode == COPY_MODE || currentMode1 == COPY_MODE)
                        {
                            CopyDialog(strings1.get(position));
                            if (selectedForCopy.size() == 0 && selectedForCopy1.size() == 0) {
                                currentMode = NORMAL_MODE;
                                currentMode1 = NORMAL_MODE;
                                setTitle("FileManagerEZ");
                            }
                        } else if (currentMode == MOVETO_MODE || currentMode1 == MOVETO_MODE)
                        {
                            MovetoDialog(strings1.get(position));
                            if (selectedForCopy.size() == 0 && selectedForCopy1.size() == 0) {
                                currentMode = NORMAL_MODE;
                                currentMode1 = NORMAL_MODE;
                                setTitle("FileManagerEZ");
                            }
                        }

                        listAdapter.notifyDataSetChanged();
                        listAdapter2.notifyDataSetChanged();
                    }
                })
        );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (currentMode != NORMAL_MODE || currentMode1 != NORMAL_MODE) {
            menu.setGroupVisible(R.id.group_mode_normal, false);
            menu.setGroupVisible(R.id.group_mode_select, true);
        } else {
            menu.setGroupVisible(R.id.group_mode_normal, true);
            menu.setGroupVisible(R.id.group_mode_select, false);
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_uncheck:
                if (currentMode != NORMAL_MODE) while (selected.size() != 0) selected.remove(0);
                if (currentMode1 != NORMAL_MODE) while (selected1.size() != 0) selected1.remove(0);
                while (selectedForCopy.size() != 0) selectedForCopy.remove(0);
                while (selectedForCopy1.size() != 0) selectedForCopy1.remove(0);
                currentMode = NORMAL_MODE;
                currentMode1 = NORMAL_MODE;
                copySwapper.setVisibility(View.GONE);
                setTitle("FileManagerEZ");
                break;
            case R.id.action_copy:
                currentMode = COPY_MODE;
                currentMode1 = COPY_MODE;
                copySwapper.setVisibility(View.VISIBLE);
                setTitle("Chose destination dir");
                for (int i = 0; i < selected.size(); i++) selectedForCopy.add(selected.get(i));
                for (int i = 0; i < selected1.size(); i++) selectedForCopy1.add(selected1.get(i));
                while (selected.size() != 0) selected.remove(0);
                while (selected1.size() != 0) selected1.remove(0);
                for (int i = 0; i < selectedForCopy.size(); i++)
                    Log.d("EPTA---", selectedForCopy.get(i).getName());
                for (int i = 0; i < selectedForCopy1.size(); i++)
                    Log.d("EPTA---", selectedForCopy1.get(i).getName());
                //Toast.makeText(getApplicationContext(), "CopyMode", Toast.LENGTH_SHORT).show();
                sortListsBy(sortType);
                listAdapter.notifyDataSetChanged();
                listAdapter2.notifyDataSetChanged();
                break;
            case R.id.action_delete:
                DeleteDialog();
                if(selected.size()==0 && selected1.size()==0)
                {
                    currentMode = NORMAL_MODE;
                    currentMode1 = NORMAL_MODE;
                    setTitle("FileManagerEZ");
                    try
                    {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException e){}
                    //notifyAllChanges();
                    sortListsBy(sortType);
                    listAdapter.notifyDataSetChanged();
                    listAdapter2.notifyDataSetChanged();
                }
                break;
            case R.id.action_moveto:
                currentMode = MOVETO_MODE;
                currentMode1 = MOVETO_MODE;
                copySwapper.setVisibility(View.VISIBLE);
                setTitle("Chose destination dir");
                for (int i = 0; i < selected.size(); i++) selectedForCopy.add(selected.get(i));
                for (int i = 0; i < selected1.size(); i++) selectedForCopy1.add(selected1.get(i));
                while (selected.size() != 0) selected.remove(0);
                while (selected1.size() != 0) selected1.remove(0);
                for (int i = 0; i < selectedForCopy.size(); i++)
                    Log.d("EPTA---", selectedForCopy.get(i).getName());
                for (int i = 0; i < selectedForCopy1.size(); i++)
                    Log.d("EPTA---", selectedForCopy1.get(i).getName());
                //Toast.makeText(getApplicationContext(), "CopyMode", Toast.LENGTH_SHORT).show();
                //notifyAllChanges();
                sortListsBy(sortType);
                listAdapter.notifyDataSetChanged();
                listAdapter2.notifyDataSetChanged();
                break;
            case R.id.action_sortByName:
                sortType = SORT_BY_NAME;
                sortListsBy(sortType);
                listAdapter.notifyDataSetChanged();
                listAdapter2.notifyDataSetChanged();
                break;
            case R.id.action_sortBySize:
                sortType = SORT_BY_SIZE;
                sortListsBy(sortType);
                listAdapter.notifyDataSetChanged();
                listAdapter2.notifyDataSetChanged();
                break;




            default:
                listAdapter.notifyDataSetChanged();
                listAdapter2.notifyDataSetChanged();
                return super.onOptionsItemSelected(item);

        }
        listAdapter.notifyDataSetChanged();
        listAdapter2.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        if(seekBar.getProgress() < 2 && (currentMode1==COPY_MODE || currentMode == COPY_MODE)) CopyDialog(new FileObject(currentDir));
        if(seekBar.getProgress() > 2 && (currentMode1==COPY_MODE || currentMode == COPY_MODE)) CopyDialog(new FileObject(currentDir1));

        if(seekBar.getProgress() < 2 && (currentMode1==MOVETO_MODE || currentMode == MOVETO_MODE)) MovetoDialog(new FileObject(currentDir));
        if(seekBar.getProgress() > 2 && (currentMode1==MOVETO_MODE || currentMode == MOVETO_MODE)) MovetoDialog(new FileObject(currentDir1));
        seekBar.setProgress(2);
    }

    @Override
    public void onBackPressed()
    {
        if (currentMode == NORMAL_MODE && currentMode1 == NORMAL_MODE) {
            if (lastRecyclerAction == 0 || (lastRecyclerAction == 1 && InMainDirectory) || (lastRecyclerAction == 2 && InMainDirectory2)) {
                if (System.currentTimeMillis() - lastCloseClicked < 2000) super.onBackPressed();
                else {
                    lastCloseClicked = System.currentTimeMillis();
                    Toast.makeText(getApplicationContext(), R.string.close_app, Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(getApplicationContext(),"//APP_CLOSED", Toast.LENGTH_LONG).show();
            } else if (lastRecyclerAction == 1) {
                if (strings.get(0).getName() == TURN_BACK_BUTTON) {
                    MoverArgument result = MoveToParent(strings, currentDir);
                    strings = result.getStrings();
                    currentDir = result.getCurrentDir();
                    recyclerView.scrollToPosition(scrollHistory.get(scrollHistory.size()-1));
                    scrollHistory.remove(scrollHistory.size()-1);
                    listAdapter.notifyDataSetChanged();
                } else if ((strings.get(0).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory) ||
                        (strings.get(0).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory)) {
                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                    strings.add(STORAGE_LOCATION);
                    strings.add(SDCARD_LOCATION);
                    recyclerView.scrollToPosition(scrollHistory.get(scrollHistory.size()-1));
                    scrollHistory.remove(scrollHistory.size()-1);
                    InMainDirectory = true;
                    listAdapter.notifyDataSetChanged();
                }
            } else if (lastRecyclerAction == 2) {
                if (strings1.get(0).getName() == TURN_BACK_BUTTON) {
                    MoverArgument result = MoveToParent(strings1, currentDir1);
                    lastRecyclerAction = 2;
                    strings1 = result.getStrings();
                    currentDir1 = result.getCurrentDir();
                    recyclerView2.scrollToPosition(scrollHistory1.get(scrollHistory1.size()-1));
                    scrollHistory1.remove(scrollHistory1.size()-1);
                    listAdapter2.notifyDataSetChanged();
                } else if ((strings1.get(0).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2) ||
                        (strings1.get(0).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2)) {
                    for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                    strings1.add(STORAGE_LOCATION);
                    strings1.add(SDCARD_LOCATION);
                    recyclerView2.scrollToPosition(scrollHistory1.get(scrollHistory1.size()-1));
                    scrollHistory1.remove(scrollHistory1.size()-1);
                    InMainDirectory2 = true;
                    listAdapter2.notifyDataSetChanged();
                }
            }
        } else {
            if (currentMode != NORMAL_MODE) {
                currentMode = NORMAL_MODE;
                while (selected.size() > 0) selected.remove(0);
                listAdapter.notifyDataSetChanged();
            }
            if (currentMode1 != NORMAL_MODE) {
                currentMode1 = NORMAL_MODE;
                while (selected1.size() > 0) selected1.remove(0);
                listAdapter2.notifyDataSetChanged();
            }
        }
        setTitle("FileManagerEZ");
        copySwapper.setVisibility(View.GONE);
        sortListsBy(sortType);
        listAdapter.notifyDataSetChanged();
        listAdapter2.notifyDataSetChanged();

    }

    public MoverArgument MoveToParent(List<FileObject> strings, File currentDir)
    {
        File[] files;
        currentDir = currentDir.getParentFile();
        for (int i = 0; i < strings.size(); ) {
            strings.remove(0);
        }
        files = currentDir.listFiles();
        if (currentDir.getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath())) {
            strings.add(STORAGE_LOCATION);
        } else if (currentDir.getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath())) {
            strings.add(SDCARD_LOCATION);
        } else strings.add(new FileObject(TURN_BACK_BUTTON));
        for (int i = 0; i < files.length; i++) {
            strings.add(new FileObject(files[i]));
        }
        MoverArgument result = new MoverArgument();
        result.setStrings(strings);
        result.setCurrentDir(currentDir);
        return result;
    }

    public MoverArgument MoveToChild(List<FileObject> strings, File currentDir, int position)
    {
        File[] files;
        currentDir = new File(currentDir.getAbsolutePath() + "/" + strings.get(position).getName());
        if (currentDir.isDirectory()) {
            for (int i = 0; i < strings.size(); ) {
                strings.remove(0);
            }
            files = currentDir.listFiles();
            strings.add(new FileObject(TURN_BACK_BUTTON));
            for (int i = 0; i < files.length; i++) {
                strings.add(new FileObject(files[i]));
            }
        } else currentDir = currentDir.getParentFile();
        MoverArgument result = new MoverArgument();
        result.setStrings(strings);
        result.setCurrentDir(currentDir);
        return result;
    }

    public void CopyDialog(final FileObject object)
    {

        if (object.getFile().isFile())
            destinationCopy = object.getFile().getParentFile().getAbsolutePath() + "/";
        if (object.getFile().isDirectory())
            destinationCopy = object.getFile().getAbsolutePath() + "/";

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Copying")
                .setMessage("Copy to \n" + destinationCopy)
                .setIcon(R.mipmap.copy_icon)
                .setCancelable(false)
                .setPositiveButton("Copy here", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try
                        {
                            for(int i = 0; i < selectedForCopy.size(); i++) Log.d("EPTACheck", selectedForCopy.get(i).getName());
                            for(int i = 0; i < selectedForCopy1.size(); i++) Log.d("EPTACheck1", selectedForCopy1.get(i).getName());
                            copyEZ(selectedForCopy, new File(destinationCopy));
                            copyEZ(selectedForCopy1, new File(destinationCopy));
                            //copyEZ(selectedForCopy, new File(destinationCopy));
                            while (selectedForCopy.size() != 0) selectedForCopy.remove(0);
                            while (selectedForCopy1.size() != 0) selectedForCopy1.remove(0);
                            Thread.sleep(1000);
                            currentMode = NORMAL_MODE;
                            currentMode1 = NORMAL_MODE;
                            setTitle("FileManagerEZ");
                            copySwapper.setVisibility(View.GONE);
                            notifyAllChanges();
                        } catch (IOException e) {
                            Log.e("EPTAerr", "errIOinDialog");
                            Log.e("EPTAerr", e.getLocalizedMessage());
                        }catch (InterruptedException e){}


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        listAdapter.notifyDataSetChanged();
        listAdapter2.notifyDataSetChanged();
    }

    public void DeleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Deletion")
                .setMessage("Delete those files?")
                .setIcon(R.mipmap.copy_icon)
                .setCancelable(false)
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            while(selected.size()!=0)
                            {
                                if(selected.get(0).getFile()!=null && selected.get(0).getFile().isFile())
                                    selected.get(0).getFile().delete();
                                if(selected.get(0).getFile()!=null && selected.get(0).getFile().isDirectory())
                                    FileUtils.deleteDirectory(selected.get(0).getFile());
                                selected.remove(0);
                            }
                            while(selected1.size()!=0)
                            {
                                if(selected1.get(0).getFile()!=null && selected1.get(0).getFile().isFile())
                                    selected1.get(0).getFile().delete();
                                if(selected1.get(0).getFile()!=null && selected1.get(0).getFile().isDirectory())
                                    FileUtils.deleteDirectory(selected1.get(0).getFile());
                                selected1.remove(0);
                            }
                        }
                        catch (IOException e)
                        {

                        }
                        notifyAllChanges();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        listAdapter.notifyDataSetChanged();
        listAdapter2.notifyDataSetChanged();
    }

    public void MovetoDialog(final FileObject object)
    {
        if (object.getFile().isFile())
            destinationCopy = object.getFile().getParentFile().getAbsolutePath() + "/";
        if (object.getFile().isDirectory())
            destinationCopy = object.getFile().getAbsolutePath() + "/";

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Moving")
                .setMessage("Move to \n" + destinationCopy)
                .setIcon(R.mipmap.copy_icon)
                .setCancelable(false)
                .setPositiveButton("Move here", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try
                        {
                            copyEZ(selectedForCopy, new File(destinationCopy));
                            copyEZ(selectedForCopy1, new File(destinationCopy));


                            while(selectedForCopy.size()!=0)
                            {
                                if(selectedForCopy.get(0).getFile()!=null && selectedForCopy.get(0).getFile().isFile())
                                    selectedForCopy.get(0).getFile().delete();
                                if(selectedForCopy.get(0).getFile()!=null && selectedForCopy.get(0).getFile().isDirectory())
                                    FileUtils.deleteDirectory(selectedForCopy.get(0).getFile());
                                selectedForCopy.remove(0);
                            }
                            while(selectedForCopy1.size()!=0)
                            {
                                if(selectedForCopy1.get(0).getFile()!=null && selectedForCopy1.get(0).getFile().isFile())
                                    selectedForCopy1.get(0).getFile().delete();
                                if(selectedForCopy1.get(0).getFile()!=null && selectedForCopy1.get(0).getFile().isDirectory())
                                    FileUtils.deleteDirectory(selectedForCopy1.get(0).getFile());
                                selectedForCopy1.remove(0);
                            }


                            Thread.sleep(1000);
                            currentMode = NORMAL_MODE;
                            currentMode1 = NORMAL_MODE;
                            setTitle("FileManagerEZ");
                            notifyAllChanges();
                        } catch (IOException e) {
                            Log.e("EPTAerr", "errIOinDialog");
                            Log.e("EPTAerr", e.getLocalizedMessage());
                        }catch (InterruptedException e){}


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        listAdapter.notifyDataSetChanged();
        listAdapter2.notifyDataSetChanged();

    }

    public void openFileInDefault(File url) throws IOException
    {
        try {
            File file = url;
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {

                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(getApplicationContext(), "Default app not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyEZ(final List<FileObject> sourceFiles, final File destFile) throws IOException
    {

        File destinationDir = destFile;
        if(destFile.isFile()) destinationDir = destFile.getParentFile();
        if(destFile.isDirectory()) destinationDir = destFile;

        for(int i = 0; i < sourceFiles.size(); i++)
        {
            if(sourceFiles.get(i).getFile().isDirectory())
                FileUtils.copyDirectoryToDirectory(sourceFiles.get(i).getFile(), destinationDir);
            if(sourceFiles.get(i).getFile().isFile())
                FileUtils.copyFileToDirectory(sourceFiles.get(i).getFile(), destinationDir);
        }

        /*progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Copying...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(1);
        progressDialog.show();
        handler = new Handler()
        {
            public void handleMessage(Message message)
            {
                try
                {

                    if (progressDialog.getProgress() < progressDialog.getMax())
                    {


                        destinationDir = destFile;
                        if(destFile.isFile()) destinationDir = destFile.getParentFile();
                        if(destFile.isDirectory()) destinationDir = destFile;

                        for(int i = 0; i < sourceFiles.size(); i++)
                        {
                            if(sourceFiles.get(i).getFile().isDirectory())
                                FileUtils.copyDirectoryToDirectory(sourceFiles.get(i).getFile(), destinationDir);
                            if(sourceFiles.get(i).getFile().isFile())
                                FileUtils.copyFileToDirectory(sourceFiles.get(i).getFile(), destinationDir);
                        }

                        progressDialog.incrementProgressBy(1);
                        handler.sendEmptyMessageDelayed(0, 100);
                    }
                    else progressDialog.dismiss();
                }
                catch (IOException e)
                {
                    Log.e("EPTAerr", "errIO");
                    Log.e("EPTAerr", e.getLocalizedMessage());
                }
            }
        };
        handler.sendEmptyMessageDelayed(0,500);
        listAdapter.notifyDataSetChanged();
        listAdapter2.notifyDataSetChanged();*/


    }

    public void notifyAllChanges()
    {
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {}
        for (int i = 0; i < strings.size(); ) {
            strings.remove(0);
        }
        files = currentDir.listFiles();
        strings.add(new FileObject(TURN_BACK_BUTTON));
        for (int i = 0; i < files.length; i++) {
            strings.add(new FileObject(files[i]));
        }

        for (int i = 0; i < strings1.size(); ) {
            strings1.remove(0);
        }
        files1 = currentDir1.listFiles();
        strings1.add(new FileObject(TURN_BACK_BUTTON));
        for (int i = 0; i < files1.length; i++) {
            strings1.add(new FileObject(files1[i]));
        }
        listAdapter2.notifyDataSetChanged();
        listAdapter.notifyDataSetChanged();
    }

    public void sortListsBy(int sortType)
    {
        switch (sortType)
        {
            case SORT_BY_NAME:
                if(!InMainDirectory)Collections.sort(strings,FileObject.COMPARE_BY_NAME);
                if(!InMainDirectory2)Collections.sort(strings1,FileObject.COMPARE_BY_NAME);
                break;
            case SORT_BY_SIZE:
                if(!InMainDirectory)Collections.sort(strings,FileObject.COMPARE_BY_SIZE);
                if(!InMainDirectory2)Collections.sort(strings1,FileObject.COMPARE_BY_SIZE);
                break;
            default:
                break;
        }
    }
}



