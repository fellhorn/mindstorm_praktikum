package skills;
import lejos.utility.DebugMessages;

import java.util.HashSet;
import java.util.Set;

import Sensor.OwnColorSensor;
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
	private static OwnColorSensor col = Sensors.getColor();
	private static EV3GyroSensor gyro = Sensors.getGyro();
	
	private static float[] rotDegree = new float[] {0.0f, 0.0f};

	private static final float SEARCH_ROTATION_TOLERANCE = 15.0f;
	private static final float SEARCH_ROTATION_TARGET = 90.0f; 
	private static final int STRAIGHT_SPEED = 100;

	private static final float STRAIGHT_ROTATIONS = 0.4f;
	private static final int ROTATION_SPEED = 80;
	private static final float STRAIGHT_ANGLE_TOLERANCE  = 2.0f;



	/**
	 * Get the right most available choice. Which is this order: right, straight, left, back
	 * 
	 * @return
	 */
	public static void getRightMostAvailableChoice() {
		message.echo("Red square choice detection started.");
		gyro.getAngleMode().fetchSample(rotDegree, 0);
		
		int groundColor = col.getColorID();
		if (groundColor != Color.RED) {
			message.echo("has to start on red.");
			// throw new RuntimeException("Has to start on a red square");
		}
		
		StraightLines.wheelRotation(STRAIGHT_ROTATIONS, STRAIGHT_SPEED);
		if (searchRight()) {
			message.echo("Found right");
			return;
		}
		message.echo("no right option");
		
		Curves.smoothSpeededLeftTurn(-1, ROTATION_SPEED);
		
		groundColor = col.getColorID();
		while(groundColor != Color.WHITE) {
			lejos.utility.Delay.msDelay(10);
			groundColor = col.getColorID();
		}
		
		StraightLines.stop();
		
		return;
		
	}
	
	private static boolean isOnwhite() {
		int groundColor = col.getColorID();
		return groundColor == Color.WHITE;
	}	
	
	private static boolean searchRight() {
		Curves.turnRight90();
		Curves.smoothSpeededRightTurn(-1, ROTATION_SPEED);
		
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
	
}
