package hr.vsite.sensor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    ArrayList<String> temperatures = new ArrayList();
    ArrayList<String> humidities = new ArrayList();

    final int SENSOR_SAMPLING_PERIOD_US = 20 * 1000000; //u mikrosekundama, ne znači da će se Android držati toga

    private SensorManager sensorManager;
    private Sensor at_sensor, rh_sensor;
    private float ambientTemperature, relativeHumidity, prevTemperature, prevHumidity;
    private long samples = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        at_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        rh_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (at_sensor != null) {
            sensorManager.registerListener(this, at_sensor, SENSOR_SAMPLING_PERIOD_US);
        } else disableTempSensor();
        if(rh_sensor != null) {
            sensorManager.registerListener(this, rh_sensor, SENSOR_SAMPLING_PERIOD_US);
        } else disableHumSensor();
        if(at_sensor == null && rh_sensor == null) {
            Toast.makeText(this, "No available sensors!", Toast.LENGTH_LONG);
        } else sensorManager.flush(this);
    }

    public void disableTempSensor(){
        findViewById(R.id.tv_temp_label).setEnabled(false);
        findViewById(R.id.tv_temp_val).setEnabled(false);
        findViewById(R.id.tv_temp_unit).setEnabled(false);
    }

    public void disableHumSensor(){
        findViewById(R.id.tv_hum_label).setEnabled(false);
        findViewById(R.id.tv_hum_unit).setEnabled(false);
        findViewById(R.id.tv_hum_val).setEnabled(false);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values.length > 0) {
            Button bt_api = findViewById(R.id.bt_chart);
            if(samples > 10) {
                bt_api.setTextColor(Color.GREEN);
            } else {
                bt_api.setTextColor(Color.RED);
            }
            if (at_sensor.equals(sensorEvent.sensor)) {
                ambientTemperature = sensorEvent.values[0];
                if (prevTemperature != ambientTemperature){
                    String temperatureStr = String.format("%.1f", ambientTemperature);
                    temperatures.add(temperatureStr);
                    TextView tv_temperature = findViewById(R.id.tv_temp_val);
                    tv_temperature.setText(temperatureStr);
                    samples++;
                    prevTemperature = ambientTemperature;
                }
            }
            if (rh_sensor.equals(sensorEvent.sensor)) {
                relativeHumidity = sensorEvent.values[0];
                if(prevHumidity != relativeHumidity){
                    String humidityStr = String.format("%.1f", relativeHumidity);
                    humidities.add(humidityStr);
                    TextView tv_humidity = findViewById(R.id.tv_hum_val);
                    tv_humidity.setText(humidityStr);
                    samples++;
                    prevHumidity = relativeHumidity;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onClick(View v) {

    }
}
