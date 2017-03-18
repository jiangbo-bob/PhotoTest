package com.example.photostest;

import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class ZidooInterpolator {
	private Interpolator				mInterpolator				= null;
	private long						mStartTime					= 0;
	private long						mProgress					= 0;
	private long						mDuration					= 3 * 1000;
	private ZidooInterpolatorListener	mZidooInterpolatorListener	= null;
	private boolean						isRunning					= false;
	private int							mType						= 0;
	private boolean						isLoop						= false;
	private boolean						mFlag						= false;

	public ZidooInterpolator(Interpolator interpolator, long animationTime, ZidooInterpolatorListener mZidooInterpolatorListener, int mType) {
		this.mDuration = animationTime;
		this.mType = mType;
		this.mZidooInterpolatorListener = mZidooInterpolatorListener;
		if (interpolator == null) {
			// mInterpolator = new LinearInterpolator();
			mInterpolator = new DecelerateInterpolator(1.5f);
		} else {
			mInterpolator = interpolator;
		}
	}

	public ZidooInterpolator(Interpolator interpolator, long animationTime, ZidooInterpolatorListener mZidooInterpolatorListener, int mType, boolean isLoop) {
		this.mDuration = animationTime;
		this.mType = mType;
		this.isLoop = isLoop;
		this.mZidooInterpolatorListener = mZidooInterpolatorListener;
		if (interpolator == null) {
			// mInterpolator = new LinearInterpolator();
			mInterpolator = new DecelerateInterpolator(1.5f);
		} else {
			mInterpolator = interpolator;
		}
	}

	public void start() {
		isRunning = true;
		mProgress = 0;
		mStartTime = AnimationUtils.currentAnimationTimeMillis();
		if (mZidooInterpolatorListener != null) {
			mZidooInterpolatorListener.startAnimation(mType);
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void reset() {
		isRunning = false;
		mStartTime = -1;
		mProgress = 0;
	}

	public float getPercent() {
		if (!isRunning) {
			return 1;
		}
		if (!isLoop) {
			mProgress = AnimationUtils.currentAnimationTimeMillis() - mStartTime;
			boolean hasMoreFrames = mProgress <= mDuration && mProgress >= 0;
			if (!hasMoreFrames) {
				isRunning = false;
				if (mZidooInterpolatorListener != null) {
					mZidooInterpolatorListener.overAnimation(mType);
				}
				return 1;
			}
			mProgress = mProgress > mDuration ? mDuration : mProgress < 0 ? 0 : mProgress;
			float percent = mInterpolator.getInterpolation((float) mProgress / (float) mDuration);
			return percent;
		} else {

			mProgress = AnimationUtils.currentAnimationTimeMillis() - mStartTime;
			boolean hasMoreFrames = mProgress <= mDuration && mProgress >= 0;
			if (!hasMoreFrames) {
				mFlag = !mFlag;
				mStartTime = AnimationUtils.currentAnimationTimeMillis();
				mProgress = 0;
			}
			float percent = mInterpolator.getInterpolation((float) mProgress / (float) mDuration);
			return mFlag ? 1 - percent : percent;
		}
	}

	// AccelerateDecelerateInterpolator 在动画开始与结束的地方速率改变比较慢，在中间的时候加速
	// AccelerateInterpolator 在动画开始的地方速率改变比较慢，然后开始加速
	// AnticipateInterpolator 开始的时候向后然后向前甩
	// AnticipateOvershootInterpolator 开始的时候向后然后向前甩一定值后返回最后的值
	// BounceInterpolator 动画结束的时候弹起
	// CycleInterpolator 动画循环播放特定的次数，速率改变沿着正弦曲线
	// DecelerateInterpolator 在动画开始的地方快然后慢
	// LinearInterpolator 以常量速率改变
	// OvershootInterpolator 向前甩一定值后再回到原来位置
	// PathInterpolator
	// 这个是新增的我说原来怎么记得是9个，这个顾名思义就是可以定义路径坐标，然后可以按照路径坐标来跑动；注意其坐标并不是
	// XY，而是单方向，也就是我可以从0~1，然后弹回0.5 然后又弹到0.7 有到0.3，直到最后时间结束。（这个后面单独说说）
}
