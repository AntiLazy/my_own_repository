package com.android.rotationimageview;

import com.android.ui.RotateImageView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button button;
	private RotateImageView rotateImageView;
	private int rotateDegree = 25;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.button = (Button)this.findViewById(R.id.button1);
		this.rotateImageView = (RotateImageView)this.findViewById(R.id.imageView1);
		this.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rotateImageView.setRotation(rotateDegree, true);
				rotateDegree +=50;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
