package Sensor;

public class SensorThread extends Thread {

	private SingleValueSensorWrapper Sgyro;
	private OwnColorSensor Scolor;
	private SingleValueSensorWrapper Sdistance;

	public float gyroAngle, color, distance;

	public SensorThread(SingleValueSensorWrapper Sgyro, OwnColorSensor Scolor, SingleValueSensorWrapper Sdistance) {
		this.Sgyro = Sgyro;
		this.Scolor = Scolor;
		this.Sdistance = Sdistance;
	}

	public void run() {
		try {
			while (true) {
				this.gyroAngle = this.Sgyro.getSample();
				this.color = this.Scolor.getColorID();
				this.distance = this.Sdistance.getSample();
				Thread.sleep(20);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
