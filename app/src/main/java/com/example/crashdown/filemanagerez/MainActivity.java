package com.example.crashdown.filemanagerez;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class MainActivity extends AppCompatActivity
{

    private static final int NORMAL_MODE = 0;
    private static final int SELECT_MODE = 1;
    private static final int COPY_MODE = 2;
    private static final int MOVETO_MODE = 3;
    public static final FileObject STORAGE_LOCATION = new FileObject(Environment.getExternalStorageDirectory(), "Storage");
    public static final FileObject SDCARD_LOCATION = new FileObject(new File(System.getenv("SECONDARY_STORAGE")), "SD-Card");
    public static final String TURN_BACK_BUTTON = "../turn_back/..";

    private File currentDir;
    private File currentDir1;

    private File[] files;
    private File[] files1;

    private List<FileObject> strings = new ArrayList<FileObject>();
    private List<FileObject> strings1 = new ArrayList<FileObject>();

    private boolean InMainDirectory = false;
    private boolean InMainDirectory2 = false;

    private static int currentMode = 0;
    private static int currentMode1 = 0;

    private List<FileObject> selected = new ArrayList<FileObject>();
    private List<FileObject> selected1 = new ArrayList<FileObject>();
    private List<FileObject> selectedForCopy = new ArrayList<>();
    private List<FileObject> selectedForCopy1 = new ArrayList<>();

    private static int lastRecyclerAction = 0;

    private static long lastCloseClicked = 0;

    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    final ListAdapter listAdapter = new ListAdapter(this, strings, currentDir, selected);
    final ListAdapter listAdapter2 = new ListAdapter(this, strings1, currentDir1, selected1);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        currentDir = STORAGE_LOCATION.getFile();
        strings.add(STORAGE_LOCATION);
        if (currentDir.isDirectory())
        {
            files = currentDir.listFiles();
            for (int i = 0; i < currentDir.list().length; i++)
                Log.d("EPTA", currentDir.list()[i]);
            for (int i = 0; i < files.length; i++)
            {
                    strings.add(new FileObject(files[i]));
            }
        }


        currentDir1 = SDCARD_LOCATION.getFile();
        strings1.add(SDCARD_LOCATION);
        if (currentDir1.isDirectory())
        {
            files1 = currentDir1.listFiles();
            for (int i = 0; i < files1.length; i++)
            {
                strings1.add(new FileObject(files1[i]));
            }
        }



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
                    public void onItemClick(View view, int position) {
                        Log.d("EPTAhui", "click------------------");
                        if(currentMode == NORMAL_MODE)
                        {
                            lastRecyclerAction=1;
                            if (strings.get(position).getName() == TURN_BACK_BUTTON)
                            {
                                MoverArgument result = MoveToParent(strings, currentDir);
                                strings = result.getStrings();
                                currentDir = result.getCurrentDir();
                                listAdapter.notifyDataSetChanged();
                            } else if ((strings.get(position).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory) ||
                                    (strings.get(position).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory))
                            {
                                for (int i = 0; i < strings.size(); ) strings.remove(0);
                                strings.add(STORAGE_LOCATION);
                                strings.add(SDCARD_LOCATION);
                                InMainDirectory = true;
                                listAdapter.notifyDataSetChanged();
                            } else if (InMainDirectory) {
                                if (position == 0) {
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
                                if (position == 1)
                                {
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
                            } else {
                                MoverArgument result = MoveToChild(strings, currentDir, position);
                                strings = result.getStrings();
                                currentDir = result.getCurrentDir();
                                listAdapter.notifyDataSetChanged();
                            }

                    }
                    else if (currentMode == SELECT_MODE && position != 0 && !InMainDirectory)
                        {
                            if(!selected.contains(strings.get(position))) selected.add(strings.get(position));
                            else selected.remove(strings.get(position));
                            if(selected.size()==0) currentMode = NORMAL_MODE;
                            Log.d("EPTAhui", "----" + strings.get(position).getName());
                            Log.d("EPTAhui", "----" + selected);
                            listAdapter.notifyDataSetChanged();
                        }
                }

                    @Override
                    public void onItemLongClick(View view, final int position)
                    {
                        lastRecyclerAction=1;
                        Log.d("EPTAhui", "longlonglongclick------------------");
                        //Toast.makeText(getApplicationContext(),strings.get(position).getName(), Toast.LENGTH_SHORT).show();
                        if(currentMode == NORMAL_MODE && position!=0 && !InMainDirectory)
                        {
                            currentMode = SELECT_MODE;
                            selected.add(strings.get(position));
                        }
                        else if(currentMode == SELECT_MODE)
                        {
                            currentMode = NORMAL_MODE;
                            while(selected.size() > 0) selected.remove(0);
                        }
                        else if(currentMode == COPY_MODE || currentMode1 == COPY_MODE)
                        {
                            CopyDialog(strings.get(position));
                            if(selectedForCopy.size()==0 && selectedForCopy1.size()==0)
                            {
                                currentMode = NORMAL_MODE;
                                currentMode1 = NORMAL_MODE;
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
                        if (currentMode1 == NORMAL_MODE)
                        {
                            lastRecyclerAction=2;
                            if (strings1.get(position).getName() == TURN_BACK_BUTTON) {
                                MoverArgument result = MoveToParent(strings1, currentDir1);
                                strings1 = result.getStrings();
                                currentDir1 = result.getCurrentDir();
                                listAdapter2.notifyDataSetChanged();
                            } else if ((strings1.get(position).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2) ||
                                    (strings1.get(position).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2)) {
                                for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                                strings1.add(STORAGE_LOCATION);
                                strings1.add(SDCARD_LOCATION);
                                InMainDirectory2 = true;
                                listAdapter2.notifyDataSetChanged();
                            } else if (InMainDirectory2) {
                                if (position == 0)
                                {
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
                                if (position == 1)
                                {
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
                            } else {
                                MoverArgument result = MoveToChild(strings1, currentDir1, position);
                                strings1 = result.getStrings();
                                currentDir1 = result.getCurrentDir();
                                listAdapter2.notifyDataSetChanged();
                            }
                        } else if (currentMode1 == SELECT_MODE && position != 0 && !InMainDirectory2)
                            {
                                if(!selected1.contains(strings1.get(position))) selected1.add(strings1.get(position));
                                else selected1.remove(strings1.get(position));
                                if(selected1.size()==0) currentMode1 = NORMAL_MODE;
                                Log.d("EPTAhui",  "----" + strings1.get(position).getName());
                                Log.d("EPTAhui", "----" + selected1);
                                listAdapter2.notifyDataSetChanged();
                            }


                    }

                    @Override
                    public void onItemLongClick(View view, final int position)
                    {
                        lastRecyclerAction=2;
                        Log.d("EPTAhui", "longlonglongclick------------------");
                        //Toast.makeText(getApplicationContext(),strings1.get(position).getName(), Toast.LENGTH_SHORT).show();
                        if(currentMode1 == NORMAL_MODE && position != 0 && !InMainDirectory2)
                        {
                            currentMode1 = SELECT_MODE;
                            selected1.add(strings1.get(position));
                        }
                        else if (currentMode1 == SELECT_MODE)
                        {
                            currentMode1 = NORMAL_MODE;
                            while(selected1.size() > 0) selected1.remove(0);
                        }
                        else if(currentMode == COPY_MODE || currentMode1 == COPY_MODE)
                        {
                            CopyDialog(strings1.get(position));
                            if(selectedForCopy.size()==0 && selectedForCopy1.size()==0)
                            {
                                currentMode = NORMAL_MODE;
                                currentMode1 = NORMAL_MODE;
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
        if(currentMode!=NORMAL_MODE||currentMode1!=NORMAL_MODE)
        {
            menu.setGroupVisible(R.id.group_mode_normal, false);
            menu.setGroupVisible(R.id.group_mode_select, true);
        }
        else
        {
            menu.setGroupVisible(R.id.group_mode_normal, true);
            menu.setGroupVisible(R.id.group_mode_select, false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_uncheck :
                if(currentMode != NORMAL_MODE) while (selected.size()!=0) selected.remove(0);
                if(currentMode1 != NORMAL_MODE) while (selected1.size()!=0) selected1.remove(0);
                currentMode = NORMAL_MODE;
                currentMode1 = NORMAL_MODE;
                break;
            case R.id.action_copy :
                currentMode = COPY_MODE;
                currentMode1 = COPY_MODE;
                for (int i = 0; i < selected.size(); i++) selectedForCopy.add(selected.get(i));
                for (int i = 0; i < selected1.size(); i++) selectedForCopy1.add(selected1.get(i));
                while (selected.size()!=0) selected.remove(0);
                while (selected1.size()!=0) selected1.remove(0);
                for (int i = 0; i < selectedForCopy.size(); i++) Log.d("EPTA---", selectedForCopy.get(i).getName());
                for (int i = 0; i < selectedForCopy1.size(); i++) Log.d("EPTA---", selectedForCopy1.get(i).getName());
                Toast.makeText(getApplicationContext(),"CopyMode",Toast.LENGTH_SHORT).show();
                break;
            case R.id.dest_Rec1 :
                if(currentMode == COPY_MODE)
                    while (selectedForCopy.size() != 0)
                    {
                        intellectCopy(selectedForCopy.get(0).getFile().getAbsolutePath(), currentDir.getAbsolutePath() + "/" + selectedForCopy.get(0).getName());
                        selectedForCopy.remove(0);
                    }
                if(currentMode1 == COPY_MODE)
                    while (selectedForCopy1.size() != 0)
                    {
                        intellectCopy(selectedForCopy1.get(0).getFile().getAbsolutePath(),currentDir.getAbsolutePath() + "/" + selectedForCopy1.get(0).getName());
                        selectedForCopy1.remove(0);
                    }
                currentMode = NORMAL_MODE;
                currentMode1 = NORMAL_MODE;
                break;
            case R.id.dest_Rec2 :
                if(currentMode == COPY_MODE)
                    while (selectedForCopy.size() != 0)
                    {
                        intellectCopy(selectedForCopy.get(0).getFile().getAbsolutePath(), currentDir1.getAbsolutePath() + "/" + selectedForCopy.get(0).getName());
                        selectedForCopy.remove(0);
                    }
                if(currentMode1 == COPY_MODE)
                    while (selectedForCopy1.size() != 0)
                    {
                        intellectCopy(selectedForCopy1.get(0).getFile().getAbsolutePath(), currentDir1.getAbsolutePath() + "/" + selectedForCopy1.get(0).getName());
                        selectedForCopy1.remove(0);
                    }
                currentMode = NORMAL_MODE;
                currentMode1 = NORMAL_MODE;
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
    public void onBackPressed() {
        if(currentMode == NORMAL_MODE && currentMode1 == NORMAL_MODE)
        {
            if(lastRecyclerAction==0 || (lastRecyclerAction==1&&InMainDirectory) || (lastRecyclerAction==2&&InMainDirectory2))
            {
                if(System.currentTimeMillis()-lastCloseClicked < 2000) super.onBackPressed();
                else
                {
                    lastCloseClicked = System.currentTimeMillis();
                    Toast.makeText(getApplicationContext(),R.string.close_app, Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(getApplicationContext(),"//APP_CLOSED", Toast.LENGTH_LONG).show();
            }

            else if (lastRecyclerAction==1)
            {
                if (strings.get(0).getName() == TURN_BACK_BUTTON)
                {
                    MoverArgument result = MoveToParent(strings, currentDir);
                    strings = result.getStrings();
                    currentDir = result.getCurrentDir();
                    listAdapter.notifyDataSetChanged();
                } else if ((strings.get(0).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory) ||
                        (strings.get(0).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory))
                {
                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                    strings.add(STORAGE_LOCATION);
                    strings.add(SDCARD_LOCATION);
                    InMainDirectory = true;
                    listAdapter.notifyDataSetChanged();
                }
            }
            else if (lastRecyclerAction==2)
            {
                if (strings1.get(0).getName() == TURN_BACK_BUTTON) {
                    MoverArgument result = MoveToParent(strings1, currentDir1);
                    lastRecyclerAction = 2;
                    strings1 = result.getStrings();
                    currentDir1 = result.getCurrentDir();
                    listAdapter2.notifyDataSetChanged();
                } else if ((strings1.get(0).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2) ||
                        (strings1.get(0).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2)) {
                    for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                    strings1.add(STORAGE_LOCATION);
                    strings1.add(SDCARD_LOCATION);
                    InMainDirectory2 = true;
                    listAdapter2.notifyDataSetChanged();
                }
            }
        }
        else
        {
            if (currentMode != NORMAL_MODE)
            {
                currentMode = NORMAL_MODE;
                while (selected.size() > 0) selected.remove(0);
                listAdapter.notifyDataSetChanged();
            }
            if (currentMode1 != NORMAL_MODE)
            {
                currentMode1 = NORMAL_MODE;
                while (selected1.size() > 0) selected1.remove(0);
                listAdapter2.notifyDataSetChanged();
            }
        }

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
        if (currentDir.getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()))
        {
            strings.add(STORAGE_LOCATION);
        }
        else if (currentDir.getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()))
        {
            strings.add(SDCARD_LOCATION);
        }
        else strings.add(new FileObject(TURN_BACK_BUTTON));
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
            strings.add(new FileObject(TURN_BACK_BUTTON));
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

    public void copyFile(File sourceFile, File destFile)
    {
        try
        {
            FileInputStream inStream = new FileInputStream(sourceFile);
            FileOutputStream outStream = new FileOutputStream(destFile);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(),"Something wrong", Toast.LENGTH_SHORT).show();
            Log.e("EPTAerr", e.getLocalizedMessage());
            Log.e("EPTAerr", sourceFile.getAbsolutePath());
            Log.e("EPTAerr", sourceFile.getName());
            Log.e("EPTAerr", destFile.getAbsolutePath());
            Log.e("EPTAerr", destFile.getName());


        }

    }

    //with dirs
    public void intellectCopy(String sourceDir, String destDir)
    {
        try {
            File src = new File(sourceDir);
            File dst = new File(destDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    intellectCopy(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e)
        {

        }
    }

    public void CopyDialog (final FileObject object)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Copying")
                .setMessage("Copy to " + object.getFile().getAbsolutePath())
                .setIcon(R.mipmap.copy_icon)
                .setCancelable(false)
                .setPositiveButton("Copy here", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String destination = "";
                        if(object.getFile().isFile()) destination = object.getFile().getParentFile().getAbsolutePath() + "/";

                        if(object.getFile().isDirectory()) destination = object.getFile().getAbsolutePath() + "/";

                        /*while(selectedForCopy.size() != 0)
                        {
                            intellectCopy(selectedForCopy.get(0).getFile().getAbsolutePath(), destination);
                            //copyFile(selectedForCopy.get(0).getFile(), new File(destination + selectedForCopy.get(0).getName()));
                            Log.d("EPTAcopy", "try1 :  " + destination);
                            selectedForCopy.remove(0);
                        }
                        while (selectedForCopy1.size() != 0)
                        {
                            intellectCopy(selectedForCopy1.get(0).getFile().getAbsolutePath(), destination);
                            //copyFile(selectedForCopy1.get(0).getFile(), new File(destination + selectedForCopy1.get(0).getName()));
                            Log.d("EPTAcopy", "try2 :  " + destination);
                            selectedForCopy1.remove(0);
                        }*/
                        try
                        {
                            copyEZ(selectedForCopy,new File(destination));
                            copyEZ(selectedForCopy1,new File(destination));
                            while(selectedForCopy.size() != 0) selectedForCopy.remove(0);
                            while(selectedForCopy1.size() != 0) selectedForCopy1.remove(0);
                        }
                        catch (IOException e)
                        {

                        }



                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        listAdapter.notifyDataSetChanged();
        listAdapter2.notifyDataSetChanged();
    }

    public void copyEZ(List<FileObject> sourceFiles, File destFile) throws IOException
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
    }

}