package com.fonsview.localktv.keyborad;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.example.locaktv.R;

import java.util.List;

public class KeyboardUtil {

	private MyKeyboardView keyboardView;
	private Keyboard k1; // 字母键盘
	private Keyboard k2; // 数字键盘
	private View view;

	public boolean isnun = false;// 是否数据键盘
	public boolean isupper = false;// 是否大写

	private EditText ed;

	private int nCurKeyboardKeyNums;
	private Keyboard nCurrentKeyboard;
	private List<Keyboard.Key> nKeys;
	private int nLastKeyIndex = 0;

	public KeyboardUtil(Activity act, EditText edit, View view) {

		this.ed = edit;

		this.view = view;

		k1 = new Keyboard(act, R.xml.vertical);
		k2 = new Keyboard(act, R.xml.symbols);

		keyboardView = (MyKeyboardView) view.findViewById(R.id.keyboard_view);

		keyboardView.setKeyboard(k1);
		
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(false);
		keyboardView.setOnKeyboardActionListener(listener2);
		keyboardView.setOnKeyListener(onKeyListener);

	}

	
	private OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {

				// 返回操作
				case KeyEvent.KEYCODE_BACK:
					if (event.getRepeatCount() == 0 && keyboardView != null) {
						hideKeyboard();	
						return true;
					}
					return false;

				// 下键操作
				case KeyEvent.KEYCODE_DPAD_DOWN:
					setFields();
					if (nLastKeyIndex >= nCurKeyboardKeyNums - 1) {
						if (null == keyboardView)
							return false;
						keyboardView.setLastKeyIndex(0);
					} else {
						int[] nearestKeyIndices = nCurrentKeyboard
								.getNearestKeys(nKeys.get(nLastKeyIndex).x,
										nKeys.get(nLastKeyIndex).y);

						for (int index : nearestKeyIndices) {
							if (nLastKeyIndex < index) {
								Key nearKey = nKeys.get(index);
								Key lastKey = nKeys.get(nLastKeyIndex);
								if (((lastKey.x >= nearKey.x) // left side
																// compare
										&& (lastKey.x < (nearKey.x + nearKey.width)))
										|| (((lastKey.x + lastKey.width) > nearKey.x) // right
																						// side
																						// compare
										&& ((lastKey.x + lastKey.width) <= (nearKey.x + nearKey.width)))) {
									keyboardView.setLastKeyIndex(index);
									break;
								}
							}
						}// end for loop

					}
					keyboardView.invalidate();
					// break;
					return true;

				case KeyEvent.KEYCODE_DPAD_UP:

					setFields();
					if (nLastKeyIndex <= 0) {
						if (null == keyboardView)
							return false;
						keyboardView.setLastKeyIndex(nCurKeyboardKeyNums - 1);
					} else {
						int[] nearestKeyIndices = nCurrentKeyboard
								.getNearestKeys(nKeys.get(nLastKeyIndex).x,
										nKeys.get(nLastKeyIndex).y);

						for (int i = nearestKeyIndices.length - 1; i >= 0; i--) {
							int index = nearestKeyIndices[i];
							if (nLastKeyIndex > index) {
								// 获得下一个键
								Key nearKey = nKeys.get(index);
								Key nextNearKey = nKeys.get(index + 1);
								
								Key lastKey = nKeys.get(nLastKeyIndex);
								if (((lastKey.x >= nearKey.x)
										&& (lastKey.x < (nearKey.x + nearKey.width)) && (((lastKey.x + lastKey.width) <= (nextNearKey.x + nextNearKey.width)) || ((lastKey.x + lastKey.width) > nextNearKey.x)))) {
									keyboardView.setLastKeyIndex(index);
									break;
								}
							}
						}
					}
					keyboardView.invalidate();
					// break;
					return true;

					// 左键操作
				case KeyEvent.KEYCODE_DPAD_LEFT:

					setFields();
					if (nLastKeyIndex <= 0) {
						if (null == keyboardView)
							return false;
						keyboardView.setLastKeyIndex(nCurKeyboardKeyNums - 1);
					} else {
						nLastKeyIndex--;
						keyboardView.setLastKeyIndex(nLastKeyIndex);
					}
					keyboardView.invalidate();
					// break;
					return true;

					// 右键操作
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					setFields();
					if (nLastKeyIndex >= nCurKeyboardKeyNums - 1) {
						if (null == keyboardView)
							return false;
						keyboardView.setLastKeyIndex(0);
					} else {
						nLastKeyIndex++;
						keyboardView.setLastKeyIndex(nLastKeyIndex);
					}
					keyboardView.invalidate();
					// break;
					return true;

					
				case KeyEvent.KEYCODE_ENTER:
				case KeyEvent.KEYCODE_DPAD_CENTER://遥控OK	
					if (null == keyboardView) {
						return false;
					}
					setFields();
					Editable editable = ed.getText();
					int start = ed.getSelectionStart();
					int curKeyCode = nKeys.get(nLastKeyIndex).codes[0];

					switch (curKeyCode) {

					case Keyboard.KEYCODE_SHIFT: // 按下shift
						changeKey();
						keyboardView.setKeyboard(k1);
						break;

					case -2: // 数字字母键盘切换
						if (isnun) {
							isnun = false;
							keyboardView.setKeyboard(k1);
						} else {
							isnun = true;
							keyboardView.setKeyboard(k2);
						}
						keyboardView.setLastKeyIndex(0); // 重置上一个键的index
						break;

					case Keyboard.KEYCODE_CANCEL: // 完成键，关闭键盘
						hideKeyboard();
						break;

					case Keyboard.KEYCODE_DELETE: 
						if (editable != null && editable.length() > 0) {
							if (start > 0) {
								editable.delete(start - 1, start);
							}
						}
						break;

					case 57419: // 向左
						if (start > 0) {
							ed.setSelection(start - 1);
						}
						break;

					case 57421: // 向右
						if (start < ed.length()) {
							ed.setSelection(start + 1);
						}
						break;

					default:
						editable.insert(start,
								Character.toString((char) curKeyCode));
						return true;
					}
					return true;
					
				default:
					return false;
				}
			}
			
			return true;
			
		}
	};

	private OnKeyboardActionListener listener2 = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
				hideKeyboard();
			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
				changeKey();
				keyboardView.setKeyboard(k1);
			} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 数字键盘切换
				if (isnun) {
					isnun = false;
					keyboardView.setKeyboard(k1);
				} else {
					isnun = true;
					keyboardView.setKeyboard(k2);
				}
				keyboardView.setLastKeyIndex(0); // 重置上一个键的index
			} else if (primaryCode == 57419) { // go left
				if (start > 0) {
					ed.setSelection(start - 1);
				}
			} else if (primaryCode == 57421) { // go right
				if (start < ed.length()) {
					ed.setSelection(start + 1);
				}
			} else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}
	};

	// 键盘大小写切换
	private void changeKey() {
		List<Key> keylist = k1.getKeys();
		if (isupper) {// 大写切换小写
			isupper = false;
			for (Key key : keylist) {
				if (key.label != null && isword(key.label.toString())) {
					key.label = key.label.toString().toLowerCase();
					key.codes[0] = key.codes[0] + 32;
				}
			}
		} else {// 小写切换大写
			isupper = true;
			for (Key key : keylist) {
				if (key.label != null && isword(key.label.toString())) {
					key.label = key.label.toString().toUpperCase();
					key.codes[0] = key.codes[0] - 32;
				}
			}
		}
	}

	private boolean isword(String str) {
		String wordstr = "abcdefghijklmnopqrstuvwxyz";
		if (wordstr.indexOf(str.toLowerCase()) > -1) {
			return true;
		}
		return false;
	}

	// 重置变量
	private void setFields() {
		if (null == keyboardView)
			return;
		nCurrentKeyboard = keyboardView.getKeyboard();
		nKeys = nCurrentKeyboard.getKeys();
		nCurKeyboardKeyNums = nKeys.size();
		nLastKeyIndex = keyboardView.getLastKeyIndex();
	}

	// 显示软键??
	public void showKeyboard() {	
		if (view.getVisibility() != View.VISIBLE) {			
			view.setVisibility(View.VISIBLE);
			keyboardView.setVisibility(View.VISIBLE);
			keyboardView.setKeyboard(k1); // 重置成小写键盘
			keyboardView.setLastKeyIndex(0); // 设置键盘弹出时默认
			
		}
		keyboardView.requestFocus();
	}

	// 隐藏软键盘
	public void hideKeyboard() {		
		if (view.getVisibility() == View.VISIBLE) {			
			view.setVisibility(View.GONE);
			keyboardView.setVisibility(View.GONE);			
		}
	}

	// 判断软键盘是否显示	
	public boolean isShowing() {
		if (view.getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}
}
