package com.synkron.pushforshawarma.contentproviders;

import java.util.HashMap;

import com.synkron.pushforshawarma.dbopenhelpers.OutletsDBOpenHelper;

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

public class OutletsContentProvider extends ContentProvider{
	
	public static final Uri CONTENT_URI = Uri.parse("content://com.synkron.pushforshawarma.contentproviders.OutletsContentProvider/outlets");
	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;
	private static final int SEARCH = 3;
	
	//database columns constants
	public static final String KEY_ID = "_id";
	public static final String KEY_OUTLET_NAME = "name";
	public static final String KEY_OUTLET_ICON = "icon";
	public static final String KEY_OUTLET_LONGITUDE = "longitude";
	public static final String KEY_OUTLET_LATITUDE = "latitude";
	public static final String KEY_SEARCH_COLUMN = KEY_OUTLET_NAME;
	
	private static final UriMatcher uriMatcher;
	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	OutletsDBOpenHelper dbHelper;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.OutletsContentProvider",
				"stocks", ALLROWS);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.OutletsContentProvider", 
				"stocks/#", SINGLE_ROW);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.OutletsContentProvider",
				SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.OutletsContentProvider",
				SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.OutletsContentProvider",
				SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
		uriMatcher.addURI("com.synkron.pushforshawarma.contentproviders.OutletsContentProvider",
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
		dbHelper = new OutletsDBOpenHelper(context, OutletsDBOpenHelper.DATABASE_NAME, null, 
				OutletsDBOpenHelper.DATABASE_VERSION);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		queryBuilder.setTables(OutletsDBOpenHelper.DATABASE_TABLE);
	
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
			default:
					break;
		}
		
		//if no sort order is specified, sort alphabetically...
		String orderBy;
		if(TextUtils.isEmpty(sortOrder)){
			orderBy = KEY_OUTLET_NAME;
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
				return "vnd.android.cursor.dir/vnd.pushforshawarma.outlet";
			case SINGLE_ROW: 
				return "vnd.android.cursor.item/vnd.pushforshawarma.outlet";
			case SEARCH:
				return SearchManager.SUGGEST_MIME_TYPE;
			default:
				throw new IllegalArgumentException("Unsupported URI: "+ uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		
		String[] columns = new String[]{
				OutletsContentProvider.KEY_OUTLET_ICON,
				OutletsContentProvider.KEY_OUTLET_NAME,
				OutletsContentProvider.KEY_OUTLET_LONGITUDE,
				OutletsContentProvider.KEY_OUTLET_LATITUDE
		};
		
		//TODO: there should be a much better way to check for duplicates in this case
		//prevent duplicates by checking if the the content value already exists...
		Cursor cursor = database.query(false, OutletsDBOpenHelper.DATABASE_TABLE, 
				columns, KEY_OUTLET_NAME + " = '" + values.getAsString(KEY_OUTLET_NAME)+
				"' and "+ KEY_OUTLET_LONGITUDE + " = '"+ values.getAsString(KEY_OUTLET_LONGITUDE)+
				"' and "+ KEY_OUTLET_LATITUDE + " = '"+ values.getAsString(KEY_OUTLET_LATITUDE)+"'",
				null, null, null, null, null);
		
		if(!cursor.moveToFirst()){
			long rowID = database.insert(OutletsDBOpenHelper.DATABASE_TABLE, null, values);
		
			if(rowID > 0){
				Uri _mUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(_mUri, null);
				
				return _mUri;
			}
			throw new SQLException("Failed to insert row into "+ uri);
		}
		
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		
		int count;
		switch(uriMatcher.match(uri)){
		case ALLROWS:
				count = database.delete(OutletsDBOpenHelper.DATABASE_TABLE, selection, selectionArgs);
			break;
		case SINGLE_ROW:
			String segment = uri.getPathSegments().get(1);
			count = database.delete(OutletsDBOpenHelper.DATABASE_TABLE, KEY_ID + "="
					+ segment
					+ (!TextUtils.isEmpty(selection) ? " AND ("
					+ selection + ')' : ""), selectionArgs);
			break;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: "+ uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		
		int count;
		
		switch(uriMatcher.match(uri)){
		case ALLROWS:
				count = database.update(OutletsDBOpenHelper.DATABASE_TABLE, values, selection, selectionArgs);
			break;
		case SINGLE_ROW:
			String segment = uri.getPathSegments().get(1);
			count = database.update(OutletsDBOpenHelper.DATABASE_TABLE, values, 
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
