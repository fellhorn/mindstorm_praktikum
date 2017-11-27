package skills;
import lejos.utility.DebugMessages;

import java.util.HashSet;
import java.util.Set;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.Color;

import mainRobotControl.AbstractInterruptableStateRunner;
import movementControl.Line.LineStates;
import skills.*;

/**
 * Class to detect the current possibilities at a red square within the labyrinth.
 * 
 * @author dennis
 *
 */
public class LabyrinthRedPoint {
	public enum Choice {
		LEFT,
		RIGHT,
		STRAIGHT,
		BACK,
	}

	private DebugMessages message = new DebugMessages(1);
	private EV3ColorSensor col = new EV3ColorSensor(SensorPort.S2);
	private EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S1);
	
	private float[] rotDegree = new float[] {0.0f, 0.0f};

	private static final int DEFAULT_CURVE_SPEED = 400;
	private static final float SEARCH_ROTATION_TOLERANCE = 5.0f;
	private static final float SEARCH_ROTATION_TARGET = 90.0f; 
	private static final int STRAIGHT_SPEED = 100;
	private static final int STRAIGHT_ROTATIONS = 1;
	private static final int ROTATION_SPEED = 60;



	/**
	 * Get the 
	 * 
	 * @return
	 */
	public Choice getLeftMostAvailableChoice() {
		message.clear();
		message.echo("Red square choice detection started.");
		gyro.getAngleMode().fetchSample(rotDegree, 0);
		
		int groundColor = col.getColorID();
		if (groundColor != Color.RED) {
			message.echo("Red square choice detection started.");
			throw new RuntimeException("Has to start on a red square");
		}
		
		boolean hasStraight;
		
		if (this.hasStraightOption()) {
			hasStraight = true;
		}
		
		if (this.hasLeftOption()) {
			this.setBack();
			return Choice.LEFT;
		}
		
		if (hasStraight) {
			return Choice.STRAIGHT;
		}
		
		if (this.hasRightOption()) {
			this.setBack();
			return Choice.RIGHT;
		}		
		
		return Choice.BACK;
		
	}
	
	private boolean isOnwhite() {
		int groundColor = col.getColorID();
		return groundColor == Color.WHITE;
	}	
	
	private boolean hasStraightOption() {
		StraightLines.wheelRotation(STRAIGHT_ROTATIONS, STRAIGHT_SPEED);
		boolean result = (col.getColorID() == Color.WHITE);
		StraightLines.stop();
		// TODO not sure whether we have to set back
		// StraightLines.wheelRotation(STRAIGHT_ROTATIONS, -STRAIGHT_SPEED);
		
		return result;
	}
	
	private boolean hasLeftOption() {
		return this.searchSide(false);
	}
	
	private boolean hasRightOption() {
		return this.searchSide(true);
	}

	private boolean searchSide(boolean searchRight) {
		int algebraicSign;
		if (searchRight) {
			Curves.smoothSpeededLeftTurn(1, ROTATION_SPEED);
			algebraicSign = 1;
		} else {
			Curves.smoothSpeededRightTurn(1, ROTATION_SPEED);
			algebraicSign = -1;
		}
		
		while (true) {
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if (this.isOnwhite()) {
				return true;
			}	
				
			if (rotDegree[0] - rotDegree[1] > SEARCH_ROTATION_TARGET + SEARCH_ROTATION_TOLERANCE) {
				return false;
			}
		}
	}
	
	private void setBack() {
		StraightLines.stop();
		
		// TODO check that
		boolean turnRight = (rotDegree[0] - rotDegree[1]) > 0;
		
		//TODO actual turning
		
	}
		
	
}
