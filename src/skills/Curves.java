package skills;

import lejos.utility.DebugMessages;

public class Curves {
	
	
	private static final int TURN_SPEED = 30;
	private static DebugMessages message = new DebugMessages(1);
	
	/**
	 * Robot performs a right turn by 90 degrees in the current place. The turn speed is constant and fairly slow.
	 * Turn angle only measured by gyroscope, which is not that accurate.<br>
	 * Turn is performed completely before method returns.
	 */
	//TODO variable turn speed and degrees
	public static void turnRight90() {
		float [] sample = {0f,0f};
		Sensors.getGyro().getAngleMode().fetchSample(sample, 0);
		Sensors.getGyro().getAngleMode().fetchSample(sample, 1);
		smoothSpeededRightTurn(-1, TURN_SPEED);
		
		while (sample[1] - sample[0] < 84) {
			Sensors.getGyro().getAngleMode().fetchSample(sample, 1);
		}
		StraightLines.stop();

	}
	
	/**
	 * Robot performs a left turn by 90 degrees in the current place. The turn speed is constant and fairly slow.
	 * Turn angle only measured by gyroscope, which is not that accurate.<br>
	 * Turn is performed completely before method returns.
	 */
	//TODO variable turn speed and degrees
	public static void turnLeft90() {
		float [] sample = {0f,0f};
		Sensors.getGyro().getAngleMode().fetchSample(sample, 0);
		Sensors.getGyro().getAngleMode().fetchSample(sample, 1);
		smoothSpeededLeftTurn(-1, TURN_SPEED);
		while (sample[0] - sample[1] < 84) {
			Sensors.getGyro().getAngleMode().fetchSample(sample, 1);
		}
		StraightLines.stop();
		//message.echo(sample[0] + ", " + sample[1]);
	}
	
	/**
	 * Robot starts driving a curve to the right. Speed and curvature can be adjusted. <br>
	 * Method returns immediately and robot keeps driving the curve.
	 * @param smoothness Value between 1.0 and -1.0 to adjust curvature <br>
	 * 			&emsp; -1.0: turn in place (around center of axis)<br>
	 * 			&emsp; 0.0: turn around left wheel as rotation center<br>
	 * 			&emsp; >0.0, <1.0: smooth curve with lateral movement<br>
	 * 			&emsp; 1.0: no turn at all
	 * @param speed Turn speed, should be ~100
	 */
	public static void smoothSpeededRightTurn(float smoothness, float speed) {
		StraightLines.getLeft().setSpeed(speed);
		StraightLines.getRight().setSpeed(Math.abs(smoothness) * speed);

		//message.echo("R: " + StraightLines.getRight().getSpeed() + " L:" + StraightLines.getLeft().getSpeed());
		StraightLines.getLeft().forward();
		if(smoothness >= 0){
			StraightLines.getRight().forward();				
		} else {
			StraightLines.getRight().backward();
		}
	}

	/**
	 * Robot starts driving a curve to the left. Speed and curvature can be adjusted. <br>
	 * Method returns immediately and robot keeps driving the curve.
	 * @param smoothness Value between 1.0 and -1.0 to adjust curvature <br>
	 * 			&emsp; -1.0: turn in place (around center of axis)<br>
	 * 			&emsp; 0.0: turn around right wheel as rotation center<br>
	 * 			&emsp; >0.0, <1.0: smooth curve with lateral movement<br>
	 * 			&emsp; 1.0: no turn at all
	 * @param speed Turn speed, should be ~100
	 */
	public static void smoothSpeededLeftTurn(float smoothness, float speed) {
		StraightLines.getRight().setSpeed(speed);
		StraightLines.getLeft().setSpeed(Math.abs(smoothness) * speed);

		//message.echo("R: " + StraightLines.getRight().getSpeed() + " L:" + StraightLines.getLeft().getSpeed());
		StraightLines.getRight().forward();
		if(smoothness >= 0){
			StraightLines.getLeft().forward();				
		} else {
			StraightLines.getLeft().backward();
		}
	}
}
