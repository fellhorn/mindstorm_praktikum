package src.movementControl;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.DebugMessages;
//import lejos.hardware.port.SensorPort;
//import lejos.hardware.sensor.EV3TouchSensor;
//import lejos.robotics.TouchAdapter;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import src.mainRobotControl.AbstractInterruptableStateRunner;


public class Walk extends AbstractInterruptableStateRunner {

	//private EV3TouchSensor t = new EV3TouchSensor(SensorPort.S4);
	//private TouchAdapter touch = new TouchAdapter(t);
	private DebugMessages message = new DebugMessages(1);
	private EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.A);
	private EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.D);
	private Timer timer;
	private int setValue = 0;
	
	private class ControllListener implements TimerListener {
		@Override
		public void timedOut() {
			adjustSpeed();
			left.resetTachoCount();
			right.resetTachoCount();
		}
		
		private void adjustSpeed() {
			// Get actual value that was measured
			int tachoLeft = left.getTachoCount();
			int tachoRight = right.getTachoCount();
			
			int diffLeft = setValue - tachoLeft;
			int diffRight = setValue - tachoRight;
			
			left.setSpeed(setValue + diffLeft);
			right.setSpeed(setValue + diffRight);
			System.out.println("Adjusted Speed");
		}

	 //TODO Implement cross feedback to slow one motor if the other is blocking
	 /* 
	 * 
	 * 
	 * private void adjustSpeedDifferential() {
			// Get actual value that was measured
			int tachoLeft = left.getTachoCount();
			int tachoRight = right.getTachoCount();
			
			int diff = tachoLeft - tachoRight;
			
			if (diff < 0) {
				// Right > Left
				left.setSpeed(setValue - diff/2);
				right.setSpeed(setValue + diff/2);
			} else {
				// Right <= Left
				left.setSpeed(setValue - diff/2);
				right.setSpeed(setValue + diff/2);
			}
		}*/
	}
	
	@Override
	protected void preLoopActions() {
		// Start engines
		left.setSpeed(setValue);
		right.setSpeed(setValue);
		left.forward();
		right.forward();
		
		message.clear();
		System.out.println("Starting Engine");
		
		// Start controller
		timer = new Timer(1000, new ControllListener());
		timer.start();
		return;		
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
