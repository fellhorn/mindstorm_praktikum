package src.movementControl;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.TouchAdapter;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import src.mainRobotControl.AbstractInterruptableStateRunner;


public class Walk extends AbstractInterruptableStateRunner {

	private EV3TouchSensor t = new EV3TouchSensor(SensorPort.S4);
	private TouchAdapter touch = new TouchAdapter(t);
	private EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.A);
	private EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.D);
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
		}

		private void adjustSpeedDifferential() {
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
		}
	}
	
	@Override
	protected void preLoopActions() {
		// Start engines
		left.setSpeed(setValue);
		right.setSpeed(setValue);
		left.forward();
		right.forward();
		
		// Start controller
		Timer timer = new Timer(1000, new ControllListener());
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
