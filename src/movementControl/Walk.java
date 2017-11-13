package src.movementControl;


import lejos.utility.DebugMessages;
import src.mainRobotControl.AbstractInterruptableStateRunner;

public class Walk extends AbstractInterruptableStateRunner {

	// private EV3TouchSensor t = new EV3TouchSensor(SensorPort.S4);
	// private TouchAdapter touch = new TouchAdapter(t);
	private DebugMessages message = new DebugMessages(1);
	private int setValue = 400;

	private void adjustSpeed() {
		// Get actual value that was measured
		int tachoLeft = src.skills.StraightLines.getLeft().getTachoCount();
		int tachoRight = src.skills.StraightLines.getRight().getTachoCount();

		int diff = tachoLeft - tachoRight;
	
		src.skills.StraightLines.getLeft().setSpeed(setValue - diff);
		src.skills.StraightLines.getRight().setSpeed(setValue + diff);
		src.skills.StraightLines.getLeft().forward();
		src.skills.StraightLines.getRight().forward();
		//System.out.println("d=" + diff + ",L" + src.skills.StraightLines.getLeft().getSpeed()
		//		+",R"+src.skills.StraightLines.getRight().getSpeed());
	}

	@Override
	protected void preLoopActions() {
		src.skills.StraightLines.getLeft().resetTachoCount();
		src.skills.StraightLines.getRight().resetTachoCount();
		
		// Start engines
		src.skills.StraightLines.getLeft().setSpeed(setValue);
		src.skills.StraightLines.getRight().setSpeed(setValue);
		src.skills.StraightLines.getLeft().forward();
		src.skills.StraightLines.getRight().forward();

		message.clear();
		System.out.println("Starting Engine");

		return;
	}

	@Override
	protected void inLoopActions() {
		adjustSpeed();
		return;
	}

	@Override
	protected void postLoopActions() {
		return;
	}
}
