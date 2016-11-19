package elcg.dina.com.instagramapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

    // Declare variables
    private Activity activity;
    private String[] filepath;
    private String[] filename;

    private static LayoutInflater inflater = null;

    public GridViewAdapter(Activity a, String[] fpath, String[] fname) {
        activity = a;
        filepath = fpath;
        filename = fname;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return filepath.length;

    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.grid_image, null);
            ImageView image = (ImageView) vi.findViewById(R.id.imageview);

            // Decode the filepath with BitmapFactory followed by the position
            Bitmap bmp = BitmapFactory.decodeFile(filepath[position]);

            // Set the decoded bitmap into ImageView
            image.setImageBitmap(bmp);
        }
        return vi;
    }
}