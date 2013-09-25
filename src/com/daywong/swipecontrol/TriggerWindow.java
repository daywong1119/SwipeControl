package com.daywong.swipecontrol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.example.swipecontrol.R;

public class TriggerWindow extends StandOutWindow {
	protected static final String TAG = "TriggerWindow";
	protected static final int DATA_TOUCH_EVENT = 0;
	private static final String SP_SMART_PICK_LIST = "SmartPickList";
	private StandOutLayoutParams mParams;
	private Context mContext = this;
	private ArrayList<RecentPick> listLauncher = new ArrayList<RecentPick>();
	private SmartPick listSmartPick = new SmartPick();
	private FrameLayout mFrame;
	private int lastTouch = -1;
	private Time t = new Time();

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
		restorePreference();

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.trigger_window, frame, true);
		mFrame = frame;

		frame.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "ACTION_DOWN");
					showLayoutElement(frame);

					// Get Top 5 Running Application
					ActivityManager m = (ActivityManager) getApplicationContext()
							.getSystemService(ACTIVITY_SERVICE);
					List<ActivityManager.RunningTaskInfo> apps = (List<RunningTaskInfo>) m
							.getRunningTasks(5);

					int count = 1;
					for (final RunningTaskInfo app : apps) {
						RecentPick im = new RecentPick(mContext, 1,
								(RelativeLayout) frame
										.findViewById(R.id.trigger_area),
								count, getPackageIcon(app.topActivity
										.getPackageName()));

						im.setPkg(app.topActivity.getPackageName());
						im.setCls(app.topActivity.getClassName());
						Log.d(TAG,
								"Package : " + app.topActivity.getPackageName());
						listLauncher.add(im);
						count++;
					}

					// Get top 5 Smart Pick
					t.setToNow();
					count = 1;
					ArrayList<SmartPickObject> listSmartObject = listSmartPick
							.getTopFive(t.hour);
					for (SmartPickObject o : listSmartObject) {
						RecentPick im = new RecentPick(mContext, 2,
								(RelativeLayout) frame
										.findViewById(R.id.trigger_area),
								count, getPackageIcon(o.getPkg()));
						im.setPkg(o.getPkg());
						im.setCls(o.getCls());
						listLauncher.add(im);
						count++;
					}

					// tell the first recent running application to Smart pick
					RunningTaskInfo a = apps.get(0);
					listSmartPick.add(new SmartPickObject(a.baseActivity
							.getPackageName(), a.baseActivity.getClassName(),
							t.hour, 1));

					return true;

				case MotionEvent.ACTION_UP:
					// Open application
					if (lastTouch > -1) {
						try {
							Intent intent = getPackageManager()
									.getLaunchIntentForPackage(
											listLauncher.get(lastTouch)
													.getPkg());
							if (intent == null) {
								intent = new Intent();
								intent.setComponent(new ComponentName(
										listLauncher.get(lastTouch).getPkg(),
										listLauncher.get(lastTouch).getCls()));
							}

							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);

							listSmartPick.add(new SmartPickObject(listLauncher
									.get(lastTouch).getPkg(), listLauncher.get(
									lastTouch).getCls(), t.hour, 1));
							savePreference();

						} catch (Exception e) {
							e.printStackTrace();
							Crittercism.logHandledException(e);
							Toast.makeText(mContext,
									"Application is currently unavailable",
									Toast.LENGTH_LONG).show();
						}
					}
					// Exit and clear apps
					for (RecentPick app : listLauncher) {
						try {
							app.unselect();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					hideLayoutElement(frame);
					listLauncher.removeAll(listLauncher);
					lastTouch = -1;
					return false;

				case MotionEvent.ACTION_MOVE:
					// Log.d(TAG,
					// "ACTION_MOVE x is" + event.getX() + " y ix "
					// + event.getY() + " hittest "
					// + hitTest(event.getX(), event.getY()));

					int s = hitTest(event.getX(), event.getY());
					if (s < listLauncher.size()) {
						if (lastTouch != -1 && lastTouch != s) {
							listLauncher.get(lastTouch).unselect();
						}
						if (s >= 0)
							listLauncher.get(s).select();

						lastTouch = s;
					}
					return true;
				}
				return true;
			}

			private void showLayoutElement(FrameLayout frame) {
				WindowManager wm = (WindowManager) mContext
						.getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();

				mParams = new StandOutLayoutParams(id, display.getWidth(), 500,
						StandOutLayoutParams.LEFT, StandOutLayoutParams.CENTER);
				mParams.setFocusFlag(true);
				TriggerWindow.this.updateViewLayout(DEFAULT_ID, mParams);

				RelativeLayout dummy = (RelativeLayout) frame
						.findViewById(R.id.dummy);
				dummy.setVisibility(RelativeLayout.VISIBLE);

				TranslateAnimation ani = new TranslateAnimation(0, 0, +100, 0);
				ani.setDuration(150);
				frame.findViewById(R.id.trigger_area).startAnimation(ani);

			}

			private void hideLayoutElement(FrameLayout frame) {
				TriggerWindow.this.updateViewLayout(DEFAULT_ID,
						new StandOutLayoutParams(id, 20, 200,
								StandOutLayoutParams.LEFT,
								StandOutLayoutParams.CENTER));

				RelativeLayout dummy = (RelativeLayout) frame
						.findViewById(R.id.dummy);
				dummy.setVisibility(RelativeLayout.GONE);

				RelativeLayout rl = (RelativeLayout) mFrame
						.findViewById(R.id.trigger_area);
				rl.removeAllViews();
			}
		});
	}

	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		Log.d(TAG, "mParam initialized");
		return new StandOutLayoutParams(id, 20, 200, StandOutLayoutParams.LEFT,
				StandOutLayoutParams.CENTER);
	}

	@Override
	public int getFlags(int id) {
		return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE;
	}

	private Drawable getPackageIcon(String packageName) {
		try {

			ApplicationInfo app = this.getPackageManager().getApplicationInfo(
					packageName, 0);

			Drawable icon = getPackageManager().getApplicationIcon(app);
			// Log.d(TAG, "Icon Size" + icon.getMinimumHeight());
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
		for (RecentPick icon : listLauncher) {
			if (icon.hitTest(x, y)) {
				return count;
			}
			count++;
		}
		return -1;
	}

	public void restorePreference() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String smartPickJSONString = preferences.getString(SP_SMART_PICK_LIST,
				"");
		if (!smartPickJSONString.equalsIgnoreCase("")) {
			Log.d(TAG, "JSON " + smartPickJSONString);
			listSmartPick.removeAll(listSmartPick);
			try {
				JSONArray arr = new JSONArray(smartPickJSONString);

				for (int x = 0; x < arr.length(); x++) {
					JSONObject o = arr.getJSONObject(x);
					String pkg = o.getString("pkg");
					String cls = o.getString("cls");
					int t = o.getInt("time");
					int c = o.getInt("count");
					listSmartPick.add(new SmartPickObject(pkg, cls, t, c));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Crittercism.logHandledException(e);
			}
		} else {
			Log.d(TAG, "can't get JSONString from Shared preference");
		}
		Log.d(TAG, "Restored");
	}

	public void savePreference() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(SP_SMART_PICK_LIST, listSmartPick.getPreferenceJSON());
		editor.commit();
		Log.d(TAG, "Saved to SP");
	}
}
