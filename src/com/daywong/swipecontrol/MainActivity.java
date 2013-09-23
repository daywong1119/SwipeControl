package com.daywong.swipecontrol;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.crittercism.app.Crittercism;
import com.example.swipecontrol.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Crittercism.initialize(getApplicationContext(),
				"52406bcc46b7c24626000002");
		StandOutWindow.closeAll(this, RecentWindow.class);

		StandOutWindow.show(this, TriggerWindow.class,
				StandOutWindow.DEFAULT_ID);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
