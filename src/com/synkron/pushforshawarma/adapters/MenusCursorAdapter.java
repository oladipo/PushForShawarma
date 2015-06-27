package com.synkron.pushforshawarma.adapters;

import com.synkron.pushforshawarma.R;
import com.synkron.pushforshawarma.contentproviders.MenusContentProvider;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MenusCursorAdapter extends CursorAdapter{
	
	private static final String TAG = "MenusCursorAdapter";
	LayoutInflater inflater;
	
	public MenusCursorAdapter(Context context ,Cursor c, int flags) {
		super(context, c, flags);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		String menuName = cursor.getString(cursor.getColumnIndex(MenusContentProvider.KEY_MENU_NAME));
		String menuDescription = cursor.getString(cursor.getColumnIndex(MenusContentProvider.KEY_MENU_DESCRIPTION));
		String menuPrice = cursor.getString(cursor.getColumnIndex(MenusContentProvider.KEY_MENU_PRICE));
		
		TextView menuNameView = (TextView)view.findViewById(R.id.menuName);
		TextView menuDescriptionView = (TextView)view.findViewById(R.id.menuDescription);
		TextView menuPriceView = (TextView)view.findViewById(R.id.menuPrice);
		
		menuNameView.setText(menuName);
		menuDescriptionView.setText(menuDescription);
		menuPriceView.setText(menuPrice);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return inflater.inflate(R.layout.layout_menus_adapter, viewGroup, false);
	}

}
