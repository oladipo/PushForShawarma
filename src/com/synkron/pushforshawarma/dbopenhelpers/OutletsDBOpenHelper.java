package com.synkron.pushforshawarma.dbopenhelpers;

import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OutletsDBOpenHelper extends SQLiteOpenHelper{

	private static final String TAG = "OutletsDBOpenHelper";
	
	public static final String DATABASE_NAME = "pushForShawarma.db";
	public static final String DATABASE_TABLE = "OutletsTable";
	public static final int DATABASE_VERSION = 2;
	
	private static final String DATABASE_CREATE = "create table "+ DATABASE_TABLE + " ("
			+ OutletsContentProvider.KEY_ID + " integer primary key autoincrement,"
			+ OutletsContentProvider.KEY_OUTLET_ICON + " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_NAME+ " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_LATITUDE+ " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_LONGITUDE + " TEXT)";
	;
	
	public OutletsDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Executing sqlite query :"+ DATABASE_CREATE);
		
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version" + oldVersion + " to "+ newVersion + " which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		
		onCreate(db);
	}

}
