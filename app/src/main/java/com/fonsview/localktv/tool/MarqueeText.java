package com.fonsview.localktv.tool;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/** 
 * @author Vince  E-mail: xhys01@163.com
 * @version 创建时间：2015-5-28 下午3:32:21 
 * 类说明 
 */
public class MarqueeText extends TextView {
	 public MarqueeText(Context con) {
	 super(con);
	}                                    
	public MarqueeText(Context context, AttributeSet attrs) {
	super(context, attrs);
	}
	public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	}
	@Override
	public boolean isFocused() {
	return true;
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction,
	Rect previouslyFocusedRect) {
	}
}