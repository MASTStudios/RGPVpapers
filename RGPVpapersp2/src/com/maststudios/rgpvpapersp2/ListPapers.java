package com.maststudios.rgpvpapersp2;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ListPapers extends Activity implements OnItemSelectedListener, OnItemClickListener {

	SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_papers);
		sharedPreferences = getSharedPreferences("RGPVPapers", MODE_PRIVATE);

		// adding the spinner adapter for action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		// actionBar.setDisplayShowHomeEnabled(false);

		SpinnerAdapter sa = ArrayAdapter.createFromResource(this, R.array.branch, android.R.layout.simple_list_item_1);
		SpinnerAdapter sa1 = ArrayAdapter.createFromResource(this, R.array.year, android.R.layout.simple_list_item_1);
		// ((ArrayAdapter)
		// sa).setDropDownViewResource(R.layout.spinner_drop_down);
		// ((ArrayAdapter)
		// sa1).setDropDownViewResource(R.layout.spinner_drop_down);
		// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setCustomView(R.layout.action_bar);
		Spinner spin1 = (Spinner) findViewById(R.id.spinner1);
		Spinner spin2 = (Spinner) findViewById(R.id.spinner2);
		ListView listview = (ListView) findViewById(R.id.listView1);
		spin1.setAdapter(sa1);
		spin2.setAdapter(sa);
		spin1.setOnItemSelectedListener(this);
		spin2.setOnItemSelectedListener(this);
		listview.setOnItemClickListener(this);
		spin1.setSelection(sharedPreferences.getInt("year", 0));
		spin2.setSelection(sharedPreferences.getInt("branch", 0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_papers, menu);
		return true;
	}

	public List<Paper> getList(String branch, String year, String subject) {
		List<Paper> l = new ArrayList<Paper>();
		Cursor cursor;
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String details;

		// creating query
		String query = "select * from papers where ''='' ";
		// query=query+"year='"+year+"'";
		if (year.compareTo("First Year") != 0) {
			query = query + "and branch='" + branch + "'";
		}else{
			query = "select * from papers";
		}
		// TODO add subject here

		cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		do {
			details = cursor.getString(2) + "-" + cursor.getString(3) + "\nSource - " + cursor.getString(7);
			Paper p = new Paper(cursor.getInt(0), cursor.getString(6), details, cursor.getString(1), true);
			l.add(p);
		} while (cursor.moveToNext());
		return l;
	}

	public void updateList() {
		String branch, year, subject;
		ListView listview = (ListView) findViewById(R.id.listView1);
		Spinner spin1 = (Spinner) findViewById(R.id.spinner1);
		Spinner spin2 = (Spinner) findViewById(R.id.spinner2);

		year = (spin1.getSelectedItem().toString());
		branch = (spin2.getSelectedItem().toString());

		PaperAdapter adapter = new PaperAdapter(this, getList(branch, year, "EC"));
		listview.setAdapter(adapter);
	}

	// handeling selection in the action bar dropdown
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("year", ((Spinner) findViewById(R.id.spinner1)).getSelectedItemPosition());
		editor.putInt("branch", ((Spinner) findViewById(R.id.spinner1)).getSelectedItemPosition());
		editor.commit();
		updateList();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, Download.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}

}
