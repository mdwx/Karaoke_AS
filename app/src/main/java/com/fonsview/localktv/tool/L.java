package com.fonsview.localktv.tool;

import android.util.Log;

/** 
 * @author Vince  E-mail: xhys01@163.com
 * @version ����ʱ�䣺2015-6-11 ����9:33:30 
 * ��˵�� 
 */
public class L  
{ 
    private L()  
    {  
        /* cannot be instantiated */  
        throw new UnsupportedOperationException("cannot be instantiated");  
    }  
  
    public static boolean isDebug = true;// �Ƿ���Ҫ��ӡbug��������application��onCreate���������ʼ��  
   
  
    // �����ĸ���Ĭ��tag�ĺ���  
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
  
    // �����Ǵ����Զ���tag�ĺ���  
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
    
    public static String _FILE_()   //�����ļ���
	 {   
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();    
		  return stackTraces[1].getFileName();   
	 }  
	 public static int _LINE_()   //���ص�ǰ��
	 {   
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();    
		  return stackTraces[1].getLineNumber();   
	 }   
	 public static String _FUNC_()   //�������ں���
	 {   
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();     
		  return stackTraces[1].getMethodName();  
	 }
	 
	 public static String _FILE_LINE_()   //�ļ���+��ǰ��
	 {     
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();     
		  StringBuffer strBuffer = new StringBuffer("[");  
		  strBuffer.append(stackTraces[1].getFileName()).append(":");    
		  strBuffer.append(stackTraces[1].getLineNumber()).append("]");  
		  return strBuffer.toString();     
	 }
	 
	 public static String _FILE_LINE_FUNC_()   //�ļ���+��ǰ��+������
	 {     
		  StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();     
		  StringBuffer strBuffer = new StringBuffer("[");  
		  strBuffer.append(stackTraces[1].getFileName()).append(":");    
		  strBuffer.append(stackTraces[1].getLineNumber()).append("|");    
		  strBuffer.append(stackTraces[1].getMethodName()).append("()]");      
		  return strBuffer.toString();     
	 }
	
	 public static String _FILE_LINE_FUNC_PACK_()   //�ļ���+��ǰ��+������+����
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