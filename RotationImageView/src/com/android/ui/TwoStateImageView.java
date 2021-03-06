package com.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TwoStateImageView extends ImageView{
	private static final int ENABLED_ALPHA = 255;
    private static final int DISABLED_ALPHA = (int) (255 * 0.4);
    private boolean mFilterEnabled = true;
	public TwoStateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public TwoStateImageView(Context context) {
		this(context,null);
	}
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		if(mFilterEnabled) {
			if(enabled) {
				setImageAlpha(ENABLED_ALPHA);
			}else {
				setImageAlpha(DISABLED_ALPHA);
			}
		}
	}
    public void enableFilter(boolean enabled) {
        mFilterEnabled = enabled;
    }
}

