package movementControl;
import Sensor.OwnColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.Color;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;
import mainRobotControl.ParcourState;
import mainRobotControl.StateMachine;
import skills.Curves;
import skills.Sensors;
import skills.StraightLines;

public class Bridge extends AbstractInterruptableStateRunner {
	
	private enum BridgeStates {
		ON_RAMP_UP,
		ON_BRIDGE,
		ON_RAMP_DOWN
	}
	private BridgeStates bridgeState;
	private EV3UltrasonicSensor sonicSensor;
	private OwnColorSensor colorSensor;
	private static final float MAX_DISTANCE = 0.35f,
								SUB_INFINITY = 0.90f;
	private static final int UP_SPEED = 400;
	private static final int TRAVERSE_SPEED = 200;
	private static final int DOWN_SPEED = 50;
	
	private DebugMessages message = new DebugMessages(5);
	
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Crossing Bridge");
		message.echo("Adjust ultrasonic sensor");		
		
		sonicSensor = Sensors.getSonic();
		colorSensor = Sensors.getColor();
		
		// Move ultra-sonic sensor ~90 degrees up
		sonicSensor.enable();
		Sensors.sonicUp();
		// TODO: bridge speed??
		
		message.echo("ON_RAMP_UP");
		bridgeState = BridgeStates.ON_RAMP_UP;
	}
	
	private void backOffAndTurn() {
		// Stop wheels
		StraightLines.stop();
		
		// Turn left 90 degrees
		Curves.turnLeft90();
		
		// Reset counters
		StraightLines.resetMotors();
	}

	@Override
	protected void inLoopActions() {
		// Check for end
		if (colorSensor.getColorID() == Color.BLUE && bridgeState == BridgeStates.ON_RAMP_DOWN) {
			running = false;
		}
		float [] sample = {0f,0f};
		sonicSensor.getDistanceMode().fetchSample(sample, 0);
		message.echo("Dist: " + sample[0]);
		
		// Continuously check if sensor measures infinity
		if (sample[0] > MAX_DISTANCE && sample[0] < SUB_INFINITY) {
			if (bridgeState == BridgeStates.ON_RAMP_UP) {
				message.echo("ON_BRIDGE");
				// TODO: set bridge speed
				backOffAndTurn();
				bridgeState = BridgeStates.ON_BRIDGE;
				
			} else if (bridgeState == BridgeStates.ON_BRIDGE) {
				message.echo("ON_RAMP_DOWN");
				// TODO: set bridge speed

				backOffAndTurn();
				bridgeState = BridgeStates.ON_RAMP_DOWN;
			} else {
				// Damn
			}
		} else {
			switch(bridgeState) {
			case ON_RAMP_UP:
				StraightLines.regulatedForwardDrive(UP_SPEED);
				break;
			case ON_BRIDGE:
				StraightLines.regulatedForwardDrive(TRAVERSE_SPEED);
				break;
			case ON_RAMP_DOWN:
				StraightLines.regulatedForwardDrive(DOWN_SPEED);
				break;
			}
		}
		return;
	}

	@Override
	protected void postLoopActions() {
		message.echo("BRIDGE_PASSED!");
		
		// The ultrasonic sensor is not used anymore
		Sensors.sonicDown();
		Sensors.getSonic().disable();
		StateMachine.getInstance().setState(ParcourState.SEARCH_SPOTS);
	}
}
