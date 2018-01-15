package movementControl;

import Sensor.OwnColorSensor;
import Sensor.SingleValueSensorWrapper;
import lejos.hardware.Sound;
import lejos.robotics.Color;
import lejos.utility.DebugMessages;
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

	private static float backoffDistance = 1;

	private boolean foundRed = false, foundWhite = false;
	private OwnColorSensor col = Sensors.getColor();
	DebugMessages mes = new DebugMessages();
	SingleValueSensorWrapper t;
	int contacts = 0;

	@Override
	protected void preLoopActions() {

		// Assumed preconditions:
		// robot is standing in front of the bridge facing the room.

		// Start by driving straight forward
		t = new SingleValueSensorWrapper(Sensors.getTouch(), "Touch");
		StraightLines.regulatedForwardDrive(400);
	}

	private void detectColor() {
		int id = col.getColorID();
		
		if (id == Color.RED) {
			foundRed = true;
			mes.clear();
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			Sound.beep();
		} else if (id == Color.WHITE) {
			foundWhite = true;
			mes.clear();
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			Sound.buzz();
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
		
		if (col.getColorID() != Color.BLACK) {
			// In case we found a spot
			detectColor();
		}

		// If we are 10cm close to the wall, stop and turn
		if (t.getSample() == 1.0) {
			contacts ++;
			if (contacts  >= 4) {
				// Increase back-off distance to shrink circle
				backoffDistance ++;
				contacts = 0;
			}
			StraightLines.wheelRotation(-0.2f * backoffDistance, 300);
			Curves.turnLeft90();
			StraightLines.resetMotors();
			StraightLines.regulatedForwardDrive(400);

		}
	}

	@Override
	protected void postLoopActions() {
		System.out.println("HAPPY END!");
		StraightLines.stop();
	}
}
