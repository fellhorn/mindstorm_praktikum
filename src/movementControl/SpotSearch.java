package movementControl;

import Sensor.SensorThread;
import lejos.robotics.Color;
import mainRobotControl.AbstractInterruptableStateRunner;
import skills.Curves;
import skills.Sensors;
import skills.StraightLines;

/**
 * Search the last room for two different colored spots. One is colored blue,
 * the other one red. When detecting a spot, play an audio-signal. When the both
 * spots are detected, the robot should stop in an idle state.
 * 
 * @author tarek
 *
 */
public class SpotSearch extends AbstractInterruptableStateRunner {

	private float angle, wallDistance;
	private static float backoffDistance = 3;
	private static final float minWallDistance = 0.1f; // In meters

	private boolean foundRed = false, foundWhite = false;

	private static SensorThread sensorThread = new SensorThread();

	@Override
	protected void preLoopActions() {

		// Assumed preconditions:
		// robot is standing in front of the bridge facing the room.

		// Start by driving straight forward
		StraightLines.regulatedForwardDrive(400);
	}

	private void detectColor() {
		int id = sensorThread.getColorID();
		
		if (id == Color.RED) {
			foundRed = true;
		} else if (id == Color.WHITE) {
			foundWhite = true;
		}
		if (foundRed && foundWhite) {
			// Success!!
			running = false;
		}
	}

	/**
	 * Drive along the right wall while searching for the spots. When hitting a
	 * wall, move backwards, turn 90 degrees to the left and forward again. When
	 * turned 360 degrees, shrink size of circle and repeat.
	 */
	@Override
	protected void inLoopActions() {
		wallDistance = sensorThread.getDistance();
		angle = sensorThread.getGyroAngle();

		if (sensorThread.getColorID() != Color.BLACK) {
			// In case we found a spot
			detectColor();
		}

		// If we are 10cm close to the wall, stop and turn
		if (wallDistance < minWallDistance) {
			StraightLines.stop();

			if (angle >= 360) {
				// Increase back-off distance to shrink circle
				backoffDistance += 5;
			}
			StraightLines.wheelRotation(backoffDistance, 340);
			Curves.turnLeft90();
			StraightLines.regulatedForwardDrive(400);
		}
	}

	@Override
	protected void postLoopActions() {
		System.out.println("HAPPY END!");
	}
}
