package com.example.crashdown.filemanagerez;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;


public class FileObject implements Serializable
{
    private String name;
    private File file;
    public FileObject(File file)
    {
        name = file.getName();
        this.file = file;
    }
    public FileObject(String string)
    {
        name = string;
    }
    public FileObject(File file, String string)
    {
        this.file = file;
        name = string;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public File getFile()
    {
        return file;
    }
    public void setFile(File file)
    {
        this.file = file;
    }

    public static final Comparator<FileObject> COMPARE_BY_NAME = new Comparator<FileObject>()
    {
        @Override
        public int compare(FileObject o1, FileObject o2)
        {
            if(o1.getFile()  != null && o2.getFile() != null)
            {
                if (o1.getFile().getAbsolutePath().equals(MainActivity.STORAGE_LOCATION.getFile().getAbsolutePath())||
                        o1.getFile().getAbsolutePath().equals(MainActivity.SDCARD_LOCATION.getFile().getAbsolutePath()))
                    return -1;
                if (o2.getFile().getAbsolutePath().equals(MainActivity.STORAGE_LOCATION.getFile().getAbsolutePath())||
                        o2.getFile().getAbsolutePath().equals(MainActivity.SDCARD_LOCATION.getFile().getAbsolutePath()))
                    return 1;
                if (o1.getFile().isDirectory() && o2.getFile().isDirectory())
                    return o1.getName().compareTo(o2.getName());
                if (o1.getFile().isDirectory() && o2.getFile().isFile())
                    return -1;
                if (o1.getFile().isFile() && o2.getFile().isDirectory())
                    return 1;
                if (o1.getFile().isFile() && o2.getFile().isFile())
                    return o1.getName().compareTo(o2.getName());
            }
            return 0;
        }
    };

    public static final Comparator<FileObject> COMPARE_BY_SIZE = new Comparator<FileObject>() {
        @Override
        public int compare(FileObject o1, FileObject o2)
        {
            if(o1.getFile()  != null && o2.getFile() != null)
            {
                if (o1.getFile().getAbsolutePath().equals(MainActivity.STORAGE_LOCATION.getFile().getAbsolutePath())||
                        o1.getFile().getAbsolutePath().equals(MainActivity.SDCARD_LOCATION.getFile().getAbsolutePath()))
                    return -1;
                if (o2.getFile().getAbsolutePath().equals(MainActivity.STORAGE_LOCATION.getFile().getAbsolutePath())||
                        o2.getFile().getAbsolutePath().equals(MainActivity.SDCARD_LOCATION.getFile().getAbsolutePath()))
                    return 1;
                if (o1.getFile().isDirectory() && o2.getFile().isFile())
                    return -1;
                if (o1.getFile().isFile() && o2.getFile().isDirectory())
                    return 1;
                if (o1.getFile().isDirectory() && o2.getFile().isDirectory())
                    return o1.getName().compareTo(o2.getName());
                if(o1.getFile().isFile() && o2.getFile().isFile())
                    return Long.compare(o2.getFile().length(), o1.getFile().length());

            }
            return 0;
        }
    };


}
