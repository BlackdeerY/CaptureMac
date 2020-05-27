package blackdeer.capturemac.app;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;

@SuppressWarnings("serial")
public class CaptureRegion extends JDialog {
	public int[] mouseCapture = new int[4];
	
	private Container container;
	public Robot robot;
	public Rectangle rectangle;
	public BufferedImage imageCaptured;
	public float forScale;
	
	CaptureRegion() {
		try {
			robot = new Robot();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		imageCaptured = null;
		rectangle = new Rectangle();
		
		container = this.getContentPane();
		container.setLayout(null);
		this.getRootPane().putClientProperty("Window.alpha", 0.25f);
		this.setSize(0, 0);
		this.setLocation(-1, -1);
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		this.setVisible(false);
	}
}