package com.synkron.pushforshawarma;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout{

	UpdateMapAfterUserInteraction updateMapAfterUserInteraction;
	private long lastTouched = 0;
	private static final long SCROLL_TIME = 200L;
	
	public TouchableWrapper(Context context) {
		super(context);

		try{
			updateMapAfterUserInteraction  = (MapActivity) context;
			
		} catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement UpdateMapAfterUserInterection");
        }
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent motionEvent){
		
		switch(motionEvent.getAction()){
			case MotionEvent.ACTION_DOWN:
				lastTouched = SystemClock.uptimeMillis();
				break;
				
			case MotionEvent.ACTION_UP:
				final long now = SystemClock.uptimeMillis();
				if((now - lastTouched) > SCROLL_TIME){
					updateMapAfterUserInteraction.onUpdateMapAfterUserInteraction();
				}
				break;
		}
		return super.dispatchTouchEvent(motionEvent);
	}
	public interface UpdateMapAfterUserInteraction{
		public void onUpdateMapAfterUserInteraction();
	}
}
