package com.fonsview.localktv.dataSources;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.fonsview.localktv.dataSources.MediaPlayerControl.Interfacefunction;
import com.fonsview.mktv.FvVideoPlayRecorder;
import com.fonsview.mktv.FvVideoPlayRecorder.OnCompletionListener;

import java.io.IOException;



public class FvVideoPlay implements InterfaceMediaPlay{
	private FvVideoPlayRecorder FvVideoPlay;
	private SurfaceView surfaceView;
	public FvVideoPlay(Context context)
	{
		FvVideoPlay = new FvVideoPlayRecorder(context);
		this.surfaceView = FvVideoPlay.getSurface(context);
	}

	@Override
	public void setAudioStreamType(int streamMusic) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void setDataSource(String path) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		// TODO Auto-generated method stub
		FvVideoPlay.setSrc(path);
	}

	@Override
	public void setDisplay(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		FvVideoPlay.setDisplay(holder);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		FvVideoPlay.start();
	}

	
	@Override
	public void seekTo(int msec) {
		// TODO Auto-generated method stub
		FvVideoPlay.seekTo(msec);
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return FvVideoPlay.getCurrentPosition();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		FvVideoPlay.stop();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		FvVideoPlay.release();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return FvVideoPlay.isPlaying();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		FvVideoPlay.pause();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return FvVideoPlay.getDuration();
	}

	@Override
	public int setAoudioTrack(int AUDIO_id) {
		// TODO Auto-generated method stub
		
		return AUDIO_id;
	}

	@Override
	public void play(String path) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		// TODO Auto-generated method stub
		Log.d("java log", path);
		FvVideoPlay.stop();
		FvVideoPlay.reset();
		FvVideoPlay.setSrc(path);		
		
		FvVideoPlay.setVolume(1f, 1f);
		
		
		FvVideoPlay.prepareAsync();
		FvVideoPlay.setOnPreparedListener(new FvVideoPlayRecorder.OnPreparedListener() {					
			@Override
			public void onPrepared(FvVideoPlayRecorder vmp) {
				// TODO Auto-generated method stub		
				vmp.setDisplay(surfaceView.getHolder());
				vmp.setAudioMixing(false);
				vmp.start();
						
			}
		});
		
	}

	@Override
	public SurfaceView getSurface() {
		// TODO Auto-generated method stub
		return surfaceView;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
		FvVideoPlay.setTmpAac("/sdcard/dst_temp.aac");
		FvVideoPlay.setDst("/sdcard/cmd.mkv");
		FvVideoPlay.setAudioTransferAddr("/sdcard/unixdomain");	
		FvVideoPlay.setOnPreparedListener(new FvVideoPlayRecorder.OnPreparedListener() {
			
			@Override
			public void onPrepared(FvVideoPlayRecorder vmp) {
				// TODO Auto-generated method stub		
				vmp.setDisplay(surfaceView.getHolder());
				vmp.setAudioMixing(false);
				vmp.start();						
			}
		});
	
		FvVideoPlay.setOnErrorListener(new FvVideoPlayRecorder.OnErrorListener() {		
			@Override
			public boolean onError(FvVideoPlayRecorder vmp, int what, int extra) {
				// TODO Auto-generated method stub				
				return true;
			}
		});
	}

	@Override
	public void setOnCompletionListener(final Interfacefunction fun) {
		// TODO Auto-generated method stub
		FvVideoPlay.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(FvVideoPlayRecorder arg0) {
				// TODO Auto-generated method stub
				fun.run();
			}
		});
	}

	@Override
	public void surfaceChanged(final Interfacefunction fun) {
		// TODO Auto-generated method stub
		this.surfaceView.getHolder().addCallback(new Callback() {
			
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
}
