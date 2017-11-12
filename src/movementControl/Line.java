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
	private EV3ColorSensor col = new EV3ColorSensor(SensorPort.S2);  //TODO check port
	private EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S3); //TODO check port
	private boolean lastLossRight = true;  //TODO should be set according to first curve 
	private boolean rotating = false;
	private boolean turnBack = false;
	private boolean adjust = false;
	private boolean assumeGap  = false;
	private float[] rotDegree = new float[] {0.0f, 0.0f};
	
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
	}

	@Override
	protected void inLoopActions() {
		int groundColor = col.getColorID(); 
		switch (groundColor) {
		case Color.WHITE:
			//reset state variables to "on line"
			rotating = false;
			assumeGap = false;
			lastLossRight  = lastLossRight != turnBack; //change lLR iff robot searched in wrong direction first
			turnBack = false;
			adjust = false;
			StraightLines.regulatedForwardDrive(450);
			//TODO ENHANCEMENT speedup if line was straight for some time
			break;
		case Color.BLACK:
			if(!assumeGap){
				searchLine();				
			} else {
				StraightLines.regulatedForwardDrive(450);
				//TODO what if the robot does not find the end of line after gap?
			}
			break;
		case Color.RED:
			//TODO check actual color of barcode, change to next state
			break;
		default: 
			//TODO think of better error case behavior
			//stop robot if measurement error occurs
			running = false; 
			break;
		}
	}

	@Override
	protected void postLoopActions() {
		//TODO clear global values in case some were set
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
				Curves.smoothSpeededLeftTurn(-1, 450);	
				if (rotDegree[0] - rotDegree[1] < -90.0 - SEARCH_ROTATION_TOLERANCE) {
					turnBack = true;
				}
			} else if(turnBack && (rotDegree[0] - rotDegree[1] < 0.0)){
				//line not found => you can turn back quicker
				Curves.smoothSpeededRightTurn(-1, 900);	
			} else if(turnBack && !adjust){
				//search for line on the right
				Curves.smoothSpeededRightTurn(-1, 450);
				if (rotDegree[0] - rotDegree[1] > 90.0 + SEARCH_ROTATION_TOLERANCE) {
					adjust = true;
				}
			} else if(adjust && (rotDegree[0] - rotDegree[1] > 0.0)) {
				//line not found => you can turn back to original position quicker
				Curves.smoothSpeededLeftTurn(-1, 900);
			} else {
				//no line here => must be gap
				adjust = false;
				rotating = false;
				assumeGap = true;
			}

		} else {
			if(!turnBack) {
				//search for line on the right
				Curves.smoothSpeededRightTurn(-1, 450);	
				if (rotDegree[0] - rotDegree[1] < -90.0 - SEARCH_ROTATION_TOLERANCE) {
					turnBack = true;
				}
			} else if(turnBack && (rotDegree[0] - rotDegree[1] < 0.0)){
				//line not found => you can turn back quicker
				Curves.smoothSpeededLeftTurn(-1, 900);	
			} else if(turnBack && !adjust){
				//search for line on the left
				Curves.smoothSpeededLeftTurn(-1, 450);
				if (rotDegree[0] - rotDegree[1] > 90.0 + SEARCH_ROTATION_TOLERANCE) {
					adjust = true;
				}
			} else if(adjust && (rotDegree[0] - rotDegree[1] > 0.0)) {
				//line not found => you can turn back to original position quicker
				Curves.smoothSpeededRightTurn(-1, 900);
			} else {
				//no line here => must be gap
				adjust = false;
				rotating = false;
				assumeGap = true;
			}
		}
	}
}
