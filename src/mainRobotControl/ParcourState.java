package mainRobotControl;

public enum ParcourState {
	IDLE,
	LOST,
	ERROR,
	SCAN_BARCODE,
	LINE_FOLLOWER, GAP_IN_LINE,
	ASCEND_BRIDGE, ON_BRIDGE, DESCENT_BRIDGE,
	SEARCH_COLORED_AREA,
}