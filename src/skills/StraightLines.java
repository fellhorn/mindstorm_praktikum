package skills;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.utility.DebugMessages;

public class StraightLines {

	private static EV3LargeRegulatedMotor left;
	private static EV3LargeRegulatedMotor right;
	private static final int MOTOR_ACC = 4000;
	private static DebugMessages message = new DebugMessages(2);
	
	/**
	 * Robot starts driving straight with the given speed. Regulation compensates one sided blocking of a wheel.<br>
	 * Method returns immediately and robot keeps driving.
	 * @param speedRef Speed that robot tries to keep
	 */
	public static void regulatedForwardDrive(int speedRef) {
		adjustSpeed(speedRef);
		//message.clear();
		//message.echo(getLeft().getTachoCount() + " - " + getRight().getTachoCount());
		getLeft().forward();
		getRight().forward();		
	}
	
	public static void regulatedBackwardDrive(int speedRef) {
		adjustSpeed(speedRef);
		getLeft().backward();
		getRight().backward();
	}

	/**
	 * Stops robot immediately and actively blocks wheels.
	 */
	public static void stop() {
		getLeft().startSynchronization();
		getLeft().stop();
		getRight().stop();
		getLeft().endSynchronization();
	}

	/**
	 * Simple P-regulator to maintain straight drives. Slows down one wheel if the other cannot move fast enough.
	 * Kp is set to 1.
	 * @param speedRef Speed that robot tries to keep
	 */
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
	
	/**
	 * Motor constructor. Never call Lejos constructor since it does not check if port is already in use 
	 * by another instance of the same motor.
	 * @return Motor on the left side of the Robot.
	 */
	public static EV3LargeRegulatedMotor getLeft() {
		if(left == null) {
			left = new EV3LargeRegulatedMotor(MotorPort.A);
			left.setAcceleration(MOTOR_ACC);
			left.synchronizeWith(new RegulatedMotor[] {getRight()});
		}
		return left;
	}
	
	/**
	 * Motor constructor. Never call Lejos constructor since it does not check if port is already in use 
	 * by another instance of the same motor.
	 * @return Motor on the right side of the Robot.
	 */
	public static EV3LargeRegulatedMotor getRight() {
		if(right == null) {
			right = new EV3LargeRegulatedMotor(MotorPort.D);
			right.setAcceleration(MOTOR_ACC);
		}
		return right;
	}
	
	/**
	 * Set the tacho-count of both motors to 0.
	 */
	public static void resetMotors() {
		getLeft().startSynchronization();
		getLeft().resetTachoCount();
		getRight().resetTachoCount();
		getLeft().endSynchronization();
	}
	
	/**
	 * Drives straight for a given number of wheel rotations. One rotation is about 17.5 cm. No regulation.<br>
	 * Motion is performed completely before method returns.
	 * @param times How many wheel rotations should be made.
	 * @param speed Speed to drive
	 */
	public static void wheelRotation(float times, int speed) {
		getLeft().setSpeed(speed);
		getRight().setSpeed(speed);
		getLeft().rotate((int)(times * 360), true);
		getRight().rotate((int)(times * 360), false);
	}
}
