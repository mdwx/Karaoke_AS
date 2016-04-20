package com.fonsview.localktv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locaktv.R;
import com.fonsview.localktv.dataSources.CursorDate;
import com.fonsview.localktv.dataSources.FSVMediaRecorder;
import com.fonsview.localktv.dataSources.MediaPlayerControl;
import com.fonsview.localktv.fragment.HintFragment;
import com.fonsview.localktv.fragment.ItemFragment;
import com.fonsview.localktv.fragment.ItemFragment.Callbacks;
import com.fonsview.localktv.fragment.KeyboardFragment;
import com.fonsview.localktv.fragment.KeyboardFragment.KeyboardCallbacks;
import com.fonsview.localktv.fragment.SideBarlFragment;
import com.fonsview.localktv.fragment.SideBarlFragment.Callbacks_Sidebar;
import com.fonsview.localktv.model.MediaBase;
import com.fonsview.localktv.tool.L;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.lidroid.xutils.view.annotation.event.OnKey;
import com.lidroid.xutils.view.annotation.event.OnProgressChanged;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity 	implements
	Callbacks_Sidebar,Callbacks,KeyboardCallbacks
{

	@ViewInject(R.id.surfaceLayout)
	private FrameLayout surfaceView;

	@ViewInject(R.id.TableLayout01)
	private TableLayout TableLayout01;

	@ViewInject(R.id.FrameLayout)
	private ViewGroup FrameLayout;

	@ViewInject(R.id.HintFragment)
	private ViewGroup HintFrameLayout;

	@ViewInject(R.id.ItemFragment)
	private ViewGroup ItemFragment;

	@ViewInject(R.id.SideBarlFragment)
	private ViewGroup SideBarlFragment;

	@ViewInject(R.id.SearchFrameLayout)
	private ViewGroup SearchFrame;

	@ViewInject(R.id.SeekBar)
	private SeekBar Seek;

	@ViewInject(R.id.Datalist)
	private ListView Datalist;


	private boolean SeekTimeUp = true;//进度条超时标志
	private int Seekoffset = 5000;//进度条显示时间
	private MediaPlayerControl mPlayer;//媒体播放器

	private FSVMediaRecorder MediaRecorder;//录音机
	private Boolean Recordering = false;//录制标志

	//当前列表位置
	private int positionDatalist;

	//媒体数据库容器
	private CursorDate mCursor;


	private ListAdapter simpleadapter;//适配器

	private SharedPreferences Preferences;
	private Editor editor;

	private Toast toast = null;

	private long preTime; //按退出键时间记录， 如果时间间隔大于2秒, 不处理  
	private DbUtils db;

	MyRunnable timerun;
	private int timeFrequency = 2000; //每两秒执行一次timerun

	private String[] items;
	private String[] items_Recorder;
	private String[] language_items;
	private String[] Style_items;
	private AlertDialog.Builder builder;
	private	ProgressDialog progressDialog;

	private File file;

	Handler handler = new MyHandler(this);

	static class MyHandler extends Handler { //这个Handler用来接受定时线程给的消息
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity mainActivity) {
                mActivity = new WeakReference<MainActivity>(mainActivity); //添加引用避免内存溢出
        }

        @Override
        public void handleMessage(Message msg) {

                switch (msg.what) {
                case 0x123:
                	 mActivity.get().setPlayerCursor();
                	 mActivity.get().play();
                	 mActivity.get().displayCursorlist();
                        break;
                }
        }
}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);

		db = DbUtils.create(this);
		mCursor = new CursorDate();
		mPlayer = new MediaPlayerControl(this);
		progressDialog = new ProgressDialog(MainActivity.this);
		surfaceView.addView(mPlayer.getMediaPlayer().getSurface());
		// 获取窗口管理器
		WindowManager wManager = this.getWindowManager();
		DisplayMetrics metrics = new DisplayMetrics();

		// 获取屏幕大小
		wManager.getDefaultDisplay().getMetrics(metrics);

		// 设置视频保持纵横比缩放到占满整个屏幕
        //surfaceView.setLayoutParams(new LayoutParams(metrics.widthPixels, MediaPlayer.getVideoHeight()*metrics.widthPixels/MediaPlayer.getVideoWidth()));

		// 设置视频占满个屏幕
		surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels,metrics.widthPixels));

		if(!mCursor.InitCursor(this.getContentResolver()))//未获得数据则开启提取数据线程
		{

		    progressDialog.setTitle("任务执行中");

		    progressDialog.setMessage("正在加载数据,请稍候...");


		    progressDialog.show();

			new Thread()//数据提取
			{
				public void run()
				{

						if(mCursor != null)
						{
							while (mCursor.getCursorFailure())
							{
								try {
									Thread.sleep(2000);
									if(mCursor.getCursor().isClosed())
									{
										return;
									}
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							handler.sendEmptyMessage(0x123);
						}
						if(progressDialog.isShowing())
						{
							progressDialog.dismiss();
						}

						while(mPlayer.getMediaPlayer().isPlaying()){

							try {
								Thread.sleep(10000);
								db.deleteAll(MediaBase.class);
								Thread.sleep(100);
								db.saveBindingIdAll(mCursor.getSetlectList());
							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

				}
			}.start();
		}
		mPlayer.initializePlayer(mCursor);

		if(AppContext.ITEMS.size() == 0)// 初始化菜单
		{
			AppContext.addItem(AppContext.ALL_LIST,getString(R.string.allsong));
			AppContext.addItem(AppContext.SELECT_LIST,getString(R.string.selectedsong));
			AppContext.addItem(AppContext.SEARCH_LIST,getString(R.string.search));
			AppContext.addItem(AppContext.SEARCH_language,getString(R.string.searchlanguage));
			AppContext.addItem(AppContext.SEARCH_Singer,getString(R.string.searchSinger));
			AppContext.addItem(AppContext.SEARCH_Number,getString(R.string.searchNumber));
			AppContext.addItem(AppContext.SEARCH_Style,getString(R.string.searchStyle));
			AppContext.addItem(AppContext.MyRecorder,getString(R.string.MyRecorder));
			AppContext.addItem(AppContext.Others,getString(R.string.OthersMusic));
			//scanSdCard();			
		}



		items = new String[] {getString(R.string.sticky), getString(R.string.del), getString(R.string.playnow)};
		items_Recorder = new String[] {getString(R.string.playnow),getString(R.string.del)};
		language_items = new String[] {getString(R.string.NationalLanguager), getString(R.string.EnglishLanguager), getString(R.string.Cantonese), getString(R.string.Taiwanese)};


		Style_items = new String[] {getString(R.string.Style_popular), getString(R.string.Style_reminiscence), getString(R.string.Style_children),  getString(R.string.Style_dance)};


		simpleadapter = new SimpleCursorAdapter(MainActivity.this, android.R.layout.simple_list_item_checked, mCursor.getCursor(), new String[]{"title"}, new int[]{android.R.id.text1},0);


		try {
			mCursor.setSetlectList(db.findAll(MediaBase.class));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 创建lFragment对象
		ItemFragment itemfragment = new ItemFragment();

		getFragmentManager().beginTransaction()
		.replace(R.id.ItemFragment, itemfragment)
		.commit();  //①

		Preferences = getSharedPreferences(AppContext.LOCAKTV,
				Activity.MODE_PRIVATE);
		editor = Preferences.edit();
		editor.putString(AppContext.Position_KEY, "0").commit();

		timerun = new MyRunnable();

		handler.postDelayed(timerun, timeFrequency);//每两秒执行一次runnable.自动隐藏进度条 	

	}

	public void setPlayerCursor() {
		// TODO Auto-generated method stub
		mPlayer.setCursor(mCursor);
	}

	public class MyRunnable implements Runnable//自动隐藏进度条
	{
		  @Override
		    public void run() {
		        // TODO Auto-generated method stub

		    	if(Seek.getVisibility()==View.VISIBLE)
		    	{
		    		if(SeekTimeUp)
		    		{
		    			Seek.setVisibility(View.GONE);
		    		}
		    		else
		    		{
		    			SeekTimeUp = true;
		    		}
		    	}

		        handler.postDelayed(this, timeFrequency);
		    }
	}

	private void FragmentSideBarVisibie(StringBuffer s)//显示右边滑动菜单
	{
		if(s != null)
		{
			editor.putString(AppContext.SIDEBAR_KEY, s.toString()).commit();
		}
		getFragmentManager().beginTransaction()
		.replace(R.id.SideBarlFragment,  new SideBarlFragment())
		.commit();  //①
		SideBarlFragment.setVisibility(View.VISIBLE);
	}
	public void displayCursorlist()//显示默认列表
	{
		simpleadapter = new SimpleCursorAdapter(MainActivity.this, android.R.layout.simple_list_item_checked, mCursor.getCursor(), new String[]{"title"}, new int[]{android.R.id.text1},CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		filledData(AppContext.DEFAULT_LIST);
	}

	@OnItemClick(R.id.Datalist)//选歌列表子菜单
	public void ItemClick(AdapterView<?> parent, View view,
			int pos, long id) throws  IOException
	{
		positionDatalist = pos;
		int selectmodes = Integer.parseInt(Preferences.getString(AppContext.Position_KEY,"1"));

		switch(selectmodes)
		{
			case AppContext.SELECT_LIST:
				builder = new AlertDialog.Builder(this)
				// 设置对话框标题
				.setTitle(this.getString(R.string.SelectList))
				// 设置图标
				.setIcon(R.drawable.tools)
				// 设置简单的列表项内容
				.setItems(items, new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						L.d(L._FILE_LINE_(), "你选中了《" + items[which] + "》");
						switch(which)
						{
							case 0:      //items "置顶", "删除","立即播放",
								mCursor.MoveToFirst(positionDatalist);
								break;
							case 1:
								mCursor.deleSelect(mCursor.getSetlectList().get(positionDatalist).getTitle().toString());
								break;
							case 2:
								mCursor.MoveToFirst(positionDatalist);
								play();
								break;
						}
						filledData(AppContext.SELECT_LIST);
					}
				});
				// 为AlertDialog.Builder添加【取消】按钮
				setNegativeButton(builder)
					.create()
					.show();
				break;

			case AppContext.DEFAULT_LIST:
			case AppContext.ALL_LIST:
			case AppContext.SEARCH_LIST:
			case AppContext.SEARCH_Singer:
			case AppContext.SEARCH_language:
			case AppContext.SEARCH_Style:
			case  AppContext.Others:

				if(((CheckedTextView)view).isChecked())
			    {
					if(mCursor.insetSelect(positionDatalist,selectmodes))
					{
						Toastshow(this.getString(R.string.addsuccess));
					}

					if(mPlayer.getRandomPlay())//如果正在随机播放
					{
						play();
					}
			    }
				else
				{
					if(mCursor.deleSelect(((TextView)view).getText().toString()))
			    	{
						Toastshow(this.getString(R.string.delsong));
			    	}
				}
				break;
			case AppContext.MyRecorder:
				builder = new AlertDialog.Builder(this)
				// 设置对话框标题
				.setTitle(this.getString(R.string.SelectList))
				// 设置图标
				.setIcon(R.drawable.tools)
				// 设置简单的列表项内容
				.setItems(items_Recorder, new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						L.d(L._FILE_LINE_(), "你选中了《" + items[which] + "》");
						switch(which)
						{
							case 0:
								mPlayer.play(mCursor.getRecorderList().get(positionDatalist).getUrl().toString());
								break;
							case 1:
								mCursor.deleteRecorderFiles(mCursor.getRecorderList().get(positionDatalist).getUrl().toString());
								filledData(AppContext.MyRecorder);
								break;

						}
					}
				});
				// 为AlertDialog.Builder添加【取消】按钮
				setNegativeButton(builder)
					.create()
					.show();
					break;

				default:
					break;
		}


	}

	private void Toastshow(String s)
	{

		if (toast == null)
		{
			toast = Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT);
		}
		else
		{
			toast.setText(s);
		}
		toast.setGravity(Gravity.TOP, 0, 150);
		toast.show();

	}

	private AlertDialog.Builder setPositiveButton(//录制确认
			AlertDialog.Builder builder)
	{
		return builder.setPositiveButton(this.getString(R.string.confirm), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{


				String title = mPlayer.getCurrentPlay().getTitle().toString();
				int i = mPlayer.getCurrentPlay().getUrl().indexOf(title);
				StringBuffer s = new StringBuffer(mPlayer.getCurrentPlay().getUrl().substring(0,i)+"Recorder/");


				file = new File(s.toString());

				s.append("Recorder"+title+".mp4");
				MediaBase rec= new MediaBase("Recorder"+title,s.toString());
				L.d(L._FILE_LINE_(),s.toString());

				MediaRecorder = new FSVMediaRecorder(s);
				MediaRecorder.startRecording();
				mCursor.getRecorderList().add(rec);

				Recordering = !Recordering;
				InfoBarShow();
			}
		});
	}
	private AlertDialog.Builder setNegativeButton(//录制取消
			AlertDialog.Builder builder)
	{
		// 调用setNegativeButton方法添加取消按钮
		return builder.setNegativeButton(this.getString(R.string.cancel), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
	}
	@OnKey({R.id.TableLayout01})
	public boolean onKey(View v, int keyCode, KeyEvent event) {//Ok菜单选项弹出后的操作
		if(event.getAction() == KeyEvent.ACTION_DOWN)
		{

			switch(keyCode)
			{
				case KeyEvent.KEYCODE_ENTER:
				case KeyEvent.KEYCODE_DPAD_CENTER://遥控OK	

					if (mPlayer.getMediaPlayer().isPlaying())
					{
						mPlayer.getMediaPlayer().pause();
					}
					else
					{
						Invisible_all();
						mPlayer.getMediaPlayer().start();
					}
					return true;
				case KeyEvent.KEYCODE_DPAD_UP:	//重新播放
					if (mPlayer != null)
					{
						if(!mPlayer.getMediaPlayer().isPlaying())
						{
							mPlayer.getMediaPlayer().start();
						}
						mPlayer.getMediaPlayer().seekTo(0);
					}
					Invisible_all();
					return true;
				case KeyEvent.KEYCODE_DPAD_DOWN:	//下一曲
					Invisible_all();
					play();
					   return true;
				case KeyEvent.KEYCODE_DPAD_LEFT://音轨切换
					if (mPlayer.getMediaPlayer().isPlaying())
					{
						int ret;

						ret = mPlayer.setAoudioTrack((mPlayer.getMediaPlayer_AUDIO()+1)%2);

						if(ret == 0 )
						{
							Toastshow(this.getString(R.string.AutoModes));
						}
						else if(ret == 1)
						{
							Toastshow(this.getString(R.string.AccompanyModes));
						}
						else
						{
							Toastshow(this.getString(R.string.NoAccompany));
						}


					}
					Invisible_all();
					return true;

				case KeyEvent.KEYCODE_DPAD_RIGHT: 	//录制
					if(!Recordering)
					{
						builder = new AlertDialog.Builder(this).setMessage("是否开始录制");

						setNegativeButton(builder);

						setPositiveButton(builder).create().show();
					}
					else
					{
						MediaRecorder.stopRecording();
						Toastshow("停止录制");
						Recordering = !Recordering;
						InfoBarShow();

					}
					Invisible_all();
					return true;
				case KeyEvent.KEYCODE_MENU://选歌菜单
					ShowMenu();
					return true;

			     default:
			    	 break;
			}

		}
		return false;
	}

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode)
        {
	        case KeyEvent.KEYCODE_ENTER:
	        case KeyEvent.KEYCODE_DPAD_CENTER://遥控OK，显示歌曲操作列表，下一曲，重播...
	        	Invisible_all();
	        	if (TableLayout01.getVisibility() != View.VISIBLE)
	        	{
	        		L.d(L._FILE_LINE_(), "Layout to show");
	        		TableLayout01.setVisibility(View.VISIBLE);
	        		TableLayout01.requestFocus();
	        	}
	        	return false;
	        case KeyEvent.KEYCODE_DPAD_UP:
	        	L.d(L._FILE_LINE_(), "is UP");
	            break;
	        case KeyEvent.KEYCODE_DPAD_DOWN:
	        	L.d(L._FILE_LINE_(), "is DOWN");
	        	if (FrameLayout.getVisibility() != View.VISIBLE && TableLayout01.getVisibility() != View.VISIBLE && !progressDialog.isShowing())
	        	{
	        		play();
	        		return true;
	        	}
	            break;
	        case KeyEvent.KEYCODE_DPAD_LEFT: //快退
	        		if(FrameLayout.getVisibility() != View.VISIBLE && TableLayout01.getVisibility() != View.VISIBLE
	        			&&  mPlayer.getMediaPlayer().isPlaying())
		        	{
	        			SeekMove(false);
		        	}
	        	L.d(L._FILE_LINE_(), "is LEFT");

	            break;
	        case KeyEvent.KEYCODE_DPAD_RIGHT:  //快进
	        	L.d(L._FILE_LINE_(), "is RIGHT");

	        	if(FrameLayout.getVisibility() == View.VISIBLE )
	        	{
	        		if(Datalist.isFocused()){
			        	switch(Integer.parseInt(Preferences.getString(AppContext.Position_KEY,"1")))
			        	{
				        	case AppContext.SEARCH_LIST:
				        	case AppContext.SEARCH_Singer:
				        		filledData(AppContext.SEARCH_LIST);
								break;
							default:
								break;
			        	}
	        		}
	        	}
	        	else if (TableLayout01.getVisibility() != View.VISIBLE &&  !progressDialog.isShowing() && mPlayer.getMediaPlayer().isPlaying())
	        	{
	        		SeekMove(true);
	        	}

	            break;
	        case KeyEvent.KEYCODE_MENU: //选歌菜单
	        	L.d(L._FILE_LINE_(), "is MENU");
	        	ShowMenu();

	        	return true;

	        case KeyEvent.KEYCODE_BACK:   // 退出
	        	if( Invisible_all() ){
	        		return true;
	        	}

	        	 long currentTime = new Date().getTime();
	             // 如果时间间隔大于2秒, 不处理
	             if ((currentTime - preTime) > 2000) {

	            	 Toastshow(this.getString(R.string.inputESC));
	                 preTime = currentTime;
	                 // 截获事件,不再处理
	                 return true;
	             }
	             break;


	        default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnProgressChanged(R.id.SeekBar)//进度条改变
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        // TODO Auto-generated method stub
    	if(progress != 0)
    	{
    		mPlayer.getMediaPlayer().seekTo(progress);
    	}
    }
    private void SeekMove(boolean flag) //改变播放进度
    {
    	int i;
    	SeekTimeUp = false;

    	Seek.setVisibility(View.VISIBLE);
    	Seek.setProgress(0);
     	Seek.setMax(mPlayer.getMediaPlayer().getDuration());

    	if(flag)
    	{
    		L.d(L._FILE_LINE_(), String.valueOf(mPlayer.getMediaPlayer().getCurrentPosition()));

    		 i = mPlayer.getMediaPlayer().getCurrentPosition() <
    				 mPlayer.getMediaPlayer().getDuration()-Seekoffset ?
    						 mPlayer.getMediaPlayer().getCurrentPosition()+
    						 	Seekoffset:mPlayer.getMediaPlayer().getCurrentPosition();
    	}
    	else
    	{
    		L.d(L._FILE_LINE_(), String.valueOf(mPlayer.getMediaPlayer().getCurrentPosition()));

    		 i = mPlayer.getMediaPlayer().getCurrentPosition()>Seekoffset?mPlayer.getMediaPlayer().getCurrentPosition()-Seekoffset:0;
    	}
		Seek.setMax(mPlayer.getMediaPlayer().getDuration());
		Seek.setProgress(i);
    }



	private boolean Invisible_all()
	{
		boolean ret = false;
		if (TableLayout01.getVisibility() == View.VISIBLE)
    	{
    		TableLayout01.setVisibility(View.GONE);
    		ret = true;
    	}

    	if (FrameLayout.getVisibility() == View.VISIBLE)
    	{
    		FrameLayout.setVisibility(View.GONE);
    		ret = true;
    	}
    	if(Seek.getVisibility() == View.VISIBLE)
    	{
    		Seek.setVisibility(View.GONE);
    		ret = true;
    	}

    	if(HintFrameLayout.getVisibility() != View.VISIBLE)
    	{
			InfoBarShow();
			HintFrameLayout.setVisibility(View.VISIBLE);
    	}

    	return ret;
	}



	private void ShowMenu()
	{
		if (FrameLayout.getVisibility() != View.VISIBLE)
    	{	Invisible_all();
    		L.d(L._FILE_LINE_(), "MENU to show");
    		FrameLayout.setVisibility(View.VISIBLE);

    		if(HintFrameLayout.getVisibility() == View.VISIBLE)
        	{
        		HintFrameLayout.setVisibility(View.GONE);
        	}

    		Preferences.edit().putString(AppContext.Position_KEY, "1").commit();

    		 filledData(Integer.parseInt(Preferences.getString(AppContext.Position_KEY,"1") )) ;

    	}
    	else
    	{
    		Invisible_all();
    	}

	}

	 //填充List数据
	public void filledData(int id)
	{

		if(mCursor.getCursor()!=null && mCursor.getCursor().getCount() != 0)
		{
			switch(id)
			{

				case AppContext.DEFAULT_LIST:
					SearchFrame.setVisibility(View.INVISIBLE);
					FragmentSideBarVisibie(mCursor.getFirstcharStrbuf());

					Datalist.setAdapter(simpleadapter);
					Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

			        editor.putString(AppContext.Position_KEY, AppContext.DEFAULT_LIST+"").commit();

					break;

				case AppContext.ALL_LIST:
					SearchFrame.setVisibility(View.INVISIBLE);
					FragmentSideBarVisibie(mCursor.getFirstcharStrbuf(0));

					Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked, mCursor.getAllList()));
					Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


					break;

				case AppContext.SELECT_LIST:
					SearchFrame.setVisibility(View.INVISIBLE);
					SideBarlFragment.setVisibility(View.INVISIBLE);

					Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_single_choice, mCursor.getSetlectList()));
					Datalist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				 break;

				case AppContext.SEARCH_LIST:
				case AppContext.SEARCH_Singer:
					SearchFrame.setVisibility(View.VISIBLE);
					SideBarlFragment.setVisibility(View.INVISIBLE);

					getFragmentManager().beginTransaction()
					.replace(R.id.SearchFrameLayout, new KeyboardFragment())
					.commit();

					Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked, mCursor.getAllList()));
					Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					mCursor.filterDateList.clear();

					break;

				case AppContext.SEARCH_language:
					SearchFrame.setVisibility(View.INVISIBLE);


					builder = new AlertDialog.Builder(this)
					// 设置对话框标题
					.setTitle(this.getString(R.string.searchlanguage))
					// 设置图标
					.setIcon(R.drawable.tools)
					// 设置简单的列表项内容
					.setItems(language_items, new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							filterData_language(which);
						}
					});
					// 为AlertDialog.Builder添加【取消】按钮
					setNegativeButton(builder)
						.create()
						.show();
					break;
				case AppContext.SEARCH_Number:
					SearchFrame.setVisibility(View.INVISIBLE);
					Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked, mCursor.getAllList()));
					Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					mCursor.filterDateList.clear();
					break;
				case AppContext.SEARCH_Style:
					SearchFrame.setVisibility(View.INVISIBLE);

					builder = new AlertDialog.Builder(this)
					// 设置对话框标题
					.setTitle(this.getString(R.string.searchStyle))
					// 设置图标
					.setIcon(R.drawable.tools)
					// 设置简单的列表项内容
					.setItems(Style_items, new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							filterData_Style(which);
						}

					});
					// 为AlertDialog.Builder添加【取消】按钮
					setNegativeButton(builder)
						.create()
						.show();
					break;

				case AppContext.MyRecorder:
					SearchFrame.setVisibility(View.INVISIBLE);
					SideBarlFragment.setVisibility(View.INVISIBLE);

					Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked, mCursor.getRecorderList()));
					Datalist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

					break;

				case AppContext.Others:
					SearchFrame.setVisibility(View.VISIBLE);
					SideBarlFragment.setVisibility(View.INVISIBLE);

					getFragmentManager().beginTransaction()
					.replace(R.id.SearchFrameLayout, new KeyboardFragment())
					.commit();

					Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked, mCursor.getOtherList()));
					Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					mCursor.filterDateList.clear();
					break;
		 	   default:
		 		   break;

			}
			Datalist.requestFocus();
		}

}
	//当前播放歌曲，下一曲
	public void InfoBarShow()
	{
		if(mCursor.getSetlectList()!=null && !mCursor.getSetlectList().isEmpty())
		{
			editor.putString(AppContext.Next_KEY,mCursor.getSetlectList().get(0).getTitle().toString());
		}
		else
		{
			editor.putString(AppContext.Next_KEY,this.getString(R.string.END));
		}
		editor.putBoolean(AppContext.Rec_KEY, Recordering);
		if(mPlayer.getCurrentPlay().getTitle() != null){

			editor.putString(AppContext.Current_KEY,mPlayer.getCurrentPlay().getTitle().toString());
		}

		editor.commit();

		if (FrameLayout.getVisibility() != View.VISIBLE) //from=0, to="VISIBLE
    	{
			HintFragment Hintfragment = new HintFragment();
			getFragmentManager().beginTransaction()
			.replace(R.id.HintFragment, Hintfragment)
			.commitAllowingStateLoss();  //①
    	}
	}

	//根据输入框筛选列表
	@Override
	public void TextChangedListener(String str){
		//当输入框里面的值为空，更新为原来的列表，否则过滤数据列表	
		int Position_key = Integer.parseInt(Preferences.getString(AppContext.Position_KEY,"3"));

		Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked,mCursor.filterData(str,Position_key)));
		Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	@Override
	public void onItemSelected(int id)
	{
		filledData(id);
		L.d(L._FILE_LINE_(), "Preferences :"+Preferences.getString(AppContext.Position_KEY,"1"));
	}


	//右侧快速选择栏被点击事件
	@Override
	public void onSidebarSelected(char c)
	{

		int pos =-1;
		int witch = Integer.parseInt(Preferences.getString(AppContext.Position_KEY,"1"));



		switch(witch)
    	{
        	case AppContext.DEFAULT_LIST:
        		pos = mCursor.getPositionByFirstchar(c);
				break;
        	case AppContext.SEARCH_Number:
        		if( c < 'A' || c > 'Z')
        		{
        			filterData_Number(c);
        		}
        		else
        		{
        			mCursor.getPositionByFirstchar(c,mCursor.filterDateList);
        		}
        	case AppContext.ALL_LIST:
        		pos = mCursor.getPositionByFirstchar(c,mCursor.getAllList());
				break;
        	case AppContext.SEARCH_language:
        	case AppContext.SEARCH_Style:
        		pos = mCursor.getPositionByFirstchar(c,mCursor.filterDateList);
				break;
			default:
				break;
    	}

		if(pos > -1 ){
			Datalist.setSelection(pos);
			Datalist.requestFocus();
		}
	}

	//通过语言搜索
	public List<MediaBase> filterData_language(int which)
	{

		mCursor.filterDateList.clear();
		StringBuffer key;
		for(MediaBase Base : mCursor.getAllList()){
			key = Base.getLanguage();

			if(key != null && key.indexOf(AppContext.language_type[which]) != -1 ){
				mCursor.filterDateList.add(Base);
			}
		}
		Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked, mCursor.filterDateList));
		Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Datalist.requestFocus();
		FragmentSideBarVisibie( mCursor.getFirstcharArray(mCursor.filterDateList));

		return mCursor.filterDateList;
	}

	//通过类型搜索
	public List<MediaBase> filterData_Style(int which)
	{

		mCursor.filterDateList.clear();
		StringBuffer key;
		for(MediaBase Base : mCursor.getAllList()){
			key = Base.getStyle();

			if(key != null && key.indexOf(AppContext.Style_type[which]) != -1 ){
				mCursor.filterDateList.add(Base);
			}
		}
		Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked, mCursor.filterDateList));
		Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Datalist.requestFocus();
		FragmentSideBarVisibie( mCursor.getFirstcharArray(mCursor.filterDateList));

		return mCursor.filterDateList;
	}

	//通过字数搜索
	public List<MediaBase> filterData_Number(char c)
	{
		StringBuffer s = new StringBuffer("123456789");
		mCursor.filterDateList.clear();
		StringBuffer key;
		for(MediaBase Base : mCursor.getAllList()){
			key = Base.getNumber();

			if(key != null && key.indexOf(String.valueOf(c)) != -1 ){
				mCursor.filterDateList.add(Base);
			}
		}

		s.append(mCursor.getFirstcharArray(mCursor.filterDateList));
		Datalist.setAdapter(new ArrayAdapter<MediaBase>(this, android.R.layout.simple_list_item_checked, mCursor.filterDateList));
		Datalist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		FragmentSideBarVisibie(s);
		Datalist.requestFocus();
		return mCursor.filterDateList;
	}


	private void scanSdCard(){
        IntentFilter intentfilter = new IntentFilter( Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentfilter.addDataScheme("file");
        L.d(L._FILE_LINE_(),Environment.getExternalStorageDirectory().getAbsolutePath());
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + "/storage")));
        }

	private void play()
	{
		mPlayer.play("");
		InfoBarShow();
	}
	private void MPlayerstop()
	{
		// 保存选歌列表		
		mPlayer.stop();
		if(MediaRecorder != null && MediaRecorder.getState()){
			MediaRecorder.stopRecording();
		}
		handler.removeCallbacks(timerun);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		filledData(AppContext.DEFAULT_LIST);
	}
	protected void onPause()
	{
		if (mPlayer.getMediaPlayer().isPlaying())
		{
			MPlayerstop();
		}
		super.onPause();
	}
	@Override
	protected void onStop()
	{
		if (mPlayer.getMediaPlayer().isPlaying())
		{
			MPlayerstop();
		}

		mCursor.release();

		super.onStop();
	}
	@Override
	protected void onDestroy()
	{
		// 停止播放
		if (mPlayer.getMediaPlayer().isPlaying())
		{
			MPlayerstop();
		}
		mCursor.release();
		// 释放资源
		mPlayer.release();
		super.onDestroy();
	}

}