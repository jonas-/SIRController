package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ros.android.BitmapFromImage;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ch.ethz.naro.IMUHandler.IMUhandlerListener;
import ch.ethz.naro.VideoHandler.VideoHandlerListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import sensor_msgs.CompressedImage;
import sensor_msgs.Image;

public class VideoListener extends AbstractNodeMain {
  
  Subscriber<sensor_msgs.CompressedImage> videoListener;
  private VideoView video;
  private ImageView image;

  private static String TAG = "Video";
  
  
  // ---- Event handler ------------
  private List _listeners = new ArrayList();
  
  public synchronized void addEventListener(VideoHandlerListener listener) {
    Log.i("Joy", "addEventListener called");
    _listeners.add(listener);
  }
  public synchronized void removeEventListener(VideoHandlerListener listener) {
    //Log.i("Joy", "removeEventListener called");
    _listeners.remove(listener);
  }
  
  private synchronized void fireVideo(sensor_msgs.CompressedImage img) {
    //Log.i("IMU", "fireEvent called");
    VideoHandler event = new VideoHandler(this, img);
    
    Iterator i = _listeners.iterator();
    
    while(i.hasNext()) {
      ((VideoHandlerListener) i.next()).handleVideo(event);
    }
  } 
  // ---------------

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("VideoStream");
  }

  public void onStart(ConnectedNode connectedNode) {
    
    videoListener = connectedNode.newSubscriber("/image_raw/compressed", sensor_msgs.CompressedImage._TYPE);
    
    videoListener.addMessageListener(new MessageListener<sensor_msgs.CompressedImage>() {
      
      @Override
      public void onNewMessage(CompressedImage msg) {
        //Log.i(TAG, "Got msg!");
        
        fireVideo(msg);
      }
    });

  }
  
}
