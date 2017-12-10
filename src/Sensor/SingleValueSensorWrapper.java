package Sensor;

import lejos.hardware.sensor.BaseSensor;
import lejos.hardware.sensor.SensorMode;

public class SingleValueSensorWrapper {

	private SensorMode mode;
	private float[] samples;

	public SingleValueSensorWrapper(BaseSensor sensor, String mode) {
		this.mode = sensor.getMode(mode);
		this.samples = new float[this.mode.sampleSize()];
	}
	
	public float getSample()
	{
		mode.fetchSample(samples, 0);
		return samples[0];
	}
	

}

