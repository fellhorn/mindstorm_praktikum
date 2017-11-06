package src.movementControl;
import lejos.utility.DebugMessages;
import src.mainRobotControl.AbstractInterruptableStateRunner;

public class Line extends AbstractInterruptableStateRunner {
	
	private DebugMessages message = new DebugMessages(5);
	
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Following white line.");		
	}

	@Override
	protected void inLoopActions() {
		return;
	}

	@Override
	protected void postLoopActions() {
		return;
	}
}
