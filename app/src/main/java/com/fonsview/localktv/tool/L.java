package com.fonsview.localktv.tool;

import android.util.Log;

/** 
 * @author Vince  E-mail: xhys01@163.com
 * @version 创建时间：2015-6-11 上午9:33:30 
 * 类说明 
 */
public class L  
{ 
    private L()  
    {  
        /* cannot be instantiated */  
        throw new UnsupportedOperationException("cannot be instantiated");  
    }  
  
    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化  
   
  
    // 下面四个是默认tag的函数  
    public static void i(String msg)  
    {  
        if (isDebug)  
            Log.i(L._FILE_(), ""+msg);  
    }  
  
    public static void d(String msg)  
    {  
        if (isDebug)  
            Log.d(L._FILE_(), ""+msg);  
    }  
  
    public static void e(String msg)  
    {  
        if (isDebug)  
            Log.e(L._FILE_(), ""+msg);  
    }  
  
    public static void v(String msg)  
    {  
        if (isDebug)  
            Log.v(L._FILE_(), ""+msg);  
    }  
  
    // 下面是传入自定义tag的函数  
    public static void i(String tag, String msg)  
    {  
        if (isDebug)  
            Log.i(tag, ""+msg);  
    }  
  
    public static void d(String tag, String msg)  
    {  
        if (isDebug)  
            Log.i(tag, ""+msg);  
    }  
  
    public static void e(String tag, String msg)  
    {  
        if (isDebug)  
            Log.i(tag,""+msg);  
    }  
  
    public static void v(String tag, String msg)  
    {  
        if (isDebug)  
            Log.i(tag, ""+msg);  
    }     
    
    public static String _FILE_()   //返回文件名
	 {   
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();    
		  return stackTraces[1].getFileName();   
	 }  
	 public static int _LINE_()   //返回当前行
	 {   
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();    
		  return stackTraces[1].getLineNumber();   
	 }   
	 public static String _FUNC_()   //返回所在函数
	 {   
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();     
		  return stackTraces[1].getMethodName();  
	 }
	 
	 public static String _FILE_LINE_()   //文件名+当前行
	 {     
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();     
		  StringBuffer strBuffer = new StringBuffer("[");  
		  strBuffer.append(stackTraces[1].getFileName()).append(":");    
		  strBuffer.append(stackTraces[1].getLineNumber()).append("]");  
		  return strBuffer.toString();     
	 }
	 
	 public static String _FILE_LINE_FUNC_()   //文件名+当前行+函数名
	 {     
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();     
		  StringBuffer strBuffer = new StringBuffer("[");  
		  strBuffer.append(stackTraces[1].getFileName()).append(":");    
		  strBuffer.append(stackTraces[1].getLineNumber()).append("|");    
		  strBuffer.append(stackTraces[1].getMethodName()).append("()]");      
		  return strBuffer.toString();     
	 }
	
	 public static String _FILE_LINE_FUNC_PACK_()   //文件名+当前行+函数名+包名
	 {     
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();     
		  StringBuffer strBuffer = new StringBuffer("[");  
		  strBuffer.append(stackTraces[1].getFileName()).append(":");    
		  strBuffer.append(stackTraces[1].getLineNumber()).append(":");    
		  strBuffer.append(stackTraces[1].getMethodName()).append("()]");      
		  strBuffer.append(stackTraces[1].getClassName()).append(".");  
		  return strBuffer.toString();     
	 }
}