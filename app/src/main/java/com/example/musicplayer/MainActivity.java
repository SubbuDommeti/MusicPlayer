package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.ListViewSong);
        runtimePermission();

    }
    public void runtimePermission()
    {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaySongs();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public ArrayList<File> findSong(File file)
    {
        ArrayList<File>arrayList=new ArrayList<>();
        File[] files=file.listFiles();
        if(files!=null)
        for(File s:files)
        {
            if(s.isDirectory() && !s.isHidden())
                    arrayList.addAll(findSong(s));
            else if(s.getName().endsWith(".mp3"))
                        arrayList.add(s);
            else if(s.getName().endsWith(".wav"))
                        arrayList.add(s);
        }
        return  arrayList;
    }

    void displaySongs()
    {
        final ArrayList<File> mySongs=findSong(Environment.getExternalStorageDirectory());
        items=new String[mySongs.size()];
        for(int i=0;i<mySongs.size();i++)
        {
          items[i]=mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");

        }
        /*ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(myAdapter);*/

        customAdapter  customAdapter=new customAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName= (String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                        .putExtra("songs",mySongs)
                        .putExtra("songname",songName)
                        .putExtra("pos",position));
            }
        });

    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View myView=getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textsong=myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[i]);
            return  myView;
        }
    }
}
