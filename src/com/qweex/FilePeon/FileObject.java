package com.qweex.FilePeon;

import java.io.DataInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileObject implements Comparable<FileObject>{
    boolean isDir;
    String path, name, permissions;
    long size;
    Date modified;

    public FileObject(File f)
    {
        isDir = f.isDirectory();
        path = f.getPath();
        name = f.getName();
        if(!isDir)
            size = f.length();
        modified = new Date(f.lastModified());

        //Permissions
        Process ls = null;
        DataInputStream osRes;
        try {
/*                System.out.println("ls -l -d \"" + path + "\"");*/
                ls = Runtime.getRuntime().exec("ls -l -d \"" + path + "\"");
                osRes = new DataInputStream(ls.getInputStream());
                permissions = osRes.readLine();
                /*System.out.println(path + " : " + permissions);*/
                if(permissions==null)
                    throw new Exception();
                permissions = permissions.substring(1, 10);
                ls.getOutputStream().close();
                ls.getInputStream().close();
                ls.getErrorStream().close();
        }
        catch (Exception e) {
            permissions = "---------";
        }
    }

    public String getName()
    {
        return name;
    }

    public String getSize()
    {
        if(isDir)
            return "";
        return humanReadableByteCount(size, false);
    }

    public String getPermissions()
    {
        return (isDir ? "d" : "" ) + permissions;
    }

    public String getModified()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yy H:mm:ss");
        return sdf.format(modified);
    }


    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @Override
    public int compareTo(FileObject other)
    {
        if(this.isDir!=other.isDir)
            return this.isDir?-1:1;
        else
            return name.compareTo(other.name);
    }

}
