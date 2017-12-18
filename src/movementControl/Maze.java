package movementControl;
import lejos.utility.DebugMessages;


import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.Color;

import mainRobotControl.AbstractInterruptableStateRunner;
import mainRobotControl.StateMachine;
import skills.*;

public class Maze extends AbstractInterruptableStateRunner {

	private DebugMessages message = new DebugMessages(1);

	private EV3ColorSensor col;  
	private EV3GyroSensor gyro;
	
	private FollowLine followLine;

	public enum MazeState {
		FOLLOW_LINE,
		CROSSING,
	}
	private MazeState state;
	private float[] rotDegree = new float[] {0.0f, 0.0f};
	
	private static final int LINE_SPEED = 100;
	private static final int ROTATION_SPEED = 60;

	/**
	 * Starts motors to run straight with ~55% speed. </br></br>
	 * {@inheritDoc}
	 */
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Finding a way out of the maze.");
		col = Sensors.getColor();
		gyro = Sensors.getGyro();
		followLine = new FollowLine(col, gyro);
		state = MazeState.FOLLOW_LINE;
		followLine.preLoopActions();
	}

	@Override
	protected void inLoopActions() {
		int groundColor = col.getColorID();
		switch (groundColor) {
		case Color.RED:
			message.clear();
			message.echo("red");
			StraightLines.stop();
			this.state = MazeState.CROSSING;
			lejos.utility.Delay.msDelay(5);
			MazeRedPoint.Choice leftMostChoice = MazeRedPoint.getLeftMostAvailableChoice();
			this.executeChoice(leftMostChoice);
			break;
		default:
			followLine.inLoopActions();
			break;
		}
		//System.out.println(1000.0 / sw.elapsed());
		//sw.reset();
	}
	
	private void executeChoice(MazeRedPoint.Choice choice) {
		StraightLines.stop();
		lejos.utility.Delay.msDelay(10);
		if (choice == MazeRedPoint.Choice.BACK) {
			Curves.turnLeft90(); Curves.turnLeft90();
		}
		StraightLines.startEngines(LINE_SPEED);
	}

	protected void postLoopActions() {
		StraightLines.regulatedForwardDrive(12000);
	}
}
