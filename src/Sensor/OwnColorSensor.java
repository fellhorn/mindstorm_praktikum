package Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.utility.DebugMessages;

/**
 * Color sensor that distinguishes white, black, blue, red and no color/air with respect to the colors actually
 * used in the course.
 * @author Daniel
 *
 */
public class OwnColorSensor extends EV3ColorSensor {

	DebugMessages mes = new DebugMessages();

	public OwnColorSensor(Port port) {
		super(port);
		setCurrentMode("RGB");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getColorID() {
		float[] sample = new float[3];
		fetchSample(sample, 0);
		mes.clear();
		mes.echo(Float.toString(sample[0] + sample[1] + sample[2]));
		if(sample[0] + sample[1] + sample[2] > 0.5){
			mes.echo("WHITE");
			return Color.WHITE;			
		}else if(sample[0] + sample[1] + sample[2] < 0.01) {
			mes.echo("NONE");
			return Color.NONE;
		}else if(sample[0] + sample[1] + sample[2] < 0.2) {
			mes.echo("BLACK");
			return Color.BLACK;
		}else if(sample[0]/sample[2] < 1){
			mes.echo("BLUE");
			return Color.BLUE;
		}else{
			mes.echo("RED");
			return Color.RED;
		}
	}

}
