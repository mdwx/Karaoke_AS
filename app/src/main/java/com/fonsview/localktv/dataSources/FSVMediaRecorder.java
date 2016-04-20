package com.fonsview.localktv.dataSources;

import android.hardware.Camera;
import android.media.MediaRecorder;

import java.io.IOException;

public class FSVMediaRecorder {
	
	private MediaRecorder mRecorder = null;
	private StringBuffer FileName;
	private boolean  isStart = false;
	
	public FSVMediaRecorder(String fileName) {
		super();
		FileName = new StringBuffer(fileName);
	}
	public FSVMediaRecorder(StringBuffer fileName) {
		super();
		FileName = fileName;
	}
	public void startRecording() {
		mRecorder = new MediaRecorder();
        //设置音源为Micphone
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        
        if(Camera.getNumberOfCameras() > 0){
	        //设置视频源
	        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	        
	        //视频大小
	        mRecorder.setVideoSize(320, 240);
        }
        //设置封装格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        
        //输出文件
        mRecorder.setOutputFile(FileName.toString());
        
        //设置声音编码格式       
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        
        if(Camera.getNumberOfCameras() > 0){
        	// 设置图像编码的格式
        	mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        }
        try {
            mRecorder.prepare();
        } catch (IOException e) {
       }

        mRecorder.start();
        
        this.isStart = true;
    }
	 public void stopRecording() {
		 
		 if (mRecorder != null) {	          
	        mRecorder.stop();
	        mRecorder.release();
	        this.isStart = false;
	        mRecorder = null;
	    }
	 }

	public boolean getState()
	{
		return this.isStart;
	}
	public MediaRecorder getmRecorder() {
		return mRecorder;
	}
	public void setmRecorder(MediaRecorder mRecorder) {
		this.mRecorder = mRecorder;
	}
	public StringBuffer getFileName() {
		return FileName;
	}
	public void setFileName(StringBuffer fileName) {
		FileName = fileName;
	}
	public void setFileName(String fileName) {
		FileName = new StringBuffer(fileName);
	}
	
}
