package com.fonsview.localktv.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fonsview.localktv.AppContext;
import com.fonsview.localktv.model.ItemContent;
import com.fonsview.localktv.tool.L;


public class ItemFragment extends ListFragment
{
	
	private Callbacks mCallbacks;
	
	private SharedPreferences Preferences;
	
	public interface Callbacks
	{
		public void onItemSelected(int  id);
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		setListAdapter(new ArrayAdapter<ItemContent>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, AppContext.ITEMS));		 
	          	      
		
	}
	
	// 当该Fragment被添加、显示到Activity时，回调该方法
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// 如果Activity没有实现Callbacks接口，抛出异常
		if (!(activity instanceof Callbacks))
		{
			throw new IllegalStateException(
				"BookListFragment所在的Activity必须实现Callbacks接口!");
		}
		// 把该Activity当成Callbacks对象
		mCallbacks = (Callbacks)activity;	
		 Preferences = getActivity().getSharedPreferences(AppContext.LOCAKTV,
					Activity.MODE_PRIVATE);	
	}
	

	@Override
	public void onDetach()
	{
		super.onDetach();
		// 将mCallbacks赋为null。
		mCallbacks = null;
	}

	
	@Override
	public void onListItemClick(ListView listView
		, View view, int position, long id)
	{
		super.onListItemClick(listView, view, position, id);
		
		Editor editor = Preferences.edit();
		editor.putString(AppContext.Position_KEY,String.valueOf(AppContext.ITEMS.get(position).id));
		editor.commit();
		// 激发mCallbacks的onItemSelected方法
		mCallbacks.onItemSelected(AppContext.ITEMS.get(position).id);		
		
		Log.d(L._FILE_LINE_(), "putLong position: "+ AppContext.ITEMS.get(position).id);
	}
	
	
	public void setActivateOnItemClick(boolean activateOnItemClick)
	{
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}
	
}
