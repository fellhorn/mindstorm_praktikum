package mainRobotControl;

import view.StateSelector;


public class Main {

	public static void main(String[] args) {
		// just some first test
		StateSelector menu = new StateSelector();
		ParcourState state = menu.getSelectedState();
		
		StateMachine.getInstance().setState(state);
	}

}
