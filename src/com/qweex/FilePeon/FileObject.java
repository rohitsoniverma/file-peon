/*
        DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                    Version 2, December 2004

 Copyright (C) 2013 Jon Petraglia <MrQweex@qweex.com>

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package com.qweex.FilePeon;     //Display what package this class belongs to

//Import the necessary objects.
import java.io.DataInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

//This class is used to store the relevant information about a file.
// Think of it primarily as a Record.
public class FileObject     //This class is just a class. Not an Activity.
        implements Comparable<FileObject>   //This means that it defines what it means to compare two of these objects.
{
    //Data members:
    boolean isDir;
    String path, name, permissions;
    long size;
    Date modified;

    //Constructor:
    public FileObject(File f)
    {
        //These are self explanatory.
        isDir = f.isDirectory();
        path = f.getPath();
        name = f.getName();
        if(!isDir)
            size = f.length();
        modified = new Date(f.lastModified());

        //Permissions is a bit trickier. We need to execute 'ls'
        // since Java does not provide a reliable way to find permissions for all three groups.
        Process ls = null;
        DataInputStream osRes;
        try {
            //Here we run the command and then tell it to wait for a result (which should be instantaneous).
            try {
                ls = Runtime.getRuntime().exec("ls -l -d \"" + path + "\"");
                ls.waitFor();   //Google 'ls linux' if you are confused about what these tags do
            }finally{ //Here we run the code when the command has completed.

                //Read the first line, which should be the ONLY line.
                osRes = new DataInputStream(ls.getInputStream());
                permissions = osRes.readLine();
                if(permissions==null) //If it returned null, there was a problem.
                    throw new Exception();

                //Otherwise, it was a success! Extract the first part of the line (skipping the digit for 'd') and close
                permissions = permissions.substring(1, 10);
                ls.getOutputStream().close();
                ls.getInputStream().close();
                ls.getErrorStream().close();
            }
        }
        //If an error occurred, we don't know the permissions. Just say everything is a no no.
        catch (Exception e)
        {
            permissions = "---------";
        }
    }

    //--------Here are the getters for the data members--------
    public String getName()
    {
        return name;
    }

    public String getSize()
    {
        if(isDir)       //If it's a directory it has no size
            return "";
        return humanReadableByteCount(size, false);
    }

    public String getPermissions()
    {
        //Append the 'd' on before the string that we retrieved from before, if it's a directory
        return (isDir ? "d" : "-" ) + permissions;
    }

    public String getModified()
    {
        //Format and return the Last Modified
        SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yy H:mm:ss");
        return sdf.format(modified);
    }

    //Here is where we define comparing two FileObjects.
    // If one is a directory and the other isn't then one is clearly a winner.
    // Otherwise, sort it by name.
    @Override
    public int compareTo(FileObject other)
    {
        if(this.isDir!=other.isDir)
            return this.isDir?-1:1;
        else
            return name.compareTo(other.name);
    }

    //Don't stress this one, I didn't even write it.
    //http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
