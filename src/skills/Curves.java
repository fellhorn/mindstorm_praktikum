package skills;

import lejos.utility.DebugMessages;

public class Curves {
	
	private static final int TURN_SPEED = 100;
	private static DebugMessages message = new DebugMessages(1);
	
	public static void turnRight90() {
		float [] sample = {0f,0f};
		Sensors.getGyro().getAngleMode().fetchSample(sample, 0);
		Sensors.getGyro().getAngleMode().fetchSample(sample, 1);
		smoothSpeededRightTurn(-1, TURN_SPEED);
		
		while (sample[0] - sample[1] < 75) {
			Sensors.getGyro().getAngleMode().fetchSample(sample, 1);
		}
		StraightLines.stop();
		message.echo(sample[0] + ", " + sample[1]);

	}
	
	public static void turnLeft90() {
		float [] sample = {0f,0f};
		Sensors.getGyro().getAngleMode().fetchSample(sample, 0);
		Sensors.getGyro().getAngleMode().fetchSample(sample, 1);
		smoothSpeededLeftTurn(-1, TURN_SPEED);
		
		while (sample[1] - sample[0] < 75) {
			Sensors.getGyro().getAngleMode().fetchSample(sample, 1);
		}
		StraightLines.stop();
		//message.echo(sample[0] + ", " + sample[1]);
	}
	
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
