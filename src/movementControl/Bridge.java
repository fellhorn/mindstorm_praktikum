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
								SUB_INFINITY = 0.90f,
								BACKOFF_DISTANCE_FIRST = 0.25f,
								BACKOFF_DISTANCE_SECOND = 0.125f;
	private static final int UP_SPEED = 400,
								TRAVERSE_SPEED = 200,
								DOWN_SPEED = 50;
	private float [] sample = {0f, 0f};
	private int majorityVote = 0;
	private static int MAJORITY_VOTE_COUNT = 100;
	
	private DebugMessages message = new DebugMessages(5);
	
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Crossing Bridge");
		message.echo("Adjust ultrasonic sensor");		
		
		sonicSensor = Sensors.getSonic();
		colorSensor = Sensors.getColor();
		
		// Move ultra-sonic sensor ~90 degrees up
		Sensors.sonicUp();
		sonicSensor.enable();
		// TODO: bridge speed??
		
		message.echo("ON_RAMP_UP");
		bridgeState = BridgeStates.ON_RAMP_UP;
	}
	
	private void backOffAndTurn(float backOffDistance) {
		// Stop wheels
		StraightLines.stop();
		

		for(int i=0;  i < MAJORITY_VOTE_COUNT ; i++) {
			sonicSensor.getDistanceMode().fetchSample(sample, 0);
			message.echo("Dist: " + sample[0]);
			
			if (sample[0] > MAX_DISTANCE && sample[0] < SUB_INFINITY) {
				majorityVote ++;
			}
		}
		message.clear();
		message.echo("Voted: " + majorityVote + " of " + MAJORITY_VOTE_COUNT);
		if(majorityVote >= MAJORITY_VOTE_COUNT * 0.5) {
			// Back off 
			StraightLines.wheelRotation(-backOffDistance, 200);
			
			// Turn left 90 degrees
			Curves.turnLeft90();
			
			// Reset counters
			StraightLines.resetMotors();
			swapState();
		}

	}

	@Override
	protected void inLoopActions() {
		// Check for end
		if (colorSensor.getColorID() == Color.BLUE && bridgeState == BridgeStates.ON_RAMP_DOWN) {
			running = false;
		}
		sonicSensor.getDistanceMode().fetchSample(sample, 0);
		message.echo("Dist: " + sample[0]);
		
		// Continuously check if sensor measures infinity
		if (sample[0] > MAX_DISTANCE && sample[0] < SUB_INFINITY) {
			if (bridgeState == BridgeStates.ON_RAMP_UP) {

				message.echo("Check ON_BRIDGE");
				backOffAndTurn(BACKOFF_DISTANCE_FIRST);

				
			} else if (bridgeState == BridgeStates.ON_BRIDGE) {
				message.echo("Check ON_RAMP_DOWN");

				backOffAndTurn(BACKOFF_DISTANCE_SECOND);

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
	
	protected void swapState() {
		if(bridgeState == BridgeStates.ON_RAMP_UP) {
			bridgeState = BridgeStates.ON_BRIDGE;
		}
		if(bridgeState == BridgeStates.ON_BRIDGE) {
			bridgeState = BridgeStates.ON_RAMP_DOWN;
		}
	}
}
