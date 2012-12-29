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
// Note: this doesn't actually change how the code is used, it just means that you
//       can use the shortened class name. For example
//       File var = new File();
//          instead of
//       java.io.File var = new java.io.File();
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class MainActivity  //Here is our main activity. This is what runs when the app is launched
        extends ListActivity    //Extending "ListActivity" means that not only will it be an Activity (think of it as an
                                // individual screen of an app), it also will automatically have a ListView as its main
                                // view. This is purely for convenience, extending "Activity" would work just as well.
        implements AdapterView.OnItemClickListener
                                //Again, this is for convenience. It refers to the "OnItemClick" method.
                                // Normally we'd have to create an object for it, instead we just say the class handles it.
{
    ListView lv;        //Our main ListView.
    String dirPath;     //The path of the current screen


    //This function is called when the Activity is first created.
    // If you've used Object Orientation before, think of it as a constructor, only in Android, there is a sequence of
    // events in the app's lifestyle.
    // http://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);         //We MUST call the super's onCreate!
        setTheme(android.R.style.Theme_Light);      //Set the theme that the user chose

        lv = getListView();     //Find the listview for the ListActivity and assign it to our variable
        dirPath = "/sdcard";    //Initialize the path to the root of the sdcard.
        try {                   // Then we try to see if another path has been specified, if not, we have just launched.
            dirPath = getIntent().getExtras().getString("path");
        } catch(NullPointerException e) {}

        //Create our custom adapter named "FileAdapter". See that .java file to learn more about it.
        FileAdapter A = new FileAdapter(this, 1, new ArrayList<FileObject>());
        //Get a list of all the files in the said directory
        File[] fileList = (new File(dirPath)).listFiles();
         //Iterate over the files and add them to our adapter.
        for (int i = 0; i < fileList.length; i++)
            A.add(new FileObject(fileList[i]));
        //Sort the adapter once we are done
        A.sort();

        //Finally bind the full adapter to the ListView.
        lv.setAdapter(A);
        //Set our OnItemClickListener
        lv.setOnItemClickListener(this);

        //Finally, we set the title
        setTitle(dirPath.substring(dirPath.lastIndexOf("/")));
    }


    //Here we handle what happens when an item in the ListView is pressed.
    // The only argument we are concerned about is "view", which is the view that was pressed.
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        //Get the name of the file and stick it on the end of the current directory.
        String path = dirPath + "/" + ((TextView)view.findViewById(R.id.fileName)).getText().toString();

        //If it is a folder...
        if(((TextView)view.findViewById(R.id.filePermissions)).getText().toString().startsWith("d"))
        {
            //Start a new MainActivity! Yay recursion!
            //An intent is...well, think of it as what the app currently "intends" to do.
            // Here, just think of it as an Activity.
            Intent next = new Intent(MainActivity.this, MainActivity.class);
            //Remember up top when we did the "getExtras().getString("path", ...)" call?
            // Here is where that is important. It puts the path as an extra to the Activity.
            // (Think of extras as like parameters for Activities.)
            next.putExtra("path", path);
            startActivity(next);
            //Here we want a slide animation, in case the system uses a zoom in or anything
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        //Otherwise, it must be a file
        else
        {
            Intent intent = new Intent();
            //Here we do things a little different by using a builtin Intent.
            intent.setAction(android.content.Intent.ACTION_VIEW);

            //Now we want to try to find what app is set to open the type of file.
            File file = new File(path);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String ext=file.getName().substring(file.getName().lastIndexOf(".")+1);
            String type = mime.getMimeTypeFromExtension(ext);

            //Once we have both the path and the mimetype, we set the data for the new Intent,
            // much like putting an Extra, and start it!
            intent.setDataAndType(Uri.fromFile(file),type);
            startActivity(intent);
        }
    }

    //Here we override the function that handles when a key is pressed.
    // In this case, we are only concerned with the Back key.
    // NOTE: The only reason this function is here is because we want the custom animation for closing the Activity.
    //       By default, pressing Back calls "finish()".
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();   //Finish the activity. Note that this only STARTS the finishing process. The Activity will actually
                        // finish once this function has returned control to its caller.
            if(!dirPath.equals("/sdcard"))  //If this is the root of the sdcard, we don't want a custom animation when finishing
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    //We put this here to say "When the user rotates the device or anything like that,
    // don't re-create the Activity. Just do nothing."
    //See Android.XML file for more info on this.
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    { super.onConfigurationChanged(newConfig); }
}
