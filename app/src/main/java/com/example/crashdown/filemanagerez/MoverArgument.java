package com.example.crashdown.filemanagerez;

import java.io.File;
import java.util.List;

/**
 * Created by Lev on 11.04.2017.
 */

public class MoverArgument
{
    private List<FileObject> strings;
    private File currentDir;
    public void setStrings(List<FileObject> strings)
    {
        this.strings = strings;
    }
    public void setCurrentDir(File currentDir)
    {
        this.currentDir = currentDir;
    }
    public List<FileObject> getStrings()
    {
        return strings;
    }
    public File getCurrentDir()
    {
        return currentDir;
    }

}
