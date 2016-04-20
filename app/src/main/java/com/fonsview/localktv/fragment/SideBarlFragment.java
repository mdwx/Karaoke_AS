package com.fonsview.localktv.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.locaktv.R;
import com.fonsview.localktv.AppContext;


public class SideBarlFragment extends Fragment
{
	
	private StringBuffer datalist ;

	private Callbacks_Sidebar mCallbacks;
	private SharedPreferences Preferences;
	
	public interface Callbacks_Sidebar
	{
		public void onSidebarSelected(char c);
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);			
		
	}
	
	// 当该Fragment被添加、显示到Activity时，回调该方法
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// 如果Activity没有实现Callbacks接口，抛出异常
		if (!(activity instanceof Callbacks_Sidebar))
		{
			throw new IllegalStateException(
				"BookListFragment所在的Activity必须实现Callbacks接口!");
		}
		// 把该Activity当成Callbacks对象
		mCallbacks = (Callbacks_Sidebar)activity;
		
		Preferences = getActivity().getSharedPreferences(AppContext.LOCAKTV,
				Activity.MODE_PRIVATE);
		
		datalist = new StringBuffer(Preferences.getString(AppContext.SIDEBAR_KEY,"ABCDEFGHIJKLMNOPQRSTUVWXYZ#"));
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		// 将mCallbacks赋为null。
		mCallbacks = null;
	}

	// 重写该方法，该方法返回的View将作为Fragment显示的组件
	@Override
	public View onCreateView(LayoutInflater inflater
		, ViewGroup container, Bundle savedInstanceState)
	{
		// 加载/res/layout/目录下的fragment_book_detail.xml布局文件
		View rootView = inflater.inflate(R.layout.side_barl,
				container, false);
		((ListView)rootView.findViewById(R.id.list)).setAdapter(new BaseAdapter(){
			@Override
			public int getCount()
			{
				// 指定一共包含所有单词
				return datalist.length();
			}
			@Override
			public Object getItem(int position)
			{
				return null;
			}
			// 重写该方法，该方法的返回值将作为列表项的ID
			@Override
			public long getItemId(int position)
			{
				return position;
			}
			// 重写该方法，该方法返回的View将作为列表框
			@Override
			public View getView(int position
					, View convertView , ViewGroup parent)
			{			
				
				TextView text = new TextView(getActivity());
				text.setText(datalist.substring(position, position+1));			
				text.setTextColor(Color.RED);
				text.setTypeface(Typeface.DEFAULT_BOLD);
				text.setGravity(Gravity.CENTER_HORIZONTAL);
				return text;
			}
		});
		
		((ListView)rootView.findViewById(R.id.list)).setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mCallbacks.onSidebarSelected(datalist.charAt(arg2));				
			}
		});
		return rootView;
	}
}
