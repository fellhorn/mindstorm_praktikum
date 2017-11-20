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
	private boolean lastLossRight = true;  //TODO should be set according to first curve 
	private boolean rotating = false;
	private boolean turnBack = false;
	private boolean adjust = false;
	private boolean assumeGap  = false;
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
	
	private static final float SEARCH_ROTATION_TOLERANCE = -10.0f; 
	private static final int LINE_SPEED = 500;
	private static final int ROTATION_SPEED = 80;

	
	
	/**
	 * Starts motors to run straight with ~50% speed. </br></br> 
	 * {@inheritDoc}
	 */
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Following white line.");
		StraightLines.startEngines(LINE_SPEED);
		lineState = LineStates.ON_LINE;
	}

	@Override
	protected void inLoopActions() {
		int groundColor = col.getColorID(); 
		switch (groundColor) {
		case Color.BLUE:  //TODO check as which color white is seen
		case Color.WHITE:  
			//reset state variables to "on line"
			if(rotating) {
				StraightLines.stop();
				StraightLines.resetMotors();
			}
			rotating = false;
			assumeGap = false;
			lastLossRight  = lastLossRight != turnBack; //change lLR iff robot searched in wrong direction first
			turnBack = false;
			adjust = false;
			StraightLines.regulatedForwardDrive(LINE_SPEED);
			//TODO ENHANCEMENT speedup if line was straight for some time
			break;
		case Color.BLACK:
		case Color.BROWN:
			//message.echo("Lost line");
			if(!assumeGap){
				searchLine();				
			} else {
				StraightLines.regulatedForwardDrive(LINE_SPEED);
				//TODO what if the robot does not find the end of line after gap?
			}
			break;
		case Color.RED:
			//TODO check actual color of barcode, change to next state
			break;
		default: 
			//TODO think of better error case behavior
			//stop robot if measurement error occurs
			message.clear();
			message.echo("Exit on color: " + groundColor);
			running = false; 
			while(true) {
				col.close();
				gyro.close();
				StraightLines.stop();
			}
			//break;
		}
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
		if(!rotating) {
			gyro.getAngleMode().fetchSample(rotDegree, 0);
			rotating = true;
		}
		gyro.getAngleMode().fetchSample(rotDegree, 1);
		if(lastLossRight) {
			if(!turnBack) {
				//search for line on the left
				//message.echo("Turn left");
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);	
				if (rotDegree[0] - rotDegree[1] < -90.0 - SEARCH_ROTATION_TOLERANCE) {
					turnBack = true;
					StraightLines.stop();
				}
			} else if(turnBack && (rotDegree[0] - rotDegree[1] < 0.0)){
				//line not found => you can turn back quicker
				//message.echo("Not found, turn back");
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);	
			} else if(turnBack && !adjust){
				//search for line on the right
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] > 90.0 + SEARCH_ROTATION_TOLERANCE) {
					adjust = true;
					StraightLines.stop();
				}
			} else if(adjust && (rotDegree[0] - rotDegree[1] > 0.0)) {
				//line not found => you can turn back to original position quicker
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
			} else {
				//no line here => must be gap
				adjust = false;
				rotating = false;
				assumeGap = true;
				StraightLines.resetMotors();
			}

		} else {
			if(!turnBack) {
				//search for line on the right
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);	
				if (rotDegree[0] - rotDegree[1] > 90.0 + SEARCH_ROTATION_TOLERANCE) {
					turnBack = true;
					StraightLines.stop();
				}
			} else if(turnBack && (rotDegree[0] - rotDegree[1] > 0.0)){
				//line not found => you can turn back quicker
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);	
			} else if(turnBack && !adjust){
				//search for line on the left
				Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
				if (rotDegree[0] - rotDegree[1] < -90.0 + SEARCH_ROTATION_TOLERANCE) {
					adjust = true;
					StraightLines.stop();
				}
			} else if(adjust && (rotDegree[0] - rotDegree[1] < 0.0)) {
				//line not found => you can turn back to original position quicker
				Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
			} else {
				//no line here => must be gap
				adjust = false;
				rotating = false;
				assumeGap = true;
				StraightLines.resetMotors();
			}
		}
		return;
	}
}
