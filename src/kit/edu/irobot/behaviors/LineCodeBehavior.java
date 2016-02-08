package kit.edu.irobot.behaviors;

import kit.edu.irobot.robot.Robot;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.filter.MeanFilter;

public class LineCodeBehavior {

	private Robot robot = null;
	private EV3ColorSensor sensor = null;
	private float[] curVal;
	private MeanFilter meanF = null;
	private int amountMeans = 10;
	private int sampleSize = -1;
	private double delta = 0.5;
	private int stage = 0;
	private boolean wasBlack = true;
	private boolean wasWhite = false;
	private long lastTime = System.currentTimeMillis();
	private long maxTime = 500; // in ms //TODO good time?
	private boolean foundCode = false;

	public LineCodeBehavior(Robot robot) {
		this.robot = robot;
		sensor = robot.getSensorLight();
		sampleSize = sensor.sampleSize();
		curVal = new float[2 * sampleSize];
		meanF = new MeanFilter(sensor, amountMeans);
		lastTime = System.currentTimeMillis();
	}

	public int search() {
		lastTime = System.currentTimeMillis();
		while (!foundCode) {
			robot.moveRobotForward();
			fetchSamples();
			if (wasWhite && curVal[0] < curVal[sampleSize] - delta) {
				// falling edge
				wasBlack = true;
				wasWhite = false;
			} else if (wasBlack && curVal[sampleSize] + delta < curVal[0]) {
				// rising edge
				wasBlack = false;
				wasWhite = true;
				stage++;
				robot.writeErrorToDisplay("Increased Stage", "now: " + stage);
				robot.beep();
				foundCode = true;
			} else if (lastTime < System.currentTimeMillis() - maxTime) {
				robot.writeErrorToDisplay("No Line found...", "");
				foundCode = true;
			}
		}
		if(foundCode){
			return stage;
		}else{
			return -1;
		}
	}

	private void fetchSamples() {
		// curVal[0] 		  - current value
		// curVal[sampleSize] - filtered value
		sensor.fetchSample(curVal, 0);
		meanF.fetchSample(curVal, sampleSize);
		robot.writeErrorToDisplay("Current mean: " + curVal[sampleSize], "");
	}

}
