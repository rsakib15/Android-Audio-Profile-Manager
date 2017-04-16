package pack.service.profilemanager;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.FloatMath;
import android.util.Log;
import android.widget.Toast;
 
public class Servicemain extends IntentService {
	private SensorManager mSensorManager;
	private Sensor proximity,accelaration,lightSensor;
	private AudioManager audio;
	
	public static boolean isRunning  = false;
	private Looper looper;
	private Handler myServiceHandler;
	
	private float prox;
	public float accelar[];
	public float light[];
	
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    
    public long lastUpdate = 0;
    public float last_x,last_y,last_z;
    public static final int SHAKE_THRESHOLD = 200;
    
    boolean faceUp=false,inFrontHas=false,lightOn=false,shacking=false;
   
    float x,y,z;
	
	
	
    public Servicemain() {
		super("Servicemain Constructor");
		// TODO Auto-generated constructor stub
	}

    @Override
    public void onCreate() {
    	super.onCreate();
        Log.d("Entry-Log","Inside OnCreate() on Servicemain()");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		proximity=(Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		accelaration=(Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		lightSensor = (Sensor)mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		audio=(AudioManager)getSystemService(AUDIO_SERVICE);
		audio.getRingerMode();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d("Entry-Log","Inside onDestroy() on Servicemain()");
    	showToast("Service Stoped");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }
    
   
    SensorEventListener proximityListener=new SensorEventListener() {
		//@Override
		public void onSensorChanged(SensorEvent event) {
			prox=event.values[0];   
		    if (prox == 0){
		    	Log.d("Proximity-Zero","Proximity : " + prox);
		    	inFrontHas = true;
		    } 
		    else {
		    	Log.d("Proximity-high","Proximity : " + prox);
		    	inFrontHas = false;
		    }
		}

		//@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			Log.d("Proximity-work","Proximity Changed");
		}
	};
	
	
	SensorEventListener accelListener=new SensorEventListener() {
		//@Override
		public void onSensorChanged(SensorEvent event) {
			accelar=event.values;
			x = accelar[0];
			y = accelar[1];
			z = accelar[2];
			if(z> 2.0){
		         faceUp = true;
		         Log.d("Accelerometer-Works", "Mobile Display Up");
		    } 
		    else if (z <= 2.0){
		         faceUp = false;
		         Log.d("Accelerometer-Works", "Mobile Display Down");
		    } 
			 
		    long curTime = System.currentTimeMillis();
		    if ((curTime - lastUpdate) > 1000) {
		    	  long diffTime = (curTime - lastUpdate);
		          lastUpdate = curTime;
		          x = accelar[0];
		          y = accelar[1];
		          z = accelar[2];

		          float speed = Math.abs(x + y + z-last_x -last_y-last_z) / diffTime * 10000;

		          if (speed > SHAKE_THRESHOLD) {
		        	  Log.d("sensor", "shake detected w/ speed: " + speed);
		              shacking=true;
		              showToast("shake detected w/ speed: " + speed);
		          }
		          else {
		              shacking = false;
		          }
		          
		          last_x = x;
		          last_y = y;
		          last_z = z;
		      }

		}
		//@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
	};
	
	
	SensorEventListener lightListener=new SensorEventListener() {
		//@Override
		public void onSensorChanged(SensorEvent event) {
			light=event.values;
		    if (light[0]< 15){
		    	lightOn = false;
		    }
		    else if (light[0] >= 15){
		        lightOn = true;
		    }
		}
		//@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
	};

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
	 	 new Thread(new Runnable() {
			public void run() {
				mSensorManager.registerListener(proximityListener, proximity, SensorManager.SENSOR_DELAY_NORMAL);
				mSensorManager.registerListener(accelListener, accelaration,SensorManager.SENSOR_DELAY_NORMAL);
				mSensorManager.registerListener(lightListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);

				while(Servicemain.isRunning==true){
					
				    if (faceUp && !inFrontHas && audio.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL){
				    	Log.d("Switch-Profile", "Ring Mode");
				    	audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				    	audio.setStreamVolume(AudioManager.STREAM_RING,audio.getStreamMaxVolume(AudioManager.STREAM_RING),0);
				    }

				    else if (!faceUp && inFrontHas && !lightOn && audio.getRingerMode()!=AudioManager.RINGER_MODE_SILENT){
				         Log.d("Switch-Profile", "Silent Mode");
				         audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				         if(audio.getMode()==AudioManager.RINGER_MODE_SILENT ) {
				        	    //myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				        	 	audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
				        }
				    }
				    
				    else if (shacking && audio.getRingerMode()!=AudioManager.RINGER_MODE_VIBRATE){
				        Log.d("Switch-Profile", "Pocket Mode");
				        audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				        audio.setStreamVolume(AudioManager.STREAM_RING,20,0);
				    }
				  
				    
					try {
						Thread.sleep(10000);
					} 
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				mSensorManager.unregisterListener(proximityListener);
				mSensorManager.unregisterListener(accelListener);
				mSensorManager.unregisterListener(lightListener);
			}
		}).run();
	}

	private void showToast(final String msg) {
		// TODO Auto-generated method stub
		Handler handler = new Handler(Looper.getMainLooper());
	    handler.post(new Runnable() {
	        public void run() {
	            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
 
}