package com.android.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.preview.CameraSurfaceView;
import com.android.util.StorageUtil;
import com.example.mycamtest.R;

public class CameraActivity extends Activity implements OnClickListener,MediaRecorder.OnErrorListener,MediaRecorder.OnInfoListener{
    private Camera mCamera;
    private boolean isPreview = false;
    private CameraSurfaceView surfaceView;
    private ImageButton imageButton;
    private Switch buttonSwitch;
    private static String TAG = "CameraActivity";
    private float previewRate = 16f/9f;
    private ImageView imageView;
    private TextView timeteTextView;
    private Handler myCameraHandler;
    private final static int START_PREVIEW = 1;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    /**
     * Camera的模式，0为拍照模式，1为录像模式，2为正在录制。
     */
    private  int cameraStatus = 0;
    /**
     * 拍照模式
     */
    public static int PHOTO_MODE = 0;
    /**
     * 录像模式
     */
    public static int VIDEO_MODE = 1;
    /**
     * 正在记录模式
     */
    public static int VIDEO_RECORDING = 2;
    private Drawable drawable_photo;
    private Drawable drawable_video;
    private Drawable drawable_video_recording;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        surfaceView = (CameraSurfaceView)this.findViewById(R.id.camera_surfaceview);
        imageButton = (ImageButton)this.findViewById(R.id.btn_shutter);
        imageView = (ImageView)this.findViewById(R.id.imageView1);
        buttonSwitch = (Switch)this.findViewById(R.id.switch1);
        drawable_photo = getResources().getDrawable(R.drawable.btn_shutter_photo);
        drawable_video = getResources().getDrawable(R.drawable.btn_shutter_video);
        drawable_video_recording = getResources().getDrawable(R.drawable.btn_shutter_video_default);

        buttonSwitch.setOnClickListener(new OnClickListener() {
        
            @Override
            public void onClick(View v) {
                if(buttonSwitch.isChecked()) {
                    cameraStatus = 0;
                    imageButton.setImageDrawable(drawable_photo);
                    
                } else {
                    cameraStatus = 1;
                    imageButton.setImageDrawable(drawable_video);
                }
                myCameraHandler.sendEmptyMessage(START_PREVIEW);
            }
        });
        timeteTextView = (TextView)this.findViewById(R.id.timeTextView);
        //获得相机状态
        this.cameraStatus = buttonSwitch.isChecked() ? 0 : 1;
        if(this.cameraStatus == 0) imageButton.setImageDrawable(drawable_photo);
        else imageButton.setImageDrawable(drawable_video);
        imageButton.setOnClickListener(this);
        imageView.setOnClickListener(this);
        myCameraHandler = new MyCameraHandler();
        StorageUtil.getStoragePath();
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        
        
         LayoutParams params = surfaceView.getLayoutParams();
         //获取屏幕宽高
         DisplayMetrics dm = this.getResources().getDisplayMetrics();
         params.width = dm.widthPixels;
         params.height = dm.heightPixels;
         surfaceView.setLayoutParams(params);
         Log.d(TAG, "params.width = "+params.width+" params.height = "+params.height);
        super.onResume();
    }
    public Camera getCamera() {
        return this.mCamera;
    }
    /*8
     * 获取相机模式,0为拍照模式，1为录像模式，2为正在录制。
     */
    public  int getCameraStatus(){
        return cameraStatus;
    }
    public void startPreview(){
        doStopCamera();
        mCamera = Camera.open();
        if(this.isPreview)
            return;
        if(this.mCamera != null) {
            
            //Toast.makeText(this, "startPreview", 1).show();
            Log.d(TAG, "startPreview");
            Log.d(TAG, previewRate+"");
           Parameters parameters = mCamera.getParameters();
           parameters.setPictureFormat(ImageFormat.JPEG);
//           Log.d(TAG, "parameters.flatten = "+parameters.flatten());
//           List<Size> previewSizes = parameters.getSupportedPreviewSizes();
//           List<Size> pictureSizes = parameters.getSupportedPictureSizes();
               parameters.setPreviewSize(1216, 800);
               parameters.setPictureSize(1216, 800);
           //List<String> focusModeStrings = parameters.getSupporteFocusModes();
               parameters.setRotation(90);
           mCamera.setParameters(parameters);
           mCamera.setDisplayOrientation(90);
           try {
//               mCamera.setPreviewDisplay(surfaceView.getSurfaceHolder());
            mCamera.setPreviewDisplay(surfaceView.getSurfaceHolder());
            mCamera.startPreview();
            this.isPreview = true;
            Log.d(TAG, "startPreview : success");
        } catch (Exception e) {
            Log.d(TAG, "error : "+e.toString());
        }
        }
    }
    public void doStopCamera() {
        if(mCamera !=null) {
            mCamera.stopPreview();
            isPreview = false;
            mCamera.release();
            mCamera = null;
        }
    }
    
    public void prepareBeforeRecording() {
        
    }
    public void doStartRecording(){
        
        if( this.mediaRecorder == null) this.mediaRecorder = new MediaRecorder();
        else this.mediaRecorder.reset();
        try {
            this.mCamera.unlock();
            this.mediaRecorder.setCamera(this.mCamera);
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            this.mediaRecorder.setProfile(profile);
            this.mediaRecorder.setPreviewDisplay(surfaceView.getSurfaceHolder().getSurface());
            this.mediaRecorder.setOrientationHint(90);
            this.mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            this.mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            this.mediaRecorder.setOrientationHint(90);
//            this.mediaRecorder.setVideoSize(1218, 800);
            this.mediaRecorder.setVideoFrameRate(50);
            this.mediaRecorder.setOutputFile(StorageUtil.getVideoPath());
//            mCamera.setDisplayOrientation(90);
            this.mediaRecorder.prepare();
            this.mediaRecorder.setOnErrorListener(this);
            this.mediaRecorder.setOnInfoListener(this);
            this.mediaRecorder.start();
            this.timeteTextView.setVisibility(View.VISIBLE);
            this.isRecording = true;
            this.isPreview = true;
            this.cameraStatus = 2;
            this.imageButton.setImageDrawable(drawable_video_recording);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("zejia.ye", "abc"+e.toString(),new IllegalStateException());
        } 
        Log.d("zejia.ye", "doStartRecording is finished!");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_shutter:
            Log.d("zejia.ye", "Shutter button is clicked!");
            switch (this.cameraStatus) {
            case 0:
                this.mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
                break;
            case 1:
                this.doStartRecording();
                break;
            case 2:
                this.saveRecord();
            default:
                break;
            }
            
            break;
        case R.id.imageView1:
            Log.d(TAG, "imageView1 is clicked.");
//            Intent intent = new Intent(CameraActivity.this, PictureUI.class);
//            intent.putExtra("picturePath", imageView.getTag().toString());
            try {
                Intent intent = new Intent(CameraActivity.this,PictureGallery.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            
            overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left); 
            break;
        default:
            break;
        }
        
        
    }
    
    private void saveRecord() {
        // TODO Auto-generated method stub
        if(isRecording) {
            this.mediaRecorder.stop();
            this.mediaRecorder.release();
            this.mediaRecorder = null;
        }
        this.isPreview = false;
        this.cameraStatus = 1;
        this.imageButton.setImageDrawable(drawable_video);
        //myCameraHandler.sendEmptyMessage(START_PREVIEW);
    }

    private ShutterCallback shutterCallback = new ShutterCallback() {
        
        @Override
        public void onShutter() {
            Log.d(TAG, "Shutter button is clicked!");
            
        }
    };
    
    private PictureCallback rawCallback = new PictureCallback() {
        
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "RawCallback , and I do nothing here.");
        }
    };
    
    private PictureCallback jpegCallback = new PictureCallback() {
        
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            if(!isPreview) return;
            if(data!=null) {
                //change the picture data from byte[] to bitmap
               Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
               //the angle of the picture is wrong and here do the rotate
//               Matrix matrix = new Matrix();
//               matrix.postRotate(90f);
//               bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
               //show the picture
               imageView.setImageBitmap(bitmap);
               //save the picture
              String picturePathString = StorageUtil.saveBitmap(bitmap);
              imageView.setTag(picturePathString);
              Log.d(TAG, "imageView.Tag = "+imageView.getTag().toString());
               isPreview = false;
               myCameraHandler.sendEmptyMessage(START_PREVIEW);
            }
        }
    };
    
    private class MyCameraHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
            case START_PREVIEW:
                
                startPreview();
                break;

            default:
                break;
            }
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        Log.d("zejia.ye", "Error what = "+what+" extra = "+ extra);
        
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        // TODO Auto-generated method stub
        Log.d("zejia.ye", "Info what = "+what+" extra = "+ extra);
    }
    
    

}
