package com.alexmejicanos.atfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private Gpio ledGpio;
    private String pinLed = "BCM5";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("GPIOLED");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            ledGpio = service.openGpio(pinLed);
            ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Log.i(TAG, "Start blinking LED GPIO pin");
        } catch (IOException e) {
            Log.e(TAG, "ERROR on PeripheralIO API", e);
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = dataSnapshot.getValue(Boolean.class);
                Log.e(TAG, "VALUE CHANGED: " + value);
                changeStatusLed(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Closing LED GPIO pin");
        try{
            ledGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "ERROR on PeripheralIO API", e);
        } finally {
            ledGpio = null;
        }
    }

    private void changeStatusLed(boolean status) {
        try {
            if (ledGpio != null)
                ledGpio.setValue(status);
        } catch (IOException e){
            Log.e(TAG, "ERROR on PeripheralIO API", e);
        }
    }
}
