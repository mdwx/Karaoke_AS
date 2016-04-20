package com.fonsview.localktv.dataSources;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.fonsview.localktv.AppContext;
import com.fonsview.localktv.model.MediaBase;
import com.fonsview.localktv.tool.L;
import com.fonsview.localktv.tool.SpellUtil;
import com.fonsview.localktv.tool.xmlparser.PullParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/** 
 * @author Vince  E-mail: xhys01@163.com
 * @version 创建时间：2015-5-26 上午9:01:26 
 * 类说明 
 */
public class CursorDate implements InterfaceCursorDate  {
	
	//媒体数据容器
	private Cursor mCursor; //系统搜索视频数据
	private Cursor mRecorderCursor;//自定义录制数据
	private Cursor mCursorOther; //其他媒体数据
	//首字符数组
	private StringBuffer FirstcharStrbuf;	

	private List<MediaBase> SetlectList;
	private List<MediaBase> AllList;
	private List<MediaBase> XML_List;
	private List<MediaBase> RecorderList;	
	private List<MediaBase> OtherList;
	private ExecutorService cachedThreadPool = null;
	private CountDownLatch Latch_Cursor;
	private CountDownLatch Latch_AllList; 
	private CountDownLatch Latch_XML; 
	private CountDownLatch LatchRecorderList; 
	private CountDownLatch Latch_OtherList; 
	public List<MediaBase> filterDateList;	
	private boolean Firstcharsearch = false;
	private boolean Alllistsearch = false;
	private boolean OtherListsearch = false;
	
	public CursorDate() {
		// TODO Auto-generated constructor stub
		if(cachedThreadPool == null){
			cachedThreadPool = ThreadPool.getInstance().getPool();			
		}
	}
	
	
	private void QueryCursor(ContentResolver Content)
	{
	  /*mCursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Video.Media.TITLE+" LIKE '%-%'"
		,null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);		//寻找title中有-的文件*/	
	
		mCursor = Content.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Video.Media.TITLE+" NOT LIKE 'Recorder%'",null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#QueryRecorderCursor(android.content.ContentResolver)
	 */
	@Override
	public void QueryRecorderCursor(ContentResolver Content)
	{		
		mRecorderCursor = Content.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Video.Media.TITLE+" LIKE 'Recorder%'",null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#QueryOtherCursor(android.content.ContentResolver)
	 */
	@Override
	public void QueryOtherCursor(ContentResolver Content)
	{		
		mCursorOther = Content.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.TITLE+" NOT LIKE 'Recorder%'",null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#InitCursor(android.content.ContentResolver)
	 */
	@Override
	public Boolean InitCursor(ContentResolver Content)
	{
		Boolean ret = true;
		if (Content != null){		
			//获取媒体数据
			
			QueryCursor(Content);
			QueryRecorderCursor(Content);
			QueryOtherCursor(Content);
			
			if(mCursor == null || mCursor.getCount() == 0)
			{				
				ret = false;		
				Latch_Cursor = new CountDownLatch(1);
				ThreadPoolexecute(new get_Cursor(Content)); //获取数据			
			}
			else
			{
				Latch_Cursor = new CountDownLatch(0);
			}
			
			SetlectList = new ArrayList<MediaBase>();
			AllList = new ArrayList<MediaBase>();
			OtherList = new ArrayList<MediaBase>();
			XML_List = new ArrayList<MediaBase>();
			filterDateList = new ArrayList<MediaBase>();		
			RecorderList = new ArrayList<MediaBase>();				
						
			ListTransition();
			
			return ret;
		}
		else
		{
			return false;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getPositionByFirstchar(char)
	 */
	@Override
	public int getPositionByFirstchar(char c)
	{
		char chr;
		for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext())
		{
			chr = SpellUtil.getSpellToUpperCase((mCursor
	                .getString(mCursor
		               		 .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))).substring(0,1)).charAt(0);
			//#代表以特殊字符开头的数据
			if((chr == c) || (c == '#' && (chr < 'A' || chr > 'z')))
			{
				return mCursor.getPosition();				
			}			
			
		}
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getPositionByFirstchar(char, java.util.List)
	 */
	@Override
	public int getPositionByFirstchar(char c,List<MediaBase> List)
	{
		int low = 0;
    	int high = List.size();    	
    	int guess;
    	
				
	
    	if(List == null || List.size() == 0)
    	{
    		return -1;
    	}  
    	
    	if(c == '#')
		{
			return 0;
		}

        while (high - low > 1) {
            guess = ((high - low)>>1)  + low;
            
            L.d(SpellUtil.getSpellToUpperCase(List.get(guess).getTitle().substring(0,1)).charAt(0)+"" + guess);
            if (SpellUtil.getSpellToUpperCase(List.get(guess).getTitle().substring(0,1)).charAt(0) < c)
            {
                low = guess;
            
            }
            else
            {
                high = guess;
            }
        }

        if (high != List.size() && SpellUtil.getSpellToUpperCase(List.get(high).getTitle().substring(0,1)).charAt(0) == c)
        {
        	 return high;
        }
        else
        {
        	return -1;		
        } 
	}
	

	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getFirstcharArray()
	 */
	@Override
	public StringBuffer getFirstcharArray()
	{	
		StringBuffer buf = new StringBuffer("#");
		int[] char_Array = new int[26];
		char chr;
		
		if(mCursor != null && !mCursor.isClosed())
		{
			for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext())
			{			
				//检测标题的首之母，支持中文
					  
				chr = SpellUtil.getSpellToUpperCase(mCursor
						.getString(mCursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)).substring(0,1)).charAt(0);
				
				//防止重复字符
				if(chr>='A' && chr <= 'Z')
				{
					char_Array[(int)chr - 65] = 1;	
				}	
			}	
		
		
			for(int i=0;i<char_Array.length; i++)
			{
				if(char_Array[i] == 1)
				{
					buf.append((char)(i+65));
				}			
			}
		}
		return buf;
	}
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getFirstcharArray(java.util.List)
	 */
	@Override
	public StringBuffer getFirstcharArray(List<MediaBase> list)
	{	
		StringBuffer buf = new StringBuffer("#");
		int[] char_Array = new int[26];
		char chr;	 
			
		 try {			
				Latch_AllList.await(); 				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 
		 
		for(MediaBase base:list)
		{			
			//检测标题的首之母，支持中文
				  
			chr = SpellUtil.getSpellToUpperCase(base.getTitle().toString().substring(0,1)).charAt(0);
			
			//防止重复字符
			if(chr>='A' && chr <= 'Z')
			{
				char_Array[(int)chr - 65] = 1;	
			}	
		}		
		
		for(int i=0;i<char_Array.length; i++)
		{
			if(char_Array[i] == 1)
			{
				buf.append((char)(i+65));
			}			
		}
		return buf;
	}
	
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getColumnTitle()
	 */
	@Override
	public String getColumnTitle()
	{
		if(mCursor != null )
		{
			return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
		}
		return null;		
	}

	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getColumnUrl()
	 */
	@Override
	public String getColumnUrl()
	{
		if(mCursor != null )
		{
			return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
		}
		return null;		
	}

	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getNextColumnUrl()
	 */
	@Override
	public String getNextColumnUrl()
	{
		if(mCursor.moveToNext())
		{
			return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
		}
		else if(mCursor.moveToFirst())
		{
			return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
		}
		else
		{
			return null;
		}
		
	}
	

	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#insetSelect(int, int)
	 */
	@Override
	public boolean insetSelect(int position ,int selectmod)
	{		
		if(selectmod == AppContext.DEFAULT_LIST)
		{
			return SetlectList.add(new MediaBase(getColumnTitle(), getColumnUrl()));
		}
		else if(selectmod == AppContext.Others)
		{
			if(!filterDateList.isEmpty())
			{
				return SetlectList.add(filterDateList.get(position));	
			}
			else
			{
				return SetlectList.add(OtherList.get(position));	
			}			
		}
		else if(selectmod != AppContext.ALL_LIST && !filterDateList.isEmpty())
		{
			return SetlectList.add(filterDateList.get(position));	
		}		
		else 
		{
			return SetlectList.add(AllList.get(position));	
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#deleSelect(java.lang.String)
	 */
	@Override
	public boolean deleSelect(String Title)
	{
		Iterator<MediaBase> list = SetlectList.iterator();
		
		while(list.hasNext())
		{
			if(list.next().getTitle().toString().equals(Title))
			{
				list.remove();
			}
		}		
		
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#MoveToFirst(int)
	 */
	@Override
	public boolean MoveToFirst(int id)
	{
		MediaBase M = SetlectList.get(id);	
		deleSelect(M.getTitle().toString());
		SetlectList.add(0, M);		
		return true;	
	}
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getFirstcharStrbuf()
	 */
	@Override
	public StringBuffer getFirstcharStrbuf()
	{
		return FirstcharStrbuf;
	}
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getFirstcharStrbuf(int)
	 */
	@Override
	public StringBuffer getFirstcharStrbuf(int pos)
	{
		if(Firstcharsearch)
		{
			getFirst_char();
		}
		
		return FirstcharStrbuf;
	}


	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#isempty()
	 */
	@Override
	public boolean isempty() {
		return (mCursor == null);
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getmCursor()
	 */
	@Override
	public Cursor getCursor() {
		return mCursor;
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getCursorFailure()
	 */
	@Override
	public boolean getCursorFailure() {
		return (mCursor == null || mCursor.getCount() == 0) && (mRecorderCursor == null || mRecorderCursor.getCount() == 0);
	}


	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#setFirstcharStrbuf(java.lang.StringBuffer)
	 */
	@Override
	public void setFirstcharStrbuf(StringBuffer firstcharStrbuf) {
		FirstcharStrbuf = firstcharStrbuf;
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#setmCursor(android.database.Cursor)
	 */
	@Override
	public void setmCursor(Cursor mCursor) {
		this.mCursor = mCursor;
	}

	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getSetlectList()
	 */
	@Override
	public List<MediaBase> getSetlectList() {
		return SetlectList;
	}
	

	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getAllList()
	 */
	@Override
	public List<MediaBase> getAllList() {

		if(Alllistsearch && AllList.size() == 0)
		{
			Transition_ALLlist();
			Self_Improve();
			Transition_Keyword();
		}
		
		return AllList;
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getOtherList()
	 */
	@Override
	public List<MediaBase> getOtherList() {
		
		if(OtherListsearch && OtherList.size() == 0)
		{
			Transition_Otherlist();
			ThreadPoolexecute(new TranOtherKeyword(OtherList)); 
		}
		
		
		return OtherList;
	}

	

	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#setSetlectList(java.util.List)
	 */
	@Override
	public void setSetlectList(List<MediaBase> setlectList) {
		
		if(setlectList != null)
		{
			this.SetlectList = setlectList;
		}
		
	}

	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#ListTransition()
	 */
	@Override
	public void ListTransition()
	{
		Transition_ALLlist();			
		getFirst_char();
		Transition_XML();		
		XMLToALLLIST();		
		Self_Improve();
		Transition_Keyword();
		Recorder_List();
		Transition_Otherlist();
	}
	
	
	/********************************************************
	*ALLlist的排序
	*
	*<p>category: com.example.locaktv.DataSources</p>
	*<p>Description: TODO</p>
	*@version: 2015-6-16下午2:06:50	
	*<p>return_type: void</p>
	*
	*/
	private void Transition_ALLlist()
	{
		Alllistsearch = false;
		Latch_AllList = new CountDownLatch(1);		
		ThreadPoolexecute(new Transition(mCursor,AllList));
		
	}
	
	
	private void Transition_Otherlist()
	{
		Latch_OtherList = new CountDownLatch(1);
		OtherListsearch = false;		
		ThreadPoolexecute(new TransitionOther(OtherList));		
	}
	
	
	
	/********************************************************
	*XML数据获取
	*
	*<p>category: com.example.locaktv.DataSources</p>
	*<p>Description: TODO</p>
	*@version: 2015-6-16下午2:07:02	
	*<p>return_type: void</p>
	*
	*/
	private void Transition_XML()
	{
		Latch_XML = new CountDownLatch(1);		
		ThreadPoolexecute(new XMLTransition("index.xml",MediaBase.class.getName(), XML_List));
	}
	
	/********************************************************
	*XML填充完善ALL数据
	*
	*<p>category: com.example.locaktv.DataSources</p>
	*<p>Description: TODO</p>
	*@version: 2015-6-16下午2:07:14	
	*<p>return_type: void</p>
	*
	*/
	private void XMLToALLLIST()
	{	
       ThreadPoolexecute(new DataFill(XML_List,AllList)); 
	}
	
	private void Self_Improve()
	{	
       ThreadPoolexecute(new SelfImprove(AllList)); 
	}
	
	private void Transition_Keyword()
	{	
       ThreadPoolexecute(new TransitionKeyword(AllList)); 
	}
	
	private void getFirst_char()
	{	
	   Firstcharsearch = false; 
       ThreadPoolexecute(new getFirstchar()); 
	}
	
	private void Recorder_List()
	{
		LatchRecorderList = new CountDownLatch(1);
       ThreadPoolexecute(new RecorderList(RecorderList)); 
	}	
	
	
	public class get_Cursor implements Runnable { //获取数据Cursor

		private ContentResolver content;
	
		
		public get_Cursor(ContentResolver Content) {
			// TODO Auto-generated constructor stub
			this.content = Content;
		}

		public void run() {
			// TODO Auto-generated method stub
			try {				
			
				while(getCursorFailure())
				{	
				
					Thread.sleep(3000);
					if(getThreadPool().isShutdown())
					{
						return;
					}
					L.d(L._FILE_LINE_(), "Cursor querying!!");
					QueryCursor(content);
					QueryRecorderCursor(content);
					QueryOtherCursor(content);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				L.d(L._FILE_LINE_(), "Cursor is finish!!");
				Latch_Cursor.countDown();
			}
		}
	}
	public class Transition implements Runnable { //将Cursor数据转ArrayList,这个list用于自定义排序以及过滤

		private List<MediaBase> list ;			
		private int Count;
		private StringBuffer title;	
		public Transition(Cursor cursor, List<MediaBase> list) {		// TODO Auto-generated constructor stub
		
			this.list = list;		
			this.title = new StringBuffer("");
		}

		public void run() {
			// TODO Auto-generated method stub
			 try {
					Latch_Cursor.await();
					while(mCursor != null && mCursor.getCount() == 0)
					{
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			list.clear();			
			Count = mCursor.getCount();
			L.d(L._FILE_LINE_(),"Count is" + String.valueOf(Count));
			for(int i=0; i<Count; i++) 
			{
				MediaBase arg = new MediaBase();							
				mCursor.moveToPosition(i);
				arg.setTitle(mCursor
		                .getString(mCursor
			               		 .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
				while(title.toString().equals(arg.getTitle().toString()) && i<Count-1)
				{
					i++;
					mCursor.moveToPosition(i);
					arg.setTitle(mCursor
			                .getString(mCursor
				               		 .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
				}
				title = arg.getTitle();
								
				arg.setKeyword(title.toString());			
				
				arg.setUrl(mCursor
		                .getString(mCursor
			               		 .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));		
				list.add(arg);				
			}	
			Collections.sort(list);			
			
			Alllistsearch = true;
			L.d(L._FILE_LINE_(), "Transition is finish");
			Latch_AllList.countDown();
		}		
	}
	public class TransitionOther implements Runnable { //将Cursor数据转ArrayList,这个list用于自定义排序以及过滤

		private List<MediaBase> list ;			
		private int Count;
		private StringBuffer title;	
		public TransitionOther(List<MediaBase> list) {		// TODO Auto-generated constructor stub
		
			this.list = list;		
			this.title = new StringBuffer("");
		}

		public void run() {
			// TODO Auto-generated method stub
			 try {
					Latch_Cursor.await();
					if(mCursorOther != null && mCursorOther.getCount() == 0)
					{
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			list.clear();			
			Count = mCursorOther.getCount();
			L.d(L._FILE_LINE_(),"Count is" + String.valueOf(Count));
			
			for(int i=0; i<Count; i++) 
			{
				MediaBase arg = new MediaBase();							
				mCursorOther.moveToPosition(i);
				arg.setTitle(mCursorOther
		                .getString(mCursorOther
			               		 .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
				while(title.toString().equals(arg.getTitle().toString()) && i<Count-1)
				{
					i++;
					mCursorOther.moveToPosition(i);
					arg.setTitle(mCursorOther
			                .getString(mCursorOther
				               		 .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
				}
				title = arg.getTitle();
								
				arg.setKeyword(title.toString());			
				
				arg.setUrl(mCursorOther
		                .getString(mCursorOther
			               		 .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));		
				list.add(arg);				
			}	
			Collections.sort(list);			
			Latch_OtherList.countDown();
			OtherListsearch = true;
			L.d(L._FILE_LINE_(), "Transition is finish");
		
		}		
	}
	
	public class TransitionKeyword implements Runnable { //将ArrayList的Keyword获取

		private List<MediaBase> alllist ;

		public TransitionKeyword(List<MediaBase> list) {
			// TODO Auto-generated constructor stub

			this.alllist = list;			
		}

		public void run() {
			// TODO Auto-generated method stub
			 try {
					Latch_AllList.await();			
					Alllistsearch = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			for(MediaBase Base: alllist)
			{
				
				Base.setKeyword(SpellUtil.getSpellAcronymToLowerCase(Base.getTitle().toString()));				
			}
			Collections.sort(alllist);
			L.d(L._FILE_LINE_(), "TransitionKeyword is finish");
			Alllistsearch = true;
		}
	}
	
	public class TranOtherKeyword implements Runnable { //将ArrayList的Keyword获取

		private List<MediaBase> alllist ;

		public TranOtherKeyword(List<MediaBase> list) {
			// TODO Auto-generated constructor stub

			this.alllist = list;			
		}

		public void run() {
			// TODO Auto-generated method stub
			 try {
				 Latch_OtherList.await();			
				 OtherListsearch = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			for(MediaBase Base: alllist)
			{
				
				Base.setKeyword(SpellUtil.getSpellAcronymToLowerCase(Base.getTitle().toString()));				
			}
			Collections.sort(alllist);			
			OtherListsearch = true;
		}
	}
	public class getFirstchar implements Runnable { //将ArrayList的Keyword获取
		public getFirstchar() {
			// TODO Auto-generated constructor stub		
		}

		public void run() {
			// TODO Auto-generated method stub
			 try {
				 Latch_Cursor.await();				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			 setFirstcharStrbuf(getFirstcharArray());
			 Firstcharsearch = true;
			
		}
	}
	
	public class XMLTransition implements Runnable { //将XML数据转ArrayList

		private String xml;
		private String models;
		private List<MediaBase> list;
		
		public XMLTransition(String xml,String models,List<MediaBase> XML_List) {
			// TODO Auto-generated constructor stub
			this.xml = xml;
			this.models = models;		
			this.list = XML_List;
		}

		public void run() {
			// TODO Auto-generated method stub
			InputStream is = null;
			byte[] buffer = com.fonsview.localktv.tool.SDCardUtils.OpenFile(xml);
			
			if(buffer != null)
			{
				is = new ByteArrayInputStream(buffer);
			}
			
			L.d(L._FILE_LINE_(), "OpenFile is finish");
			try 
			{   
				if(is != null)
				{
					PullParser<MediaBase> parser = new PullParser<MediaBase>(models);  						
					List<MediaBase> list_T = parser.parse(is);
					
					for(MediaBase base : list_T)
					{
					//	L.d(L._FILE_LINE_(), base.toString());
						base.setKeyword(SpellUtil.getSpellAcronymToLowerCase(base.getTitle().toString()));
						list.add(base);
					}					
					Collections.sort(list);	
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				L.d(L._FILE_LINE_(), "XMLTransition is finish");
				Latch_XML.countDown();
			}		
		}
	}
	
	public class DataFill implements Runnable { //将XML数据填充ALLList

		private List<MediaBase> xmllist;	
		private List<MediaBase> alllist;
		
		public DataFill(List<MediaBase> XML_List,List<MediaBase> AllList) {
			// TODO Auto-generated constructor stub
			this.xmllist = XML_List;
			this.alllist = AllList;				
		}

		public void run() {
			// TODO Auto-generated method stub
			
			 try {
					Latch_XML.await();
					Latch_AllList.await(); 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			 
			int allsize = alllist.size();
			int xmlsize = xmllist.size();		
			int i = 0;
			int j = 0;
			
			
			if(xmlsize <= 0)
			{			
				L.d(L._FILE_LINE_(), "DataFill is finish");	
				return;
			}
			
			
			while(i<allsize && j<xmlsize)
			{
				if(alllist.get(i).compareTo(xmllist.get(j)) == 0)
				{					
					if(alllist.get(i).getKeyword() != null && xmllist.get(j).getKeyword() != null)
					{
						alllist.get(i).SetMember(xmllist.get(j));
					}
					
					i++;
					j++;
					
				}
				while(j<xmlsize && alllist.get(i).compareTo(xmllist.get(j)) > 0)
				{
					j++;	
				}
				while(j<xmlsize && i<allsize && alllist.get(i).compareTo(xmllist.get(j)) < 0)
				{
					i++;	
				}
				
			}
			
			
			L.d(L._FILE_LINE_(), "DataFill is finish");			
		}
	}
	
	
	public class SelfImprove implements Runnable { //通过title填充其他数据

		private List<MediaBase> alllist;
		private String regEx = ".*?(?=-)";
		private Pattern p = Pattern.compile(regEx);  
		
		
		
		public SelfImprove(List<MediaBase> AllList) {
			// TODO Auto-generated constructor stub
			this.alllist = AllList;				
		}

		public void run() {
			// TODO Auto-generated method stub
			
			 try {
					Latch_XML.await();
					Latch_AllList.await(); 		
					Alllistsearch = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			  Matcher m;	
			  StringBuffer title;
			  
			   for(MediaBase Base: alllist)
			   {
				   title = new StringBuffer(Base.getTitle());		
				   
				   if(title.toString().getBytes().length == title.length() || title.indexOf("英语") > 0)
				   {
					   Base.setLanguage(AppContext.language_type[AppContext.EnglishLanguager]);
				   }				   
				   else if(title.indexOf("国语") > 0)
				   {
					   Base.setLanguage(AppContext.language_type[AppContext.NationalLanguager]);
				   }
				   else if(title.indexOf("粤语") > 0)
				   {
					   Base.setLanguage(AppContext.language_type[AppContext.Cantonese]);
				   }
				   else if(title.indexOf("台语") > 0)
				   {
					   Base.setLanguage(AppContext.language_type[AppContext.Taiwanese]);
				   }
				   
				   m = p.matcher(title); 
				  if(m.find())
			   	  {
					   Base.setSinger(SpellUtil.getSpellAcronymToLowerCase(m.group(0)));
					   Base.setNumber(String.valueOf(m.group(0).length()));
					   while(m.find())
					   {
						   if(m.group(0).length() != 0)
						   {   
							   if(m.group(0).indexOf('(') == -1)
							   {
								   Base.setNumber(String.valueOf(m.group(0).length()));
							   }
							   else
							   {
								   Base.setNumber(String.valueOf(m.group(0).indexOf('(')));
							   }
							  
							   break;
						   }
					   }					  
			   	  }
				  else
				  {
					  Base.setNumber(String.valueOf(title.length()));
				  }
			   }			
			L.d(L._FILE_LINE_(), "SelfImprove is finish");	
			Alllistsearch = true;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#filterData(java.lang.String, int)
	 */			
	@Override
	public List<MediaBase> filterData(String filterStr, int Position_key){	
		
		List<MediaBase> DataList;
		
		if(Position_key == AppContext.Others)
		{
			DataList = getOtherList();
		}
		else  //if(Position_key == AppContext.SEARCH_LIST || Position_key == AppContext.SEARCH_Singer)
		{
			DataList = getAllList();
		}
		
		filterDateList.clear();
		
		if(TextUtils.isEmpty(filterStr)){		
			return DataList;
			
		}else{			
			StringBuffer key;//全拼关键字			
			for(MediaBase Base : DataList){
				if(Position_key == AppContext.SEARCH_Singer)
				{
					 key = Base.getSinger();
				}
				else
				{
					 key = Base.getKeyword();
				}
				
				if(key != null && key.toString().toLowerCase().indexOf(filterStr.toLowerCase()) > 0 ){
					filterDateList.add(Base);
				}
			}
			return filterDateList;			
		}
	}
	
	public class RecorderList implements Runnable { //将Cursor数据转ArrayList,这个list用于自定义排序以及过滤

		private List<MediaBase> list ;	
		private int Count;
				
		public RecorderList(List<MediaBase> list) {
			// TODO Auto-generated constructor stub		
			this.list = list;
		}

		public void run() {
			// TODO Auto-generated method stub
			 try {
					Latch_Cursor.await();
					if(mRecorderCursor != null && mRecorderCursor.getCount() == 0)
					{
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			list.clear();			
			Count = mRecorderCursor.getCount();
			L.d(L._FILE_LINE_(),"Count is" + String.valueOf(Count));
			for(int i=0; i<Count; i++) 
			{
				MediaBase arg = new MediaBase();							
				mRecorderCursor.moveToPosition(i);
				arg.setUrl(mRecorderCursor
		                .getString(mRecorderCursor
			               		 .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));	
				arg.setTitle(mRecorderCursor
		                .getString(mRecorderCursor
			               		 .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
				arg.setKeyword(arg.getTitle());	
				
				File file = new File(arg.getUrl().toString());
				if (file.exists())
				{
					list.add(arg);
				}
			}
			
			Collections.sort(list);	
			
			LatchRecorderList.countDown();
			
			L.d(L._FILE_LINE_(), "RecorderList is finish");
			
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getThreadPool()
	 */
	@Override
	public ExecutorService getThreadPool()
	{
		return cachedThreadPool;
	}


	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getmRecorderCursor()
	 */
	@Override
	public Cursor getmRecorderCursor() {
		return mRecorderCursor;
	}


	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#setmRecorderCursor(android.database.Cursor)
	 */
	@Override
	public void setmRecorderCursor(Cursor mRecorderCursor) {
		this.mRecorderCursor = mRecorderCursor;
	}


	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#deleteRecorderFiles(java.lang.String)
	 */
	@Override
	public  void deleteRecorderFiles(String fileNames)
	{	
		Iterator<MediaBase> list = RecorderList.iterator();
		
		while(list.hasNext())
		{
			if(list.next().getUrl().toString().equals(fileNames))
			{
				list.remove();
				deleteFiles(fileNames);				
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#deleteFiles(java.lang.String)
	 */
	@Override
	public  void deleteFiles(String... fileNames) {
		if (fileNames.length <= 0)
		return;
		for (int i = 0; i < fileNames.length; i++) {
		File file = new File(fileNames[i]);
		if (file.exists())
		file.delete();
		}
	}
	
	public void ThreadPoolexecute(Runnable command)
	{
		if(!cachedThreadPool.isShutdown()){
			cachedThreadPool.execute(command);
		}
	
	}


	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#getRecorderList()
	 */
	@Override
	public List<MediaBase> getRecorderList() {
		
		return RecorderList;
	}


	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#setRecorderList(java.util.List)
	 */
	@Override
	public void setRecorderList(List<MediaBase> recorderList) {
		RecorderList = recorderList;
	}


	/* (non-Javadoc)
	 * @see com.fonsview.localktv.dataSources.InterfaceCursorDate#release()
	 */
	@Override
	public void release() {
		// TODO Auto-generated method stub
		if(mCursor!=null &&  !mCursor.isClosed())
		{
			mCursor.close();	
			mCursor = null;
		}
		if(mRecorderCursor !=null &&  !mRecorderCursor.isClosed())
		{
			mRecorderCursor.close();			
			mRecorderCursor = null;
		}
		if(mCursorOther !=null &&  !mCursorOther.isClosed())
		{
			mCursorOther.close();			
			mCursorOther = null;
		}
		if(cachedThreadPool !=null)
		{
			cachedThreadPool = null;			
		}	
	}
}
