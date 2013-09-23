package com.daywong.swipecontrol;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.example.swipecontrol.R;

public class TriggerWindow extends StandOutWindow {

	protected static final String TAG = "TriggerWindow";
	protected static final int DATA_TOUCH_EVENT = 0;
	private StandOutLayoutParams mParams;
	private Context mContext = this;
	private ArrayList<RecentApplication> listIcon = new ArrayList<RecentApplication>();
	private FrameLayout mFrame;
	private int lastTouch = -1;

	@Override
	public String getAppName() {
		return "TriggerWindow";
	}

	@Override
	public int getAppIcon() {
		return 0;
	}

	@Override
	public void createAndAttachView(final int id, final FrameLayout frame) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.trigger_window, frame, true);
		mFrame = frame;

		frame.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "ACTION_DOWN");
					WindowManager wm = (WindowManager) mContext
							.getSystemService(Context.WINDOW_SERVICE);
					Display display = wm.getDefaultDisplay();
					mParams.width = display.getWidth();
					
					TriggerWindow.this.updateViewLayout(DEFAULT_ID, mParams);
					
					ActivityManager m = (ActivityManager) getApplicationContext()
							.getSystemService(ACTIVITY_SERVICE);
					List<ActivityManager.RunningTaskInfo> apps = (List<RunningTaskInfo>) m
							.getRunningTasks(5);

					int count = 1;
					for (final RunningTaskInfo app : apps) {

						RecentApplication im = new RecentApplication(mContext,
								(RelativeLayout) frame
										.findViewById(R.id.trigger_area),
								count, getPackageIcon(app.baseActivity
										.getPackageName()));
						im.setAppInfo(app);
						listIcon.add(im);
						count++;
					}

					// add System Icon
					// RecentApplication im = new RecentApplication(mContext,
					// (RelativeLayout) frame
					// .findViewById(R.id.trigger_area), count,
					// getResources().getDrawable(
					// R.drawable.navigation_back), 50, 50);
					// im.extendHitArea(50);
					// im.setAppInfo(listIcon.get(1).getAppInfo());
					// listIcon.add(im);
					// im = new RecentApplication(mContext, (RelativeLayout)
					// frame
					// .findViewById(R.id.trigger_area), count,
					// getResources().getDrawable(
					// R.drawable.navigation_forward), 350, 50);
					// im.extendHitArea(50);
					// listIcon.add(im);
					return true;

				case MotionEvent.ACTION_UP:
					// Open application
					if (lastTouch > -1) {
						try {
							Intent intent = new Intent();
							intent.setComponent(listIcon.get(lastTouch)
									.getAppInfo().topActivity);
							intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
							Crittercism.logHandledException(e);
						}
					}
					// Exit and clear apps
					TriggerWindow.this.updateViewLayout(DEFAULT_ID,
							new StandOutLayoutParams(id, 20, 200,
									StandOutLayoutParams.LEFT,
									StandOutLayoutParams.CENTER));

					for (RecentApplication app : listIcon) {
						try {
							app.unselect();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					RelativeLayout rl = (RelativeLayout) mFrame
							.findViewById(R.id.trigger_area);
					rl.removeAllViews();
					listIcon.removeAll(listIcon);
					lastTouch = -1;
					return false;

				case MotionEvent.ACTION_MOVE:
					Log.d(TAG,
							"ACTION_MOVE x is" + event.getX() + " y ix "
									+ event.getY() + " hittest "
									+ hitTest(event.getX(), event.getY()));

					int s = hitTest(event.getX(), event.getY());
					if (s < listIcon.size()) {
						if (lastTouch != -1 && lastTouch != s) {
							listIcon.get(lastTouch).unselect();
						}
						if (s >= 0)
							listIcon.get(s).select();

						lastTouch = s;
					}
					return true;
				}
				return true;
			}
		});
	}

	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		Log.d(TAG, "mParam initialized");
		return mParams = new StandOutLayoutParams(id, 20, 200,
				StandOutLayoutParams.LEFT, StandOutLayoutParams.CENTER);
	}

	@Override
	public int getFlags(int id) {
		return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE;
	}

	private Drawable getPackageIcon(String getPackageManager) {
		try {

			ApplicationInfo app = this.getPackageManager().getApplicationInfo(
					getPackageManager, 0);

			Drawable icon = getPackageManager().getApplicationIcon(app);
			Log.d(TAG, "Icon Size" + icon.getMinimumHeight());
			if (icon.getMinimumHeight() > 100) {
				Bitmap b = ((BitmapDrawable) icon).getBitmap();
				Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 200, 200,
						false);
				icon = new BitmapDrawable(bitmapResized);
				Log.d(TAG, "Icon resized");
			}
			return icon;
		} catch (NameNotFoundException e) {
			Toast toast = Toast.makeText(this, "error in getting icon",
					Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
			return null;
		}
	}

	private int hitTest(float x, float y) {
		int count = 0;
		for (RecentApplication icon : listIcon) {
			if (icon.hitTest(x, y)) {
				return count;
			}
			count++;
		}
		return -1;
	}
}
