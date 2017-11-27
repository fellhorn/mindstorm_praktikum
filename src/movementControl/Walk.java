package src.movementControl;


import lejos.utility.DebugMessages;
import src.mainRobotControl.AbstractInterruptableStateRunner;

public class Walk extends AbstractInterruptableStateRunner {

	private DebugMessages message = new DebugMessages(1);


	@Override
	protected void preLoopActions() {
		src.skills.StraightLines.resetMotors();
		src.skills.StraightLines.startEngines(400);

		message.clear();
		System.out.println("Starting Engine");

		return;
	}

	@Override
	protected void inLoopActions() {
		src.skills.StraightLines.regulatedForwardDrive(400);
		return;
	}

	@Override
	protected void postLoopActions() {
		src.skills.StraightLines.resetMotors();
		src.skills.StraightLines.stop();
		return;
	}
}
