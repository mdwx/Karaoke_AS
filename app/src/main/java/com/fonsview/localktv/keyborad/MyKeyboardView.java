package com.fonsview.localktv.keyborad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class MyKeyboardView extends KeyboardView {

	private Keyboard currentKeyboard;
	private List<Key> keys = new ArrayList<Key>();
	private int lastKeyIndex = 0;
	private Key focusedKey;
	private Rect rect;
	
	public MyKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onDraw(Canvas canvas) {

		 super.onDraw(canvas);
	        currentKeyboard = this.getKeyboard();
	        keys = currentKeyboard.getKeys();
	        Paint p = new Paint();
	        p.setColor(Color.CYAN);
	        p.setStyle(Style.STROKE);
	        p.setStrokeWidth(3.75f);
	        focusedKey = keys.get(lastKeyIndex);
	        rect = new Rect(
	                focusedKey.x, focusedKey.y + 4,
	                focusedKey.x + focusedKey.width,
	                focusedKey.y + focusedKey.height
	            );
	        canvas.drawRect(rect, p);    
	   }

	private CharSequence adjustCase(CharSequence label) {
		if (currentKeyboard.isShifted() && label != null && label.length() < 3
				&& Character.isLowerCase(label.charAt(0))) {
			label = label.toString().toUpperCase();
		}
		return label;
	}

	public int getLastKeyIndex() {
		return lastKeyIndex;
	}

	public void setLastKeyIndex(int index) {
		this.lastKeyIndex = index;
	}

}
