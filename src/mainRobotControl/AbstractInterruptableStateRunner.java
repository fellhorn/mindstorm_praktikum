package src.mainRobotControl;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.utility.Delay;


/**
 * Abstract template for a class that controls the robot in a specific stage of the race.
 * @author Dennis, Daniel
 */
public abstract class AbstractInterruptableStateRunner {
	
	/**
	 * Button to interrupt current state and go to menu.
	 */
	protected final Key INTERRUPT_BUTTON = Button.LEFT;
	/**
	 * Indicates that state is being executed. Set false to leave state.
	 */
	protected boolean running = true;
	
	
	/**
	 * Performs the actions defined to solve this state of the course. </br>
	 * Each state can be interrupted to the main menu by clicking the left button.
	 */
	public void run() {
		INTERRUPT_BUTTON.addKeyListener(new KeyListener(){
		    @Override public void keyReleased(final Key k){
		    }
		    @Override public void keyPressed(final Key k){
		      running = false;}
		});
		preLoopActions();
		while(running) {
			inLoopActions();
			Delay.msDelay(10);
			
		}
		postLoopActions();
	}
	
	
	/**
	 * Configuring robot on start of the state. If state is entered from another state, 
	 * this MUST be in sync with previous states postLoopActions() method.
	 */
	protected abstract void preLoopActions();
	
	/**
	 * Function that is called each 10-15ms. Can be used to fetch new sensor values or 
	 * control parameters and adjust the robots behavior. </br></br>
	 * ATTENTION: This should NOT contain any kind of loop as it is called repeatedly anyways.
	 *  Any loops will interfere with the return to menu function.
	 */
	protected abstract void inLoopActions();
	
	/**
	 * Stopping state execution after state has been terminated. If state is exited to another state, 
	 * this MUST be in sync with next states preLoopActions() method.
	 */

	protected abstract void postLoopActions();
}
