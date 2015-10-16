package com.example.mycrop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private CropImageView mView;
	private Button button_crop;
	private ImageView imageView;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFormat(PixelFormat.TRANSLUCENT);
	  setContentView(R.layout.activity_main);
	  mView = (CropImageView) findViewById(R.id.cropimage);
//	  BitmapFactory.decodeResource(res, id)
	  mView.setDrawable(getResources().getDrawable(R.drawable.comp), 300,
	    300);
	  
	  imageView = (ImageView)findViewById(R.id.imageView);
	  
	  button_crop = (Button)findViewById(R.id.button1);
	  button_crop.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			Thread thread = new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					try {
//						Thread.sleep(2000);
//						Bitmap mBitmap= mView.getCropImage();
//						imageView.setImageBitmap(mBitmap);
//						Log.d("zejia.ye", "clip img sucess");
//						//Toast.makeText(MainActivity.this, "clip img", 1).show();
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						Log.d("zejia.ye", e.toString());
//					}
//					
//				}
//			});
//			  thread.start();

			Bitmap mBitmap= mView.getCropImage();
			imageView.setImageBitmap(mBitmap);
			RotateAnimation rAnima = new RotateAnimation(0, 60);//顺时针旋转70度
			rAnima.setDuration(5000);
			mView.startAnimation(rAnima);
		}
	});
	  
	  
	 }

	}