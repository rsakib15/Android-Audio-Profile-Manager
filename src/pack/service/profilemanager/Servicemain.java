package pack.service.profilemanager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
 
public class Servicemain extends IntentService {
	private SensorManager mSensorManager;
	private Sensor proximity,accelaration,lightSensor;
	private AudioManager audio;
	
	public static boolean isRunning  = false;
	private float prox;
	public float accelar[];
	public float light[];
	
    public long lastUpdate = 0;
    public float last_x,last_y,last_z;
    public static final int SHAKE_THRESHOLD = 100;
    
    boolean faceUp=false,inFrontHas=false,lightOn=false,shacking=false,pocketOn=false;
   
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
			 
			if(y<=-7.0 || y>=7.0 ){
				pocketOn=true;
			}
			else{
				pocketOn=false;
			}
		    long curTime = System.currentTimeMillis();
		    if ((curTime - lastUpdate) > 1000) {
		          lastUpdate = curTime;
		          x = accelar[0];
		          y = accelar[1];
		          z = accelar[2];
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
				    if (faceUp && !pocketOn){
				    	Log.d("Switch-Profile", "Ring Mode");
				    	audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  
				    	showToast("Normal Mode");
				    }
				    
				    else if (faceUp && !lightOn && !pocketOn && !inFrontHas){
				    	Log.d("Switch-Profile", "Ring Mode");
				    	audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				    	showToast("Normal Mode");
				    }
				    
				    else if (!faceUp && inFrontHas && !lightOn && !pocketOn){ 
				         Log.d("Switch-Profile", "Silent Mode");
				         showToast("Silent Mode");
				         audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				         if(audio.getMode()==AudioManager.RINGER_MODE_SILENT ) {
				        	 	audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
				        }
				    }
				       	    
				    else if (pocketOn && inFrontHas && !lightOn){
				        Log.d("Switch-Profile", "Pocket Mode");
				        showToast("Pocket Mode");
				        audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
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