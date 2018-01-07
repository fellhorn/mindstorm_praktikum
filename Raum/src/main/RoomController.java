package main;

import java.io.IOException;

import javax.swing.plaf.synth.SynthSpinnerUI;

import robotControl.RobotController;

public class RoomController extends RobotController {
	private class DistanceListener {
		int distance;
		Directions direction;
		
		public DistanceListener(int distance, Directions direction) {
			this.distance = distance;
			this.direction = direction;
		}
		public void update() {
			
		}
	}
	private static enum Directions {
		BACKOFF,
		TURN,
		NORTH,
		WEST,
		SOUTH,
		EAST {
			public Directions next() {
				return NORTH;
			};
		};		
	};
	
	private int backOffDistance = 35;
	private int actualDistance = 0;
	private static Directions backOffDir;
	
	private static Directions direction;
	private float directionDegrees = 0; // direction from start counter-clockwise
	/**
	 * Constructor for Controller in Labyrinth Parcour
	 */
	public RoomController() {
		direction = Directions.SOUTH;
	}

	private void backOff(Directions nextDir) {
		backOffDir = direction;
		direction = Directions.BACKOFF;
		
		
	}
	
	private void addDistanceListener() {}
	
	private void turnRight90() {
		
		// Save old direction 
		// move gradually to dir + 90 degs
	}
	
	/**
	 * Implements the logic for the robot controls.
	 * @param lightSensorRedValue Value of the red channel from the lightsensor
	 * @param lightSensorBlueValue Value of the blue channel from the lightsensor
	 * @param lightSensorGreenValue Value of the green channel from the lightsensor
	 * @param distance distance meassured from the distancesensor
	 * All light values are given in rgb pixel values (0-255)
	 */
	public double[] getControlAction(int lightSensorRedValue, int lightSensorBlueValue, int lightSensorGreenValue, double distance, boolean touch)
	{
		//speed in pixel per cycle
		double motorSpeedLeft = 0;
		double motorSpeedRight = 0;
		//robot kinematics is as follows:
		//center of rotation is one third in, seen from the front
		//the light sensor is mounted on the center of the front 
		//the robot has a touchbar mounted on the front
		
		// Insert Code here
		switch (direction) {
		case SOUTH:
			motorSpeedLeft = 1;
			motorSpeedRight = 1;
			if (touch) {
				backOff(Directions.EAST);
			}
			break;
		case WEST:
			motorSpeedLeft = 1;
			motorSpeedRight = 1;
			if (touch) {
		
				backOff(Directions.SOUTH);
			}
			break;
		case EAST:
			motorSpeedLeft = 1;
			motorSpeedRight = 1;
			if (touch) {
				backOff(Directions.NORTH);
			}
			break;
		case NORTH:
			motorSpeedLeft = 1;
			motorSpeedRight = 1;
			if (touch) {
				backOff(Directions.WEST);
			}
			break;
		case BACKOFF:
			actualDistance++;
			motorSpeedLeft = motorSpeedRight = -1;
			if (actualDistance > backOffDistance) {
				direction = Directions.TURN; actualDistance = 0;
				backOffDistance += 10;
			}
			break;
		case TURN:
			if (directionDegrees < 90) {
				System.out.println("current degrees: " + directionDegrees);
	
				directionDegrees+=2.307691573;
				motorSpeedLeft = 1;
				motorSpeedRight = -1;
			} else {
				if (backOffDir != Directions.EAST) {
					direction = Directions.values()[backOffDir.ordinal()+1];
				} else 
				{
					direction = Directions.NORTH;
				}
				motorSpeedLeft = motorSpeedRight = 0;
				directionDegrees = 0.0f;
			}
			System.out.println(backOffDir);
			break;
			default:
				break;
		}
		
		double[] motorSpeeds = {motorSpeedLeft, motorSpeedRight};
		return motorSpeeds;
	}


	@Override
	public double[] getControlAction(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}
}
