package skills;

import Sensor.OwnColorSensor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.DebugMessages;

public class Sensors {
	
	private static DebugMessages message = new DebugMessages(4);
	private static EV3UltrasonicSensor sonic;
	private static EV3TouchSensor touch;
	private static EV3GyroSensor gyro;
	private static OwnColorSensor col;
	private static EV3MediumRegulatedMotor motor;
	private static final int SONIC_MOVEMENT_ANGLE = 1300;
	private static enum SonicStates {
		UP,
		DOWN
	}
	private static SonicStates sonicState = SonicStates.DOWN;
	/**
	 * Default position sonicUp -> measured distance is ~15cm
	 * SonicDown means 90� down
	 */
	public static void sonicDown() {
		// Forward = up
		// Backward = down
		if (sonicState == SonicStates.UP) {
			getMedMotor().setSpeed(300);
			getMedMotor().rotate(-SONIC_MOVEMENT_ANGLE, true);
		}
		sonicState = SonicStates.DOWN;
	}
	
	public static void sonicUp() {
		if (sonicState == SonicStates.DOWN) {
			getMedMotor().setSpeed(300);
			getMedMotor().rotate(SONIC_MOVEMENT_ANGLE, false);
		}
		sonicState = SonicStates.UP;
	}

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
		
		// Use calibrated position as "UP" position
		sonicState = SonicStates.UP;
		
		while(sample[0] < distance){
			getSonic().getDistanceMode().fetchSample(sample, 0);
			message.echo("Dist: " + sample[0]);
		}	
		getMedMotor().stop();
		getSonic().getDistanceMode().fetchSample(sample, 0);
		message.clear();
		message.echo(sample[0] + " - diff: " + (sample[0] - distance));
	}
	
	/**
	 * Sensor constructor. Never call Lejos constructor since it does not check if port is already in use 
	 * by another instance of the same sensor.
	 * @return Ultrasonic sensor of the Robot.
	 */
	public static EV3UltrasonicSensor getSonic() {
		if (sonic == null) {
			sonic = new EV3UltrasonicSensor(SensorPort.S3);
		}
		return sonic;
	}
	
	/**
	 * Sensor constructor. Never call Lejos constructor since it does not check if port is already in use 
	 * by another instance of the same sensor.
	 * <br><b>Currently we don't use a touch sensor.</b>
	 * @return Touch sensor of the Robot.
	 */
	public static EV3TouchSensor getTouch() {
		if (touch == null) {
			touch = new EV3TouchSensor(SensorPort.S4);
		}
		return touch;
	}
	
	/**
	 * Sensor constructor. Never call Lejos constructor since it does not check if port is already in use 
	 * by another instance of the same sensor.
	 * @return Gyro sensor of the Robot.
	 */
	public static EV3GyroSensor getGyro() {
		if (gyro == null) {
			gyro = new EV3GyroSensor(SensorPort.S1);
		}
		return gyro;
	}
	
	/**
	 * Sensor constructor. Never call Lejos constructor since it does not check if port is already in use 
	 * by another instance of the same sensor.
	 * @return Color sensor of the Robot.
	 */
	public static OwnColorSensor getColor() {
		if (col == null) {
			col = new OwnColorSensor(SensorPort.S2);
		}
		return col;
	}
	
	/**
	 * Motor constructor. Never call Lejos constructor since it does not check if port is already in use 
	 * by another instance of the same sensor.
	 * @return Motor to align the ultrasonic sensor of the Robot.
	 */
	public static EV3MediumRegulatedMotor getMedMotor() {
		if (motor == null) {
			motor = new EV3MediumRegulatedMotor(MotorPort.B);
		}
		return motor;
	}
}
