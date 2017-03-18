package com.example.photostest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class TestView extends View {

	Paint			mPaint				= new Paint(Paint.FILTER_BITMAP_FLAG);
	boolean			isAnimation			= false;
	ZidooBitmap		mZidooBitmap		= null;
	ZidooBitmap		mZidooExitBitmap	= null;
	private int		mViewWidth;
	private int		mViewHeight;
	private Handler	mHandler			= null;

	public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public TestView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TestView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					initStart();
					break;
				default:
					break;
				}
			}
		};
	}

	@Override
	protected void onDraw(Canvas canvas) {
		onDrawIn(canvas);
		onDrawOut(canvas);
		if (isAnimation) {
			postInvalidateOnAnimation();
		}
	}

	void onDrawIn(Canvas canvas) {
		float input = mZidooBitmap.mZidooInterpolator.getPercent();
		Bitmap bitmap = mZidooBitmap.mBitmap;
		canvas.save();
		mZidooBitmap.mCurrentX = (mZidooBitmap.mStartX + (mZidooBitmap.mTargetX - mZidooBitmap.mStartX) * input);
		mZidooBitmap.mCurrentY = (mZidooBitmap.mStartY + (mZidooBitmap.mTargetY - mZidooBitmap.mStartY) * input);
		canvas.translate(mZidooBitmap.mCerterX + mZidooBitmap.mCurrentX, mZidooBitmap.mCerterY + mZidooBitmap.mCurrentY);
		canvas.drawBitmap(bitmap, mZidooBitmap.mInMatrix, mPaint);
		canvas.restore();
	}

	void onDrawOut(Canvas canvas) {
		float input = mZidooExitBitmap.mZidooInterpolator.getPercent();
		Bitmap bitmap = mZidooExitBitmap.mBitmap;
		canvas.save();
		mZidooExitBitmap.mCurrentX = (mZidooExitBitmap.mStartX + (mZidooExitBitmap.mTargetX - mZidooExitBitmap.mStartX) * input);
		mZidooExitBitmap.mCurrentY = (mZidooExitBitmap.mStartY + (mZidooExitBitmap.mTargetY - mZidooExitBitmap.mStartY) * input);
		canvas.translate(mZidooExitBitmap.mCerterX + mZidooExitBitmap.mCurrentX, mZidooExitBitmap.mCerterY + mZidooExitBitmap.mCurrentY);
		canvas.drawBitmap(bitmap, mZidooExitBitmap.mInMatrix, mPaint);
		canvas.restore();
	}

	private void initStart() {
		if (mZidooBitmap == null) {
			mZidooBitmap = new ZidooBitmap(R.drawable.z14);
			mZidooExitBitmap = new ZidooBitmap(R.drawable.z21);
		}
		ZidooBitmap zidooBitmap = mZidooBitmap;
		mZidooBitmap = mZidooExitBitmap;
		mZidooExitBitmap = zidooBitmap;
		mZidooBitmap.start(false);
		mZidooExitBitmap.start(true);
		isAnimation = true;
		postInvalidateOnAnimation();
	}

	class ZidooBitmap {
		float				mCurrentX			= 0;
		float				mCurrentY			= 0;
		float				mStartX				= 0;
		float				mTargetX			= 0;
		float				mTargetY			= 0;
		float				mStartY				= 0;
		float				mCerterX			= 0;
		float				mCerterY			= 0;
		Matrix				mInMatrix			= new Matrix();
		Bitmap				mBitmap				= null;
		ZidooInterpolator	mZidooInterpolator	= null;

		void start(boolean isExit) {
			if (isExit) {
				mStartX = mCurrentX;
				mStartY = mCurrentY;
				mTargetX = mViewWidth;
				mTargetY = 0;
			} else {
				mStartX = -mViewWidth;
				mStartY = 0;
				mTargetX = 0;
				mTargetY = 0;
			}
			mZidooInterpolator.start();
		}

		public ZidooBitmap(int id) {

			Bitmap reuseBitmap = BitmapFactory.decodeResource(getResources(), id);
			int bitmapW = reuseBitmap.getWidth();
			int bitmapH = reuseBitmap.getHeight();
			int vW = mViewWidth;
			int vH = mViewHeight;
			int newBitmapW = 0;
			int newBitmapH = 0;
			float bitmapRatio = (float) bitmapW / (float) bitmapH;
			float viewRatio = (float) vW / (float) vH;
			if (bitmapRatio > viewRatio) {
				newBitmapH = vH;
				newBitmapW = (int) (bitmapW * (float) vH / bitmapH);
			} else {
				newBitmapW = vW;
				newBitmapH = (int) (bitmapH * (float) vW / bitmapW);
			}
			mBitmap = scaleBitmap(reuseBitmap, newBitmapW, newBitmapH);
			if (bitmapRatio > viewRatio) {
				mCerterX = -(newBitmapW - vW);
				mCerterY = 0;
			} else {
				mCerterX = 0;
				mCerterY = -(newBitmapH - vH);
			}

			mZidooInterpolator = new ZidooInterpolator(null, 3000, new ZidooInterpolatorListener() {

				@Override
				public void startAnimation(int mtype) {

				}

				@Override
				public void overAnimation(int mtype) {
					isAnimation = false;
					mHandler.removeMessages(0);
					mHandler.sendEmptyMessageDelayed(0, 3000);
				}
			}, 0);

		}
	}

	private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
		if (origin == null) {
			return null;
		}
		int height = origin.getHeight();
		int width = origin.getWidth();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
		Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
		if (newBM.equals(origin)) {
			return newBM;
		}
		origin.recycle();
		return newBM;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mViewWidth = w;
		mViewHeight = h;
		initStart();
	}

}
