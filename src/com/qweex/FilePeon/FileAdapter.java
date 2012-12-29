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
import java.util.ArrayList;
import java.util.Collections;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//This class is for populating a ListView. Generally, this is what an ArrayAdapter is for, but you usually need to extend
// it to account for how you want to modify the data before putting it in a view.

/*
Before continuing, I want to talk about why an Adapter is needed for a Listview. Before I do that though, I need to explain
why ListViews are used, period. When you use a ListView, you think it's just something that scrolls, containing many sub-views,
but you can accomplish the same thing with a ScrollView.

But if you did a ScrollView and loaded, say, 100 elements, you'd need to create and load the views for all 100 of those elements,
which takes a long time and wastes memory.

A ListView gets around this by only loading a certain number of views at a time, only what is necessary. Instead of loading
all 100, it loads, say, 6 or 7 at a time. When you scroll, it then recycles these views by simply putting the new data in them.

This is brilliant and reduces memory cost and loadup time, but how does the ListView know what to load? That's where an
Adapter comes in. An Adapter receives the data and then has a function called "getView()" that is called whenever a view
needs to be recycled. A normal ArrayAdapter simply takes parts of an ArrayList and puts it into a single view. Extending
it allows us to handle everything in a more specific way, like using an Object's members instead of a single Object.

And that is what an Adapter is and how it's needed.
 */

public class FileAdapter
        extends ArrayAdapter<FileObject>
{
    private ArrayList<FileObject> entries;  //Here is the actual data that the ListView should contain.
    private Activity activity;              //Here is a reference to the activity that created the FileAdapter object

    //Constructor: just call the super() and intialize the members.
    public FileAdapter(Activity a, int textViewResourceId, ArrayList<FileObject> entries) {
        super(a, textViewResourceId, entries);
        this.entries = entries;
        this.activity = a;
    }

    //As stated above, this is the crux of the Adapter. This is where the magic happens.
    // The relevant parameter is "recycleView". It's the old view (if there is one) that we are going to recycle.
    // Position is also given as to where in the ArrayList the relevant data is stored.
    @Override
    public View getView(int position, View recycleView, ViewGroup parent) {
        View v = recycleView;

        //When first starting off, there are no views, so they need to be created.
        //However, creating them every time would take away the benefit of recycling, so we can check to see if the view
        // already exists
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.file, null);
        }

        //Get the relevant data and set all the views.
        final FileObject file = entries.get(position);
        ((TextView)v.findViewById(R.id.fileName)).setText(file.getName());
        ((TextView)v.findViewById(R.id.fileSize)).setText(file.getSize());
        ((TextView)v.findViewById(R.id.fileModified)).setText(file.getModified());
        ((TextView)v.findViewById(R.id.filePermissions)).setText(file.getPermissions());
        return v; //getView() is assumed to return the view that it modifies or creates, hence the name.
    }

    //We can call this to sort the ArrayList according to the function defined in the FileObject class.
    public void sort()
    {
        Collections.sort(entries);
    }
}