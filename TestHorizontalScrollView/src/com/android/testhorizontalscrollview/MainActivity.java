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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	private HorizontalScrollView horizontalScrollView;
	private MatrixImageView imageView2;
	private Button button1;
	private int degree = 25;
	PointF middleF = new PointF();
	Matrix	matrix = new Matrix();
	private SeekBar seekBar;
	private int currentDegree = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ImageView imageView1 = (ImageView)this.findViewById(R.id.imageView1);
//        imageView1.setImageBitmap(oldRemeber(getBitmapFromAssets("people.png")));
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
        for(int i = 0;i<2;i++) {
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
        this.seekBar = (SeekBar)this.findViewById(R.id.seekBar1);
        this.seekBar.setMax(160);
        this.seekBar.setProgress(80);
        this.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int degree = progress - currentDegree - (seekBar.getMax()>>1);
				
				imageView2.setRotation(degree, true);
			}
		});
        
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
    /**
	 * 怀旧效果(相对之前做了优化快一倍)
	 * @param bmp
	 * @return
	 */
	private Bitmap oldRemeber(Bitmap bmp)
	{
		// 速度测试
		long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++)
		{
			for (int k = 0; k < width; k++)
			{
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
				pixels[width * i + k] = newColor;
			}
		}
		
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("may", "used time="+(end - start));
		return bitmap;
	}
	
}
