package elcg.dina.com.instagramapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Fragment2 extends Fragment {

    ImageView captureImageView,nextBtn;
    private static final int CAMERA_REQUEST = 1888;
    private SurfaceView preview=null;
    private SurfaceHolder previewHolder=null;
    private Camera camera=null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    String imgPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_2, container, false);
        captureImageView = (ImageView)view.findViewById(R.id.captureImage);
        preview=(SurfaceView)view.findViewById(R.id.cameraView);
        previewHolder=preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        nextBtn = (ImageView)view.findViewById(R.id.nextBtn);


        captureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inPreview) {
                    camera.takePicture(null, null, photoCallback);
                    inPreview=false;
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),FinalActivity.class);
                i.putExtra("imagePath",imgPath);
                startActivity(i);

            }
        });

        return view;
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview=true;
        }
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };


    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera=null;
        inPreview=false;

        super.onPause();
    }
    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result=size;
                }
                else {
                    int resultArea=result.width * result.height;
                    int newArea=size.width * size.height;

                    if (newArea > resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result=size;
            }
            else {
                int resultArea=result.width * result.height;
                int newArea=size.width * size.height;

                if (newArea < resultArea) {
                    result=size;
                }
            }
        }

        return(result);
    }

    private void initPreview(int width, int height) {
        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
                Log.e("PreviewDemo",
                        "ExceptioninsetPreviewDisplay", t);
                Toast.makeText(getActivity(), t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters=camera.getParameters();
                Camera.Size size=getBestPreviewSize(width, height, parameters);
                Camera.Size pictureSize=getSmallestPictureSize(parameters);

                if (size != null && pictureSize != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(pictureSize.width,
                            pictureSize.height);
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    camera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }
    Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePhotoTask().execute(data);
            camera.startPreview();
            inPreview=true;
        }
    };

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpeg) {


            String[] all = new File(Environment.getExternalStorageDirectory()+"/MyImagesForApp").list();
            File photo=
                    new File(Environment.getExternalStorageDirectory()+"/MyImagesForApp",
                            (all.length+1)+".jpg");


            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos=new FileOutputStream(photo.getPath());

                fos.write(jpeg[0]);
                fos.close();
                imgPath = photo.getAbsolutePath();
            }
            catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }

            return(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Camera.CameraInfo info=new Camera.CameraInfo();

            for (int i=0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    camera=Camera.open(i);
                }
            }
        }

        if (camera == null) {
            camera=Camera.open();
        }

        startPreview();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            captureImageView.setImageBitmap(photo);
        }
    }
}