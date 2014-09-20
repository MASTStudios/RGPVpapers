package com.maststudios.rgpvpapersp2;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PaperSelect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paper_select);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.paper_select, menu);
		return true;
	}

}
