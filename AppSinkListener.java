package application;

import java.nio.ByteBuffer;

import org.freedesktop.gstreamer.Buffer;
import org.freedesktop.gstreamer.FlowReturn;
import org.freedesktop.gstreamer.Sample;
import org.freedesktop.gstreamer.Structure;
import org.freedesktop.gstreamer.elements.AppSink;


import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


public class AppSinkListener implements AppSink.NEW_SAMPLE {
	
	private Image actualFrame;
	private int lastWidth = 0;
	private int lastHeigth = 0;
	private byte[] byteArray;
	private ImageContainer imageContainer = new ImageContainer();
	
	public ImageContainer getImageContainer(){
		return imageContainer;
	}

	@Override
	public FlowReturn newSample(AppSink appSink) {
		// Try to get a sample
		Sample sample = appSink.pullSample();
        Buffer buffer = sample.getBuffer();
        ByteBuffer byteBuffer = buffer.map(false);
        if (byteBuffer != null){
    		Structure capsStruct = sample.getCaps().getStructure(0);
            int width = capsStruct.getInteger("width");
            int height = capsStruct.getInteger("height");
        	if (width != lastWidth || height != lastHeigth){
        		lastWidth = width;
        		lastHeigth = height;
        		byteArray = new byte[width * height * 4];
        	}
        	// Writes the buffer to the byteArray
	        byteBuffer.get(byteArray);
	        actualFrame = convertBytesToImage(byteArray, width, height);
	        // Writes the new Image to the ImageContainer. If an other part of the programm wants to do something like displaying or storing 
	        //with the frames it can set up a changeListener to get a chance to do something with the newest frame.
	        imageContainer.setImage(actualFrame);
	        buffer.unmap();
	        }
        sample.dispose();
		return FlowReturn.OK;
	}
	
	private Image convertBytesToImage(byte[] pixels, int width, int height){
		//Writes a bytearray to a WritableImage.
		WritableImage img = new WritableImage(width, height);
		PixelWriter pw = img.getPixelWriter();
		pw.setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), pixels, 0, width *4);
		return img;
	}
	
}