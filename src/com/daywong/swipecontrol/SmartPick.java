package com.daywong.swipecontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.crittercism.app.Crittercism;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class SmartPick extends ArrayList<SmartPickObject> {
	ParseUser pu = ParseUser.getCurrentUser();
	ParseObject app;
	private static final long serialVersionUID = 1L;
	private static final String TAG = "SmartPick";

	public SmartPick() {
		super();
	}

	@Override
	public boolean add(SmartPickObject object) {
		int position = findObjectPos(object);
		if (position > -1) {
			Log.d(TAG,
					"old object , increase this value Hour is "
							+ get(position).getTime());
			get(position).increase();
		} else {
			Log.d(TAG, "new object ,Create new instance");

			return super.add(object);
		}
		return false;
	}

	public void sort() {
		Collections.sort(this, new Comparator<SmartPickObject>() {
			@Override
			public int compare(SmartPickObject lhs, SmartPickObject rhs) {
				return rhs.getCount() - lhs.getCount();
			}
		});
	}

	public ArrayList<SmartPickObject> getTopFive(int time) {
		this.sort();
		ArrayList<SmartPickObject> l = new ArrayList<SmartPickObject>();
		int found = 0;
		int count = 0;
		ArrayList<String> cache = new ArrayList<String>();
		while (found <= 4 && count < this.size() - 1) {
			int mT = this.get(count).getTime();
			if (mT == time || mT == time + 1 || mT == time - 1) {
				if (!cache.contains(this.get(count).getPkg())) {
					cache.add(this.get(count).getPkg());
					l.add(this.get(count));
					found++;
				}
				if (found >= 4)
					break;
			}
			count++;
		}
		return l;
	}

	/**
	 * Compare with given SartPickOnject to see whether it is exist or not ,
	 * return -1 if not exist. comparator Time and Package Name
	 */
	private int findObjectPos(SmartPickObject o) {
		int count = 0;
		for (SmartPickObject spo : this) {
			if (spo.getPkg().equalsIgnoreCase(o.getPkg())
					&& spo.getTime() == o.getTime()) {
				return count;
			}
			count++;
		}
		return -1;

	}

	public void setPreferenceJSON() {

	}

	public String getPreferenceJSON() {
		JSONArray arr = new JSONArray();

		for (SmartPickObject o : this) {
			try {
				JSONObject j = new JSONObject();
				j.put("cls", o.getCls());
				j.put("pkg", o.getPkg());
				j.put("time", o.getTime());
				j.put("count", o.getCount());
				arr.put(j);
			} catch (Exception e) {
				e.printStackTrace();
				Crittercism.logHandledException(e);
			}
		}
		return arr.toString();
	}
}
