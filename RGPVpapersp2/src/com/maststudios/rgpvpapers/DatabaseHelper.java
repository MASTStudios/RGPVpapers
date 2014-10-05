package com.maststudios.rgpvpapers;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHelper extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "rgpvdb";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
//	public ArrayList<Paper> getPapersBySql(String query) {
//		SQLiteDatabase db = getReadableDatabase();
//		Cursor cus = db.rawQuery(query, null);
//		ArrayList<Paper> result = new ArrayList<Paper>();
//		while (cus.moveToNext()) {
//			Paper temp = new Paper(cus.getString(0), cus.getString(1),
//					cus.getString(2), cus.getString(3), cus.getString(4),
//					cus.getString(5), cus.getString(6), cus.getString(7),
//					cus.getString(8));
//			result.add(temp);
//		}
//		return result;
//	}
}
