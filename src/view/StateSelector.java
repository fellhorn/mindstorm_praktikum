package view;
import lejos.utility.TextMenu;
import java.util.Arrays;

import mainRobotControl.ParcourState;;

public class StateSelector {
	private static final int TOP_ROW = 1;
	private static final String TITLE = "States selection";
	
	private TextMenu textMenu;
	
	public StateSelector() {
		String[] statesArray = Arrays.toString(ParcourState.values()).replaceAll("^.|.$", "").split(", ");
		textMenu = new TextMenu(statesArray, TOP_ROW, TITLE);
	}
	
	public ParcourState getSelectedState() {
		int selectedIndex = textMenu.select();
		return ParcourState.values()[selectedIndex];
	}
}
