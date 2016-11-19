package elcg.dina.com.instagramapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class HomeScreen extends AppCompatActivity  {

    ArrayList<ImageItem> imgItemArray;
    ListView listView ;
    ListViewCustomAdaptor listViewCustomAdaptor ;
    File mainFolder;
    String imagesFileString;
    public static DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        imgItemArray = new ArrayList<>();
        db = new DatabaseHandler(this);

        mainFolder = new File(Environment.getExternalStorageDirectory(), "MyImagesForApp");

                if (mainFolder.exists()){
                    if(db.getImagesCount()>0) {
                        imgItemArray = db.getAllImages();
                        Log.w("count",imgItemArray.size()+"");
                        Log.w("last item" , imgItemArray.get(imgItemArray.size()-1).getImageURL());
                    }
                    else{
                        readCopyFromAssets();
                        copyImagesToInternalStorage();
                        imagesFileString = read_file();
                        exetracJsonData(imagesFileString);
                    }

                }
                else {
                    mainFolder.mkdirs();
                    readCopyFromAssets();
                    copyImagesToInternalStorage();
                    imagesFileString = read_file();
                    exetracJsonData(imagesFileString);
                }



        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        listView = (ListView)findViewById(R.id.listViewImages);

        /*---------------------------------------------------*/
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0 :
                        tab.setIcon(R.drawable.home1);
                        tabLayout.getTabAt(2).setIcon(R.drawable.cameran);
                        break;
                    case 2 :
                        tab.setIcon(R.drawable.cam);
                        tabLayout.getTabAt(0).setIcon(R.drawable.homen);
                        Intent i = new Intent(HomeScreen.this,GalleryActivity.class);
                        startActivity(i);
                        HomeScreen.this.finish();
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        DatabaseHandler handler = new DatabaseHandler(this);
// Get access to the underlying writeable database
        SQLiteDatabase db = handler.getWritableDatabase();
// Query for items from the database and get a cursor back
        Cursor imagesCursor = db.rawQuery("SELECT _id,*  FROM images", null);
        //Cursor cur =  db.rawQuery( "select rowid _id,* from images", null);

        listViewCustomAdaptor = new ListViewCustomAdaptor(this,imagesCursor);
        listView.setAdapter(listViewCustomAdaptor);
        listViewCustomAdaptor.notifyDataSetChanged();

    }



    @Override
    protected void onResume() {
        super.onResume();
//        imgItemArray = db.getAllImages();
//        listViewCustomAdaptor = new ListViewCustomAdaptor(this,R.layout.custome_image ,imgItemArray);
//        listView.setAdapter(listViewCustomAdaptor);
//        listViewCustomAdaptor.notifyDataSetChanged();

    }

    private void copyImagesToInternalStorage() {
            AssetManager assetManager = getAssets();
            String[] files = null;
            try {
                files = assetManager.list("img");
            } catch (IOException e) {
                Log.e("tag", "Failed to get asset file list.", e);
            }
            for(String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open("img/"+filename);
                    File outFile = new File(mainFolder.getAbsolutePath()+"/", filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch(IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
            }
        }

    public void exetracJsonData(String fileString) {

        try {
            JSONObject mainJsnobject = new JSONObject(fileString);
            JSONArray jsonArray = mainJsnobject.getJSONArray("images");
            Log.w("Json Array" , jsonArray.toString());
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject ;
                jsonObject = jsonArray.getJSONObject(i);
                Log.w("Json object" , jsonObject.toString());
                ImageItem imageItem = new ImageItem();
                imageItem.setImageURL(mainFolder.getAbsolutePath()+"/"+jsonObject.getString("imgURL"));
                imageItem.setImageCaption(jsonObject.getString("imgCaption"));
                imageItem.setImageIsLiked(jsonObject.getBoolean("imgIsLiked"));
                db.addImage(imageItem);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String read_file() {
        try {
            FileInputStream fis = new FileInputStream(mainFolder.getAbsolutePath()+"/images.txt");
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    private void readCopyFromAssets(){
        AssetManager assetManager = this.getAssets();
        InputStream in = null;
        try {
            in = assetManager.open("images");
            OutputStream out = new FileOutputStream(mainFolder.getAbsolutePath()+"/images.txt");
            copyFile(in,out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

    }

}
