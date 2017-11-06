package src.mainRobotControl;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;

public abstract class AbstractInterruptableStateRunner extends Thread {
	public final Key INTERRUPT_BUTTON = Button.ESCAPE;
	
	public void run() {
		INTERRUPT_BUTTON.addKeyListener(new KeyListener(){
		    @Override public void keyReleased(    final Key k){
		    }
		    @Override public void keyPressed(    final Key k){
		      Thread.currentThread().interrupt();
		    }
		});
		while(!Thread.currentThread().isInterrupted()) {
			runState();
		}
	}
		
	protected abstract void runState();
}
