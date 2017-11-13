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
		ON_LINE,
		TURN_BACK_RIGHT,
		TURN_BACK_LEFT,
		SEARCH_LINE_LEFT,
		SEARCH_LINE_RIGHT,
		LINE_LOST_RIGHT,
		LINE_LOST_LEFT,
		ON_GAP,
		ERROR
	} 
	private LineStates lineState;
	private float[] rotDegree = new float[] {0.0f, 0.0f};
	
	private static final int DEFAULT_CURVE_SPEED = 400;
	private static final float SEARCH_ROTATION_TOLERANCE = 5.0f; 

	
	
	/**
	 * Starts motors to run straight with ~50% speed. </br></br> 
	 * {@inheritDoc}
	 */
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Following white line.");
		StraightLines.startEngines(450);
		lineState = LineStates.ON_LINE;
	}

	@Override
	protected void inLoopActions() {
		int groundColor = col.getColorID(); 
		switch (groundColor) {
		case Color.WHITE:
		case Color.BLUE:
			//reset state variables to "on line"
			lineState = LineStates.ON_LINE;
			StraightLines.regulatedForwardDrive(450);
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
			//TODO check actual color of barcode, change to next state
			break;
		default: 
			//TODO think of better error case behavior
			//stop robot if measurement error occurs
			System.out.println("\n" + groundColor);
			lineState = LineStates.ERROR;
			break;
		}
	}

	@Override
	protected void postLoopActions() {
		//TODO clear global values in case some were set
		StraightLines.stop();
	}
	
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
	
	private void searchLine(){
		// Get current angle
		gyro.getAngleMode().fetchSample(rotDegree, 1);
		// left = positive dir
		switch (lineState) {
		case LINE_LOST_LEFT:
			// Search left first
			gyro.getAngleMode().fetchSample(rotDegree, 0);
			lineState = LineStates.SEARCH_LINE_LEFT;
			break;
		case LINE_LOST_RIGHT:
			// Search right first
			gyro.getAngleMode().fetchSample(rotDegree, 0);
			lineState = LineStates.SEARCH_LINE_RIGHT;
			break;
		case SEARCH_LINE_LEFT:
			if (changeStateIf90(LineStates.TURN_BACK_LEFT)) {
				break;
			}
			Curves.smoothSpeededLeftTurn(-1, DEFAULT_CURVE_SPEED);
			break;
		case TURN_BACK_LEFT:
			if (changeStateIf90(LineStates.SEARCH_LINE_RIGHT)) {
				break;
			}
			Curves.smoothSpeededRightTurn(-1, DEFAULT_CURVE_SPEED);
			break;
		case SEARCH_LINE_RIGHT:
			if (changeStateIf90(LineStates.TURN_BACK_RIGHT)) {
				break;
			}
			Curves.smoothSpeededRightTurn(-1, DEFAULT_CURVE_SPEED);
			break;
		case TURN_BACK_RIGHT:
			if (changeStateIf90(LineStates.ON_GAP)) {
				break;
			}
			Curves.smoothSpeededLeftTurn(-1, DEFAULT_CURVE_SPEED);
			break;
		case ON_LINE:
			StraightLines.regulatedForwardDrive(450);
			break;
		case ON_GAP:
			break;
			default:
				// Error
		}
	}
}
