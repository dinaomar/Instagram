package elcg.dina.com.instagramapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by dina on 28/10/16.
 */

public class ListViewCustomAdaptor extends CursorAdapter {


    public ListViewCustomAdaptor(Context context, Cursor cursor) {
        super(context, cursor);

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.custome_image, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView captionTV = (TextView) view.findViewById(R.id.txtCaption);
        ImageView main = (ImageView) view.findViewById(R.id.mainImage);
        final ImageView like = (ImageView) view.findViewById(R.id.likeImage);
        // Extract properties from cursor
        final String caption = cursor.getString(cursor.getColumnIndexOrThrow("image_caption"));
        final String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
        String isLiked = cursor.getString(cursor.getColumnIndexOrThrow("is_liked"));

        // Populate fields with extracted properties
        captionTV.setText(caption);
        Bitmap bitmap = BitmapFactory.decodeFile(url);
        main.setImageBitmap(bitmap);

        final int position = cursor.getPosition();

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(context);
                ImageItem imageItem = new ImageItem();
                imageItem.setImageIsLiked(true);
                imageItem.setImageCaption(caption);
                imageItem.setImageURL(url);
                db.updateImage(imageItem);
                like.setImageResource(R.drawable.like);
            }
        });

        Boolean liked = Boolean.parseBoolean(isLiked);

        if(liked){
            like.setImageResource(R.drawable.like);
        }


    }


}
