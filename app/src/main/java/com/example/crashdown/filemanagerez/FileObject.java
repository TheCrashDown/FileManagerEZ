package com.example.crashdown.filemanagerez;

import java.io.File;


public class FileObject
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
}
