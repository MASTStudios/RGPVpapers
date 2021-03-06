package com.maststudios.rgpvpapers;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class Download extends Activity {

	private Uri uri;
	private String downloadTitle;
	private File file;
	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-7460320732883199/4215371418");
		AdRequest adRequest = new AdRequest.Builder().build();
		interstitial.loadAd(adRequest);
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				// Call displayInterstitial() function
				displayInterstitial();
			}
		});

		// populating data on the screen got id and getting everyting from the
		// database
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from papers join subject on papers.subjectCode=subject.subjectCode where id='" + getIntent().getLongExtra("id", 0) + "'", null);

		TextView subjectName = (TextView) findViewById(R.id.subjectName);
		TextView downloadYear = (TextView) findViewById(R.id.downloadYear);
		TextView isDownloaded = (TextView) findViewById(R.id.isDownloaded);
		TextView webDownloadLink = (TextView) findViewById(R.id.webDownloadLink);
		Button downloadButton = (Button) findViewById(R.id.downloadButton);
		Button openButton = (Button) findViewById(R.id.openButton);
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

		if (cursor.moveToFirst()) {
			subjectName.setText(cursor.getString(8));
			downloadYear.setText(cursor.getString(1));
			webDownloadLink.setText(cursor.getString(6));
			uri = Uri.parse(cursor.getString(3));
			downloadTitle = cursor.getString(8) + "(" + cursor.getString(1) + ")";
			file = new File(Environment.getExternalStorageDirectory() + cursor.getString(4));

			if (file.exists() && cursor.getString(4).compareTo("") != 0) {
				downloadButton.setVisibility(android.view.View.GONE);
				isDownloaded.setVisibility(android.view.View.GONE);
				progressBar.setVisibility(android.view.View.GONE);
				isDownloaded.setText("The paper is already downloaded on your device. Click the button to open it.");
			} else {
				isDownloaded.setVisibility(android.view.View.GONE);
				openButton.setVisibility(android.view.View.GONE);
			}
		} else {
			// TODO handle error here
		}
		db.close();
	}

	@Override
	protected void onResume() {
		AdView adView = (AdView) this.findViewById(R.id.adView1);
		AdRequest adRequest = new AdRequest.Builder().build();
		// Load ads into Banner Ads
		adView.loadAd(adRequest);

		super.onResume();
	}

	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	// open paper if already downloaded
	public void open(View view) {
		try{
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		startActivity(intent);
		}catch(Exception e){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("It seems that you do not have a pdf reader installed, we suggest you install Adobe PDF reader.").setTitle("Warning");
			builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String url = "https://play.google.com/store/apps/details?id=com.adobe.reader";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	// downloads or opens the paper
	public void download(View view) {

		final String id = getIntent().getLongExtra("id", 0) + "";
		Request request = new Request(uri);
		request.setTitle(downloadTitle);
		request.setDestinationInExternalPublicDir("/RGPV-Papers", id + ".pdf");
		System.out.println("created request");

		final DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		final long downloadId = dm.enqueue(request);
		final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
		final Context context = this;

		System.out.println("starting thread");
		// thread for progressbar
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean downloading = true;
				while (downloading) {
					DownloadManager.Query q = new DownloadManager.Query();
					q.setFilterById(downloadId);

					Cursor cursor = dm.query(q);
					cursor.moveToFirst();
					int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
					int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
					if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
						downloading = false;
					}
					final double dl_progress = (((double) bytes_downloaded) / ((double) bytes_total)) * 100;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pb.setProgress((int) dl_progress);
						}
					});

					cursor.close();
				}

			}
		}).start();

		// adding broadcast reciever for download completion
		final BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// getting the action
				String action = intent.getAction();

				// checking if it is for download_complete
				// TODO add for download failed
				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
					// getting the download id of the download that was
					// completed
					System.out.println("receieved intent 1");
					long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
					Query query = new Query();
					query.setFilterById(downloadId);
					Cursor c = dm.query(query);
					if (c.moveToFirst()) {
						int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
						System.out.println("receieved intent 1");

						if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

							System.out.println("Download complete");

							// uristring contains the location of the file
							// that is saved
							String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

							// upadting the database with the location of
							// the saved file
							DatabaseHelper databaseHelper = new DatabaseHelper(context);
							SQLiteDatabase db = databaseHelper.getReadableDatabase();
							db.execSQL("UPDATE papers set localURL ='/RGPV-Papers/" + id + ".pdf' where id= " + id);
							db.close();
							unregisterReceiver(this);
							File f = new File(Environment.getExternalStorageDirectory() + "/RGPV-Papers/" + id + ".pdf");
							// System.out.println(Environment.getExternalStorageDirectory()
							// + getIntent().getStringExtra("link"));
							try {
								Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.fromFile(f));
								startActivity(intent1);
								finish();
							} catch (Exception e) {
								AlertDialog.Builder builder = new AlertDialog.Builder(context);
								builder.setMessage("It seems that you do not have a pdf reader installed, we suggest you install Adobe PDF reader.").setTitle("Warning");
								builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										String url = "https://play.google.com/store/apps/details?id=com.adobe.reader";
										Intent i = new Intent(Intent.ACTION_VIEW);
										i.setData(Uri.parse(url));
										startActivity(i);
									}
								});
								builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										finish();
									}
								});
								AlertDialog dialog = builder.create();
								dialog.show();
							}
						}
					}
				}
			}
		};
		registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
