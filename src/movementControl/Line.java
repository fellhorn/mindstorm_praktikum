package src.movementControl;
import lejos.utility.DebugMessages;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.Color;
import src.mainRobotControl.AbstractInterruptableStateRunner;

public class Line extends AbstractInterruptableStateRunner {
	
	private DebugMessages message = new DebugMessages(1);
	private boolean lastLossRigth = true;  //TODO should be set according to first curve 
	private EV3ColorSensor col = new EV3ColorSensor(SensorPort.S2);  //TODO check port
	private EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S3); //TODO check port
	
	
	/**
	 * Starts motors to run straight with (??) speed. </br></br>
	 * {@inheritDoc}
	 */
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Following white line.");
		//TODO move straight forward
	}

	@Override
	protected void inLoopActions() {
		int groundColor = col.getColorID(); 
		switch (groundColor) {
		case Color.WHITE:
			//TODO regulate to continue straight drive
			//TODO ENHANCEMENT speedup if line was straight for some time
			break;
		case Color.BLACK:
			if(lastLossRigth) {
				//TODO search  left first
			} else {
				//TODO search right first
			}
			//TODO assume gap and continue to drive straight
			break;
		case Color.RED:
			//TODO check actual color of barcode, change to next state
			break;
		default: 
			//TODO think of better error case behavior
			//stop robot if measurement error occurs
			running = false; 
			break;
		}
	}

	@Override
	protected void postLoopActions() {
		//TODO stop motors, clear global values in case some were set
	}
}
