import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.TouchAdapter;

public class Main {

	static EV3TouchSensor t = new EV3TouchSensor(SensorPort.S1);
	static TouchAdapter touch = new TouchAdapter(t);

	
	//hello world tester
	public static void main(String[] args) {
		System.out.println("Hello World!");

		//wait for press on touch sensor
		while(!(touch.isPressed())){
			//IDLE
		}
		
		System.out.println("Thank you.");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
