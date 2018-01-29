package movementControl;

import lejos.utility.DebugMessages;
import Sensor.OwnColorSensor;
import Sensor.SingleValueSensorWrapper;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.Color;

import mainRobotControl.AbstractInterruptableStateRunner;
import mainRobotControl.ParcourState;
import mainRobotControl.StateMachine;
import skills.*;

public class Line extends AbstractInterruptableStateRunner {

	private DebugMessages message = new DebugMessages(1);

	private OwnColorSensor col;
	private EV3GyroSensor gyro;
	private EV3UltrasonicSensor sonic;
	private SingleValueSensorWrapper touch = new SingleValueSensorWrapper(Sensors.getTouch(), "Touch");
	
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
	private int gapCount = 0;
	private float[] dist = new float[] { 0.0f };

	private static final float SEARCH_ROTATION_TOLERANCE = 5.0f;
	private static final int LINE_SPEED = 500;
	private static final int ROTATION_SPEED = 100;

	/**
	 * Starts motors to run straight with ~66% speed. </br>
	 * </br>
	 * {@inheritDoc}
	 */
	@Override
	protected void preLoopActions() {
		sonic = Sensors.getSonic();

		//Sensors.calibrateSonic(0.25f);
		col = Sensors.getColor();
		gyro = Sensors.getGyro();
		Sensors.calibrateSonic(0.25f);
		Sensors.sonicDown();
		sonic.disable();
		StraightLines.regulatedForwardDrive(LINE_SPEED);
		lineState = LineStates.ON_LINE_LAST_RIGHT;
	}

	@Override
	protected void inLoopActions() {
		//set to 2 for full course and 0 for testing obstacle only
		//TODO change to bool that activates ultrasonic => test quicker

		/*message.clear();
		message.echo(gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " "); 
		message.echo(gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " "); 
		message.echo(gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " "); 
		message.echo(gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " " + gapCount + " "); 
		*/
		
		//if (gapCount >= 2) {

			//sonic.getDistanceMode().fetchSample(dist, 0);
			//if (dist[0] < 0.15) {
			
			if (touch.getSample() == 1.0 && (lineState == LineStates.ON_LINE_LAST_LEFT || 
											 lineState == LineStates.ON_LINE_LAST_RIGHT)) {
				// GO BACK CAUSE TOUCH
				float angleArray[] = {0,0};
				gyro.fetchSample(angleArray, 0);
				StraightLines.setStraightAngle(angleArray[0]);
				message.clear();
				message.echo("refAngle: " + angleArray[0]);
				
				StraightLines.wheelRotation(-0.3f, LINE_SPEED);
				
				StraightLines.stop();
				Curves.turnRight90();
				StraightLines.wheelRotation(1.2f, LINE_SPEED);
				Curves.turnLeft90();
				StraightLines.wheelRotation(2.8f, LINE_SPEED);
				Curves.turnLeft90();
				//StraightLines.wheelRotation(0.5f, LINE_SPEED);
				gapCount = -1;
				StraightLines.resetMotors();
				lineState = LineStates.ON_GAP_LAST_RIGHT;
				
				// try to drive slower after bumper to drive more accurate afterwards
				LINE_SPEED = LINE_SPEED / 3;
				
			}
		//}
		int groundColor = col.getColorID();
		switch (groundColor) {
		case Color.BLUE: // TODO check as which color white is seen
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
			}
			StraightLines.regulatedForwardDrive(LINE_SPEED);
			break;
		case Color.BLACK:
		case Color.BROWN:
			searchLine();
			break;
		case Color.RED:
			// setting back to align to wood block
			StraightLines.wheelRotation(-1.5f, LINE_SPEED);
			running = false;
			break;
		default:
			// TODO think of better error case behavior
			// stop robot if measurement error occurs
			message.clear();
			message.echo("Exit on color: " + groundColor);
			StraightLines.stop();
			break;
		}

	}

	@Override
	protected void postLoopActions() {
		// TODO clear global values in case some were set
		//sonic.disable();
		StraightLines.stop();
		StraightLines.resetMotors();
		/*lejos.utility.Delay.msDelay(1000);
		float[] angleArray = new float[] { StraightLines.getStraightAngle(), 0.0f };
		gyro.fetchSample(angleArray, 1);
		message.echo("angle: " + angleArray[1]);
		lejos.utility.Delay.msDelay(1000);
		if (angleArray[0] - angleArray[1] < 0.0) {
			Curves.smoothSpeededRightTurn(-1, 50);
			while(((angleArray[0] - angleArray[1]) % 360) < 0.0) {
				gyro.fetchSample(angleArray, 1);
				message.echo("angle: " + angleArray[1]);
			}
			StraightLines.stop();
			StraightLines.resetMotors();
		} else {
			Curves.smoothSpeededLeftTurn(-1, 50);
			while(((angleArray[0] - angleArray[1]) % 360) > 0.0) {
				gyro.fetchSample(angleArray, 1);
				message.echo("angle: " + angleArray[1]);
			}
			StraightLines.stop();
			StraightLines.resetMotors();
		}
		message.echo("finalAngle: " + angleArray[1]);*/
		
		
		StateMachine.getInstance().setState(ParcourState.MAZE);
		message.echo("MAZE");
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
			if (rotDegree[0] - rotDegree[1] > 40.0 - SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_SMALL_LAST_LEFT;
			}
			break;

		case SEARCH_LINE_SMALL_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			// search for line on the left
			// message.echo("Turn left");
			Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] < -40.0 + SEARCH_ROTATION_TOLERANCE) {
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
				if (rotDegree[0] - rotDegree[1] < -40.0 + SEARCH_ROTATION_TOLERANCE) {
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
				if (rotDegree[0] - rotDegree[1] > 40.0 - SEARCH_ROTATION_TOLERANCE) {
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
				gapCount++;
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
				gapCount++;
				StraightLines.stop();
				lejos.utility.Delay.msDelay(5);
				StraightLines.resetMotors();
			}
			break;

		case ON_GAP_LAST_LEFT:
			StraightLines.wheelRotation(1.0f, LINE_SPEED);
			lineState = LineStates.SEARCH_LINE_LAST_LEFT;
			break;
			
		case ON_GAP_LAST_RIGHT:
			StraightLines.wheelRotation(1.0f, LINE_SPEED);
			lineState = LineStates.SEARCH_LINE_LAST_RIGHT;
			// TODO what if the robot does not find the end of line after gap?
			break;

		case ERROR:
			break;
		}
	}
}
