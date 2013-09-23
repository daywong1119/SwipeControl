package com.daywong.swipecontrol;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RecentApplication extends ImageView {
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getOffsetX() {
		float rst = (extended == 0) ? x + iv.getWidth() : x + iv.getWidth()
				+ (extended * 2);
		return rst;
	}

	public float getOffsetY() {
		float rst = (extended == 0) ? y + iv.getHeight() : y + iv.getHeight()
				+ (extended * 2);
		return rst;
	}

	public int getCount() {
		return count;
	}

	public boolean hitTest(float rx, float ry) {
		if (rx > x && rx < getOffsetX()) {
			if (ry > y && ry < getOffsetY()) {
				return true;
			}
		}
		return false;
	}

	private RunningTaskInfo mAppInfo;
	private TranslateAnimation ani;
	private boolean selecting = false;
	private int extended = 0;
	private float x;
	private float y;
	private int count;
	private Drawable icon;
	private RelativeLayout.LayoutParams lp;
	private ImageView iv;
	private RelativeLayout rl;

	// private int height ;
	// private int width;

	public RecentApplication(Context context, RelativeLayout rlay, int c,
			Drawable b) {
		super(context);
		count = c;
		icon = b;

		iv = new ImageView(context);
		iv.setImageDrawable(icon);
		rl = rlay;
		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		x = 100 * c;
		y = 120;
		lp.leftMargin = (int) x;
		lp.topMargin = (int) y;

		rl.addView(iv, lp);
	}

	public RecentApplication(Context context, RelativeLayout rlay, int c,
			Drawable b, int rx, int ry) {
		super(context);
		count = c;
		icon = b;

		iv = new ImageView(context);
		iv.setImageDrawable(icon);
		rl = rlay;
		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		x = rx;
		y = ry;
		lp.leftMargin = (int) x;
		lp.topMargin = (int) y;

		rl.addView(iv, lp);
	}

	public void select() {
		if (selecting == false) {
			selecting = true;
			ani = new TranslateAnimation(0, 0, 0, -100);
			ani.setDuration(250);
			ani.setRepeatMode(TranslateAnimation.REVERSE);
			ani.setRepeatCount(1);
			ani.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (selecting)
						iv.startAnimation(ani);
				}
			});
			iv.startAnimation(ani);
		}
	}

	public void unselect() {
		selecting = false;
		// iv.clearAnimation();
	}

	public void extendHitArea(int area) {
		this.extended = area;
		this.x -= area;
		this.y -= area;
	}

	public void setAppInfo(RunningTaskInfo app) {
		mAppInfo = app;
	}

	public RunningTaskInfo getAppInfo() {
		return mAppInfo;
	}

}
