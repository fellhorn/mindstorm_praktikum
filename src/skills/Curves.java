package src.skills;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Curves {
	
	private static EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.A);
	private static EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.D);

	public static void smoothSpeededRightTurn(float smoothness, float speed) {
		right.setSpeed(speed);
		left.setSpeed(smoothness * speed);
		right.forward();
		left.forward();	
	}

	public static void smoothSpeededLeftTurn(float smoothness, float speed) {
		smoothSpeededRightTurn((1.0f / smoothness), (speed * smoothness));		
	}
}
