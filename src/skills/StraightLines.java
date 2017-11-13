package src.skills;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.DebugMessages;

public class StraightLines {

	private static EV3LargeRegulatedMotor left;
	private  static EV3LargeRegulatedMotor right;
	private static DebugMessages message = new DebugMessages(2);
	public static void regulatedForwardDrive(int speedRef) {
		adjustSpeed(speedRef);
		getLeft().forward();
		getRight().forward();
	//	left.resetTachoCount();
	//	right.resetTachoCount();
		
	}

	public static void startEngines(int speed) {
		getLeft().setAcceleration(4000);
		getRight().setAcceleration(4000);
		getLeft().setSpeed(speed);
		getRight().setSpeed(speed);
		getLeft().forward();
		getRight().forward();	
		message.clear();
		message.echo("Started Motors.");
	}

	public static void stop() {
		getLeft().stop();
		getRight().stop();
	}

	//implementation with tacho count (as in Walk) doesn't work right now since timing can't be guaranteed
	private static void adjustSpeed(int speedRef) {
		// Get actual value that was measured
		int tachoLeft = getLeft().getSpeed();
		int tachoRight = getRight().getSpeed();
		
		int diffLeft = speedRef - tachoLeft;
		int diffRight = speedRef - tachoRight;
		
		getLeft().setSpeed(speedRef + diffLeft);
		getRight().setSpeed(speedRef + diffRight);
		message.echo("New Speed");
	}
	
	public static EV3LargeRegulatedMotor getLeft() {
		if(left == null) {
			left = new EV3LargeRegulatedMotor(MotorPort.A);
		}
		return left;
	}
	
	public static EV3LargeRegulatedMotor getRight() {
		if(right == null) {
			right = new EV3LargeRegulatedMotor(MotorPort.D);
		}
		return right;
	}
}
