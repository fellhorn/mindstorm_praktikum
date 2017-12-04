package idleControl;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;
import skills.Curves;
import skills.Sensors;

public class Test extends AbstractInterruptableStateRunner {

	EV3ColorSensor col;
	int id;
	DebugMessages message;
	@Override
	protected void preLoopActions() {
		// TODO Auto-generated method stub
		//src.skills.Sensors.calibrateSonic(0.3f);
		//skills.StraightLines.wheelRotation(0.25f, 400);
		/*col = Sensors.getColor();
		boolean tr = col.setFloodlight(Color.YELLOW);
		message = new DebugMessages(1);
		message.clear();
		System.out.println(tr + " " + col.getFloodlight());
		id = col.getColorID();
		//message.echo("Color: " +  id);*/
	}

	@Override
	protected void inLoopActions() {
		/*if(id != col.getColorID()){
			id = col.getColorID();
			message.clear();
			message.echo("Color: " +  id);
		
		}*/
		Curves.smoothSpeededLeftTurn(0, 300);
		
	}

	@Override
	protected void postLoopActions() {
		// TODO Auto-generated method stub

	}

}
