package movementControl;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;
import skills.Curves;
import skills.Sensors;
import skills.StraightLines;

public class Bridge extends AbstractInterruptableStateRunner {
	
	private enum BridgeStates {
		ON_RAMP_UP,
		BACK_UP_LEFT,
		ON_PLANE,
		ON_RAMP_DOWN
	}
	private BridgeStates bridgeState;
	private EV3UltrasonicSensor sonicSensor;
	private static final float MAX_DISTANCE = 0.35f;
	
	private DebugMessages message = new DebugMessages(5);
	
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Crossing Bridge\nAdjust ultrasonic sensor");		
		
		sonicSensor = Sensors.getSonic();
		
		// Move ultra-sonic sensor ~90 degrees down
		Sensors.sonicDown();
		message.echo("ON_RAMP_UP");
		// drive straight forward
		StraightLines.regulatedForwardDrive(500);
		bridgeState = BridgeStates.ON_RAMP_UP;
		
	}
	
	private void backUpAndTurn() {
		// Stop wheels
		StraightLines.stop();
		
		// Back up 
		StraightLines.wheelRotation(-1, 300);
		
		// Turn left 90 degrees
		Curves.turnLeft90();
		
		// Continue driving forward
		StraightLines.regulatedForwardDrive(500);
	}

	@Override
	protected void inLoopActions() {
		float [] sample = {0f,0f};
		sonicSensor.getDistanceMode().fetchSample(sample, 0);
		
		// Continuously check if sensor measures infinity
		if (sample[0] > MAX_DISTANCE) {
			if (bridgeState == BridgeStates.ON_RAMP_UP) {
				message.echo("ON_PLANE");

				backUpAndTurn();
				bridgeState = BridgeStates.ON_PLANE;
				
			} else if (bridgeState == BridgeStates.ON_PLANE) {
				message.echo("ON_RAMP_DOWN");

				backUpAndTurn();
				bridgeState = BridgeStates.ON_RAMP_DOWN;
			} else {
				// Damn
			}
		} else {
			StraightLines.regulatedForwardDrive(500);
		}
		return;
	}

	@Override
	protected void postLoopActions() {
		return;
	}
}
