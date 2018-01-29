package idleControl;

import Sensor.OwnColorSensor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;
import skills.Curves;
import skills.Sensors;

public class Test extends AbstractInterruptableStateRunner {

	//EV3ColorSensor col;
	int ticks;
	DebugMessages message;
	OwnColorSensor col;
	@Override
	protected void preLoopActions() {
		// TODO Auto-generated method stub
		//src.skills.Sensors.calibrateSonic(0.3f);
		//skills.StraightLines.wheelRotation(0.25f, 400);
		col = Sensors.getColor();
		/*boolean tr = col.setFloodlight(Color.YELLOW);
		*/message = new DebugMessages(1);
		message.clear();
/*
		message.clear();
		System.out.println(tr + " " + col.getFloodlight());
		int id = col.getColorID();
		message.echo("Color: " +  id);
		//col = new OwnColorSensor(SensorPort.S2);
		ticks = 0;*/
	}

	@Override
	protected void inLoopActions() {
		/*if(id != col.getColorID()){*/
			int id = col.getColorID();
			lejos.utility.Delay.msDelay(500);
			//message.clear();
			//message.echo("Color: " +  id);
		
		/*}*/
		//spiral 
		//float smooth = 1.0f - (ticks / 5000.0f);
		//Curves.smoothSpeededLeftTurn(smooth, 300);
		//ticks++;
		//col.getColorID();
		
	}

	@Override
	protected void postLoopActions() {
		// TODO Auto-generated method stub

	}

}
