package skills;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.Color;
import lejos.utility.DebugMessages;
import mainRobotControl.ParcourState;
import mainRobotControl.StateMachine;
import movementControl.Line.LineStates;

/**
 * Wrapper for a basic line follower
 * 
 * @author dennis
 *
 */
public class FollowLine {
	
	private DebugMessages message = new DebugMessages(1);

	private EV3ColorSensor col;
	private EV3GyroSensor gyro;

	public enum LineStates {
		ON_LINE_LAST_LEFT, 
		ON_LINE_LAST_RIGHT, 
		SEARCH_LINE_LAST_LEFT, 
		SEARCH_LINE_LAST_RIGHT, 
		TURN_BACK_LAST_LEFT, 
		TURN_BACK_LAST_RIGHT, 
		SEARCH_LINE_SMALL_LAST_LEFT, 
		SEARCH_LINE_SMALL_LAST_RIGHT, 
		TURN_BACK_SMALL_LAST_LEFT, 
		TURN_BACK_SMALL_LAST_RIGHT, 
		TO_STRAIGHT_LAST_LEFT, 
		TO_STRAIGHT_LAST_RIGHT, 
		ON_GAP_LAST_LEFT, 
		ON_GAP_LAST_RIGHT,
		ERROR
	}

	private LineStates lineState;

	private float[] rotDegree = new float[] { 0.0f, 0.0f };

	private static final float SEARCH_ROTATION_TOLERANCE = 5.0f;
	private static final int LINE_SPEED = 80;
	private static final int ROTATION_SPEED = 55;
	
	public FollowLine(EV3ColorSensor colorSensor, EV3GyroSensor gyroSensor) {
		this.col = colorSensor;
		this.gyro = gyroSensor;
	}
	
	public void preLoopActions() {
		StraightLines.startEngines(LINE_SPEED);
		lineState = LineStates.ON_LINE_LAST_RIGHT;
	}

	public void inLoopActions() {
		int groundColor = col.getColorID();
		switch (groundColor) {
		case Color.WHITE:
			// reset state to ON_LINE
			if ((lineState != LineStates.ON_LINE_LAST_LEFT) && (lineState != LineStates.ON_LINE_LAST_RIGHT)) {
				StraightLines.stop();
				lejos.utility.Delay.msDelay(5);
				StraightLines.resetMotors();
				if ((lineState == LineStates.TURN_BACK_LAST_LEFT) || (lineState == LineStates.SEARCH_LINE_LAST_RIGHT)
						|| (lineState == LineStates.TURN_BACK_SMALL_LAST_LEFT)
						|| (lineState == LineStates.SEARCH_LINE_SMALL_LAST_RIGHT)
						|| (lineState == LineStates.ON_GAP_LAST_RIGHT)) {
					lineState = LineStates.ON_LINE_LAST_RIGHT;
				} else {
					lineState = LineStates.ON_LINE_LAST_LEFT;
				}
				StraightLines.startEngines(LINE_SPEED);
			} else {
				StraightLines.regulatedForwardDrive(LINE_SPEED);
				// TODO ENHANCEMENT speedup if line was straight for some time
			}
			break;
		case Color.BLACK:
		case Color.BROWN:
			searchLine();
			break;
		default:
			break;
		}

	}

	private void searchLine() {
		// Just lost line: get initial rotation position
		switch (lineState) {
		case ON_LINE_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 0);
			lineState = LineStates.SEARCH_LINE_SMALL_LAST_LEFT;
			break;

		case ON_LINE_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 0);
			lineState = LineStates.SEARCH_LINE_SMALL_LAST_RIGHT;
			break;

		case SEARCH_LINE_SMALL_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			// search for line on the right
			Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] > 25.0 - SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_SMALL_LAST_LEFT;
			}
			break;

		case SEARCH_LINE_SMALL_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			// search for line on the left
			// message.echo("Turn left");
			Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] < -25.0 + SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_SMALL_LAST_RIGHT;
			}
			break;

		case TURN_BACK_SMALL_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if (rotDegree[0] - rotDegree[1] > 5.0) {
				// line not found => you can turn back quicker
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			} else {
				// search for line on the left
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] < -25.0 + SEARCH_ROTATION_TOLERANCE) {
					lineState = LineStates.SEARCH_LINE_LAST_LEFT;
				}
			}

			break;

		case TURN_BACK_SMALL_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if (rotDegree[0] - rotDegree[1] < -5.0) {
				// line not found => you can turn back quicker
				// message.echo("Not found, turn back");
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
			} else {
				// search for line on the right
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] > 25.0 - SEARCH_ROTATION_TOLERANCE) {
					lineState = LineStates.SEARCH_LINE_LAST_RIGHT;
				}
			}
			break;

		case SEARCH_LINE_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			// search for line on the right
			Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] > 90.0 - SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_LAST_LEFT;
			}
			break;

		case SEARCH_LINE_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			// search for line on the left
			// message.echo("Turn left");
			Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] < -90.0 + SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_LAST_RIGHT;
			}
			break;

		case TURN_BACK_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if (rotDegree[0] - rotDegree[1] > 5.0) {
				// line not found => you can turn back quicker
				Curves.smoothSpeededRightTurn(-1, 2 * ROTATION_SPEED);
			} else {
				// search for line on the left
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] < -90.0 + SEARCH_ROTATION_TOLERANCE) {
					lineState = LineStates.TO_STRAIGHT_LAST_LEFT;
				}
			}

			break;

		case TURN_BACK_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if (rotDegree[0] - rotDegree[1] < -5.0) {
				// line not found => you can turn back quicker
				// message.echo("Not found, turn back");
				Curves.smoothSpeededLeftTurn(-1, 2 * ROTATION_SPEED);
			} else {
				// search for line on the right
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] > 90.0 - SEARCH_ROTATION_TOLERANCE) {
					lineState = LineStates.TO_STRAIGHT_LAST_RIGHT;
				}
			}
			break;

		case TO_STRAIGHT_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if (rotDegree[0] - rotDegree[1] < -5.0) {
				// line not found => you can turn back to original position
				// quicker
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
			} else {
				// no line here => must be gap
				lineState = LineStates.ON_GAP_LAST_LEFT;
				StraightLines.stop();
				lejos.utility.Delay.msDelay(5);
				StraightLines.resetMotors();
			}
			break;

		case TO_STRAIGHT_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if (rotDegree[0] - rotDegree[1] > 5.0) {
				// line not found => you can turn back to original position
				// quicker
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			} else {
				// no line here => must be gap
				lineState = LineStates.ON_GAP_LAST_RIGHT;
				StraightLines.stop();
				lejos.utility.Delay.msDelay(5);
				StraightLines.resetMotors();
			}
			break;

		case ON_GAP_LAST_LEFT:
			StraightLines.wheelRotation(0.5f, LINE_SPEED);
			lineState = LineStates.SEARCH_LINE_LAST_LEFT;
			break;
			
		case ON_GAP_LAST_RIGHT:
			StraightLines.wheelRotation(0.5f, LINE_SPEED);
			lineState = LineStates.SEARCH_LINE_LAST_RIGHT;
			// TODO what if the robot does not find the end of line after gap?
			break;

		case ERROR:
			break;
		}
	}
	
}
