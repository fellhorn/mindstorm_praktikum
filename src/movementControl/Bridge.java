package movementControl;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;

public class Bridge extends AbstractInterruptableStateRunner {
	
	private DebugMessages message = new DebugMessages(5);
	
	protected void runState() {
		try {
			message.echo("Crossing Bridge. Don't fall!");
			Thread.sleep(100);
		} catch (InterruptedException e) {
			return;
		}
	}
}
