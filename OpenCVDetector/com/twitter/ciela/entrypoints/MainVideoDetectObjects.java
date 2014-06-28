package com.twitter.ciela.entrypoints;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import com.twitter.ciela.detector.Detector;
import com.twitter.ciela.detector.FaceDetector;
import com.twitter.ciela.detector.TitsDetector;
import com.twitter.ciela.util.DrawUtil;

public class MainVideoDetectObjects extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private BufferedImage mainImage;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (mainImage != null) {
			g.drawImage(mainImage, 0, 0, mainImage.getWidth(), mainImage.getHeight(), this);
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		VideoCapture capture = new VideoCapture(0);
		try {
			int width = (int)capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH);
			int height = (int)capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT);
			if (width == 0 || height == 0) {
				System.exit(1);
			}
			log.info("Width: " + width + ", Height: " + height);
			
			JFrame frame = new JFrame("camera");
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			MainVideoDetectObjects mainPanel = new MainVideoDetectObjects();
			frame.setContentPane(mainPanel);
			frame.setVisible(true);
			frame.setSize(width + frame.getInsets().left + frame.getInsets().right, height + frame.getInsets().top + frame.getInsets().bottom); 
			
			Detector detector = new TitsDetector("resources/cascade_oppai.xml");
			detector = new FaceDetector();
			Mat capturedImage = new Mat();
			while (capture.isOpened() && frame.isShowing()) {
				capture.read(capturedImage);
//				Imgproc.resize(capturedImage, capturedImage, new Size(width / 2, height / 2));
				MatOfRect detectedRects = detector.detect(capturedImage);
//				log.info("Detected: " + detectedRects.toArray().length);
				capturedImage = DrawUtil.drawMosaic(capturedImage, detectedRects, 10);
//				Imgproc.resize(capturedImage, capturedImage, new Size(width, height));
				mainPanel.mainImage = matToBufferedImage(capturedImage);
				frame.repaint();
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			capture.release();
		}
	}

	private static BufferedImage matToBufferedImage(Mat mat) {
		int dataSize = mat.cols() * mat.rows() * (int)mat.elemSize();
		byte[] data = new byte[dataSize];
		mat.get(0, 0, data);
		
		int type = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
		
		if (type == BufferedImage.TYPE_3BYTE_BGR) {
			for (int i = 0; i < dataSize; i+=3) {
				byte b = data[i + 0];
				data[i + 0] = data[i + 2];
				data[i + 2] = b;
			}
		}
		
		BufferedImage result = new BufferedImage(mat.cols(), mat.rows(), type);
		result.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
		
		return result;
	}
}
