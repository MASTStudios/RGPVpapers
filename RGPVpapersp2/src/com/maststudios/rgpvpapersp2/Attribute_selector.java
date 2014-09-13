package com.maststudios.rgpvpapersp2;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Attribute_selector extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attribute_selector);
		DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
		dbh.getPapersBySql("select * from papers");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.attribute_selector, menu);
		return true;
		
	}

}
