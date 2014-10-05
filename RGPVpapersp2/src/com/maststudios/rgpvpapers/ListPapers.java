package com.maststudios.rgpvpapers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ListPapers extends Activity implements OnItemSelectedListener, OnItemClickListener {

	SharedPreferences sharedPreferences;
	String selectedSemester, selectedBranch, selectedSubjectname;
	int selectedSubjectPosition, selectedSubjectId;
	SpinnerAdapter sa, sa1, sa2;
	Spinner spin1, spin2, spin3;
	private ShareActionProvider mShareActionProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_papers);
		sharedPreferences = getSharedPreferences("RGPVPapers", MODE_PRIVATE);

		// initializing database

		// initializing spinners
		sa = ArrayAdapter.createFromResource(this, R.array.branch, android.R.layout.simple_list_item_1);
		sa1 = ArrayAdapter.createFromResource(this, R.array.semester, android.R.layout.simple_list_item_1);
		sa2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		spin1 = (Spinner) findViewById(R.id.spinner1);
		spin2 = (Spinner) findViewById(R.id.spinner2);
		spin3 = (Spinner) findViewById(R.id.spinner3);
		spin1.setAdapter(sa1);
		spin2.setAdapter(sa);

		// initialing spinner values through shared preferences
		spin1.setSelection(sharedPreferences.getInt("semester", 0));
		spin2.setSelection(sharedPreferences.getInt("branch", 0));

		// adding on click listener to listview
		ListView listview = (ListView) findViewById(R.id.listView1);
		listview.setOnItemClickListener(this);

		// adding values to selectedBranch etc
		selectedSemester = (String) sa1.getItem(sharedPreferences.getInt("semester", 0));
		selectedBranch = (String) sa.getItem(sharedPreferences.getInt("branch", 0));
		selectedSubjectId = sharedPreferences.getInt("subjectid", 0);
		selectedSubjectPosition = sharedPreferences.getInt("subjectPosition", 0);

		// updating the subjects list according to the selected branch and
		// semester
		updateSubjects();
		spin3.setSelection(sharedPreferences.getInt("subjectPosition", 0));
		System.out.println(sharedPreferences.getInt("subjectPosition", 0) + "asdasdasdasdasdasddddddddddddddddddddddddddd");

		// adding on click listeners to spinners
		spin1.setOnItemSelectedListener(this);
		spin2.setOnItemSelectedListener(this);
		spin3.setOnItemSelectedListener(this);

	}

	@Override
	protected void onResume() {
		//AdView adView = (AdView) this.findViewById(R.id.adView);
		//AdRequest adRequest = new AdRequest.Builder().build();
		// Load ads into Banner Ads
		//adView.loadAd(adRequest);
		
		super.onResume();
		updateList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_papers, menu);
		MenuItem item = menu.findItem(R.id.action_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/*");
		shareIntent.putExtra(Intent.EXTRA_TEXT, "Abe sun! ye app se RGPV ke purane papers mil rahe hai... download kar le https://play.google.com/store/apps/details?id=com.maststudios.rgpvpapers");

		mShareActionProvider.setShareIntent(shareIntent);
		return super.onCreateOptionsMenu(menu);
	}

	// gets the list of papers based on the branch semester and subject
	public List<Paper> getList(String branch, String semester, String subject) {
		List<Paper> l = new ArrayList<Paper>();
		Cursor cursor;
		String details;

		DatabaseHelper databaseHelper;
		SQLiteDatabase db;
		databaseHelper = new DatabaseHelper(this);
		db = databaseHelper.getReadableDatabase();

		// creating query
		String query = "select * from papers where ''='' ";
		// query=query+"year='"+year+"'";
		if (semester.compareTo("1") == 0 || semester.compareTo("2") == 0) {
			query = "select * from papers where subjectCode in (select subjectCode from subject_branch where semester = '1' or semester = '2')";
		} else {
			query = "select * from papers join subject on subject.subjectCode=papers.subjectCode where papers.subjectCode in (select subjectCode from subject_branch where semester = '" + semester
					+ "' and branch='" + branch + "') and subjectName='" + subject + "'";
		}

		cursor = db.rawQuery(query, null);
		// System.out.println(query);

		if (cursor.moveToFirst()) {
			do {
				File file = new File(Environment.getExternalStorageDirectory() + cursor.getString(4));
				Boolean isDownloaded;

				if (file.exists() && cursor.getString(4).compareTo("") != 0) {
					isDownloaded = true;
				} else {
					isDownloaded = false;
				}
				Paper p = new Paper(Integer.parseInt(cursor.getString(0)), cursor.getString(8), cursor.getString(5), cursor.getString(1), isDownloaded);
				l.add(p);
			} while (cursor.moveToNext());
		}
		db.close();
		return l;
	}

	// upadtes the subjects in the spinners based on the selected branch and
	// semester
	public void updateSubjects() {

		DatabaseHelper databaseHelper;
		SQLiteDatabase db;
		databaseHelper = new DatabaseHelper(this);
		db = databaseHelper.getReadableDatabase();

		Cursor cursor;
		List<String> list = new ArrayList<String>();

		cursor = db.rawQuery("select subjectName from subject_branch join subject on subject_branch.subjectCode=subject.subjectCode where branch='" + selectedBranch + "' and semester='"
				+ selectedSemester + "'", null);
		// System.out.println("select subjectCode from subject_branch where branch='"
		// + selectedBranch + "' and semester='" + selectedSemester + "'");
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		sa2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		spin3.setAdapter(sa2);
		db.close();

	}

	// updates the list based on the values of the class variables
	// selectedBranch etc.
	public void updateList() {
		String branch, semester, subject, subjectCode;
		ListView listview = (ListView) findViewById(R.id.listView1);

		semester = (spin1.getSelectedItem().toString());
		branch = (spin2.getSelectedItem().toString());
		if (spin3.getSelectedItem() == null) {
			return;
		}
		subject = (spin3.getSelectedItem().toString());

		PaperAdapter adapter = new PaperAdapter(this, getList(branch, semester, subject));
		listview.setAdapter(adapter);

	}

	// handeling selection in the action bar dropdown
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (sharedPreferences.getInt("semester", 0) != ((Spinner) findViewById(R.id.spinner1)).getSelectedItemPosition()
				|| sharedPreferences.getInt("branch", 0) != ((Spinner) findViewById(R.id.spinner2)).getSelectedItemPosition()) {
			DatabaseHelper databaseHelper;
			SQLiteDatabase db;
			databaseHelper = new DatabaseHelper(this);
			db = databaseHelper.getReadableDatabase();

			editor.putInt("semester", ((Spinner) findViewById(R.id.spinner1)).getSelectedItemPosition());
			editor.putInt("branch", ((Spinner) findViewById(R.id.spinner2)).getSelectedItemPosition());

			selectedBranch = (String) ((Spinner) findViewById(R.id.spinner2)).getSelectedItem();
			selectedSemester = (String) ((Spinner) findViewById(R.id.spinner1)).getSelectedItem();
			selectedSubjectPosition = 0;
			editor.putInt("subjectPosition", 0);

			Cursor cursor = db.rawQuery("select * from subject where subjectName='" + selectedSubjectname + "'", null);

			if (cursor.moveToFirst()) {
				editor.putInt("subjectid", Integer.parseInt(cursor.getString(0)));
			} else {
				System.out.println("select * from subject where subjectName='" + ((Spinner) findViewById(R.id.spinner3)).getSelectedItem() + "'");
			}

			updateSubjects();

			db.close();

		} else if (((Spinner) findViewById(R.id.spinner3)).getSelectedItemPosition() > 0) {
			selectedSubjectname = (String) ((Spinner) findViewById(R.id.spinner3)).getSelectedItem();
			editor.putInt("subjectPosition", ((Spinner) findViewById(R.id.spinner3)).getSelectedItemPosition());
		}
		editor.commit();
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
			if (((LinearLayout) findViewById(R.id.searchmenu)).getVisibility() == android.view.View.GONE) {
				((LinearLayout) findViewById(R.id.searchmenu)).setVisibility(android.view.View.VISIBLE);
			} else {
				((LinearLayout) findViewById(R.id.searchmenu)).setVisibility(android.view.View.GONE);
			}
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
		updateList();
	}
}
