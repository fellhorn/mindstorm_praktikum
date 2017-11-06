package src.mainRobotControl;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;

public abstract class AbstractInterruptableStateRunner {
	public final Key INTERRUPT_BUTTON = Button.LEFT;
	protected boolean running = true;
	
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
		}
		postLoopActions();
	}
		
	protected abstract void preLoopActions();
	
	protected abstract void inLoopActions();
	
	protected abstract void postLoopActions();
}
