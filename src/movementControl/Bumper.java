package movementControl;

import Sensor.SingleValueSensorWrapper;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;
import skills.Curves;
import skills.Sensors;
import skills.StraightLines;

public class Bumper extends AbstractInterruptableStateRunner{

	SingleValueSensorWrapper t;
	DebugMessages mes = new DebugMessages();
	@Override
	protected void preLoopActions() {

		t = new SingleValueSensorWrapper(Sensors.getTouch(), "Touch");
		StraightLines.regulatedForwardDrive(400);
	}

	@Override
	protected void inLoopActions() {
		if(t.getSample() == 1.0) {
			mes.clear();
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			mes.echo("#############");
			StraightLines.wheelRotation(-0.5f, 200);
			Curves.turnLeft90();
			StraightLines.resetMotors();
			StraightLines.regulatedForwardDrive(400);
		}
	}
	
	@Override
	protected void postLoopActions() {
		return;
	}

}
