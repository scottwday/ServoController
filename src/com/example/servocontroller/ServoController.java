package com.example.servocontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;






import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;



public class ServoController extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnCheckedChangeListener
{

	  
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private OutputStream outStream = null;
    
  ConnectedThread btThread = null;
  
  // SPP UUID service 
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  
  SeekBar seekBar1;
  SeekBar seekBar2;
  SeekBar seekBar3;
  SeekBar seekBar4;
  
  ToggleButton toggleButton1;
  ToggleButton toggleButton2;
  ToggleButton toggleButton3;
  ToggleButton toggleButton4;
  
  ToggleButton toggleButton5;
  ToggleButton toggleButton6;
  ToggleButton toggleButton7;
  ToggleButton toggleButton8;

  Button button1;
  Button button2;
  Button button3;
  Button button4;

  Button button5;
  Button button6;
  Button button7;
  Button button8;

  
  Button btnConnect;
  
  TextView lblStatus;
  
  Timer timer;
  TimerTask timerTask;
	
  int setpoint = 0;
  String btBuf = "";
  
  boolean isConnected = false;
  
  private static final String TAG = "bluetooth1";
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servo_controller);
        
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
        seekBar4 = (SeekBar) findViewById(R.id.seekBar4);

        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        toggleButton3 = (ToggleButton) findViewById(R.id.toggleButton3);
        toggleButton4 = (ToggleButton) findViewById(R.id.toggleButton4);
        toggleButton5 = (ToggleButton) findViewById(R.id.toggleButton5);
        toggleButton6 = (ToggleButton) findViewById(R.id.toggleButton6);
        toggleButton7 = (ToggleButton) findViewById(R.id.toggleButton7);
        toggleButton8 = (ToggleButton) findViewById(R.id.toggleButton8);

        
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        
        btnConnect = (Button) findViewById(R.id.btnConnect);
        lblStatus = (TextView) findViewById(R.id.lblStatus);
        
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar3.setOnSeekBarChangeListener(this);
        seekBar4.setOnSeekBarChangeListener(this);
        
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);

        toggleButton1.setOnCheckedChangeListener(this);
        toggleButton2.setOnCheckedChangeListener(this);
        toggleButton3.setOnCheckedChangeListener(this);
        toggleButton4.setOnCheckedChangeListener(this);
        toggleButton5.setOnCheckedChangeListener(this);
        toggleButton6.setOnCheckedChangeListener(this);
        toggleButton7.setOnCheckedChangeListener(this);
        toggleButton8.setOnCheckedChangeListener(this);
        
        btnConnect.setOnClickListener(this);
                
        timer = new Timer();
        timerTask = new UpdateTimer();
    	
        timer.scheduleAtFixedRate(timerTask, 1000, 200);
        
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

    }
    
    /*
    public void onPause()
    {
    	super.onPause();
        timer.cancel();
        timer.purge();
    }

    public void onResume()
    {
    	timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }*/
    
    
    private BluetoothDevice GetDevice(String name)
    {
    	String str = "";
    	BluetoothDevice result = null;
    	BluetoothDevice first = null;
    	Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
    	// If there are paired devices
    	if (pairedDevices.size() > 0) 
    	{
    	    // Loop through paired devices
    	    for (BluetoothDevice device : pairedDevices) 
    	    {
    	    	String deviceName = device.getName();
    	        // Add the name and address to an array adapter to show in a ListView
    	        str = str + deviceName + " : " + device.getAddress() + "\n";
    	        
    	        if (deviceName.toLowerCase().equals(name.toLowerCase()))
    	        	result = device;
    	        
    	        if (first == null)
    	        	first = device;
    	    }
    	}
    	
    	if (result == null)
    	{
    		result = first;
    		Toast.makeText(getBaseContext(), "Linvor not found, using Device: " + result.getName(), Toast.LENGTH_SHORT).show();
    	}
    	
    	return result;
    }
    
    
    
    public void onClick(View v)
    {
    	switch (v.getId())
    	{
    	case R.id.btnConnect:
    		//Toast.makeText(getBaseContext(), "Connecting...", Toast.LENGTH_SHORT).show();
    		
    		BluetoothDevice device = null;
    		
    		device = GetDevice("linvor");
    		
    		if (device != null)
    		{
    			Toast.makeText(getBaseContext(), "Connecting to " + device.getName() + " at " + device.getAddress(), Toast.LENGTH_SHORT).show();
    		
    			ConnectThread t = new ConnectThread(device);
    			t.start();
    			
    		}
    		else
    		{
    			Toast.makeText(getBaseContext(), "Couldnt find paired linvor device", Toast.LENGTH_SHORT).show();
        				
    		}
    		
    		break;
    		
    	case R.id.toggleButton1:
    		OnToggleButtonClicked(1, toggleButton1.isChecked());
    		break;
    		
    	case R.id.toggleButton2:
    		OnToggleButtonClicked(2, toggleButton2.isChecked());
    		break;
    		
    	case R.id.toggleButton3:
    		OnToggleButtonClicked(3, toggleButton3.isChecked());
    		break;
    		
    	case R.id.toggleButton4:
    		OnToggleButtonClicked(4, toggleButton4.isChecked());
    		break;

    	case R.id.toggleButton5:
    		OnToggleButtonClicked(5, toggleButton5.isChecked());
    		break;
    		
    	case R.id.toggleButton6:
    		OnToggleButtonClicked(6, toggleButton6.isChecked());
    		break;
    		
    	case R.id.toggleButton7:
    		OnToggleButtonClicked(7, toggleButton7.isChecked());
    		break;
    		
    	case R.id.toggleButton8:
    		OnToggleButtonClicked(8, toggleButton8.isChecked());
    		break;
    		
    		
    	case R.id.button1:
    		OnButtonClicked(1);
    		break;
    		
    	case R.id.button2:
    		OnButtonClicked(2);
    		break;
    		
    	case R.id.button3:
    		OnButtonClicked(3);
    		break;
    		
    	case R.id.button4:
    		OnButtonClicked(4);
    		break;
    		
    	case R.id.button5:
    		OnButtonClicked(5);
    		break;
    		
    	case R.id.button6:
    		OnButtonClicked(6);
    		break;
    		
    	case R.id.button7:
    		OnButtonClicked(7);
    		break;
    		
    	case R.id.button8:
    		OnButtonClicked(8);
    		break;
    		
    	}
    }
    
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
		switch (seekBar.getId())
		{
		case R.id.seekBar1:
			OnSliderChange(1, progress);
			break;
		case R.id.seekBar2:
			OnSliderChange(1, progress);
			break;
		case R.id.seekBar3:
			OnSliderChange(1, progress);
			break;
		case R.id.seekBar4:
			OnSliderChange(1, progress);
			break;
		
		}
		
	}

	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.toggleButton1:
			OnToggleButtonClicked(1, arg1);
			break;
		case R.id.toggleButton2:
			OnToggleButtonClicked(2, arg1);
			break;
		case R.id.toggleButton3:
			OnToggleButtonClicked(3, arg1);
			break;
		case R.id.toggleButton4:
			OnToggleButtonClicked(4, arg1);
			break;
		case R.id.toggleButton5:
			OnToggleButtonClicked(5, arg1);
			break;
		case R.id.toggleButton6:
			OnToggleButtonClicked(6, arg1);
			break;
		case R.id.toggleButton7:
			OnToggleButtonClicked(7, arg1);
			break;
		case R.id.toggleButton8:
			OnToggleButtonClicked(8, arg1);
			break;
		}
	}
	
    
    
    void OnToggleButtonClicked(int n, boolean state)
    {
    	if (n <= 4)
    	{
    		lblStatus.setText("Motor " + n + " = " + state);
    		SetServoPower(n, !state);
    	}
    	else
    	{
    		n = n - 4;
    		lblStatus.setText("Toggle " + n + " = " + state);
    		SetOutput(n, !state);
    	}
    }
    
    void OnButtonClicked(int n)
    {    	
    	lblStatus.setText("Button " + n);
    	
    	//Will play 1.wav, 2.wav, 3.wav etc
    	PlayWav("" + n);
    }
    
    void OnSliderChange(int n, int v)
    {
    	lblStatus.setText("Slider " + n + " = " + v);    	
    }
    

    int lastSlider1 = 0;
    int lastSlider2 = 0;
    int lastSlider3 = 0;
    int lastSlider4 = 0;
    
    public void OnTimer()
    {
    	
    	if (lastSlider1 != seekBar1.getProgress())
    	{
    		lastSlider1 = seekBar1.getProgress();
    		SetServo(1,lastSlider1);
    	}
    	
    	if (lastSlider2 != seekBar2.getProgress())
    	{
    		lastSlider2 = seekBar2.getProgress();
    		SetServo(2,lastSlider2);
    	}
    	
    	if (lastSlider3 != seekBar3.getProgress())
    	{
    		lastSlider3 = seekBar3.getProgress();
    		SetServo(3,lastSlider3);
    	}
    	
    	if (lastSlider4 != seekBar4.getProgress())
    	{
    		lastSlider4 = seekBar4.getProgress();
    		SetServo(4,lastSlider4);
    	}
    	
    }
    
    public void OnGotSerialText(String text)
    {
    	isConnected = true;
    	
    	if (text != "")
    		lblStatus.setText(text);    	
    }
    
    public void OnConnected()
    {
    	isConnected = true;
    	
    	btnConnect.setText("Connected");
    }
    
    void writeString(String cmd)
    {
    	if (isConnected)
    		btThread.writeString(cmd);	
    }
    
    
    public void SetServo(int n, int v)
    {
    	char channel = (char) ('a' + n - 1);
    	
    	String cmd = "" + channel + String.format("%02d", v) + "\n";
    	writeString(cmd);
    }
    
    public void SetOutput(int n, Boolean v)
    {
    	char channel = (char) ('A' + n - 1);
    	char value = v?'0':'1';
    	
    	String cmd = "" + channel + value + "\n";
    	writeString(cmd);
    }
    
    public void SetServoPower(int n, Boolean v)
    {
    	char channel = (char) ('0' + n - 1);
    	char value = v?'0':'1';
    	
    	String cmd = "" + channel + value + "\n";
    	writeString(cmd);
    }
    
    public void PlayWav(String file)
    {
    	String cmd = "p" + file + "\n";
    	writeString(cmd);
    }
    
    
    
    
    
    public void manageConnectedSocket(BluetoothSocket mmSocket)
    {
    	btThread = new ConnectedThread(mmSocket);
    	btThread.start();
    	
    }
        
    void errorExit(String a, String b)
    {
    	
    	
    }
    
    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) 
        { 
          errorExit("Fatal Error", "Bluetooth not support");
        } 
        else 
        {
          if (btAdapter.isEnabled()) 
          {
            Log.d(TAG, "...Bluetooth ON...");
          } 
          else 
          {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
          }
        }
      }
    
    
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
      
        Log.d(TAG, "...Send data: " + message + "...");
      
        try 
        {
          outStream.write(msgBuffer);
        } 
        catch (IOException e) 
        {
          //String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
          //if (address.equals("00:00:00:00:00:00")) 
          //  msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
          //  msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
           
          errorExit("Fatal Error", "Something bad happened");       
        }
      }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_servo_controller, menu);
        return true;
    }
            
    class UpdateTimer extends TimerTask {
    	   
    	   public void run() 
    	   {
    		   ServoController.this.runOnUiThread(new Runnable() 
    		   	{
    			   public void run() 
    			   {
    				   OnTimer();
    			   }
    		   });
    	   }
    	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

public class ConnectThread extends Thread 
{

	
	private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    
    public ConnectThread( BluetoothDevice device) 
    {    
   
    	// Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
    	BluetoothSocket tmp = null;
        mmDevice = device;
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try 
        {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } 
        catch (IOException e) 
        { 
        	
        }
        mmSocket = tmp;
    }
 
    public void run() {
        // Cancel discovery because it will slow down the connection
    	btAdapter.cancelDiscovery();
 
        try 
        {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } 
        catch (IOException connectException) 
        {
        	// Unable to connect; close the socket and get out
            try 
            {
                mmSocket.close();
            } 
            catch (IOException closeException) 
            { 
            	
            }
            
            try
            {
            	Toast.makeText(getBaseContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
            	
            }
            
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
    
    
    
    
}
    
    
    
    
    
    private class ConnectedThread extends Thread 
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
     
        public ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
     
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
     
        class InvokeOnConnected implements Runnable
        {
        	public void run()
        	{
        		OnConnected();
        	}
        }
     
        
        class InvokeOnGotSerialText implements Runnable
        {
        	String str;
        	InvokeOnGotSerialText(String s) { str = s; }
        	public void run()
        	{
        		OnGotSerialText(str);
        	}
        }
     
        public void run() 
        {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
     
            //Notify the UI that we are connected
            ServoController.this.runOnUiThread(new InvokeOnConnected() );
            
            // Keep listening to the InputStream until an exception occurs
            while (true) 
            {
                try 
                {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    
                    // Send the obtained bytes to the UI activity
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    
                    String s = "";
                    for (int i=0; i<bytes; i++)
                    {
                    	s = s + (char)buffer[i];
                    }
                    
                    ServoController.this.runOnUiThread(new InvokeOnGotSerialText(s) );
                    
                    //OnGotSerialText(s);
                    
                } 
                catch (IOException e) 
                {
                    break;
                }
            }
        }
     
        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }
     
        public void writeString(String s)
        {
        	byte[] b = s.getBytes();
        	
        	write(b);
        }
        
        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	
    
    
    
}
