package Sensor;

import lejos.hardware.sensor.EV3ColorSensor;
import skills.Sensors;

//only copied from templates on ILIAS

public class SensorThread extends Thread {

	private SingleValueSensorWrapper Sgyro;
	private EV3ColorSensor Scolor;
	private SingleValueSensorWrapper Sdistance;

	private float gyroAngle, distance;
	private int colorID;
	
	//public SensorThread(SingleValueSensorWrapper Sgyro, OwnColorSensor Scolor, SingleValueSensorWrapper Sdistance) {
	public SensorThread() {	
		this.Sgyro = new SingleValueSensorWrapper(Sensors.getGyro(), "Angle");
		this.Scolor = Sensors.getColor();
		this.Sdistance = new SingleValueSensorWrapper(Sensors.getSonic(), "");
	}
	
	public float getGyroAngle() {
		return gyroAngle;
	}
	
	public int getColorID() {
		return colorID;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public void run() {
		try {
			while (true) {
				gyroAngle = Sgyro.getSample();
				colorID = this.Scolor.getColorID();
				distance = this.Sdistance.getSample();
				Thread.sleep(20);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
