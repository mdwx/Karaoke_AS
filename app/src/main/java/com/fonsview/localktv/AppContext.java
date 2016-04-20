package com.fonsview.localktv;

import android.app.Application;

import com.fonsview.localktv.model.ItemContent;

import java.util.ArrayList;
import java.util.List;

public class AppContext extends Application {
	
	private static AppContext instance;//用于传体Application对象
	
	public static List<ItemContent> ITEMS = new ArrayList<ItemContent>();	//初始化菜单
	
	//歌曲搜索方式
	public static final int DEFAULT_LIST = 0;// 0默认数据
	public static final int ALL_LIST = 1;    //1自定义排序列表
	public static final int SELECT_LIST = 2; //2已选歌列表
	public static final int SEARCH_LIST = 3; //3模糊搜索列表
	
	public static final int SEARCH_language = 4; //语种搜索
	public static final int SEARCH_Singer = 5; //歌手搜索
	public static final int SEARCH_Number = 6; //字数搜索
	public static final int SEARCH_Style = 7; //风格搜索     
	public static final int MyRecorder = 8; //我的录音
	public static final int  Others = 9; //其他音乐
	
	public static final int NationalLanguager = 0; //国语歌曲 
	public static final int EnglishLanguager = 1; //英语歌曲 
	public static final int Cantonese = 2; //粤语歌曲
	public static final int Taiwanese = 3; // 台语歌曲  
	
	
	public static final int Style_popular = 0; //流行歌曲
	public static final int Style_reminiscence = 1; //怀旧歌曲
	public static final int Style_children = 2; //儿歌
	public static final int Style_dance = 3; // 舞曲
	
	//for SharedPreferences
	public static final String LOCAKTV = "Locaktv"; //SharedPreferences
	public static final String SIDEBAR_KEY = "CharData";//右侧字符串集A-Z
	public static final String Position_KEY = "SelectPosition";//当前选择的列表，DEFAULT_LIST,ALL_LIST，SELECT_LIST，SEARCH_LIST。。。。。
	public static final String Next_KEY = "NextHint";//下一曲提示
	public static final String Current_KEY = "CurrentHint";//当前歌曲提示
	public static final String Rec_KEY = "Rec";//是否处于录制状态
	
	public static final String[] language_type = new String[]{"n","e","c","t"};//national,english,Cantonese,Taiwanese
	public static final String[] Style_type = new String[] {"p","r","c","d"};//popular,reminiscence,children,dance
		
			
		public static AppContext getInstance() {//获取单例
		    return AppContext.instance;
		} 
		@Override
		public void onCreate() {
		    // TODO Auto-generated method stub
		    super.onCreate();
		    instance = this;
		}		
		
		public static void addItem(int Key ,String Title)//添加菜单
		{
			ITEMS.add(new ItemContent(Key,Title));		
		}

}
