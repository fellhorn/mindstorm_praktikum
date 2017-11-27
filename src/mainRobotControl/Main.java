package mainRobotControl;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;

import view.StateSelector;


public class Main {

	public static final Key QUIT_BUTTON = Button.ESCAPE;
	public static boolean running = true;
	
	public static void main(String[] args) {
		QUIT_BUTTON.addKeyListener(new KeyListener(){
		    @Override public void keyReleased(final Key k){
		    }
		    @Override public void keyPressed(final Key k){
		      System.exit(0);
		    }
		});
		
		
		while(running) {
			// just some first test
			StateSelector menu = new StateSelector();
			ParcourState state = menu.getSelectedState();
			
			StateMachine.getInstance().setState(state);
		}

	}

}
