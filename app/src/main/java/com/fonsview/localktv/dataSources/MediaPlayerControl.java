package com.fonsview.localktv.dataSources;

import android.app.Activity;

import com.fonsview.localktv.model.MediaBase;
import com.fonsview.localktv.tool.L;

import java.io.IOException;

public class MediaPlayerControl{
	
	//媒体播放控制器
	private InterfaceMediaPlay MediaPlayer;	
	private boolean SurfaceLive = false;//标示Surface存活周期
	private int MediaPlayer_AUDIO = 0;;//播放音轨，原音，伴唱
	private MediaBase CurrentPlay;//当前播放文件
	private int position = 0;	//记录当前播放位置
	private boolean firstplay = true;
		
	//媒体数据库容器
	private CursorDate Cursor = null;
	
	//播放测试次数
	private int try_play = 0;
	
	//随机播放标志
	private Boolean RandomPlay = true;
	
	public MediaPlayerControl(Activity Activity)
	{
		this.MediaPlayer = new MyMediaPlay(Activity);	
		CurrentPlay = new MediaBase();
	}
	
	public void initializePlayer(CursorDate Cursor){
		
		this.Cursor = Cursor;	
		MediaPlayer.initialize();
		MediaPlayer.setOnCompletionListener(new Interfacefunction() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				play("");
			}
		});
		
		MediaPlayer.surfaceChanged(new Interfacefunction() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub	
				surface();
			}
		});
	}
	
	public void play(String url) //播放音乐
	{		
		while(Cursor != null  && Cursor.getCursor() != null && Cursor.getCursor().getCount() != 0 && SurfaceLive && try_play < 5 )
		{
			L.d(L._FILE_LINE_(), "try_play:"+String.valueOf(try_play));
			try_play++;			
			if(_play(url))
			{				
				try_play = 0;
				break;
			}
		}
	}
	
	
	private boolean _play(String url) //播放音乐
	{
		try {
			// 设置需要播放的视频
			if(url.length() < 3)
			{
				if(!Cursor.getSetlectList().isEmpty())//已选列表有数据，播放
				{
					CurrentPlay.setTitle(Cursor.getSetlectList().get(0).getTitle());
					CurrentPlay.setUrl(Cursor.getSetlectList().get(0).getUrl());
					Cursor.deleSelect(Cursor.getSetlectList().get(0).getTitle().toString());					
					
					
					MediaPlayer.play(CurrentPlay.getUrl().toString());
					
					RandomPlay = false;
				}
				else if(Cursor.getNextColumnUrl() != null)//列表还有数据，继续播放
				{	
					if(firstplay){
						Cursor.getCursor().move((int)(Math.random()*Cursor.getCursor().getCount()));
						firstplay = false;
					}					
					
					if(Cursor.getCursor().isLast() || Cursor.getCursor().isAfterLast())
					{
						Cursor.getCursor().moveToFirst();
					}
										
					CurrentPlay.setTitle(Cursor.getColumnTitle());
					CurrentPlay.setUrl(Cursor.getColumnUrl());
					
					MediaPlayer.play(Cursor.getColumnUrl());
					
					RandomPlay = true;
				}
				else
				{
					//播放默认文件					
					return false;					
				}				
				
			}
			else
			{				
				MediaPlayer.play(url);			
			}			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
			return false;
		}							   
		
		MediaPlayer.setAoudioTrack(MediaPlayer_AUDIO);
		return true;
	}	 
	
	public void surface()
	{
		SurfaceLive = true;
		
		if(this.Cursor.getCursor() != null &&  Cursor.getCursor().getCount() != 0 )
		{
			if(position != 0 && !RandomPlay && CurrentPlay != null)
			{
				play(CurrentPlay.getUrl().toString());
				MediaPlayer.seekTo(position);
			}
			else
			{
				play("");		
			}				
		}
	}
	public void setCursor(CursorDate Cursor){
		this.Cursor = Cursor;
	}
	public InterfaceMediaPlay getMediaPlayer() {
		return MediaPlayer;
	}

	public void setMediaPlayer(InterfaceMediaPlay MediaPlayer) {
		this.MediaPlayer = MediaPlayer;
	}

	public int getMediaPlayer_AUDIO() {
		return MediaPlayer_AUDIO;
	}

	public void setMediaPlayer_AUDIO(int MediaPlayer_AUDIO) {
		this.MediaPlayer_AUDIO = MediaPlayer_AUDIO;
	}

	public MediaBase getCurrentPlay() {
		return CurrentPlay;
	}

	public void setCurrentPlay(MediaBase currentPlay) {
		CurrentPlay = currentPlay;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Boolean getRandomPlay() {
		return RandomPlay;
	}

	public void setRandomPlay(Boolean randomPlay) {
		RandomPlay = randomPlay;
	}

	public void stop() {
		// TODO Auto-generated method stub
		// 保存当前的播放位置
		position = MediaPlayer.getCurrentPosition();			
		SurfaceLive = false;
		MediaPlayer.stop();
	}

	public void release() {
		// TODO Auto-generated method stub
		MediaPlayer.release();
	}
	
	public interface Interfacefunction {
		public void run();
	}

	public int setAoudioTrack(int i) {
		// TODO Auto-generated method stub
		MediaPlayer_AUDIO = MediaPlayer.setAoudioTrack(i);
		
		return MediaPlayer_AUDIO;
	}

}
