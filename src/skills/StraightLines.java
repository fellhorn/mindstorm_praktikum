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
	}

	public static void startEngines(int speed) {
		getLeft().setAcceleration(1500);
		getRight().setAcceleration(1500);
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

	
	private static void adjustSpeed(int speedRef) {
		// Get actual value that was measured
		int tachoLeft = getLeft().getTachoCount();
		int tachoRight = getRight().getTachoCount();
		
		int diff = tachoLeft - tachoRight;
	
		getLeft().setSpeed(speedRef - diff);
		getRight().setSpeed(speedRef + diff);
		//message.echo("New Speed");
		//System.out.println("d=" + diff + ",L" + src.skills.StraightLines.getLeft().getSpeed()
		//		+",R"+src.skills.StraightLines.getRight().getSpeed());
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
	
	public static void resetMotors() {
		getLeft().resetTachoCount();
		getRight().resetTachoCount();
	}
}
