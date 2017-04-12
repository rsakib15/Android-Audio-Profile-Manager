package pack.service.profilemanager;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity{
	Intent i;
	Thread t= null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Entry-Log","Inside the Oncreate() on Service Activity");
        setContentView(R.layout.main);
    }
   
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("Entry-Log","Inside the OnResume() on Service Activity");
	}

	
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("Entry-Log","Inside the OnStart() on Service Activity");
	}
    
    public void onStop(View arg) {
		Log.d("Entry-Log","Inside the OnStart() on Service Activity");
		Servicemain.isRunning=false;
		stopService(i);
	}

	public void startClicked(View arg0){
    	i = new Intent(this, Servicemain.class);
    	Log.d("Entry-Log","Inside the StartClicked() on Service Activity");
    	Servicemain.isRunning=true;
    	startService(i); 
    }
    
    public void stopClicked(View arg0){
    	Log.d("Entry-Log","Inside the StopClicked() on Service Activity");
    	Servicemain.isRunning=false;
    	stopService(i);
    	
    }
}