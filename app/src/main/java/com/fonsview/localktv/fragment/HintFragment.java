package com.fonsview.localktv.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.locaktv.R;
import com.fonsview.localktv.AppContext;


public class HintFragment extends Fragment
{
	
	private String NextHint;
	private String Current;
	private boolean Rec;
	
	private SharedPreferences Preferences;
	
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
		Preferences = getActivity().getSharedPreferences(AppContext.LOCAKTV,
				Activity.MODE_PRIVATE);
		
	}
	
	public View onCreateView(LayoutInflater inflater
			, ViewGroup container, Bundle savedInstanceState)
		{
		
			View rootView = inflater.inflate(R.layout.hint,
				container, false);
			NextHint = Preferences.getString(AppContext.Next_KEY,"");
			Current = Preferences.getString(AppContext.Current_KEY,"");
			Rec = Preferences.getBoolean(AppContext.Rec_KEY,false);
		((TextView)rootView.findViewById(R.id.NextMusic)).setText(NextHint);
		((TextView)rootView.findViewById(R.id.CurrentMusic)).setText(Current);
		int state = Rec?View.VISIBLE:View.INVISIBLE;
		
		((TextView)rootView.findViewById(R.id.Rec)).setVisibility(state);
		return rootView;
	}
	
	
}
