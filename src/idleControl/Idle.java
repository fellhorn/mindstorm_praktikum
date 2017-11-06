package src.idleControl;
import lejos.utility.DebugMessages;
import src.mainRobotControl.AbstractInterruptableStateRunner;

public class Idle extends AbstractInterruptableStateRunner {
	
	private DebugMessages message = new DebugMessages(5);
	
	protected void runState() {
		try {
			message.echo("Nothing to do here.");
			Thread.sleep(100);
		} catch (InterruptedException e) {
			return;
		}
	}
}
