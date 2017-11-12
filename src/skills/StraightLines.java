package src.skills;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class StraightLines {

	private static EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.A);
	private static EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.D);
	
	public static void regulatedForwardDrive(int speedRef) {
		adjustSpeed(speedRef);
	//	left.resetTachoCount();
	//	right.resetTachoCount();
		
	}

	public static void startEngines(int speed) {
		left.setSpeed(speed);
		right.setSpeed(speed);
		left.forward();
		right.forward();	
	}

	public static void stop() {
		left.stop();
		right.stop();
	}

	//implementation with tacho count (as in Walk) doesn't work right now since timing can't be guaranteed
	private static void adjustSpeed(int speedRef) {
		// Get actual value that was measured
		int tachoLeft = left.getSpeed();
		int tachoRight = right.getSpeed();
		
		int diffLeft = speedRef - tachoLeft;
		int diffRight = speedRef - tachoRight;
		
		left.setSpeed(speedRef + diffLeft);
		right.setSpeed(speedRef + diffRight);
	}
}
