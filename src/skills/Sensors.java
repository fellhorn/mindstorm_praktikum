package skills;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.DebugMessages;

public class Sensors {
	
	private static DebugMessages message = new DebugMessages(4);
	private static EV3UltrasonicSensor sonic;
	private static EV3MediumRegulatedMotor motor;
	

	public static void calibrateSonic(float distance) {
		float [] sample = new float [] {0.0f};
		getSonic().getDistanceMode().fetchSample(sample, 0);
		getMedMotor().setSpeed(5);
		getMedMotor().backward();
		
		
		while(sample[0] < distance){
			getSonic().getDistanceMode().fetchSample(sample, 0);
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
	
	public static EV3MediumRegulatedMotor getMedMotor() {
		if (motor == null) {
			motor = new EV3MediumRegulatedMotor(MotorPort.B);
		}
		return motor;
	}
}
