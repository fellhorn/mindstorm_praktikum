package src.movementControl;
import lejos.utility.DebugMessages;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.Color;
import src.mainRobotControl.AbstractInterruptableStateRunner;
import src.skills.*;

public class Line extends AbstractInterruptableStateRunner {

	private DebugMessages message = new DebugMessages(1);
	private EV3ColorSensor col = new EV3ColorSensor(SensorPort.S2);
	private EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S1);
	private enum LineStates {
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
	//private lejos.utility.Stopwatch sw;
	private float[] rotDegree = new float[] {0.0f, 0.0f};

	private static final int DEFAULT_CURVE_SPEED = 400;
	private static final float SEARCH_ROTATION_TOLERANCE = 5.0f;
	private static final int LINE_SPEED = 600;
	private static final int ROTATION_SPEED = 60;



	/**
	 * Starts motors to run straight with ~55% speed. </br></br>
	 * {@inheritDoc}
	 */
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Following white line.");
		StraightLines.startEngines(LINE_SPEED);
		lineState = LineStates.ON_LINE_LAST_RIGHT;
		//sw = new lejos.utility.Stopwatch();
	}

	@Override
	protected void inLoopActions() {
		int groundColor = col.getColorID();
		switch (groundColor) {
		case Color.BLUE:  //TODO check as which color white is seen
		case Color.WHITE:
			//reset state to ON_LINE
			if((lineState != LineStates.ON_LINE_LAST_LEFT) && (lineState != LineStates.ON_LINE_LAST_RIGHT)) {
				StraightLines.stop();
				lejos.utility.Delay.msDelay(5);
				StraightLines.resetMotors();
				if((lineState == LineStates.TURN_BACK_LAST_LEFT)
						|| (lineState == LineStates.SEARCH_LINE_LAST_RIGHT)
						|| (lineState == LineStates.TURN_BACK_SMALL_LAST_LEFT)
						|| (lineState == LineStates.SEARCH_LINE_SMALL_LAST_RIGHT)
						|| (lineState == LineStates.ON_GAP_LAST_RIGHT)) {
					lineState = LineStates.ON_LINE_LAST_RIGHT;
				} else {
					lineState = LineStates.ON_LINE_LAST_LEFT;
				}
			}
			StraightLines.regulatedForwardDrive(LINE_SPEED);
			//TODO ENHANCEMENT speedup if line was straight for some time
			break;
		case Color.BLACK:
		case Color.BROWN:
			if(lineState == LineStates.ON_GAP){
				StraightLines.regulatedForwardDrive(450);
				//TODO what if the robot does not find the end of line after gap?
			} else {
				lineState = LineStates.LINE_LOST_LEFT;
				searchLine();
			}
			break;
		case Color.RED:
			//TODO change to next state
			break;
		default:
			//TODO think of better error case behavior
			//stop robot if measurement error occurs
			message.clear();
			message.echo("Exit on color: " + groundColor);
			StraightLines.stop();
			break;
		}
		//System.out.println(1000.0 / sw.elapsed());
		//sw.reset();
	}

	@Override
	protected void postLoopActions() {
		//TODO clear global values in case some were set
		col.close();
		gyro.close();
		StraightLines.stop();
	}

	private void searchLine(){
		//Just lost line: get initial rotation position
		switch(lineState) {
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
			//search for line on the right
			Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] > 25.0 - SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_SMALL_LAST_LEFT;
			}
			break;

		case SEARCH_LINE_SMALL_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			//search for line on the left
			//message.echo("Turn left");
			Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] < -25.0 + SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_SMALL_LAST_RIGHT;
			}
			break;


		case TURN_BACK_SMALL_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if(rotDegree[0] - rotDegree[1] > 5.0){
				//line not found => you can turn back quicker
				Curves.smoothSpeededLeftTurn(-1, 2*ROTATION_SPEED);
			} else {
				//search for line on the left
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] < -25.0 + SEARCH_ROTATION_TOLERANCE) {
					lineState = LineStates.SEARCH_LINE_LAST_LEFT;
				}
			}

			break;

		case TURN_BACK_SMALL_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if(rotDegree[0] - rotDegree[1] < -5.0){
				//line not found => you can turn back quicker
				//message.echo("Not found, turn back");
				Curves.smoothSpeededRightTurn(-1, 2*ROTATION_SPEED);
			} else {
				//search for line on the right
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] > 25.0 - SEARCH_ROTATION_TOLERANCE) {
					lineState = LineStates.SEARCH_LINE_LAST_RIGHT;
				}
			}
			break;


		case SEARCH_LINE_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			//search for line on the right
			Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] > 90.0 - SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_LAST_LEFT;
			}
			break;

		case SEARCH_LINE_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			//search for line on the left
			//message.echo("Turn left");
			Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
			if (rotDegree[0] - rotDegree[1] < -90.0 + SEARCH_ROTATION_TOLERANCE) {
				lineState = LineStates.TURN_BACK_LAST_RIGHT;
			}
			break;


		case TURN_BACK_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if(rotDegree[0] - rotDegree[1] > 5.0){
				//line not found => you can turn back quicker
				Curves.smoothSpeededLeftTurn(-1, 2*ROTATION_SPEED);
			} else {
				//search for line on the left
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] < -90.0 + SEARCH_ROTATION_TOLERANCE) {
					lineState = LineStates.TO_STRAIGHT_LAST_LEFT;
				}
			}

			break;

		case TURN_BACK_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if(rotDegree[0] - rotDegree[1] < -5.0){
				//line not found => you can turn back quicker
				//message.echo("Not found, turn back");
				Curves.smoothSpeededRightTurn(-1, 2*ROTATION_SPEED);
			} else {
				//search for line on the right
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] > 90.0 - SEARCH_ROTATION_TOLERANCE) {
					lineState = LineStates.TO_STRAIGHT_LAST_RIGHT;
				}
			}
			break;


		case TO_STRAIGHT_LAST_LEFT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if(rotDegree[0] - rotDegree[1] < -5.0) {
				//line not found => you can turn back to original position quicker
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			} else {
				//no line here => must be gap
				lineState = LineStates.ON_GAP_LAST_LEFT;
				StraightLines.stop();
				lejos.utility.Delay.msDelay(5);
				StraightLines.resetMotors();
			}
			break;

		case TO_STRAIGHT_LAST_RIGHT:
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if(rotDegree[0] - rotDegree[1] > 5.0) {
				//line not found => you can turn back to original position quicker
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
			} else {
				//no line here => must be gap
				lineState = LineStates.ON_GAP_LAST_RIGHT;
				StraightLines.stop();
				lejos.utility.Delay.msDelay(5);
				StraightLines.resetMotors();
			}
			break;


		case ON_GAP_LAST_LEFT:
			StraightLines.wheelRotation(0.5f, LINE_SPEED);
			lineState = LineStates.SEARCH_LINE_LAST_LEFT;
		case ON_GAP_LAST_RIGHT:
			StraightLines.wheelRotation(0.5f, LINE_SPEED);
			lineState = LineStates.SEARCH_LINE_LAST_RIGHT;
			//TODO what if the robot does not find the end of line after gap?
			break;


		case ERROR:
			break;
	private float distance(float a, float b) {
		return a < b ? b-a : a-b;
	}
	private boolean angleGreater90(float a, float b) {
		return distance(a, b) >= 90 + SEARCH_ROTATION_TOLERANCE;
	}
	private boolean changeStateIf90(LineStates lineState) {
		if (angleGreater90(rotDegree[0], rotDegree[1])) {
			StraightLines.stop();
			this.lineState = lineState;
			return true;
		}
		return false;
	}
}
