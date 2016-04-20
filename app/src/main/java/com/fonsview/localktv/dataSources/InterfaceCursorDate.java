package com.fonsview.localktv.dataSources;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.fonsview.localktv.model.MediaBase;

import android.content.ContentResolver;
import android.database.Cursor;

public interface InterfaceCursorDate {

	
	public abstract void QueryRecorderCursor(ContentResolver Content);

	public abstract void QueryOtherCursor(ContentResolver Content);

	/********************************************************
	 *Cursor初始化
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: 数据的获取以及内存分配</p>
	 *@version: 2015-6-16下午1:58:47	
	 *@param resolver
	 *@return true:初始化正常, flase:初始化失败
	 *<p>return_type: Boolean</p>
	 *
	 */
	public abstract Boolean InitCursor(ContentResolver Content);

	/********************************************************
	 *获取当前字母首次出现的位置
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description:  获取当前字母首次出现的位置</p>
	 *@version: 2015-6-16下午1:59:33	
	 *@param c 当前字符
	 *@return 首次出现的位置 , -1表示不存在
	 *<p>return_type: int</p>
	 *
	 */
	public abstract int getPositionByFirstchar(char c);

	/********************************************************
	 *获取当前字母首次出现的位置
	 *
	 *<p>category: com.example.locaktv.DataSources</P>
	 *<p>Description: 给定起始位置和结束位置，用二分法查找当前字符首次出现的的位置</p>
	 *@version: 2015-6-16下午2:00:10	
	 *@param c  当前字符
	 *@param List  内容list
	 *@param start  查找起始位置
	 *@param end  查找最终位置
	 *@return 首次出现的位置 , -1表示不存在
	 *<p>return_type: int</p>
	 *
	 */
	public abstract int getPositionByFirstchar(char c, List<MediaBase> List);

	/********************************************************
	 **获取数据所有首字母集合，A-Z的字集
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: </p>
	 *@version: 2015-6-16下午2:01:29	
	 *@return 首字母集合
	 *<p>return_type: StringBuffer</p>
	 *
	 */
	public abstract StringBuffer getFirstcharArray();

	public abstract StringBuffer getFirstcharArray(List<MediaBase> list);

	/********************************************************
	 **得到当前标题名（歌曲名）
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: TODO</p>
	 *@version: 2015-6-16下午2:02:24	
	 *@return 歌曲名
	 *<p>return_type: String</p>
	 *
	 */
	public abstract String getColumnTitle();

	/********************************************************
	 *得到当前歌曲的路径
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: TODO</p>
	 *@version: 2015-6-16下午2:03:04	
	 *@return 当前歌曲的路径
	 *<p>return_type: String</p>
	 *
	 */
	public abstract String getColumnUrl();

	/********************************************************
	 *得到下一首歌曲的路径
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: TODO</p>
	 *@version: 2015-6-16下午2:03:31	
	 *@return  下一首歌曲的路径
	 *<p>return_type: String</p>
	 *
	 */
	public abstract String getNextColumnUrl();

	/********************************************************
	 *将当前选中的歌曲添加到选歌列表
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: TODO</p>
	 *@version: 2015-6-16下午2:04:13	
	 *@param position 当前选中歌曲的位置
	 *@param selectmod  数据的模式  
	 *@return  true：添加成功，flase：添加失败
	 *<p>return_type: boolean</p>
	 *
	 */
	public abstract boolean insetSelect(int position, int selectmod);

	/********************************************************
	 *根据名字从选歌列表中删除
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: 根据名字删除所有同名歌曲</p>
	 *@version: 2015-6-16下午2:04:43	
	 *@param Title 歌曲名字
	 *@return 
	 *<p>return_type: boolean</p>
	 *
	 */
	public abstract boolean deleSelect(String Title);

	/********************************************************
	 *将指定位置的歌曲置顶
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: TODO</p>
	 *@version: 2015-6-16下午2:05:19	
	 *@param id 歌曲位置
	 *@return
	 *<p>return_type: boolean</p>
	 *
	 */
	public abstract boolean MoveToFirst(int id);

	public abstract StringBuffer getFirstcharStrbuf();

	public abstract StringBuffer getFirstcharStrbuf(int pos);

	public abstract boolean isempty();

	public abstract Cursor getCursor();

	
	/*获取资源是否失败*/
	public abstract boolean getCursorFailure();

	public abstract void setFirstcharStrbuf(StringBuffer firstcharStrbuf);

	public abstract void setmCursor(Cursor mCursor);

	public abstract List<MediaBase> getSetlectList();

	/********************************************************
	 *得到排序后的所有数据
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: 得到排序后的所有数据，阻塞等待排序完成</p>
	 *@version: 2015-6-16下午2:05:50	
	 *@return
	 *<p>return_type: List MediaBase</p>
	 *
	 */
	public abstract List<MediaBase> getAllList();

	/********************************************************
	 *得到排序后的所有数据
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: 得到排序后的所有数据，阻塞等待排序完成</p>
	 *@version: 2015-6-16下午2:05:50	
	 *@return
	 *<p>return_type: List MediaBase</p>
	 *
	 */
	public abstract List<MediaBase> getOtherList();

	/********************************************************
	 *填充选歌列表
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: TODO</p>
	 *@version: 2015-6-16下午2:06:16	
	 *@param setlectList
	 *<p>return_type: void</p>
	 *
	 */
	public abstract void setSetlectList(List<MediaBase> setlectList);

	/********************************************************
	 *数据转换
	 *
	 *<p>category: com.example.locaktv.DataSources</p>
	 *<p>Description: 线程池任务，ALLlist的排序，XML数据获取，XML填充完善ALL数据</p>
	 *@version: 2015-6-16下午2:06:27	
	 *<p>return_type: void</p>
	 *
	 */
	public abstract void ListTransition();

	/**
	 * 根据输入框中的值来过滤数据得到List
	 * @param filterStr
	 * @return 
	 */
	public abstract List<MediaBase> filterData(String filterStr,
			int Position_key);

	public abstract ExecutorService getThreadPool();

	public abstract Cursor getmRecorderCursor();

	public abstract void setmRecorderCursor(Cursor mRecorderCursor);

	public abstract void deleteRecorderFiles(String fileNames);

	public abstract void deleteFiles(String... fileNames);

	public abstract List<MediaBase> getRecorderList();

	public abstract void setRecorderList(List<MediaBase> recorderList);

	public abstract void release();

}