package com.example.josephpalmowski.headgestureapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class
MainActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {

    private BluetoothAdapter mBluetoothAdapter;
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    Handler handler3 = new Handler();
    Handler handler4 = new Handler();
    Boolean continueToMoveLeft = false;
    Boolean continueToMoveRight = false;
    Boolean continueToMoveDown = false;
    Boolean continueToMoveUp = false;
    int directionLatch = 0;
    float currentVerticalVelocity, previousVerticalVelocity, currentHorizontalVelocity, previousHorizontalVelocity  = 0.0f;
    long currentTime, previousTime = 0L;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    String hex = "";
    ImageView cursor, cursor2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        mBluetoothAdapter.startLeScan(this);

        cursor = (ImageView) findViewById(R.id.cursor);
        cursor2 = (ImageView) findViewById(R.id.cursor);

        int left2 = cursor2.getLeft();
        int left1 = cursor.getLeft();

     //   System.out.println("Cursor 1 Current location: " + left1);
      //  System.out.println("Cursor 2 Current location: " + left2);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        mBluetoothAdapter.startLeScan(this);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        // System.out.println(device.getName());

        hex = bytesToHex(scanRecord);
        System.out.println(hex);
        // System.out.println(device.getName());
        //System.out.println(device.getAddress());

        if (device.getAddress().equals("FF:9D:B3:3F:50:34")) {

            previousHorizontalVelocity = currentHorizontalVelocity;
            previousVerticalVelocity = currentVerticalVelocity;

            previousTime = currentTime;
            currentTime = System.currentTimeMillis();
            String output = hex.substring(4, 12);
            String output2 = hex.substring(12, 20);
            String output3 = hex.substring(20, 28);

            Long i = Long.parseLong(output, 16);
            Float f = Float.intBitsToFloat(i.intValue());
            currentHorizontalVelocity = f;
            //positive f is head left, negative f is head right
            Long i2 = Long.parseLong(output2, 16);
            Float f2 = Float.intBitsToFloat(i2.intValue());
            currentVerticalVelocity = f2;
            //positve f2 is head up, negative f2 is head down
            Long i3 = Long.parseLong(output3, 16);
            Float f3 = Float.intBitsToFloat(i3.intValue());
            System.out.println("Gyro 1: " + f + " Gyro 2: " + f2 + " Gyro 3: " + f3);
            handler.postDelayed(moveCursorHorizontal, 0);
            handler3.postDelayed(moveCursorVertical, 0);


        }








    }
    


    public Runnable moveCursorHorizontal = new Runnable() {
        @Override
        public void run() {
            float averageHorizontalVelocity = (currentHorizontalVelocity + previousHorizontalVelocity) /2;
            long elapsedTime = currentTime - previousTime;
            float horizontalDisplacement = averageHorizontalVelocity * elapsedTime;

            int leftPos = cursor.getLeft();
            if ((leftPos > 300) || (leftPos < -300)){
                leftPos = 0;
            }
            System.out.println("left: " + leftPos);

            int setLeftPos = leftPos + (Math.round(horizontalDisplacement)/3) ;
                cursor.setLeft(setLeftPos);

           // }
           // if (continueToMoveLeft) {
               // handler.postDelayed(this, 0);
            handler.removeCallbacks(this, 0);
           // }
            //  else  {handler.removeCallbacks(this, 0);}



        }
    };


    public Runnable moveCursorVertical = new Runnable() {
        @Override
        public void run() {
            float averageVerticalVelocity = (currentVerticalVelocity + previousVerticalVelocity) /2;
            long elapsedTime = currentTime - previousTime;
            float verticalDisplacement = averageVerticalVelocity * elapsedTime;

            int topPos = cursor.getTop();
            if ((topPos < -175) || (topPos > 175))  {
                topPos = 0;
            }
            System.out.println("top: " + topPos);
                int setTopPos = topPos - (Math.round(verticalDisplacement)/6);
                cursor.setTop(setTopPos);
         //   }
          //  if (continueToMoveUp) {
              //  handler3.postDelayed(this, 0 );
            handler3.removeCallbacks(this,0);
           //     int up1 = cursor.getTop();
              //  System.out.println("Cursor 1 Current location: " + up1);

           // }
           // else { handler3.removeCallbacks(this,0);}
        }
    };


    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }
}
