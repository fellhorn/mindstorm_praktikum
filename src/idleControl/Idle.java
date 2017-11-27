package idleControl;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;

public class Idle extends AbstractInterruptableStateRunner {
	
	private DebugMessages message = new DebugMessages(5);


	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Nothing to do here.");		
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
