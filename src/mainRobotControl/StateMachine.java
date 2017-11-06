package src.mainRobotControl;

import lejos.utility.DebugMessages;

public class StateMachine {
	private static StateMachine instance = null;
	private ParcourState currentState = ParcourState.IDLE;
	private AbstractInterruptableStateRunner currentController;
	private DebugMessages def= new DebugMessages(15);
	
	private StateMachine() {
		currentState = ParcourState.IDLE;
	}
	
	private StateMachine(ParcourState startState) {
		currentState = startState;
	}
	
	public static StateMachine getInstance() {
		if (instance == null) {
			instance = new StateMachine();
		}
		return instance;
	}
	
	public static StateMachine getInstance(ParcourState startState) {
		if (instance == null) {
			instance = new StateMachine(startState);
		}
		return instance;
	}
	
	public void setState(ParcourState newState) {
		ParcourState previousState = currentState; 
		currentState = newState;
		
		startStateController(previousState);
	}
	
	private void startStateController(ParcourState previousState) {
		switch (currentState) {
		case IDLE: startIdleState(previousState); break;
		case LINE_FOLLOWER:
		case GAP_IN_LINE: startLineState(previousState); break;
		case ASCEND_BRIDGE:
		case DESCENT_BRIDGE:
		case ON_BRIDGE: startBridgeState(previousState); break;
		case RUN_AD_1: startWalkingState(previousState); break;
		default:
			def.clear();
			def.echo("This state is currently not handled.");
			try {
				Thread.sleep(1000);
				//Then fall back into menu.
			} catch (InterruptedException e) {
				return;
			}
			break;
		}	
	}
	
	private void startIdleState(ParcourState previousState) {
		currentController = new src.idleControl.Idle();
		currentController.run();
	}
	
	private void startLineState(ParcourState previousState) {
		currentController = new src.movementControl.Line();
		currentController.run();
	}
	
	private void startBridgeState(ParcourState previousState) {
		currentController = new src.movementControl.Bridge();
		currentController.run();
	}
	
	private void startWalkingState(ParcourState previousState) {
		currentController = new src.movementControl.Walk();
		currentController.run();
	}
	
	public ParcourState getState() {
		return currentState;
	}

}
