package movementControl;

import skills.StraightLines;
import mainRobotControl.AbstractInterruptableStateRunner;

import lejos.utility.DebugMessages;

public class Walk extends AbstractInterruptableStateRunner {

	private DebugMessages message = new DebugMessages(1);


	@Override
	protected void preLoopActions() {
		StraightLines.resetMotors();
		StraightLines.regulatedForwardDrive(400);

		message.clear();
		System.out.println("Starting Engine");

		return;
	}

	@Override
	protected void inLoopActions() {
		StraightLines.regulatedForwardDrive(400);
		return;
	}

	@Override
	protected void postLoopActions() {
		StraightLines.resetMotors();
		StraightLines.stop();
		return;
	}
}
