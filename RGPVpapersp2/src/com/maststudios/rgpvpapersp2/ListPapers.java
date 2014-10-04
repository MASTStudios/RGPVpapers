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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ListPapers extends Activity implements OnItemSelectedListener, OnItemClickListener {

	SharedPreferences sharedPreferences;
	String selectedSemester, selectedBranch;
	int selectedSubjectPosition, selectedSubjectId;
	SpinnerAdapter sa,sa1,sa2;
	DatabaseHelper databaseHelper;
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_papers);
		sharedPreferences = getSharedPreferences("RGPVPapers", MODE_PRIVATE);
		
		//initializing database
		db = databaseHelper.getReadableDatabase();
		databaseHelper = new DatabaseHelper(this);
		
		//initializing spinners
		sa = ArrayAdapter.createFromResource(this, R.array.branch, android.R.layout.simple_list_item_1);
		sa1 = ArrayAdapter.createFromResource(this, R.array.semester, android.R.layout.simple_list_item_1);
		sa2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		Spinner spin1 = (Spinner) findViewById(R.id.spinner1);
		Spinner spin2 = (Spinner) findViewById(R.id.spinner2);
		Spinner spin3 = (Spinner) findViewById(R.id.spinner3);
		spin1.setAdapter(sa1);
		spin2.setAdapter(sa);
		spin3.setAdapter(sa2);
		
		//adding on click listeners to spinners
		spin1.setOnItemSelectedListener(this);
		spin2.setOnItemSelectedListener(this);
		
		//initialing spinner values through shared preferences
		spin1.setSelection(sharedPreferences.getInt("semester", 0));
		spin2.setSelection(sharedPreferences.getInt("branch", 0));
		
		//adding on click listener to listview
		ListView listview = (ListView) findViewById(R.id.listView1);
		listview.setOnItemClickListener(this);
		
		//adding values to selectedBranch etc
		selectedSemester=(String) sa1.getItem(sharedPreferences.getInt("semester", 0));
		selectedBranch=(String) sa1.getItem(sharedPreferences.getInt("branch", 0));
		selectedSubjectId=sharedPreferences.getInt("subjectid", 0);
		selectedSubjectPosition=sharedPreferences.getInt("subjectPosition", 0);
		
		//updating the subjects list according to the selected branch and semester
		updateSubjects();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_papers, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// gets the list pf papers based on the branch semester and subject
	public List<Paper> getList(String branch, String semester, String subject) {
		List<Paper> l = new ArrayList<Paper>();
		Cursor cursor;
		String details;

		// creating query
		String query = "select * from papers where ''='' ";
		// query=query+"year='"+year+"'";
		if (semester.compareTo("First Year") != 0) {
			query = query + "and branch='" + branch + "'";
		} else {
			query = "select * from papers";
		}
		// TODO add subject here

		cursor = db.rawQuery("select * from papers", null);
		cursor.moveToFirst();
		do {
			details = cursor.getString(2) + "-" + cursor.getString(3) + "\nSource - " + cursor.getString(7);
			Paper p = new Paper(cursor.getInt(0), cursor.getString(6), details, cursor.getString(1), true);
			l.add(p);
		} while (cursor.moveToNext());
		return l;
	}
	
	//upadtes the subjects in the spinners based on the selected branch and semester
	public void updateSubjects(){
		int subjectCode;
		Cursor cursor, cursor1;
		List<String> list=new ArrayList<String>();
		
		cursor=db.rawQuery("select subjectCode from subject_branch where branch='"+selectedBranch+"' and semester='"+selectedSemester+"'",null);
		cursor.moveToFirst();
		do{
			cursor1=db.rawQuery("select * from subject where subjectCode = '"+cursor.getString(0)+"'", null);
			list.add(cursor1.getString(1));
		}while(cursor.moveToNext());
		sa2=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
	}
	
	// updates the list based on the values of the class variables
	// selectedBranch etc.
	public void updateList() {
		String branch, year, subject, subjectCode;
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			((LinearLayout) findViewById(R.id.searchmenu)).setVisibility(android.view.View.VISIBLE);
			return true;
		case R.id.action_share:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void cancelSearch(View view) {
		((LinearLayout) findViewById(R.id.searchmenu)).setVisibility(android.view.View.GONE);
	}

	public void Search(View view) {
		DatabaseHelper db = new DatabaseHelper(this);

	}
}
