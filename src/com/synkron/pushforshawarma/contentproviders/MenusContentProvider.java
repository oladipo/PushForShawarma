package com.synkron.pushforshawarma.contentproviders;

import java.util.HashMap;

import com.synkron.pushforshawarma.dbopenhelpers.PushForShawarmaDBOpenHelper;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MenusContentProvider extends ContentProvider{
	
	private static final String TAG = "MenusContentProvider";
	
	public static final Uri CONTENT_URI = Uri.parse("content://com.synkron.pushforshawarma.contentproviders.MenusContentProvider/menus");
	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;
	private static final int SEARCH = 3;
	private static final int OUTLET_MENUS = 4;
	
	//table columns constants
	public static final String KEY_ID = "_id";
	public static final String KEY_OUTLET_CODE = "outletCode";
	public static final String KEY_MENU_CODE = "code";
	public static final String KEY_MENU_NAME = "name";
	public static final String KEY_MENU_IMAGE_URL = "imageURL";
	public static final String KEY_MENU_PRICE = "price";
	public static final String KEY_MENU_DESCRIPTION = "description";
	
	public static final String KEY_SEARCH_COLUMN = KEY_MENU_NAME;
	
	
	private static final UriMatcher uriMatcher;
	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	PushForShawarmaDBOpenHelper dbHelper;
	
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.MenusContentProvider",
				"menus", ALLROWS);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.MenusContentProvider", 
				"menus/#", SINGLE_ROW);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.MenusContentProvider",
				"menus/*", OUTLET_MENUS);
		
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.MenusContentProvider",
				SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.MenusContentProvider",
				SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.MenusContentProvider",
				SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.MenusContentProvider",
				SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH);
	}
	
	//Projection for search suggestions....
	private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;

	static{
		SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
		SEARCH_SUGGEST_PROJECTION_MAP.put("_id", KEY_ID + " AS "+ "_id");
		SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, KEY_SEARCH_COLUMN 
				+ " AS "+ SearchManager.SUGGEST_COLUMN_TEXT_1);
		SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, KEY_ID 
				+ " AS "+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	};
	
	@Override
	public boolean onCreate() {
		Context context = getContext();
		
		dbHelper = new PushForShawarmaDBOpenHelper(context, PushForShawarmaDBOpenHelper.DATABASE_NAME, null, 
				PushForShawarmaDBOpenHelper.DATABASE_VERSION);
		
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		queryBuilder.setTables(PushForShawarmaDBOpenHelper.DATABASE_MENUS_TABLE);
		
		//if this is a row query, limit the result set to the passed in row.
		switch(uriMatcher.match(uri)){
			case SINGLE_ROW:
				queryBuilder.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
				break;
			case SEARCH:
				String query = uri.getPathSegments().get(1);
				queryBuilder.appendWhere(KEY_SEARCH_COLUMN + " LIKE \"%"+ query + "%\"");
				queryBuilder.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP);
				break;
			case OUTLET_MENUS:
				queryBuilder.appendWhere(KEY_OUTLET_CODE + " = '" + uri.getPathSegments().get(1)+ "'");
				
			default:
					break;
		}
		
		//if no sort order is specified, sort alphabetically...
		String orderBy;
		if(TextUtils.isEmpty(sortOrder)){
			orderBy = KEY_MENU_NAME;
		}else{
			orderBy = sortOrder;
		}
		
		//Apply the query to the underlying database.

		Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, 
					null, null, orderBy);

		//Register the contexts ContentResolver to be notified if the cursor result set changes..
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch(uriMatcher.match(uri)){
		case ALLROWS: 
			return "vnd.android.cursor.dir/vnd.pushforshawarma.menu";
		case SINGLE_ROW: 
			return "vnd.android.cursor.item/vnd.pushforshawarma.menu";
		case SEARCH:
			return SearchManager.SUGGEST_MIME_TYPE;
		case OUTLET_MENUS:
			return "vnd.android.cursor.dir/vnd.pushforshawarma.menu";
		default:
			throw new IllegalArgumentException("Unsupported URI: "+ uri);
	}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		
		String[] columns = new String[]{
				MenusContentProvider.KEY_MENU_NAME,
				MenusContentProvider.KEY_MENU_CODE,
				MenusContentProvider.KEY_OUTLET_CODE
		};
		
		Cursor cursor = database.query(false, PushForShawarmaDBOpenHelper.DATABASE_MENUS_TABLE, 
				columns, KEY_MENU_NAME + " = '" + values.getAsString(KEY_MENU_NAME)+
				"' and "+ KEY_MENU_CODE + " = '"+ values.getAsString(KEY_MENU_CODE)+
				"' and "+ KEY_OUTLET_CODE + " = '"+ values.getAsString(KEY_OUTLET_CODE)+"'",
				null, null, null, null, null);
		
		if(!cursor.moveToFirst()){
			long rowID = database.insert(PushForShawarmaDBOpenHelper.DATABASE_MENUS_TABLE, null, values);
		
			if(rowID > 0){
				Uri _mUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(_mUri, null);
				
				return _mUri;
			}
			throw new SQLException("Failed to insert row into "+ uri);
		}else{
			//update existing rows..
			database.update(PushForShawarmaDBOpenHelper.DATABASE_MENUS_TABLE, values, 
					KEY_OUTLET_CODE + " = '" + values.getAsString(KEY_OUTLET_CODE)+
					"' and "+ KEY_MENU_CODE + " = '" + values.getAsString(KEY_MENU_CODE)+
					"' and "+ KEY_MENU_NAME + " = '"+ values.getAsString(KEY_MENU_NAME)+
					"' and "+ KEY_MENU_IMAGE_URL + " = '"+ values.getAsString(KEY_MENU_IMAGE_URL)+
					"' and "+ KEY_MENU_PRICE + " = '"+ values.getAsString(KEY_MENU_PRICE)+
					"' and "+ KEY_MENU_DESCRIPTION + " = '"+ values.getAsString(KEY_MENU_DESCRIPTION)+"'", null);
			
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		
		int count;
		
		switch(uriMatcher.match(uri)){
		case ALLROWS:
				count = database.update(PushForShawarmaDBOpenHelper.DATABASE_MENUS_TABLE, values, selection, selectionArgs);
			break;
		case SINGLE_ROW:
			String segment = uri.getPathSegments().get(1);
			count = database.update(PushForShawarmaDBOpenHelper.DATABASE_MENUS_TABLE, values, 
					KEY_ID 
					+ "=" + segment
					+ (!TextUtils.isEmpty(selection) ? " AND ("
					+ selection + ')' : ""), selectionArgs);
			
			break;
			
			default:
				throw new IllegalArgumentException("Unknown URI "+ uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

}
