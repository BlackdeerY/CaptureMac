package blackdeer.capturemac.app;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

public class Application {
	private static class GlobalMouseMotionListener implements NativeMouseMotionListener {
		@Override
		public void nativeMouseMoved(NativeMouseEvent e) {
			if (thread == null) {
				thread = new Thread() {
					@Override
					public void run() {
						mousePosition[0] = e.getX();
						mousePosition[1] = e.getY();
						// 캡처한 이미지가 있고, 고정상태가 아니고, 숨기지 않았을 때에 캡처 이미지가 마우스를 따라다님.
						if (viewWindow.captureRegion.imageCaptured != null && moving == true && hiding == false) {
							viewWindow.setVisible(true);
							viewWindow.setLocation(mousePosition[0] + 10, mousePosition[1] + 20);
						} else if (captureStart == true) {
							viewWindow.captureRegion.setSize((int)(((float)(mousePosition[0] - viewWindow.captureRegion.mouseCapture[0])) / viewWindow.captureRegion.forScale),
									(int)(((float)(mousePosition[1] - viewWindow.captureRegion.mouseCapture[1])) / viewWindow.captureRegion.forScale));
						}
						try {
							sleep(10);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if (thread == this) {
							thread = null;
						}
					}
				};
				thread.start();
			}
		}
		
		@Override
		public void nativeMouseDragged(NativeMouseEvent e) {
		}
	}
	
	private static class GlobalKeyListner implements NativeKeyListener {
		@Override
		public void nativeKeyPressed(NativeKeyEvent e) {
			int mod = e.getModifiers();
			// 10: Windows LCtrl + LAlt 
			// 40966: Mac LCtrl + LCommand
			// 1310729: Mac VNC LCtrl + LCommand
			if(captureStart == false && mod == 40966) {
				if(viewWindow.captureRegion.imageCaptured == null && captureStart == false) {
					viewWindow.captureRegion.mouseCapture[0] = mousePosition[0];
					viewWindow.captureRegion.mouseCapture[1] = mousePosition[1];
					viewWindow.captureRegion.forScale = (float)(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth())
														/ (float)(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
					viewWindow.captureRegion.setLocation((int)((float)(viewWindow.captureRegion.mouseCapture[0]) / viewWindow.captureRegion.forScale),
														(int)((float)(viewWindow.captureRegion.mouseCapture[1]) / viewWindow.captureRegion.forScale));
					viewWindow.captureRegion.setVisible(true);
					captureStart = true;
				} else if (viewWindow.captureRegion.imageCaptured != null && captureStart == false && hiding == true) {
					viewWindow.captureRegion.imageCaptured = null;
					viewWindow.image_View.setSize(0, 0);
					viewWindow.setSize(0, 0);
					viewWindow.setLocation(-1, -1);
					viewWindow.setVisible(false);
					
					viewWindow.captureRegion.mouseCapture[0] = mousePosition[0];
					viewWindow.captureRegion.mouseCapture[1] = mousePosition[1];
					viewWindow.captureRegion.forScale = (float)(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth())
														/ (float)(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
					viewWindow.captureRegion.setLocation((int)((float)(viewWindow.captureRegion.mouseCapture[0]) / viewWindow.captureRegion.forScale),
														(int)((float)(viewWindow.captureRegion.mouseCapture[1]) / viewWindow.captureRegion.forScale));
					viewWindow.captureRegion.setVisible(true);
					captureStart = true;
				}
			}
		}
		
		@Override
		public void nativeKeyReleased(NativeKeyEvent e) {
			int mod = e.getModifiers();
			int key = e.getRawCode();
			//System.out.println("mod: " + mod + " / key: " + key);
			//if(captureStart == true && mod != 10) {
			if(captureStart == true && mod != 40966) {
				captureStart = false;
				viewWindow.captureRegion.mouseCapture[2] = mousePosition[0];
				viewWindow.captureRegion.mouseCapture[3] = mousePosition[1];
				viewWindow.captureRegion.setSize(0, 0);
				viewWindow.captureRegion.setLocation(-1, -1);
				viewWindow.captureRegion.setVisible(false);
				if (viewWindow.captureRegion.mouseCapture[2] > viewWindow.captureRegion.mouseCapture[0] && viewWindow.captureRegion.mouseCapture[3] > viewWindow.captureRegion.mouseCapture[1]) {
					try {
						viewWindow.captureRegion.rectangle.setBounds(
								viewWindow.captureRegion.mouseCapture[0],
								viewWindow.captureRegion.mouseCapture[1],
								viewWindow.captureRegion.mouseCapture[2] - viewWindow.captureRegion.mouseCapture[0],
								viewWindow.captureRegion.mouseCapture[3] - viewWindow.captureRegion.mouseCapture[1]);
						viewWindow.captureRegion.imageCaptured = viewWindow.captureRegion.robot.createScreenCapture(viewWindow.captureRegion.rectangle);
						viewWindow.image_View.setSize(
								viewWindow.captureRegion.mouseCapture[2] - viewWindow.captureRegion.mouseCapture[0],
								viewWindow.captureRegion.mouseCapture[3] - viewWindow.captureRegion.mouseCapture[1]);
						viewWindow.image_View.setIcon(new ImageIcon(new ImageIcon(viewWindow.captureRegion.imageCaptured).getImage()));
						viewWindow.setSize(
								viewWindow.captureRegion.mouseCapture[2] - viewWindow.captureRegion.mouseCapture[0],
								viewWindow.captureRegion.mouseCapture[3] - viewWindow.captureRegion.mouseCapture[1]);
						viewWindow.setLocation(mousePosition[0] + 10, mousePosition[1] + 20);
						viewWindow.setVisible(true);
						moving = true;
						hiding = false;
					} catch (Exception ex) {
					}
				}
			// esc: 캡처 이미지 삭제
			} else if (viewWindow.captureRegion.imageCaptured != null && mod == 40960 && key == 53) {
				viewWindow.captureRegion.imageCaptured = null;
				viewWindow.image_View.setSize(0, 0);
				viewWindow.setSize(0, 0);
				viewWindow.setLocation(-1, -1);
				viewWindow.setVisible(false);
			// LShift, RShift: 캡처 이미지 현재 위치에 고정/해제
			} else if (viewWindow.captureRegion.imageCaptured != null && mod == 40960 && (key == 56 || key == 60)) {
				moving = !moving;
			// `: 캡처 이미지 숨기기/보이기
			} else if (viewWindow.captureRegion.imageCaptured != null && mod == 40960 && key == 50) {
				hiding = !hiding;
				if (hiding == true) {
					viewWindow.setVisible(false);
				} else {
					viewWindow.setVisible(true);
					if (moving == true) {
						viewWindow.setLocation(mousePosition[0] + 10, mousePosition[1] + 20);
					}
				}
			// Ctrl + F12: 전체화면을 이미지로 저장
			} else if (mod == 40962 && key == 111) {
				Date date = new Date();
				String time = simpleDateFormat.format(date);
				BufferedImage image = viewWindow.captureRegion.robot.createScreenCapture(new Rectangle(0, 0, (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight())));
				File filePath = new File(dirDownloads + time + ".png");
				try {
					ImageIO.write(image, "png", filePath);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			// Ctrl + F11: 캡처 이미지만  저장
			} else if (viewWindow.captureRegion.imageCaptured != null && mod == 40962 && key == 103) {
				Date date = new Date();
				String time = simpleDateFormat.format(date);
				File filePath = new File(dirDownloads + time + ".png");
				try {
					ImageIO.write(viewWindow.captureRegion.imageCaptured, "png", filePath);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
		}
		
		@Override
		public void nativeKeyTyped(NativeKeyEvent e) {
		}
	}
	
	private static int[] mousePosition = new int[4];
	private static boolean captureStart = false;
	private static boolean moving = false;
	private static boolean hiding = false;
	private static ViewWindow viewWindow;
	private static String dirDownloads;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH;mm;ss");
	public static Thread thread = null;
	
	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		dirDownloads = System.getProperty("user.home") + "/Downloads/";
		System.out.println(dirDownloads);
		try { 
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			System.exit(1);
		}
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
		}
		viewWindow = new ViewWindow();
		GlobalScreen.addNativeMouseMotionListener(new GlobalMouseMotionListener());
		GlobalScreen.addNativeKeyListener(new GlobalKeyListner());
	}
}
