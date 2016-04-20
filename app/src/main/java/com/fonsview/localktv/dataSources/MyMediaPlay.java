package com.fonsview.localktv.dataSources;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.TrackInfo;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.fonsview.localktv.dataSources.MediaPlayerControl.Interfacefunction;
import com.fonsview.localktv.tool.L;

import java.io.IOException;

public class MyMediaPlay implements InterfaceMediaPlay{//android原生播放器

	private MediaPlayer MediaPlayer;
	private SurfaceView surfaceView;
	public MyMediaPlay(Activity Activity){
		MediaPlayer = new MediaPlayer();
		surfaceView = new SurfaceView(Activity);		
	}

	@Override
	public void setAudioStreamType(int streamMusic) {
		// TODO Auto-generated method stub
		MediaPlayer.setAudioStreamType(streamMusic);
		
	}

	@Override
	public void setDataSource(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		// TODO Auto-generated method stub
		MediaPlayer.setDataSource(path);
	}

	@Override
	public void setDisplay(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		MediaPlayer.setDisplay(holder);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		MediaPlayer.start();
	}

	@Override
	public void seekTo(int msec) {
		// TODO Auto-generated method stub
		MediaPlayer.seekTo(msec);
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return MediaPlayer.getCurrentPosition();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		MediaPlayer.stop();
		
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		MediaPlayer.release();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return MediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		MediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return MediaPlayer.getDuration();
	}

	@Override
	public int setAoudioTrack(int AUDIO_ID) {
		// TODO Auto-generated method stub
		 //切换音轨实现原声伴唱
			TrackInfo[] trackInfos = MediaPlayer.getTrackInfo(); 		//获得所有轨道
			int AUDIOMold = 0;
		 	if (trackInfos != null && trackInfos.length > 0) 
		 	{

				for (int i=0; i<trackInfos.length; i++){

					final TrackInfo info = trackInfos[i]; 
	   
					//如果有音轨，而且当前音轨是第二号音轨，则切换到第一个音轨
					if (info.getTrackType() == TrackInfo.MEDIA_TRACK_TYPE_AUDIO) 
					{ 
					 	if(AUDIO_ID == AUDIOMold )
					 	{
					 		MediaPlayer.selectTrack(i);					 		
					 		return AUDIOMold;
					 	}			 
						AUDIOMold++;
						L.d(L._FILE_LINE_(), "is  AudioTrack!!");
					}				
				}				
		 	}
			 	return -1;
	    }

	@Override
	public void play(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		MediaPlayer.reset();
		
		MediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		// 视频播放源
		MediaPlayer.setDataSource(path);
		
		MediaPlayer.prepare();
		// TODO Auto-generated method stub
		MediaPlayer.start();	
		// 把视频画面输出到SurfaceView
		MediaPlayer.setDisplay(surfaceView.getHolder());  //①

	}

	@Override
	public SurfaceView getSurface() {
		// TODO Auto-generated method stub
		return surfaceView;
	}
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		surfaceView.getHolder().setKeepScreenOn(true);	
	}
	
	@Override
	public void surfaceChanged(final Interfacefunction fun)
	{
		surfaceView.getHolder().addCallback(new Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				fun.run();				
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				
			}
		});	
	}
	@Override
	public void setOnCompletionListener(final Interfacefunction fun) {
		// TODO Auto-generated method stub
		MediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				fun.run();
			}
		});
	}
	
	
}
