package com.example.mycrop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



public class CropImageView extends View {
	 // \u5728touch\u91cd\u8981\u7528\u5230\u7684\u70b9\uff0c
	 private float mX_1 = 0;
	 private float mY_1 = 0;
	 // \u89e6\u6478\u4e8b\u4ef6\u5224\u65ad
	 private final int STATUS_SINGLE = 1;
	 private final int STATUS_MULTI_START = 2;
	 private final int STATUS_MULTI_TOUCHING = 3;
	 // \u5f53\u524d\u72b6\u6001
	 private int mStatus = STATUS_SINGLE;
	 // \u9ed8\u8ba4\u88c1\u526a\u7684\u5bbd\u9ad8
	 private int cropWidth;
	 private int cropHeight;
	 // \u6d6e\u5c42Drawable\u7684\u56db\u4e2a\u70b9
	 private final int EDGE_LT = 1;
	 private final int EDGE_RT = 2;
	 private final int EDGE_LB = 3;
	 private final int EDGE_RB = 4;
	 private final int EDGE_MOVE_IN = 5;
	 private final int EDGE_MOVE_OUT = 6;
	 private final int EDGE_NONE = 7;

	 public int currentEdge = EDGE_NONE;

	 protected float oriRationWH = 0;
	 protected final float maxZoomOut = 5.0f;
	 protected final float minZoomIn = 0.333333f;

	 protected Drawable mDrawable;
	 protected FloatDrawable mFloatDrawable;

	 protected Rect mDrawableSrc = new Rect();// \u56fe\u7247Rect\u53d8\u6362\u65f6\u7684Rect
	 protected Rect mDrawableDst = new Rect();// \u56fe\u7247Rect
	 protected Rect mDrawableFloat = new Rect();// \u6d6e\u5c42\u7684Rect
	 protected boolean isFrist = true;
	 private boolean isTouchInSquare = true;

	 protected Context mContext;

	 public CropImageView(Context context) {
	  super(context);
	  init(context);
	 }

	 public CropImageView(Context context, AttributeSet attrs) {
	  super(context, attrs);
	  init(context);
	 }

	 public CropImageView(Context context, AttributeSet attrs, int defStyle) {
	  super(context, attrs, defStyle);
	  init(context);

	 }

	 @SuppressLint("NewApi")
	 private void init(Context context) {
	  this.mContext = context;
	  try {
	   if (android.os.Build.VERSION.SDK_INT >= 11) {
	    this.setLayerType(LAYER_TYPE_SOFTWARE, null);
	   }
	  } catch (Exception e) {
	   e.printStackTrace();
	  }
	  mFloatDrawable = new FloatDrawable(context);
	 }

	 public void setDrawable(Drawable mDrawable, int cropWidth, int cropHeight) {
	  this.mDrawable = mDrawable;
	  this.cropWidth = cropWidth;
	  this.cropHeight = cropHeight;
	  this.isFrist = true;
	  invalidate();
	 }

	 @SuppressLint("ClickableViewAccessibility")
	 @Override
	 public boolean onTouchEvent(MotionEvent event) {

	  if (event.getPointerCount() > 1) {
	   if (mStatus == STATUS_SINGLE) {
	    mStatus = STATUS_MULTI_START;
	   } else if (mStatus == STATUS_MULTI_START) {
	    mStatus = STATUS_MULTI_TOUCHING;
	   }
	  } else {
	   if (mStatus == STATUS_MULTI_START
	     || mStatus == STATUS_MULTI_TOUCHING) {
	    mX_1 = event.getX();
	    mY_1 = event.getY();
	   }

	   mStatus = STATUS_SINGLE;
	  }

	  switch (event.getAction()) {
	  case MotionEvent.ACTION_DOWN:
	   mX_1 = event.getX();
	   mY_1 = event.getY();
	   currentEdge = getTouch((int) mX_1, (int) mY_1);
	   isTouchInSquare = mDrawableFloat.contains((int) event.getX(),
	     (int) event.getY());

	   break;

	  case MotionEvent.ACTION_UP:
	   checkBounds();
	   break;

	  case MotionEvent.ACTION_POINTER_UP:
	   currentEdge = EDGE_NONE;
	   break;

	  case MotionEvent.ACTION_MOVE:
	   if (mStatus == STATUS_MULTI_TOUCHING) {

	   } else if (mStatus == STATUS_SINGLE) {
	    int dx = (int) (event.getX() - mX_1);
	    int dy = (int) (event.getY() - mY_1);

	    mX_1 = event.getX();
	    mY_1 = event.getY();
	    // \u6839\u64da\u5f97\u5230\u7684\u90a3\u4e00\u4e2a\u89d2\uff0c\u5e76\u4e14\u53d8\u6362Rect
	    if (!(dx == 0 && dy == 0)) {
	     switch (currentEdge) {
	     case EDGE_LT:
	      mDrawableFloat.set(mDrawableFloat.left + dx,
	        mDrawableFloat.top + dy, mDrawableFloat.right,
	        mDrawableFloat.bottom);
	      break;

	     case EDGE_RT:
	      mDrawableFloat.set(mDrawableFloat.left,
	        mDrawableFloat.top + dy, mDrawableFloat.right
	          + dx, mDrawableFloat.bottom);
	      break;

	     case EDGE_LB:
	      mDrawableFloat.set(mDrawableFloat.left + dx,
	        mDrawableFloat.top, mDrawableFloat.right,
	        mDrawableFloat.bottom + dy);
	      break;

	     case EDGE_RB:
	      mDrawableFloat.set(mDrawableFloat.left,
	        mDrawableFloat.top, mDrawableFloat.right + dx,
	        mDrawableFloat.bottom + dy);
	      break;

	     case EDGE_MOVE_IN:
	      if (isTouchInSquare) {
	       mDrawableFloat.offset((int) dx, (int) dy);
	      }
	      break;

	     case EDGE_MOVE_OUT:
	      break;
	     }
	     mDrawableFloat.sort();
	     invalidate();
	    }
	   }
	   break;
	  }

	  return true;
	 }

	 // \u6839\u636e\u521d\u89e6\u6478\u70b9\u5224\u65ad\u662f\u89e6\u6478\u7684Rect\u54ea\u4e00\u4e2a\u89d2
	 public int getTouch(int eventX, int eventY) {
	  if (mFloatDrawable.getBounds().left <= eventX
	    && eventX < (mFloatDrawable.getBounds().left + mFloatDrawable
	      .getBorderWidth())
	    && mFloatDrawable.getBounds().top <= eventY
	    && eventY < (mFloatDrawable.getBounds().top + mFloatDrawable
	      .getBorderHeight())) {
	   return EDGE_LT;
	  } else if ((mFloatDrawable.getBounds().right - mFloatDrawable
	    .getBorderWidth()) <= eventX
	    && eventX < mFloatDrawable.getBounds().right
	    && mFloatDrawable.getBounds().top <= eventY
	    && eventY < (mFloatDrawable.getBounds().top + mFloatDrawable
	      .getBorderHeight())) {
	   return EDGE_RT;
	  } else if (mFloatDrawable.getBounds().left <= eventX
	    && eventX < (mFloatDrawable.getBounds().left + mFloatDrawable
	      .getBorderWidth())
	    && (mFloatDrawable.getBounds().bottom - mFloatDrawable
	      .getBorderHeight()) <= eventY
	    && eventY < mFloatDrawable.getBounds().bottom) {
	   return EDGE_LB;
	  } else if ((mFloatDrawable.getBounds().right - mFloatDrawable
	    .getBorderWidth()) <= eventX
	    && eventX < mFloatDrawable.getBounds().right
	    && (mFloatDrawable.getBounds().bottom - mFloatDrawable
	      .getBorderHeight()) <= eventY
	    && eventY < mFloatDrawable.getBounds().bottom) {
	   return EDGE_RB;
	  } else if (mFloatDrawable.getBounds().contains(eventX, eventY)) {
	   return EDGE_MOVE_IN;
	  }
	  return EDGE_MOVE_OUT;
	 }

	 @Override
	 protected void onDraw(Canvas canvas) {

	  if (mDrawable == null) {
	   return;
	  }

	  if (mDrawable.getIntrinsicWidth() == 0
	    || mDrawable.getIntrinsicHeight() == 0) {
	   return;
	  }

	  configureBounds();
	  // \u5728\u753b\u5e03\u4e0a\u82b1\u56fe\u7247
	  mDrawable.draw(canvas);
	  canvas.save();
	  // \u5728\u753b\u5e03\u4e0a\u753b\u6d6e\u5c42FloatDrawable,Region.Op.DIFFERENCE\u662f\u8868\u793aRect\u4ea4\u96c6\u7684\u8865\u96c6
	  canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
	  // \u5728\u4ea4\u96c6\u7684\u8865\u96c6\u4e0a\u753b\u4e0a\u7070\u8272\u7528\u6765\u533a\u5206
	  canvas.drawColor(Color.parseColor("#a0000000"));
	  canvas.restore();
	  // \u753b\u6d6e\u5c42
	  mFloatDrawable.draw(canvas);
	 }

	 protected void configureBounds() {
	  // configureBounds\u5728onDraw\u65b9\u6cd5\u4e2d\u8c03\u7528
	  // isFirst\u7684\u76ee\u7684\u662f\u4e0b\u9762\u5bf9mDrawableSrc\u548cmDrawableFloat\u53ea\u521d\u59cb\u5316\u4e00\u6b21\uff0c
	  // \u4e4b\u540e\u7684\u53d8\u5316\u662f\u6839\u636etouch\u4e8b\u4ef6\u6765\u53d8\u5316\u7684\uff0c\u800c\u4e0d\u662f\u6bcf\u6b21\u6267\u884c\u91cd\u65b0\u5bf9mDrawableSrc\u548cmDrawableFloat\u8fdb\u884c\u8bbe\u7f6e
	  if (isFrist) {
	   oriRationWH = ((float) mDrawable.getIntrinsicWidth())
	     / ((float) mDrawable.getIntrinsicHeight());

	   final float scale = mContext.getResources().getDisplayMetrics().density;
	   int w = Math.min(getWidth(), (int) (mDrawable.getIntrinsicWidth()
	     * scale + 0.5f));
	   int h = (int) (w / oriRationWH);

	   int left = (getWidth() - w) / 2;
	   int top = (getHeight() - h) / 2;
	   int right = left + w;
	   int bottom = top + h;

	   mDrawableSrc.set(left, top, right, bottom);
	   mDrawableDst.set(mDrawableSrc);

	   int floatWidth = dipTopx(mContext, cropWidth);
	   int floatHeight = dipTopx(mContext, cropHeight);

	   if (floatWidth > getWidth()) {
	    floatWidth = getWidth();
	    floatHeight = cropHeight * floatWidth / cropWidth;
	   }

	   if (floatHeight > getHeight()) {
	    floatHeight = getHeight();
	    floatWidth = cropWidth * floatHeight / cropHeight;
	   }

	   int floatLeft = (getWidth() - floatWidth) / 2;
	   int floatTop = (getHeight() - floatHeight) / 2;
	   mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth,
	     floatTop + floatHeight);

	   isFrist = false;
	  }

	  mDrawable.setBounds(mDrawableDst);
	  mFloatDrawable.setBounds(mDrawableFloat);
	 }

	 // \u5728up\u4e8b\u4ef6\u4e2d\u8c03\u7528\u4e86\u8be5\u65b9\u6cd5\uff0c\u76ee\u7684\u662f\u68c0\u67e5\u662f\u5426\u628a\u6d6e\u5c42\u62d6\u51fa\u4e86\u5c4f\u5e55
	 protected void checkBounds() {
	  int newLeft = mDrawableFloat.left;
	  int newTop = mDrawableFloat.top;

	  boolean isChange = false;
	  if (mDrawableFloat.left < getLeft()) {
	   newLeft = getLeft();
	   isChange = true;
	  }

	  if (mDrawableFloat.top < getTop()) {
	   newTop = getTop();
	   isChange = true;
	  }

	  if (mDrawableFloat.right > getRight()) {
	   newLeft = getRight() - mDrawableFloat.width();
	   isChange = true;
	  }

	  if (mDrawableFloat.bottom > getBottom()) {
	   newTop = getBottom() - mDrawableFloat.height();
	   isChange = true;
	  }

	  mDrawableFloat.offsetTo(newLeft, newTop);
	  if (isChange) {
	   invalidate();
	  }
	 }

	 // \u8fdb\u884c\u56fe\u7247\u7684\u88c1\u526a\uff0c\u6240\u8c13\u7684\u88c1\u526a\u5c31\u662f\u6839\u636eDrawable\u7684\u65b0\u7684\u5750\u6807\u5728\u753b\u5e03\u4e0a\u521b\u5efa\u4e00\u5f20\u65b0\u7684\u56fe\u7247
	 public Bitmap getCropImage() {
	  Bitmap tmpBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
	    Config.RGB_565);
	  Canvas canvas = new Canvas(tmpBitmap);
	  mDrawable.draw(canvas);

	  Matrix matrix = new Matrix();
	  float scale = (float) (mDrawableSrc.width())
	    / (float) (mDrawableDst.width());
	  matrix.postScale(scale, scale);

	  Bitmap ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left,
	    mDrawableFloat.top, mDrawableFloat.width(),
	    mDrawableFloat.height(), matrix, true);
	  tmpBitmap.recycle();
	  tmpBitmap = null;

	  return ret;
	 }

	 public int dipTopx(Context context, float dpValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (dpValue * scale + 0.5f);
	 }
	}