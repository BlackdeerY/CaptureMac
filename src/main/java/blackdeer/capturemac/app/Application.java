package blackdeer.capturemac.app;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
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
			if (timing.compareAndSet(false, true)) {
				mousePosition[0] = e.getX();
				mousePosition[1] = e.getY();
				if (viewWindow.captureRegion.imageCaptured != null && moving == true && hiding == false) {
					viewWindow.setVisible(true);
					viewWindow.setLocation(e.getX() + 10, e.getY() + 20);
				} else if (captureStart == true) {
					viewWindow.captureRegion.setSize((int)(((float)(e.getX() - viewWindow.captureRegion.mouseCapture[0])) / viewWindow.captureRegion.forScale),
							(int)(((float)(e.getY() - viewWindow.captureRegion.mouseCapture[1])) / viewWindow.captureRegion.forScale));
				}
				while (true) {
					if (timing.compareAndSet(true, false)) {
						break;
					}
				}
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
	public static AtomicBoolean timing = new AtomicBoolean(false);
	
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
		
		System.out.println("============================================================\nCaptureMac - https://github.com/BlackdeerY/CaptureMac\n------------------------------------------------------------\nLCtrl + LCommand: Decide Capture Region and Capture Image.\nLShift or RShift: Fix/Move Captured Image.\n`: Hide/Show Captured Image.\nesc: Dispose Captured Image.\nLCtrl + F11: Save Captured Image to ~/Downloads.\nLCtrl + F12: Save Image of Fullscreen to ~/Downloads.\n============================================================");
	}
}
