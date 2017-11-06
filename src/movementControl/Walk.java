package movementControl;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.TouchAdapter;
import mainRobotControl.AbstractInterruptableStateRunner;


public class Walk extends AbstractInterruptableStateRunner {

	private EV3TouchSensor t = new EV3TouchSensor(SensorPort.S1);
	private TouchAdapter touch = new TouchAdapter(t);
	private EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.A);
	private EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.D);
	
	protected void runState() {
		left.setSpeed((int) (left.getMaxSpeed()*0.7));
		left.forward();
		right.setSpeed((int) (right.getMaxSpeed()*0.7));
		right.forward();
		while(!(touch.isPressed()));
	}
}
