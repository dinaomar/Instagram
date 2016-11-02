package elcg.dina.com.instagramapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dina on 30/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "imageManager";

    private static final String TABLE_IMAGES = "images";

    private static final String KEY_ID = "_id";
    private static final String IMG_URL = "url";
    private static final String IMG_CAPTION = "image_caption";
    private static final String IMG_IS_LIKED = "is_liked";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TABLE_IMAGES + " (" +
                KEY_ID + " INTEGER PRIMARY KEY autoincrement, " +
                IMG_URL + " TEXT, " +
                IMG_CAPTION + " TEXT, " +
                IMG_IS_LIKED + " BOOLEAN);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);

        // Create tables again
        onCreate(db);
    }

    // Adding new image
    public void addImage(ImageItem imageItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(IMG_URL, imageItem.getImageURL());
        values.put(IMG_CAPTION, imageItem.getImageCaption());
        values.put(IMG_IS_LIKED, imageItem.getImageIsLiked());

        // Inserting Row
        db.insert(TABLE_IMAGES, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<ImageItem> getAllImages() {
        ArrayList<ImageItem> contactList = new ArrayList<ImageItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ImageItem imageItem = new ImageItem();
                imageItem.setId(Integer.parseInt(cursor.getString(0)));
                imageItem.setImageURL(cursor.getString(1));
                imageItem.setImageCaption(cursor.getString(2));
                imageItem.setImageIsLiked(Boolean.parseBoolean(cursor.getString(3)));
                // Adding image to list
                contactList.add(imageItem);
            } while (cursor.moveToNext());
        }

        // return image list
        return contactList;
    }

    public int getImagesCount() {

        String countQuery = "SELECT  * FROM " + TABLE_IMAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        try {
            if (cursor.moveToFirst()) {
                count = cursor.getCount();
            }
            return count;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        cursor.close();

        // return count
        return cursor.getCount();
    }
    }

    // Updating single contact
    public int updateImage(ImageItem imageItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,imageItem.getId());
        values.put(IMG_URL, imageItem.getImageURL());
        values.put(IMG_CAPTION, imageItem.getImageCaption());
        values.put(IMG_IS_LIKED , imageItem.getImageIsLiked());

        // updating row
        return db.update(TABLE_IMAGES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(imageItem.getId())});
    }
}
