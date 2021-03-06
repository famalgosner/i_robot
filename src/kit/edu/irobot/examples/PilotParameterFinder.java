package kit.edu.irobot.examples;

import kit.edu.irobot.utils.UnregulatedPilot;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class PilotParameterFinder {

	public static void main(String[] args) {
		PilotParameterFinder finder = new PilotParameterFinder();
		
		while (true) {
			finder.updateDisplay();
			
			int button = Button.waitForAnyPress();
			switch (button) {
			case Button.ID_UP: 
				finder.previuos();
				break;
			case Button.ID_DOWN: 
				finder.next();
				break;
			case Button.ID_RIGHT: 
				finder.increase();
				break;
			case Button.ID_LEFT: 
				finder.decrease();
				break;
			case Button.ID_ENTER: 
				finder.action();
				break;
			case Button.ID_ESCAPE:
				return;
			
			}
		}
	}
	
	double wheelDiameter = 4.275;
	double tracWidth = 14.315;
	double rotateSpeed = 50;
	double moveSpeed = 50;
	
	Mode mode = Mode.TURN_90;
	State state = State.IDLE;
	int selectedRow = 0;
	int numRows = 5;
	
	String[] selector = new String[numRows];
	
	UnregulatedPilot pilot;
	
	public enum Mode {
		TURN_90(   "T  90°"),
		TURN_C90(   "T -90°"),
		TURN_180(  "T 180°"),
		TURN_360(  "T 360°"),
		DRIVE_20(  "D  20cm"),
		BACK_20(  "B  20cm"),
		DRIVE_100( "D   1m"),
		//CIRCLE_50( "C  50cm"),
		//CIRCLE_100("C   1m"),
		//EIGHT("Eight"),
		;
		String title;
		Mode(String description) {
			title = description;
		}
	}
	
	public enum State {
		IDLE,
		ACTIVE,
	}
	
	public PilotParameterFinder() {
		pilot = new UnregulatedPilot((int)tracWidth*10, (int)wheelDiameter*10);
		//pilot = new DifferentialPilot(wheelDiameter, tracWidth, robot.getMotorLeft(), robot.getMotorRight());
		for (int i = 0; i < numRows; ++i) {
			selector[i] = (i == selectedRow) ? "x " : "  ";
		}
	}
	
	public void updateDisplay() {
		LCD.clear();
		switch (state) {
		case IDLE:
			LCD.drawString("Enter for Actn", 0, 0);
			break;
		case ACTIVE:
			LCD.drawString("Active...", 0, 0);
			break;
		default:
			break;
		
		}
		LCD.drawString(selector[0] + "Actn: " + mode.title, 0, 1);
		LCD.drawString(selector[1] + "diam: " + wheelDiameter, 0, 2);
		LCD.drawString(selector[2] + "wdth: " + tracWidth, 0, 3);
		LCD.drawString(selector[3] + "Rpwr: " + rotateSpeed, 0, 4);
		LCD.drawString(selector[4] + "Mpwr: " + moveSpeed, 0, 5);
	}
	
	public void action() {
		if (state != State.IDLE) return;
		state = State.ACTIVE;
		updateDisplay();
		
		switch (mode) {
		case DRIVE_20:
			pilot.travel(200);
			break;
		case BACK_20:
			pilot.travel(-200);
			break;
		case DRIVE_100:
			pilot.travel(1000);
			break;
		/*case CIRCLE_100:
			pilot.arc(50, 360);
			break;
		case CIRCLE_50:
			pilot.arc(25, 360);
			break;
		case EIGHT:
			pilot.arc(50, 360);
			pilot.arc(-50, 360);
			break;*/
		case TURN_180:
			pilot.rotate(180);
			break;
		case TURN_360:
			pilot.rotate(360);
			break;
		case TURN_90:
			pilot.rotate(90);
			break;
		case TURN_C90:
			pilot.rotate(-90);
			break;
		
		}
		state = State.IDLE;
		updateDisplay();
	}
	
	public void updatePilot() {
		/*pilot = new DifferentialPilot(wheelDiameter, tracWidth, robot.getMotorLeft(), robot.getMotorRight());
		pilot.setRotateSpeed(rotateSpeed);
		pilot.setTravelSpeed(moveSpeed);*/
		
		if (pilot != null) {
			pilot.stop();
			pilot.close();
			pilot = null;
		}
		
		pilot = new UnregulatedPilot((int)tracWidth*10, (int)wheelDiameter*10);
		pilot.setBasePower((int)moveSpeed);
		pilot.setBaseRotatePower((int) rotateSpeed);
	}
	
	public void next() {
		selectedRow++;
		if (selectedRow >= numRows) selectedRow = 0;
		
		for (int i = 0; i < numRows; ++i) {
			selector[i] = (i == selectedRow) ? "x " : "  ";
		}
		
		updateDisplay();
	}
	
	public void previuos() {
		selectedRow--;
		if (selectedRow < 0) selectedRow = numRows-1;

		for (int i = 0; i < numRows; ++i) {
			selector[i] = (i == selectedRow) ? "x " : "  ";
		}
		
		updateDisplay();
	}
	
	public void increase() {
		switch (selectedRow) {
		case 0: 
			int modeIdx = (mode.ordinal() + 1) % Mode.values().length;
			mode = Mode.values()[modeIdx];
			break;
		case 1:
			wheelDiameter += 0.05;
			break;
		case 2:
			tracWidth += 0.05;
			break;
		case 3:
			rotateSpeed += 5;
			break;
		case 4:
			moveSpeed += 5;
			break;
		}
		updatePilot();
		updateDisplay();
	}
	
	public void decrease() {
		switch (selectedRow) {
		case 0: 
			int modeIdx = (mode.ordinal() - 1 + Mode.values().length) % Mode.values().length;
			mode = Mode.values()[modeIdx];
			break;
		case 1:
			wheelDiameter -= 0.05;
			break;
		case 2:
			tracWidth -= 0.05;
			break;
		case 3:
			rotateSpeed -= 5;
			break;
		case 4:
			moveSpeed -= 5;
			break;
		}
		updatePilot();
		updateDisplay();
	}

}
