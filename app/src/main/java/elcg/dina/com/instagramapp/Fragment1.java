package elcg.dina.com.instagramapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import elcg.dina.com.instagramapp.R;

public class Fragment1 extends Fragment {

    private Cursor cursor;
    private int columnIndex;
    ImageView firstImageGallery,nextBtn;

    Uri uri1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_1, container, false);

        firstImageGallery = (ImageView)view.findViewById(R.id.firstGalleryImage);
        nextBtn = (ImageView)view.findViewById(R.id.nextBtn);


        //Searching Images ID's from Gallery. _ID is the Default id code for all. You can retrive image,contacts,music id in the same way.
        String[] list = {MediaStore.Images.Media._ID};

        //Retriving Images from Database(SD CARD) by Cursor.
        cursor = getActivity().getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, list, null, null, MediaStore.Images.Thumbnails._ID);
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

        cursor.moveToFirst();
        int imageID = cursor.getInt(columnIndex);
        Uri uri = Uri.withAppendedPath(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);
        firstImageGallery.setImageURI(uri);

        GridView sdcardimage = (GridView) view.findViewById(R.id.gallery);
        ImageAdapter adapter=new ImageAdapter(getActivity());
        sdcardimage.setAdapter(adapter);


        sdcardimage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cursor.moveToPosition(position);
                int imageID = cursor.getInt(columnIndex);
                uri1 = Uri.withAppendedPath(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);
                firstImageGallery.setImageURI(uri1);

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeImage();
            }
        });

        return view;

    }

   private void storeImage(){
       String imgPathAfterCopy="";
       try {

           Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver() , Uri.parse((uri1.toString())));
           int bytes = bitmap.getByteCount();

           ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
           bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

           byte[] array = buffer.array(); //Get the underlying array containing the data.

           String[] all = new File(Environment.getExternalStorageDirectory()+"/MyImagesForApp").list();
           File photo=
                   new File(Environment.getExternalStorageDirectory()+"/MyImagesForApp",
                           (all.length+1)+".jpg");
           FileOutputStream fos=new FileOutputStream(photo.getPath());

           fos.write(array[0]);
           fos.close();

           imgPathAfterCopy = photo.getAbsolutePath();


       } catch (IOException e) {
           e.printStackTrace();
       }
       Intent i = new Intent(getActivity(),FinalActivity.class);
       i.putExtra("imagePath",imgPathAfterCopy);
       startActivity(i);

   }

    // Adapter for Grid View
    private class ImageAdapter extends BaseAdapter {

        private Context context;

        public ImageAdapter(Context localContext) {

            context = localContext;

        }

        public int getCount() {

            return cursor.getCount();

        }

        public Object getItem(int position) {

            return position;

        }

        public long getItemId(int position) {

            return position;

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();


            if (convertView == null) {
                holder.picturesView = new ImageView(context);
                //Converting the Row Layout to be used in Grid View
                convertView = getLayoutInflater(Bundle.EMPTY).inflate(R.layout.grid_image, parent, false);

                //You can convert Layout in this Way with the Help of View Stub. View Stub is newer. Read about ViewStub.Inflate
                // and its parameter.
                //convertView= ViewStub.inflate(context,R.layout.row,null);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            cursor.moveToPosition(position);
            int imageID = cursor.getInt(columnIndex);

            //In Uri "" + imageID is to convert int into String as it only take String Parameter and imageID is in Integer format.
            //You can use String.valueOf(imageID) instead.
            Uri uri = Uri.withAppendedPath(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);

            //Setting Image to View Holder Image View.
            holder.picturesView = (ImageView) convertView.findViewById(R.id.imageview);
            holder.picturesView.setImageURI(uri);
            holder.picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            return convertView;

        }
        // View Holder pattern used for Smooth Scrolling. As View Holder pattern recycle the findViewById() object.
        class ViewHolder {
            private ImageView picturesView;
        }
    }
}