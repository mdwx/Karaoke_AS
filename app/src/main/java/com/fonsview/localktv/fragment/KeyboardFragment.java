package com.fonsview.localktv.fragment;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.locaktv.R;
import com.fonsview.localktv.AppContext;
import com.fonsview.localktv.keyborad.KeyboardUtil;
import com.fonsview.localktv.tool.L;

import java.lang.reflect.Method;


public class KeyboardFragment extends Fragment
{
	private static final String Position_KEY = "SelectPosition";
	private static final String LOCAKTV = "Locaktv";
	private EditText edit;
	private RelativeLayout keyboardContent;
	private KeyboardUtil keyboardUtil;
	
	private KeyboardCallbacks mCallbacks;
	private SharedPreferences Preferences;
	// 定义??个回调接口，该Fragment??在Activity??要实现该接口
	// 该Fragment将???过该接口与它所在的Activity交互
	public interface KeyboardCallbacks
	{
		public void TextChangedListener(String str);
	}

	
	// 当该Fragment被添加???显示到Activity时，回调该方??
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// 如果Activity没有实现Callbacks接口，抛出异??
		if (!(activity instanceof KeyboardCallbacks))
		{
			throw new IllegalStateException(
				"Fragment??在的Activity必须实现Callbacks接口!");
		}
		// 把该Activity当成Callbacks对象
		mCallbacks = (KeyboardCallbacks)activity;
	}
	// 当该Fragment从它??属的Activity中被删除时回调该方法
	@Override
	public void onDetach()
	{
		super.onDetach();
		// 将mCallbacks赋为null??
		mCallbacks = null;
	}
	// 当用户点击某列表项时??发该回调方法
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(L._FILE_LINE_(), "Fragment  onCreate");
		 Preferences = getActivity().getSharedPreferences(LOCAKTV,
					Activity.MODE_PRIVATE);	
		super.onCreate(savedInstanceState);
	}

	// 重写该方法，该方法返回的View将作为Fragment显示的组??
	@Override
	public View onCreateView(LayoutInflater inflater
		, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(L._FILE_LINE_(), "Fragment  onCreateView");
		// 加载/res/layout/目录下的xml布局文件
		View rootView = inflater.inflate(R.layout.fragmentkeyborad,
				container, false);	
		
		edit = (EditText) rootView.findViewById(R.id.edit);
		disableSysKeyboard(edit);
		
		if(Integer.parseInt(Preferences.getString(Position_KEY,"3")) == AppContext.SEARCH_Singer)
		{
			edit.setHint(getActivity().getString(R.string.inputsingerKey));
		}
		else
		{
			edit.setHint(getActivity().getString(R.string.inputKey));
		}
		
		keyboardContent = (RelativeLayout) rootView.findViewById(R.id.keyboardContent);		
		showKeyboard();
		
		edit.setOnKeyListener(new OnKeyListener() {			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
				{
					showKeyboard();
					return true;
				}	
				
				return false;					
			}
		});
		
		//编辑框修改监??
		edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				mCallbacks.TextChangedListener(arg0.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub				
			}
		});
		
		return rootView;
	}
	
// 禁用系统键盘
	private void disableSysKeyboard(EditText editText) {
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

		if (android.os.Build.VERSION.SDK_INT <= 10) {
			editText.setInputType(InputType.TYPE_NULL);
		} else { // 解决4.0中屏蔽掉系统键盘光标丢失问题
			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			try {
				Class<EditText> cls = EditText.class;
				Method setSoftInputShownOnFocus;
				setSoftInputShownOnFocus = cls.getMethod(
						"setSoftInputShownOnFocus", boolean.class);
				setSoftInputShownOnFocus.setAccessible(true);
				setSoftInputShownOnFocus.invoke(editText, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showKeyboard() {
		if (keyboardUtil == null) {
			keyboardUtil = new KeyboardUtil(getActivity(), edit, keyboardContent);
			keyboardUtil.showKeyboard();
		} else if (!keyboardUtil.isShowing()) {
			keyboardUtil.showKeyboard();
		}
	}
	
	
}
