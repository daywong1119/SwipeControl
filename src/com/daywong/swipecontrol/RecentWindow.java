package com.daywong.swipecontrol;

import java.util.List;
import java.util.Locale;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.ui.Window;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.swipecontrol.R;

public class RecentWindow extends StandOutWindow {
	private static final String TAG = "RecentWindow";
	private Context mContext = this;

	@Override
	public String getAppName() {
		return "Recent";
	}

	@Override
	public int getAppIcon() {
		return 0;
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.recent_window, frame, true);

		ActivityManager m = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> apps = (List<RunningTaskInfo>) m
				.getRunningTasks(10);

		for (final RunningTaskInfo app : apps) {
			ImageView iv = new ImageView(this);
			iv.setImageDrawable(getPackageIcon(app.baseActivity
					.getPackageName()));

			LinearLayout rl = (LinearLayout) frame.findViewById(R.id.layout1);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			rl.addView(iv, lp);

			// tv.setOnClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// Intent intent = new Intent();
			// intent.setComponent(app.topActivity);
			// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// mContext.startActivity(intent);
			// }
			// });
		}
	}

	@Override
	public StandOutLayoutParams getParams(int id,
			wei.mark.standout.ui.Window window) {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		return new StandOutLayoutParams(id, display.getWidth(),
				display.getHeight(), StandOutLayoutParams.CENTER,
				StandOutLayoutParams.CENTER);
	}

	@Override
	public void onReceiveData(int id, int requestCode, Bundle data,
			Class<? extends StandOutWindow> fromCls, int fromId) {
		Log.d(TAG, "onReceivdDate runs");
		switch (requestCode) {
		case TriggerWindow.DATA_TOUCH_EVENT:
			Window window = getWindow(id);
			if (window == null) {
				String errorText = String.format(Locale.US,
						"%s received data but Window id: %d is not open.",
						getAppName(), id);
				Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
				return;
			}
			Float fx = data.getFloat("move_x");
			Float fy = data.getFloat("move_y");
			Log.d(TAG, "got date from Trigger x is " + fx.toString() + " y is "
					+ fy.toString());
			break;
		default:
			Log.d("MultiWindow", "Unexpected data received.");
			break;
		}
	}

	private Drawable getPackageIcon(String getPackageManager) {
		try {

			ApplicationInfo app = this.getPackageManager().getApplicationInfo(
					getPackageManager, 0);

			Drawable icon = getPackageManager().getApplicationIcon(app);
			return icon;
		} catch (NameNotFoundException e) {
			Toast toast = Toast.makeText(this, "error in getting icon",
					Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
			return null;
		}
	}
}
