package com.android.testhorizontalscrollview;

import java.io.IOException;
import java.io.InputStream;

import com.android.ui.MatrixImageView;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	private HorizontalScrollView horizontalScrollView;
	private MatrixImageView imageView2;
	private Button button1;
	private int degree = 25;
	PointF middleF = new PointF();
	Matrix	matrix = new Matrix();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button)this.findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imageView2.setRotation(degree, true);
				degree+=30;
			}
		});
        horizontalScrollView = (HorizontalScrollView)this.findViewById(R.id.horizontalScrollView1);
//        LinearLayout layout = (LinearLayout)this.findViewById(R.id.horizonlayout);
        imageView2 = (MatrixImageView)this.findViewById(R.id.imageView2);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        for(int i = 0;i<50;i++) {
        	ImageButton imageView = new ImageButton(this);
        	imageView.setImageResource(R.drawable.test);
        	imageView.setClickable(true);
        	imageView.setBackgroundColor(Color.GRAY);
        	
        	
        	layout.addView(imageView);
        }
        horizontalScrollView.addView(layout);
//        imageView2.setImageBitmap(getBitmapFromAssets("people.png"));
        imageView2.setImageResource(R.drawable.people);
        matrix = imageView2.getImageMatrix();
//        imageView2.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				try {
//					float degree = getRotationDegree(event);
//					Log.d("zejia.ye", "degree = "+degree);
//					matrix.postRotate(degree);
//					imageView2.setImageMatrix(matrix);
//					imageView2.invalidate();
//				} catch (IllegalArgumentException e) {
//					// TODO: handle exception
//				}
//
//				
//				return false;
//			}
//		});
        
    }
    /**
     * 获取旋转角度
     * @param 动作
     * @return 角度
     */
    private float getRotationDegree(MotionEvent e){
    	double rotaion_x = e.getX(0) - e.getX(1);
    	double roation_y = e.getY(0) - e.getY(1);
    	double radians = Math.atan2(roation_y, rotaion_x);
    	return (float)Math.toDegrees(radians);
    }
    /**
     * 从assets文件获取图片资源
     * @param fileName 图片名字
     * @return 图片bitmap
     */
    private Bitmap getBitmapFromAssets(String fileName) {
    	Bitmap imageBitmap = null;
    	AssetManager manager = getResources().getAssets();
    	try {
			InputStream is = manager.open(fileName);
			imageBitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
    	return imageBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
