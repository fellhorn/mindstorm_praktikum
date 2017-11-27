package movementControl;
import lejos.utility.DebugMessages;
import mainRobotControl.AbstractInterruptableStateRunner;

public class Bridge extends AbstractInterruptableStateRunner {
	
	private DebugMessages message = new DebugMessages(5);
	
	@Override
	protected void preLoopActions() {
		message.clear();
		message.echo("Crossing Bridge. Don't fall!");		
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
