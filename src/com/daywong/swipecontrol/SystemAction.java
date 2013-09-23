package com.daywong.swipecontrol;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SystemAction extends ImageView {
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getOffsetX() {
		return x + iv.getWidth();
	}

	public float getOffsetY() {
		return y + iv.getHeight();
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

	private float x;
	private float y;
	private int count;
	private Drawable icon;
	private RelativeLayout.LayoutParams lp;
	private ImageView iv;
	private RelativeLayout rl;

	// private int height ;
	// private int width;

	public SystemAction(Context context, RelativeLayout rlay, int c, Drawable b) {
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
		y = 500;
		lp.leftMargin = (int) x;
		lp.topMargin = (int) y;

		rl.addView(iv, lp);
	}

	public void select() {
		lp.topMargin = (int) y - 50;
		iv.setLayoutParams(lp);
	}

	public void unselect() {
		lp.topMargin = (int) y + 50;
		iv.setLayoutParams(lp);
	}

}
