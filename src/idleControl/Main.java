package idleControl;
import mainRobotControl.AbstractInterruptableStateRunner;

public class Main extends AbstractInterruptableStateRunner {
	protected void runState() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			return;
		}
	}
}
