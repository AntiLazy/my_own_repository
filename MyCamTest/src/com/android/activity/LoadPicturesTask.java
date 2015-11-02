package com.android.activity;

import java.io.File;
import java.util.ArrayList;

import com.android.activity.PictureGallery.ImageAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

public class LoadPicturesTask extends AsyncTask<String, Void, Void> {
	
	private ImageAdapter adapter;
	private ArrayList<Bitmap> pictureList;
	private ArrayList<String> picturePathList;
	private Context context;
	
	public LoadPicturesTask(Context context,ImageAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
		this.pictureList = adapter.getBitmaps();
		this.picturePathList = adapter.getPaths();
	}
	
	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		File files[] = new File(params[0]).listFiles();
        for(File file:files) {
//            Log.d(TAG, "file.getName() = " + file.getName());
            if(file.getName().endsWith(".jpg")) {
//                Log.d(TAG, "file.getAbsolutePath() = "+file.getAbsolutePath());
            	picturePathList.add(file.getAbsolutePath());
                pictureList.add(BitmapFactory.decodeFile(file.getAbsolutePath()));
                publishProgress();
            }
        }
		return null;
	}
	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		adapter.notifyDataSetChanged();
	}
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		adapter.notifyDataSetInvalidated();
		Toast.makeText(context, "length = "+adapter.getBitmaps().size(), 1).show();
	}

}