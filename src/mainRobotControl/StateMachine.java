package mainRobotControl;

public class StateMachine {
	private static StateMachine instance = null;
	private ParcourState currentState = ParcourState.IDLE;
	private AbstractInterruptableStateRunner currentController;
	
	public StateMachine() {
		currentState = ParcourState.IDLE;
	}
	
	public StateMachine(ParcourState startState) {
		currentState = startState;
	}
	
	public static StateMachine getInstance() {
		if (instance == null) {
			instance = new StateMachine();
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
		default:
			//not handled yet
			break;
		}	
	}
	
	private void startIdleState(ParcourState previousState) {
		currentController = new idleControl.Main();
	}
	
	public ParcourState getState() {
		return currentState;
	}

}
