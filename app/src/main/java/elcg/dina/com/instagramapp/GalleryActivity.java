package elcg.dina.com.instagramapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.setSelectedTabIndicatorColor(Color.BLACK);

        final Fragment1 tab1 = new Fragment1();
        final Fragment2 tab2 = new Fragment2();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, tab1).commit();


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0 :
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, tab1).commit();
                        break;
                    case 1 :
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, tab2).commit();
                        break;
                    case 2 :
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


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        Intent i = new Intent(GalleryActivity.this , HomeScreen.class);
        startActivity(i);
    }
}
