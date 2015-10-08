package com.android.custom;

import com.android.ui.MatrixImageView;
import com.android.ui.OriMatrixImageView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
    MatrixImageView imageView;
    private Button button;
    private DisplayMetrics dm;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = (MatrixImageView)this.findViewById(R.id.imageView1);
//        dm = getResources().getDisplayMetrics();
//         bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
//        this.imageView.postDelayed(new Runnable() {
//            
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//               imageView.init();
//            }
//        }, 1000);
        
//        setContentView(R.layout.activity_main2);
//        this.imageView = (OriMatrixImageView)this.findViewById(R.id.imageView1);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
