package movementControl;

import lejos.utility.DebugMessages;
import Sensor.OwnColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.Color;

import mainRobotControl.AbstractInterruptableStateRunner;
import mainRobotControl.ParcourState;
import mainRobotControl.StateMachine;
import skills.*;

public class Maze extends AbstractInterruptableStateRunner {

	private DebugMessages message = new DebugMessages(1);

	private OwnColorSensor col;
	private EV3GyroSensor gyro;

	private FollowLine followLine;


	private float[] rotDegree = new float[] { 0.0f, 0.0f };

	private static final int LINE_SPEED = 350;
	
	private static final int APPROACH_SPEED = 300;
	
	private boolean closeToMaze = false;
	

	/**
	 * Starts motors to run straight with ~55% speed. </br>
	 * </br>
	 * {@inheritDoc}
	 */
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Finding a way out of the maze.");
		col = Sensors.getColor();
		gyro = Sensors.getGyro();

		followLine = new FollowLine(col, gyro);

		followLine.preLoopActions();
		Sensors.sonicDown();
		enterMaze();
	}

	@Override
	protected void inLoopActions() {
		/*int groundColor = col.getColorID();
		switch (groundColor) {
		case Color.RED:
			message.echo("found red");
			StraightLines.stop();
			lejos.utility.Delay.msDelay(5);
			message.echo("checking available choices");
			MazeRedPoint.Choice myChoice = MazeRedPoint.getRightMostAvailableChoice();
			this.executeChoice(myChoice);
			break;
		case Color.BLUE:
			message.echo("Found blue, switching to bridge");
			running = false;
			break;
		default:
			// followLine.preLoopActions();
			followLine.inLoopActions();
			break;*/
		//}
		inMazeAction();
	}

	private void enterMaze() {
		StraightLines.resetMotors();
		if(!closeToMaze) {
			StraightLines.regulatedForwardDrive(APPROACH_SPEED);			
		} else {
			StraightLines.regulatedForwardDrive(LINE_SPEED);
		}
		while (true) {
			int groundColor = col.getColorID();

			if (groundColor == Color.WHITE) {
				break;
			}
			if (groundColor == Color.BLUE) {
				closeToMaze = true;
			}

			lejos.utility.Delay.msDelay(10);
		}
		StraightLines.wheelRotation(0.2f, LINE_SPEED);
		Curves.turnRight90();

	}


	private void inMazeAction() {
		int groundColor = col.getColorID();
		switch (groundColor) {
		case Color.RED:
			message.echo("found red");
			StraightLines.stop();
			lejos.utility.Delay.msDelay(5);
			message.echo("checking available choices");
			MazeRedPoint.getRightMostAvailableChoice();
			this.continueDriving();
			break;
		case Color.BLUE:
			message.echo("Found blue, switching to bridge");
			this.running = false;
			break;
		default:
			// followLine.preLoopActions();
			followLine.inLoopActions();
			break;
		}
	}

	private void continueDriving() {
		message.echo("continue driving");
		StraightLines.stop();
		lejos.utility.Delay.msDelay(10);
		StraightLines.resetMotors();
		StraightLines.regulatedForwardDrive(LINE_SPEED);
	}

	protected void postLoopActions() {
		message.echo("Post loop action");
		message.echo("Post loop action");
		message.echo("Post loop action");
		message.echo("Post loop action");
		StraightLines.stop();
		StateMachine.getInstance().setState(ParcourState.ON_BRIDGE);
	}
}
