package com.fonsview.localktv.dataSources;

import java.io.IOException;

import com.fonsview.localktv.dataSources.MediaPlayerControl.Interfacefunction;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

public interface InterfaceMediaPlay {

	
	void play(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException;
	
	void setAudioStreamType(int streamMusic);

	void setDataSource(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException;
	
	void setDisplay(SurfaceHolder holder);

	void start();

	void seekTo(int msec);

	int getCurrentPosition();

	void stop();

	void release();

	boolean isPlaying();

	void pause();

	int getDuration();

	int setAoudioTrack(int AUDIO_id);

	SurfaceView getSurface();

	void initialize();

	void setOnCompletionListener(Interfacefunction fun);

	void surfaceChanged(Interfacefunction fun);

}
