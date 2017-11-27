package skills;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.DebugMessages;

public class Sensors {
	
	private static DebugMessages message = new DebugMessages(4);
	private static EV3UltrasonicSensor sonic;
	private static EV3TouchSensor touch;
	private static EV3GyroSensor gyro;
	private static EV3ColorSensor col;
	private static EV3MediumRegulatedMotor motor;
	

	/**
	 * Uses small motor to move ultrasonic Sensor to measure the desired distance. The motor is set 
	 * to the smallest measurement point BIGGER that the given distance. </br>
	 * Motor stalls afterwards to fix sensor position.
	 * </br></br>
	 * Push sensor arm down manually before calling.
	 * @param distance Distance to set sensor in meter (so 0.xy for cm)
	 */
	public static void calibrateSonic(float distance) {
		float [] sample = new float [] {0.0f};
		getSonic().getDistanceMode().fetchSample(sample, 0);
		getMedMotor().setSpeed(300);
		getMedMotor().forward();
		
		
		while(sample[0] < distance){
			getSonic().getDistanceMode().fetchSample(sample, 0);
			message.echo("Dist: " + sample[0]);
		}	
		getMedMotor().stop();
		getSonic().getDistanceMode().fetchSample(sample, 0);
		message.clear();
		message.echo(sample[0] + " - diff: " + (sample[0] - distance));
	}
	
	public static EV3UltrasonicSensor getSonic() {
		if (sonic == null) {
			sonic = new EV3UltrasonicSensor(SensorPort.S3);
		}
		return sonic;
	}
	
	public static EV3TouchSensor getTouch() {
		if (touch == null) {
			touch = new EV3TouchSensor(SensorPort.S4);
		}
		return touch;
	}
	
	public static EV3GyroSensor getGyro() {
		if (gyro == null) {
			gyro = new EV3GyroSensor(SensorPort.S1);
		}
		return gyro;
	}
	
	public static EV3ColorSensor getColor() {
		if (col == null) {
			col = new EV3ColorSensor(SensorPort.S2);
		}
		return col;
	}
	
	public static EV3MediumRegulatedMotor getMedMotor() {
		if (motor == null) {
			motor = new EV3MediumRegulatedMotor(MotorPort.B);
		}
		return motor;
	}
}
