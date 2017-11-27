package idleControl;

import mainRobotControl.AbstractInterruptableStateRunner;

public class Test extends AbstractInterruptableStateRunner {

	@Override
	protected void preLoopActions() {
		// TODO Auto-generated method stub
		//src.skills.Sensors.calibrateSonic(0.3f);
		skills.StraightLines.wheelRotation(0.25f, 400);
	}

	@Override
	protected void inLoopActions() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void postLoopActions() {
		// TODO Auto-generated method stub

	}

}
