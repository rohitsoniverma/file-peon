package com.qweex.FilePeon;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {
    ListView lv;
    String dirPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Light);
        lv = getListView();
        dirPath = "/sdcard";
        try {
            dirPath = getIntent().getExtras().getString("path");
        } catch(NullPointerException e) {}

        FileAdapter A = new FileAdapter(this, 1, new ArrayList<FileObject>());
        File dir = new File(dirPath);
        File[] fileList = dir.listFiles();
        for (int i = 0; i < fileList.length; i++)
        {
            A.add(new FileObject(fileList[i]));
        }
        A.sort();
        lv.setAdapter(A);
        lv.setOnItemClickListener(this);
        if(isTabletDevice(this))
            setTitle(dirPath);
        else
            setTitle(dirPath.substring(dirPath.lastIndexOf(File.pathSeparator)));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        String n = dirPath + "/" + ((TextView)view.findViewById(R.id.fileName)).getText().toString();
        System.out.println("DEREDADEADA: " + n);
        if(((TextView)view.findViewById(R.id.filePermissions)).getText().toString().startsWith("d"))
        {
            Intent next = new Intent(MainActivity.this, MainActivity.class);
            next.putExtra("path", n);
            startActivity(next);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else
        {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(n);

            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String ext=file.getName().substring(file.getName().lastIndexOf(".")+1);
            String type = mime.getMimeTypeFromExtension(ext);
            intent.setDataAndType(Uri.fromFile(file),type);
            startActivity(intent);
        }
    }

    public void onBackPressed()
    {
        doBackThings();
    }

    public void doBackThings() {
        System.out.println("HERP");
        finish();
        if(!dirPath.equals("/sdcard"))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            doBackThings();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    { super.onConfigurationChanged(newConfig); }



    /************************** Utility methods **************************/
    //http://stackoverflow.com/a/9624844/1526210
    public static boolean isTabletDevice(android.content.Context activityContext) {
        // Verifies if the Generalized Size of the device is XLARGE to be
        // considered a Tablet
        boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >=	//Changed this from == to >= because my tablet was returning 8 instead of 4.
                Configuration.SCREENLAYOUT_SIZE_LARGE);


        // If XLarge, checks if the Generalized Density is at least MDPI (160dpi)
        if (xlarge) {
            android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
            Activity activity = (Activity) activityContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            //This next block lets us get constants that are not available in lower APIs.
            // If they aren't available, it's safe to assume that the device is not a tablet.
            // If you have a tablet or TV running Android 1.5, what the fuck is wrong with you.
            int xhigh = -1, tv = -1;
            try {
                Field f = android.util.DisplayMetrics.class.getDeclaredField("DENSITY_XHIGH");
                xhigh = (Integer) f.get(null);
                f = android.util.DisplayMetrics.class.getDeclaredField("DENSITY_TV");
                xhigh = (Integer) f.get(null);
            }catch(Exception e){}

            // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160, DENSITY_TV=213, DENSITY_XHIGH=320
            if (metrics.densityDpi == android.util.DisplayMetrics.DENSITY_DEFAULT
                    || metrics.densityDpi == android.util.DisplayMetrics.DENSITY_HIGH
                    || metrics.densityDpi == android.util.DisplayMetrics.DENSITY_MEDIUM
                    || metrics.densityDpi == tv
                    || metrics.densityDpi == xhigh
                    ) {

                return true;
            }
        }
        return false;
    }
}
