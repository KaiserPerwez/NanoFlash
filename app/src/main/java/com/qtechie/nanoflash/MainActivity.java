package com.qtechie.nanoflash;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    private android.hardware.Camera.Parameters params;
    private TextView tv_flash_status,tv_error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        final RelativeLayout mainScreen=(RelativeLayout)findViewById(R.id.mainScreen);
        mainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenTapped(view);
            }
        });

        tv_flash_status=(TextView) findViewById(R.id.tv_flash_status);
        tv_error= (TextView) findViewById(R.id.tv_error);

		/*
		 * First check if device is supporting flashlight or not
		 */
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            tv_flash_status.setText("Oops!");
            try{tv_error.setVisibility(View.VISIBLE);}
            catch (Exception e){
                Toast.makeText(this, "Error hasFlash: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            tv_error.setText("Sorry, your device doesn't support flash light! Please EXIT");
            return;
        }

        // get the camera
        getCamera();

        // displaying button image
        //------auto lauch flashOn at app launch----//
        if (isFlashOn)// turn off flash
            turnOffFlash();
        else         // turn on flash
            turnOnFlash();
    }
    public void screenTapped(View view) {
        try {
            if (tv_flash_status.getText().equals("Oops!"))
                tv_error.setVisibility(View.VISIBLE);
            else
                tv_error.setVisibility(View.GONE);

            if (isFlashOn)// turn off flash
                turnOffFlash();
            else         // turn on flash
                turnOnFlash();

        }
        catch (Exception e){
            Toast.makeText(this, "Screen Tap error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // Your code here
    }
    /*
     * Get the camera
     */
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Toast.makeText(this, "CameraOpenFailed.Error:"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Turning On flash
     */
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            // changing button/switch image
            tv_flash_status.setText("ON");
        }

    }

    /*
     * Turning Off flash
     */
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            try {
                params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                isFlashOn = false;
            }
            catch (Exception e){
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            tv_flash_status.setText("OFF");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // on resume turn on the flash
        if (hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}
