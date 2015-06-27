package com.synkron.pushforshawarma.dbopenhelpers;

import com.synkron.pushforshawarma.contentproviders.MenusContentProvider;
import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PushForShawarmaDBOpenHelper extends SQLiteOpenHelper{

	private static final String TAG = "OutletsDBOpenHelper";
	
	public static final String DATABASE_NAME = "pushForShawarma.db";
	public static final String DATABASE_OUTLETS_TABLE = "OutletsTable";
	public static final String DATABASE_MENUS_TABLE = "MenusTable";
	
	public static final int DATABASE_VERSION = 7;
	
	private static final String DATABASE_CREATE_OUTLETS_TABLE = "create table "+ DATABASE_OUTLETS_TABLE + " ("
			+ OutletsContentProvider.KEY_ID + " integer primary key autoincrement,"
			+ OutletsContentProvider.KEY_OUTLET_CODE  + " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_ICON + " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_NAME+ " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_LATITUDE+ " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_ADDRESS+ " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_PHONE+ " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_EMAIL+ " TEXT,"
			+ OutletsContentProvider.KEY_OUTLET_LONGITUDE + " TEXT)";
	;
	
	private static final String DATABASE_CREATE_MENUS_TABLE = "create table "+ DATABASE_MENUS_TABLE + " ("
			+ MenusContentProvider.KEY_ID + " integer primary key autoincrement,"
			+ MenusContentProvider.KEY_OUTLET_CODE  + " TEXT,"
			+ MenusContentProvider.KEY_MENU_IMAGE_URL + " TEXT,"
			+ MenusContentProvider.KEY_MENU_CODE+ " TEXT,"
			+ MenusContentProvider.KEY_MENU_NAME+ " TEXT,"
			+ MenusContentProvider.KEY_MENU_DESCRIPTION+ " TEXT,"
			+ MenusContentProvider.KEY_MENU_PRICE + " TEXT)";
	;
	
	public PushForShawarmaDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Executing sqlite query :"+ DATABASE_CREATE_OUTLETS_TABLE);
		
		db.execSQL(DATABASE_CREATE_OUTLETS_TABLE);
		
		Log.i(TAG, "Executing sqlite query :"+ DATABASE_CREATE_MENUS_TABLE);
		
		db.execSQL(DATABASE_CREATE_MENUS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version" + oldVersion + " to "+ newVersion + " which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_OUTLETS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MENUS_TABLE);
		
		onCreate(db);
	}

}
