package mediaplayer;


import java.net.URI;
import java.util.logging.Logger;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.elements.AppSink;
import org.freedesktop.gstreamer.elements.PlayBin;
import org.freedesktop.gstreamer.fx.FXImageSink;
import org.freedesktop.gstreamer.message.Message;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * The main idea is to use FXImageSink and PlayBin to play a media file in this ImageView.
 * A main JavaFX Application can simply add this Node to the scene graph and control the media
 * in a very basic way (start/stop/repeat)
 */


public class GstMediaPlayer extends ImageView {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private AppSink videosink;
	
	private PlayBin playbin;
	
	private boolean repeat = false;

	public GstMediaPlayer(double x, double y) {
		init(null, x, y);
	}
	
	public GstMediaPlayer() {
		init(null, 0, 0);
	}
	
	public GstMediaPlayer(URI video, double x, double y) {
		init(video, x, y);
	}
	
	public GstMediaPlayer(URI video) {
		init(video, 0, 0);
	}
	
	private void init (URI video, double x, double y) {		

		LOGGER.info("Init");	
		
		setX(x);
		setY(y);
		
        Gst.init("GstMediaPlayer", new String[0]);
		
		videosink = new AppSink("GstVideoComponent");
        videosink.set("max-buffers", 5000);
        videosink.set("drop", true);
        
        FXImageSink fXImageSink = new FXImageSink(videosink);
        fXImageSink.imageProperty().addListener(
        new ChangeListener<Image>() {
			@Override
			public void changed(ObservableValue<? extends Image> observable, Image oldValue, final Image newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						setImage(newValue);
					}
				});				
			}        	
		});

        if (video != null)
        	playbin = new PlayBin("playbin", video);
        else
        	playbin = new PlayBin("playbin");
        playbin.setVideoSink(videosink);
        
        setPreserveRatio(true);
        
        playbin.getBus().connect("element", new Bus.MESSAGE() {			
			@Override
			public void busMessage(Bus arg0, Message arg1) {
				LOGGER.info("Bus message: " + arg1.getStructure());				
			}
		});
        
        playbin.getBus().connect(new Bus.EOS() {			
			@Override
			public void endOfStream(GstObject source) {	
				playbin.stop();
				if (repeat) playbin.play();
			}
		});

	}
	
	public void setURI(URI uri) {
		playbin.setURI(uri);
	}

	public void close() {
		playbin.close();
	}

	public void stop() {
		repeat = false;
		playbin.stop();
    	LOGGER.fine("stopping/stopped ");
	}

	public void play() {
        playbin.play();
	}

	public void play(URI uri) {
		playbin.stop();
		playbin.setURI(uri);
        playbin.play();
	}

	public void playAndRepeat() {
		playAndRepeat(null);
	}

	public void playAndRepeat(URI uri) {
		LOGGER.info("playAndRepeat " + uri);
		repeat = true;
		if (uri != null) {
			play(uri);
		} else {
			playbin.play();
		}  	
	}
}
