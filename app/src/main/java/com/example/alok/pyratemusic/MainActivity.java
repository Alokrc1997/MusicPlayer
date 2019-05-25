package com.example.alok.pyratemusic;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView songLists;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songLists=findViewById(R.id.songList);
        runTimePermission();
    }

    public void runTimePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }
    public ArrayList<File> findSongs(File file)
    {
        ArrayList<File> arrayList=new ArrayList<File>();
        File[] files=file.listFiles();
        for(File singleFiles:files)
        {
            if(singleFiles.isDirectory()&&!singleFiles.isHidden())
            {
                arrayList.addAll(findSongs(singleFiles));
            }
            else if(singleFiles.getName().endsWith(".mp3")||singleFiles.getName().endsWith(".wav"))
            {
                arrayList.add(singleFiles);
            }

        }
        return arrayList;

    }
    void display()
    {
        final ArrayList<File> mySong =findSongs(Environment.getExternalStorageDirectory());
        items=new String[mySong.size()];
        for(int i=0;i<mySong.size();i++)
        {
            items[i]=mySong.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        songLists.setAdapter(arrayAdapter);

        songLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String name=songLists.getItemAtPosition(i).toString();
                Intent intent=new Intent(new Intent(MainActivity.this,Music.class));
                intent.putExtra("songlist",mySong);
                intent.putExtra("pos",i);
                intent.putExtra("songname",items);
                 startActivity(intent);



            }
        });
    }

}
