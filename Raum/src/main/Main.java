package main;

import run.Task;

public class Main {

	
	public static void main(String [] args)
	{
		
		RoomController robCon = new RoomController();	
		Task.runSimulation(robCon);
	}
	
	
	
	
}
