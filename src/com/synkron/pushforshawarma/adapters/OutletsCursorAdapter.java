package com.synkron.pushforshawarma.adapters;

import com.synkron.pushforshawarma.R;
import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OutletsCursorAdapter extends CursorAdapter{

	LayoutInflater inflater;
	
	public OutletsCursorAdapter(Context context ,Cursor c, int flags) {
		super(context, c, flags);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		String outletName = cursor.getString(cursor.getColumnIndex(OutletsContentProvider.KEY_OUTLET_NAME));
		TextView txtVwName = (TextView) view.findViewById(R.id.OutletName);
		
		txtVwName.setText(outletName);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		
		return inflater.inflate(R.layout.layout_outlets_adapter, viewGroup, false);
	}

}