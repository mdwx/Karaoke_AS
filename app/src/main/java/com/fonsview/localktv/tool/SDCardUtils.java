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
 * SD����صĸ�����
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
	 * �ж�SDCard�Ƿ����
	 * 
	 * @return
	 */
	public static boolean isSDCardEnable()
	{
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

	}

	/**
	 * ��ȡ����SD��·��
	 * 
	 * @return
	 */
	public static String getSDCardPath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	}

	/**
	 * ��ȡSD����ʣ������ ��λbyte
	 * 
	 * @return
	 */
	public static long getSDCardAllSize()
	{
		if (isSDCardEnable())
		{
			StatFs stat = new StatFs(getSDCardPath());
			// ��ȡ���е����ݿ������
			long availableBlocks = (long) stat.getAvailableBlocks() - 4;
			// ��ȡ�������ݿ�Ĵ�С��byte��
			long freeBlocks = stat.getAvailableBlocks();
			return freeBlocks * availableBlocks;
		}
		return 0;
	}

	/**
	 * ��ȡָ��·�����ڿռ��ʣ����������ֽ�������λbyte
	 * 
	 * @param filePath
	 * @return �����ֽ� SDCard���ÿռ䣬�ڲ��洢���ÿռ�
	 */
	public static long getFreeBytes(String filePath)
	{
		// �����sd�����µ�·�������ȡsd����������
		if (filePath.startsWith(getSDCardPath()))
		{
			filePath = getSDCardPath();
		} else
		{// ������ڲ��洢��·�������ȡ�ڴ�洢�Ŀ�������
			filePath = Environment.getDataDirectory().getAbsolutePath();
		}
		StatFs stat = new StatFs(filePath);
		long availableBlocks = (long) stat.getAvailableBlocks() - 4;
		return stat.getBlockSize() * availableBlocks;
	}

	/**
	 * ��ȡϵͳ�洢·��
	 * 
	 * @return
	 */
	public static String getRootDirectoryPath()
	{
		return Environment.getRootDirectory().getAbsolutePath();
	}
	
	/** 
	 * ��ȡ����SD��·�� 
	 * @return? Ӧ�þ�һ����¼��� 
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
	        //    ����չ�洢�豸���ļ�   
	    	List<String> ExtSDCardPath = getExtSDCardPath();
	    	
	    	 // ����SDcard
	        File myFile = new File(getSDCardPath() + FileName);		        
	       
	     // ����SDcard   
	        for (String path : ExtSDCardPath) {			        	
		        if (!myFile.exists()) {  
		        	myFile = new File(path + File.separator + FileName); 		        
			       
		        }		        
	        }  
	       
	     
	        // �ж��Ƿ����
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
		// ӵ�пɶ���дȨ��    
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) ||  
				android.os.Environment.getExternalStorageState().endsWith(android.os.Environment.MEDIA_MOUNTED_READ_ONLY)) 
		{    
		     
		    try {    
		        //    ����չ�洢�豸���ļ�   
		    	List<String> ExtSDCardPath = getExtSDCardPath();
		    	
		    	 // ����SDcard
		        File myFile = new File(getSDCardPath() + FileName);		        
		       
		     // ����SDcard   
		        for (String path : ExtSDCardPath) {			        	
			        if (!myFile.exists()) {  
			        	myFile = new File(path + File.separator + FileName); 		        
				       
			        }		        
		        }  
		       
		     
		        // �ж��Ƿ����
		        if (!myFile.exists()) {  
		        	 Log.d("SDCardUtils: ", "File is invalidate");
		           return null;
		        }    

	            // ������    
	            FileInputStream inputStream = new FileInputStream(myFile);    
	            byte[] buffer = new byte[Buff_lenght];
	            
	            int readBytes = 0;  
	            
	            while (readBytes < Buff_lenght)
	            {  
					int read =  inputStream.read(buffer, readBytes, Buff_lenght - readBytes); 
					   //�ж��ǲ��Ƕ�������������ĩβ ����ֹ������ѭ����  
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
