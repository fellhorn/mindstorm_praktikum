package movementControl;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;

public class Line extends AbstractInterruptableStateRunner {
	
	private DebugMessages message = new DebugMessages(5);
	
	protected void runState() {
		try {
			message.echo("Following the white line.");
			Thread.sleep(100);
		} catch (InterruptedException e) {
			return;
		}
	}
}
