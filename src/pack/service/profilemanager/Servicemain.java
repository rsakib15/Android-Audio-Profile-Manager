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
	private Sensor proximity,accelaration;
	private AudioManager audio;
	
	private static final String TAG = "MyService";
	public static boolean isRunning  = false;
	private Looper looper;
	private Handler myServiceHandler;
	private float prox;
	public float acc[];
	
	float mAccel = (float) 0.00;
	float mAccelCurrent = SensorManager.GRAVITY_EARTH;
	float mAccelLast = SensorManager.GRAVITY_EARTH;
	
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
			if(prox==0){
				audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
			else{
				audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}		
			showToast(String.valueOf(prox));
		}
		
		//@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	
	SensorEventListener accelListener=new SensorEventListener() {
		//@Override
		public void onSensorChanged(SensorEvent event) {
			acc=event.values;
			
			 x = acc[0];
	         y = acc[1];
	         z = acc[2];
			
	        
			//showToast("x: " + String.valueOf(acc[0]) + " y: " + String.valueOf(acc[1]) + " z: " + String.valueOf(acc[2]) );
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
				mSensorManager.registerListener(accelListener, accelaration,SensorManager.SENSOR_DELAY_UI);
				while(Servicemain.isRunning==true){

			        //float norm_Of_g =FloatMath.sqrt(x * x + y * y + z * z);

			        // Normalize the accelerometer vector
			        //x = (x / norm_Of_g);
			        //y = (y / norm_Of_g);
			        //z = (z / norm_Of_g);
			        //int inclination = (int) Math.round(Math.toDegrees(Math.acos(z)));
			        //Log.i("tag","incline is:"+inclination);

			        if (z > 9.0 && (x<=0.0) && (y<=0.0))
			        {
			            // device is flat
			        	 showToast("UP");
			        }
			        if (z < 0.0  && (x<=0.0) && (y<=0.0))
			        {
			            // device is flat
			            showToast("down");
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