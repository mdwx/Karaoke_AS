package com.fonsview.localktv.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * SD卡相关的辅助类
 * 
 * @author zhy
 * 
 */
public class SDCardUtils
{
	private static int Buff_lenght = 1024 * 64;
	private SDCardUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 判断SDCard是否可用
	 * 
	 * @return
	 */
	public static boolean isSDCardEnable()
	{
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

	}

	/**
	 * 获取内置SD卡路径
	 * 
	 * @return
	 */
	public static String getSDCardPath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	}

	/**
	 * 获取SD卡的剩余容量 单位byte
	 * 
	 * @return
	 */
	public static long getSDCardAllSize()
	{
		if (isSDCardEnable())
		{
			StatFs stat = new StatFs(getSDCardPath());
			// 获取空闲的数据块的数量
			long availableBlocks = (long) stat.getAvailableBlocks() - 4;
			// 获取单个数据块的大小（byte）
			long freeBlocks = stat.getAvailableBlocks();
			return freeBlocks * availableBlocks;
		}
		return 0;
	}

	/**
	 * 获取指定路径所在空间的剩余可用容量字节数，单位byte
	 * 
	 * @param filePath
	 * @return 容量字节 SDCard可用空间，内部存储可用空间
	 */
	public static long getFreeBytes(String filePath)
	{
		// 如果是sd卡的下的路径，则获取sd卡可用容量
		if (filePath.startsWith(getSDCardPath()))
		{
			filePath = getSDCardPath();
		} else
		{// 如果是内部存储的路径，则获取内存存储的可用容量
			filePath = Environment.getDataDirectory().getAbsolutePath();
		}
		StatFs stat = new StatFs(filePath);
		long availableBlocks = (long) stat.getAvailableBlocks() - 4;
		return stat.getBlockSize() * availableBlocks;
	}

	/**
	 * 获取系统存储路径
	 * 
	 * @return
	 */
	public static String getRootDirectoryPath()
	{
		return Environment.getRootDirectory().getAbsolutePath();
	}
	
	/** 
	 * 获取外置SD卡路径 
	 * @return? 应该就一条记录或空 
	 */

	static	public List<String> getExtSDCardPath()
	{
		List<String> lResult = new ArrayList<String>();

		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("extSdCard") || line.contains("external_storage"))
				{
					String [] arr = line.split(" ");
					String path = arr[1];
					File file = new File(path);
					if (file.isDirectory())
					{
						lResult.add(path);
					}
				}
			}
			isr.close();
			} catch (Exception e) {			
			}
		return lResult;
	}
	
	public static String FindFile(String FileName)
	{
		try {    
	        //    打开扩展存储设备的文件   
	    	List<String> ExtSDCardPath = getExtSDCardPath();
	    	
	    	 // 内置SDcard
	        File myFile = new File(getSDCardPath() + FileName);		        
	       
	     // 外置SDcard   
	        for (String path : ExtSDCardPath) {			        	
		        if (!myFile.exists()) {  
		        	myFile = new File(path + File.separator + FileName); 		        
			       
		        }		        
	        }  
	       
	     
	        // 判断是否存在
	        if (!myFile.exists()) {  
	        	 Log.d("SDCardUtils: ", "File is invalidate");
	           return null;
	        } 
	        return myFile.getAbsolutePath();
	        
			} catch (Exception e) {    
		        // TODO: handle exception    
		    }
		return null;        
	}
	public static byte[] OpenFile(String FileName)
	{
		// 拥有可读可写权限    
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) ||  
				android.os.Environment.getExternalStorageState().endsWith(android.os.Environment.MEDIA_MOUNTED_READ_ONLY)) 
		{    
		     
		    try {    
		        //    打开扩展存储设备的文件   
		    	List<String> ExtSDCardPath = getExtSDCardPath();
		    	
		    	 // 内置SDcard
		        File myFile = new File(getSDCardPath() + FileName);		        
		       
		     // 外置SDcard   
		        for (String path : ExtSDCardPath) {			        	
			        if (!myFile.exists()) {  
			        	myFile = new File(path + File.separator + FileName); 		        
				       
			        }		        
		        }  
		       
		     
		        // 判断是否存在
		        if (!myFile.exists()) {  
		        	 Log.d("SDCardUtils: ", "File is invalidate");
		           return null;
		        }    

	            // 读数据    
	            FileInputStream inputStream = new FileInputStream(myFile);    
	            byte[] buffer = new byte[Buff_lenght];
	            
	            int readBytes = 0;  
	            
	            while (readBytes < Buff_lenght)
	            {  
					int read =  inputStream.read(buffer, readBytes, Buff_lenght - readBytes); 
					   //判断是不是读到了数据流的末尾 ，防止出现死循环。  
					if (read == -1) {  
					    break;  
					}  
					readBytes += read;  
	            }  
	            inputStream.close();    
	            return buffer;
	            
		    } catch (Exception e) {    
		        // TODO: handle exception    
		    }
		}	
		return null;
	}
	
	public static void wirtFile(String FileName,String buffer)
	{
		if ( Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			File myFile = new File(android.os.Environment    
	                .getExternalStorageDirectory().getAbsolutePath()    
	                + File.separator + FileName);  
			if (!myFile.exists()) {    
				 try {
					myFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		        }  
			
			FileOutputStream outputStream = null;
			
			try {
				
				outputStream = new FileOutputStream(myFile);
				outputStream.write(buffer.getBytes("UTF-8"));
				outputStream.close();
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}
	}
}
