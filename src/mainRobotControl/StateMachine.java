package mainRobotControl;

import lejos.utility.DebugMessages;

public class StateMachine {
	private static StateMachine instance = null;
	private ParcourState currentState = ParcourState.IDLE;
	private AbstractInterruptableStateRunner currentController;
	private DebugMessages def = new DebugMessages(1);
	
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
		case LINE_FOLLOWER: startLineState(previousState); break;
		case ON_BRIDGE: startBridgeState(previousState); break;
		case RUN_AD_1: startWalkingState(previousState); break;
		case TEST: startTestingState(previousState); break;
		case MAZE: startMazeState(previousState); break;
		case SEARCH_SPOTS: startSpotState(previousState); break;
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
		currentController = new idleControl.Idle();
		currentController.run();
	}
	
	private void startLineState(ParcourState previousState) {
		currentController = new movementControl.Line();
		currentController.run();
	}
	
	private void startBridgeState(ParcourState previousState) {
		currentController = new movementControl.Bridge();
		currentController.run();
	}
	
	private void startWalkingState(ParcourState previousState) {
		currentController = new movementControl.Walk();
		currentController.run();
	}
	
	private void startTestingState(ParcourState previousState) {
		currentController = new idleControl.Test();
		currentController.run();
	}
	
	private void startMazeState(ParcourState previousState) {
		currentController = new movementControl.Maze();
		currentController.run();
	}
	
	private void startSpotState(ParcourState previousState) {
		currentController = new movementControl.SpotSearch();
		currentController.run();
	}
	
	public ParcourState getState() {
		return currentState;
	}

}
