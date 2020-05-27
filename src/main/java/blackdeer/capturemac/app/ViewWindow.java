package blackdeer.capturemac.app;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ViewWindow extends JDialog {
	private Container container;
	public CaptureRegion captureRegion;
	JLabel image_View;
	
	public ViewWindow() {
		captureRegion = new CaptureRegion();
		
		container = this.getContentPane();
		container.setLayout(null);
		
		image_View = new JLabel();
		image_View.setBounds(0, 0, 0, 0);
		container.add(image_View);
		
		this.getRootPane().putClientProperty("Window.alpha", 0.25f);
		this.setSize(0, 0);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		this.setVisible(false);
	}
}
