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
public class MazeRedPoint {
	public enum Choice {
		LEFT,
		RIGHT,
		STRAIGHT,
		BACK,
	}

	private static DebugMessages message = new DebugMessages(1);
	private static EV3ColorSensor col = Sensors.getColor();
	private static EV3GyroSensor gyro = Sensors.getGyro();
	
	private static float[] rotDegree = new float[] {0.0f, 0.0f};

	private static final int DEFAULT_CURVE_SPEED = 10;
	private static final float SEARCH_ROTATION_TOLERANCE = 0.0f;
	private static final float SEARCH_ROTATION_TARGET = 90.0f; 
	private static final int STRAIGHT_SPEED = 100;
	private static final float STRAIGHT_ROTATIONS = 0.2f;
	private static final int ROTATION_SPEED = 60;
	private static final float STRAIGHT_ANGLE_TOLERANCE  = 4.0f;



	/**
	 * Get the 
	 * 
	 * @return
	 */
	public static Choice getLeftMostAvailableChoice() {
		message.clear();
		message.echo("Red square choice detection started.");
		gyro.getAngleMode().fetchSample(rotDegree, 0);
		
		int groundColor = col.getColorID();
		if (groundColor != Color.RED) {
			message.echo("has to start on red.");
			// throw new RuntimeException("Has to start on a red square");
		}
		
		boolean hasStraight;
		
		message.echo("searching straight");
		
		if (hasStraightOption()) {
			message.echo("found a straight option");
			return Choice.STRAIGHT;
		}
		message.echo("no straight option");
				
		if (hasLeftOption()) {
			setBack();
			return Choice.LEFT;
		}
		message.echo("no left option");
		setBack();
		
		if (hasRightOption()) {
			return Choice.RIGHT;
		}
		message.echo("no right option");
		setBack();
		
		return Choice.BACK;
		
	}
	
	private static boolean isOnwhite() {
		int groundColor = col.getColorID();
		return groundColor == Color.WHITE || groundColor == Color.BLUE;
	}	
	
	private static boolean hasStraightOption() {
		StraightLines.wheelRotation(STRAIGHT_ROTATIONS, STRAIGHT_SPEED);
		boolean result = (col.getColorID() == Color.WHITE);
		StraightLines.stop();
		StraightLines.wheelRotation(STRAIGHT_ROTATIONS, -STRAIGHT_SPEED);
		
		return result;
	}
	
	private static boolean hasLeftOption() {
		return searchSide(false);
	}
	
	private static boolean hasRightOption() {
		return searchSide(true);
	}

	private static boolean searchSide(boolean searchRight) {
		if (searchRight) {
			Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
		} else {
			Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
		}
		
		while (true) {
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			if (isOnwhite()) {
				return true;
			}	
				
			if (Math.abs(rotDegree[0] - rotDegree[1]) > SEARCH_ROTATION_TARGET + SEARCH_ROTATION_TOLERANCE) {
				StraightLines.stop();
				return false;
			}
		}
	}
	
	private static void setBack() {
		message.echo("setting back");
		StraightLines.stop();
		
		boolean turnRight = (rotDegree[0] - rotDegree[1]) > 0.0f;
		
		if (turnRight) {
			Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
		} else {
			Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
		}
		
		while (true) {
			gyro.getAngleMode().fetchSample(rotDegree, 1);
			message.echo("" + Math.abs(rotDegree[0] - rotDegree[1]));
			if (Math.abs(rotDegree[0] - rotDegree[1]) < STRAIGHT_ANGLE_TOLERANCE) {
				StraightLines.stop();
				break;
			}
		}
		
	}
		
	
}
