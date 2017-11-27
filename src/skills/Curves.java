package src.skills;

import lejos.utility.DebugMessages;

public class Curves {
	

	private static DebugMessages message = new DebugMessages(4);
	
	
	public static void smoothSpeededRightTurn(float smoothness, float speed) {
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

	public static void smoothSpeededLeftTurn(float smoothness, float speed) {
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
}
