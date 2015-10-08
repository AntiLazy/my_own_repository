package com.android.ui;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MatrixImageView extends ImageView implements Rotatable{
    private final static String TAG = "MatrixImageView";
    /**
     * 鐭╅樀
     */
    private Matrix mMatrix = new Matrix();
    
    private float mImageWidth;
    
    private float mImageHeight;

    private GestureDetector mGestureDetector;
    
    private boolean isInit = false;
    
    private Bitmap bitmap;
    public MatrixImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "init imageview");
        MatrixTouchListener mListener = new MatrixTouchListener();
        setOnTouchListener(mListener);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener(mListener));
        setBackgroundColor(Color.BLACK);
        //缂╂斁绫诲瀷涓篎IT_CENTER,琛ㄧず鎶婂浘鐗囨寜姣斾緥鎵╁ぇ/缂╁皬澶iew鐨勫搴︼紝鍓т腑
    }
    public void setLocation(int x, int y) {
        this.setFrame(x, y - this.getHeight(), x + this.getWidth(), y);
    }
//    public void setSize(float width,float height){
//        
//    }
    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);
//        Log.d(TAG, "setImageBitmap");
//        //璁剧疆瀹屽浘鐗囧悗锛岃幏鍙栨敼鍥剧墖鐨勫潗鏍囩煩闃�
//        mMatrix.set(getImageMatrix());
//        float[] values = new float[9];
//        mMatrix.getValues(values);
//        mImageWidth = getWidth()/values[Matrix.MSCALE_X];
//        Log.d(TAG, "setBitmap:--"+getWidth()+"/"+values[Matrix.MSCALE_X]+"="+mImageWidth);
//        mImageHeight = (getHeight() - values[Matrix.MTRANS_Y]*2)/values[Matrix.MSCALE_Y];
//        LayoutParams params =  this.getLayoutParams();
//        params.width = (int)mImageWidth;
//        params.height = (int)mImageHeight;
//        
        //this.setLayoutParams(params);
    }
    public void init(){
        mMatrix.set(getImageMatrix());
        float[] values = new float[9];
        mMatrix.getValues(values);
        mImageWidth = getWidth()/values[Matrix.MSCALE_X];
        Log.d(TAG, "mImageWidth="+mImageWidth+"  getWidth()="+getWidth()+ " values[Matrix.MSCALE_X]"+values[Matrix.MSCALE_X]);
//        setDrawingCacheEnabled(true);
//        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
//        Log.d(TAG, "width from bitmap = "+bitmap.getWidth());
//        Log.d(TAG, "setBitmap:--"+getWidth()+"/"+values[Matrix.MSCALE_X]+"="+mImageWidth);
        mImageHeight = (getHeight() - values[Matrix.MTRANS_Y]*2)/values[Matrix.MSCALE_Y];
        isInit = true;
    }
    public void layoutInCenter(DisplayMetrics dm){
        Log.d(TAG, "layoutInCenter......");
        int left = (int) ((dm.widthPixels - mImageWidth)/2);
        int top = (int) ((dm.heightPixels - mImageHeight)/2);
        this.layout(left, top, (int)(left+mImageWidth),(int)(top+mImageHeight));
        Log.d(TAG, "layoutInCenter:left = "+left+" top = "+top);
        
    }
    
    public class MatrixTouchListener implements OnTouchListener {
        /**
         * 鎷栧姩鐓х墖妯″紡
         */
        private static final int MODE_DRAG = 1;
        /**
         * 鏀惧ぇ缂╁皬鐓х墖妯″紡
         */
        private static final int MODE_ZOOM = 2;
        /**
         * 涓嶆敮鎸丮atrix
         */
        private static final int MODE_UNABLE = 3;
        /**
         * 鏈�澶х缉鏀剧骇鍒�
         */
        private float mMaxScale = 6;
        /**
         * 鍙屽嚮鏃剁缉鏀剧骇鍒�
         */
        private float mDoubleClickScale = 2;
        
        private int mMode = 0;
        /**缂╂斁寮�濮嬫椂鎵嬫寚闂磋窛*/
        private float mStartDis;
        
        private Matrix mCurrentMatrix = new Matrix();
        
        private PointF startPoint = new PointF();
        
        

        @Override
        public boolean onTouch(View v,MotionEvent event) {
            Log.d(TAG, "onTouch is working.");
            if(!isInit) init();
            switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mMode = MODE_DRAG;
                startPoint.set(event.getX(), event.getY());
                isMatrixEnable();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                reSetMatrix();
                break;
            case MotionEvent.ACTION_MOVE:
               // setLocation((int) event.getX(), (int) event.getY());
                Log.d(TAG, "onTouch ACTION_MOVE.");
                if(mMode == MODE_ZOOM) {
                    setZoomMatrix(event);
                }else if(mMode == MODE_DRAG) {
                    setDragMatrix(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if(mMode == MODE_UNABLE) return true;
                mMode = MODE_ZOOM;
                mStartDis = distance(event);
                break;
            default:
                break;
            }
            return mGestureDetector.onTouchEvent(event);
        }
        private void setDragMatrix(MotionEvent event) {
            Log.d(TAG, "setDragMatrix......");
            if(isZoomChanged()) {
                Log.d(TAG, "setDragMatrix...working...");
                float dx = event.getX() - startPoint.x;
                float dy = event.getY() - startPoint.y;
                Log.d(TAG, "ori: dx = " + dx + " dy = " + dy);
                //閬垮厤涓庡弻鍑诲啿绐侊紝澶т簬10f鎵嶆槸鎷栧姩
                if(Math.sqrt(dx * dx + dy * dy) > 10f) {
                   startPoint.set(event.getX(), event.getY());
                   //鍦ㄥ綋鍓嶅熀纭�涓婄Щ鍔�
                   mCurrentMatrix.set(getImageMatrix());
                   float[] values = new float[9];
                   mCurrentMatrix.getValues(values);
                   dx = checkDxBound(values,dx);
                   dy = checkDyBound(values, dy);
                   Log.d(TAG, "result: dx = " + dx + " dy = " + dy);
                   mCurrentMatrix.postTranslate(dx, dy);
                   setImageMatrix(mCurrentMatrix);
                }
            }
        }

        private float checkDxBound(float[] values, float dx) {
            float width = getWidth();
            Log.d(TAG, "mImageWidth = "+mImageWidth+" values[Matrix.MSCALE_X]"+values[Matrix.MSCALE_X]);
            Log.d(TAG, "mImageWidth * values[Matrix.MSCALE_X] = "+mImageWidth * values[Matrix.MSCALE_X]);
            Log.d(TAG, "width = "+width);
            if(mImageWidth * values[Matrix.MSCALE_X] < width) return 0;
            if(values[Matrix.MTRANS_X] + dx > 0)
                dx = -values[Matrix.MTRANS_X];
            else if(values[Matrix.MTRANS_X]+dx < -(mImageWidth*values[Matrix.MSCALE_X] - width))
                dx = -(mImageWidth*values[Matrix.MSCALE_X] - width)-values[Matrix.MTRANS_X];
            return dx;
        }

        /**
         * 璁剧疆缂╂斁Matrix
         * @param event
         */
        private void setZoomMatrix(MotionEvent event) {
            Log.d(TAG, "setZoomMatrix.....");
            // 鍙湁鍚屾椂瑙﹀睆涓や釜鐐规墠鎵ц
            if(event.getPointerCount() < 2) return;
            float endDis = distance(event);//缁撴潫璺濈
            //涓や釜鎵嬫寚骞舵嫝鍍忕礌澶т簬10
            if(endDis > 10f) {
                float scale = endDis/mStartDis;//鑾峰彇缂╂斁鍊嶆暟
                mStartDis = endDis; //閲嶇疆璺濈
                mCurrentMatrix.set(getImageMatrix());//鍒濆鍖朚atrix
                float[] values = new float[9];
                mCurrentMatrix.getValues(values);
                
                scale = checkMaxScale(scale,values);
                setImageMatrix(mCurrentMatrix);
                Log.d(TAG, "setZoomMatrix......working");
            }
        }
        /**
         * 妫�鏌cale锛屼娇鍥惧儚缂╂斁鍚庝笉浼氳秴鍑烘渶澶у�嶆暟
         * @param scale
         * @param values
         * @return
         */
        private float checkMaxScale(float scale, float[] values) {
            if(scale*values[Matrix.MSCALE_X] > mMaxScale)
                scale = mMaxScale / values[Matrix.MSCALE_X];
            mCurrentMatrix.postScale(scale, scale, getWidth()>>1, getHeight()>>1);
            return scale;
        }

        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            return (float)Math.sqrt(dx * dx + dy * dy);
        }

        private void isMatrixEnable() {
            //鍔犺浇鍑洪敊鏃朵笉鍙缉鏀�
            if(getScaleType()!= ScaleType.CENTER) {
                setScaleType(ScaleType.MATRIX);
                Log.d(TAG, "isMatrixEnable....change");
            } else {
                mMode = MODE_UNABLE;//璁剧疆涓轰笉鏀寔鎵嬪娍
                Log.d(TAG, "isMatrixEnable....unable");
            }
        }
        /**
         * 鍒ゆ柇鏄惁闇�瑕侀噸缃�
         * @return 褰撶缉鏀剧骇鍒皬浜庢ā鏉跨缉鏀剧骇鍒椂锛岄噸缃�
         */
        private boolean checkRest() {
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            //鑾峰彇褰撳墠x杞寸殑缂╂斁绾у埆
            float scale = values[Matrix.MSCALE_X];
            //鑾峰緱妯℃澘鐨剎杞寸缉鏀剧骇鍒紝涓よ�呭潗姣旇緝
            mMatrix.getValues(values);
            return scale<values[Matrix.MSCALE_X];
        }
        /**
         * 閲嶇疆Matrix
         */
        private void reSetMatrix(){
            if(checkRest()) {
                mCurrentMatrix.set(mMatrix);
                setImageMatrix(mCurrentMatrix);
            }
        }
        
        private boolean isZoomChanged(){
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            //鑾峰彇褰撳墠x杞寸缉鏀剧骇鍒�
            float scale = values[Matrix.MSCALE_X];
            mMatrix.getValues(values);
            return scale!=values[Matrix.MSCALE_X];
        }
        
        private float checkDyBound(float[] values,float dy) {
            float height = getHeight();
            if(mImageHeight*values[Matrix.MSCALE_Y]<height) return 0;
            if(values[Matrix.MTRANS_Y]+dy > 0) {
                dy = values[Matrix.MTRANS_Y];
            } else if(values[Matrix.MTRANS_Y] + dy < -(mImageHeight*values[Matrix.MSCALE_Y]-height)){
                dy=-(mImageHeight*values[Matrix.MSCALE_Y]-height)-values[Matrix.MTRANS_Y];
            }
            return dy;
        }
        
        public void onDoubleClick() {
            Log.d(TAG, "onDoubleClick.....");
            float scale = isZoomChanged()?1:mDoubleClickScale;
            mCurrentMatrix.set(mMatrix);
            mCurrentMatrix.postScale(scale, scale, getWidth()>>1, getHeight()>>2);
//            setSize(getWidth()*scale, getHeight()*scale);
            setImageMatrix(mCurrentMatrix);
        }
    }
    private class  GestureListener extends SimpleOnGestureListener{
        private final MatrixTouchListener listener;
        public GestureListener(MatrixTouchListener listener) {
            this.listener=listener;
        }
        @Override
        public boolean onDown(MotionEvent e) {
            //鎹曡幏Down浜嬩欢
            return true;
        }
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //瑙﹀彂鍙屽嚮浜嬩欢
            listener.onDoubleClick();
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            // TODO Auto-generated method stub

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub
            super.onShowPress(e);
        }

        

        

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // TODO Auto-generated method stub
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // TODO Auto-generated method stub
            return super.onSingleTapConfirmed(e);
        }

    }
    private static final int ANIMATION_SPEED = 270; // 270 deg/sec

    private int mCurrentDegree = 0; // [0, 359]
    private int mStartDegree = 0;
    private int mTargetDegree = 0;

    private boolean mClockwise = false, mEnableAnimation = true;

    private long mAnimationStartTime = 0;
    private long mAnimationEndTime = 0;
    
	@Override
	public void setRotation(int degree, boolean animation) {
		 mEnableAnimation = animation;
	        // make sure in the range of [0, 359]
	        degree = degree >= 0 ? degree % 360 : degree % 360 + 360;
	        if (degree == mTargetDegree) return;

	        mTargetDegree = degree;
	        if (mEnableAnimation) {
	            mStartDegree = mCurrentDegree;
	            mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();

	            int diff = mTargetDegree - mCurrentDegree;
	            diff = diff >= 0 ? diff : 360 + diff; // make it in range [0, 359]

	            // Make it in range [-179, 180]. That's the shorted distance between the
	            // two angles
	            diff = diff > 180 ? diff - 360 : diff;

	            mClockwise = diff >= 0;
	            mAnimationEndTime = mAnimationStartTime
	                    + Math.abs(diff) * 1000 / ANIMATION_SPEED;
	        } else {
	            mCurrentDegree = mTargetDegree;
	        }

	        invalidate();
	}
	
	 @Override
	    protected void onDraw(Canvas canvas) {
	        Drawable drawable = getDrawable();
	        if (drawable == null) return;

	        Rect bounds = drawable.getBounds();
	        int w = bounds.right - bounds.left;
	        int h = bounds.bottom - bounds.top;

	        if (w == 0 || h == 0) return; // nothing to draw

	        if (mCurrentDegree != mTargetDegree) {
	            long time = AnimationUtils.currentAnimationTimeMillis();
	            if (time < mAnimationEndTime) {
	                int deltaTime = (int)(time - mAnimationStartTime);
	                int degree = mStartDegree + ANIMATION_SPEED
	                        * (mClockwise ? deltaTime : -deltaTime) / 1000;
	                degree = degree >= 0 ? degree % 360 : degree % 360 + 360;
	                mCurrentDegree = degree;
	                invalidate();
	            } else {
	                mCurrentDegree = mTargetDegree;
	            }
	        }

	        int left = getPaddingLeft();
	        int top = getPaddingTop();
	        int right = getPaddingRight();
	        int bottom = getPaddingBottom();
	        int width = getWidth() - left - right;
	        int height = getHeight() - top - bottom;

	        int saveCount = canvas.getSaveCount();
	        
	        // Scale down the image first if required.
	        if ((getScaleType() == ImageView.ScaleType.FIT_CENTER) &&
	                ((width < w) || (height < h))) {
	            float ratio = Math.min((float) width / w, (float) height / h);
	            canvas.scale(ratio, ratio, width / 2.0f, height / 2.0f);
	        }
	        canvas.translate(left + width / 2, top + height / 2);
	        canvas.rotate(-mCurrentDegree);
	        canvas.translate(-w / 2, -h / 2);
	        drawable.draw(canvas);
	        canvas.restoreToCount(saveCount);
	    }
	 
	 
	 
}