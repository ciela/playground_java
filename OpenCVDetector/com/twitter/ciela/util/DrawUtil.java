package com.twitter.ciela.util;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class DrawUtil {

	/**
	 * draw specified rectangles on image
	 * @param image {@link Mat} of source image
	 * @param rectangles {@link MatOfRect} of rectangles to be drawn
	 */
	public static void drawRectangles(Mat image, MatOfRect rectangles) {
		for (Rect rect : rectangles.toArray()) {
			Core.rectangle(
					image, 
					new Point(rect.x, rect.y), 
					new Point(rect.x + rect.width, rect.y + rect.height), 
					new Scalar(255, 0, 0), // color
					2);	// thickness
		}
	}
	
	/**
	 * draw specified circles 
	 * @param image {@link Mat} of source image
	 * @param circles {@link Mat} of circles to be drawn
	 */
	public static void drawCircles(Mat image, Mat circles) {		
		double[] data;
		double rho;
		Point pt = new Point();
		for (int i = 0; i < circles.cols(); i++) {
			data = circles.get(0, i);
			pt.x = data[0];
			pt.y = data[1];
			rho = data[2];
			Core.circle(image, pt, (int) rho, new Scalar(255, 0, 0), 2);
		}
	}
	
	/**
	 * 
	 * @param image
	 * @param imageToBeDrawn
	 * @param rectangles
	 */
	public static void drawImage(Mat image, Mat imageToBeDrawn, MatOfRect rectangles) {
		for (Rect rect : rectangles.toArray()) {
			Mat imageROI = new Mat(image, rect);
			Imgproc.resize(imageToBeDrawn, imageToBeDrawn, new Size(imageROI.width(), imageROI.height()));
			Core.add(imageROI, imageToBeDrawn, imageROI);
		}
	}
	
	/**
	 * overlay blur filter specified rectangles
	 * @param image {@link Mat} of source image
	 * @param rectangles {@link MatOfRect} of rectangles to be drawn
	 * @return {@link Mat} of blurred image
	 */
	public static Mat drawBlur(Mat image, MatOfRect rectangles) {
		Mat dstImage = image.clone();
		for (Rect rect : rectangles.toArray()) {
			Mat imageROI = new Mat(image, rect);
			Mat dstImageROI = new Mat(dstImage, rect);
			Imgproc.blur(imageROI, dstImageROI, new Size(15, 15));
		}
		return dstImage;
	}
	
	/**
	 * overlay mosaic filter specified rectangles
	 * @param {@link Mat} of source image
	 * @param rectangles {@link MatOfRect} of rectangles to be drawn
	 * @return {@link Mat} of mosaic image
	 */
	public static Mat drawMosaic(Mat image, MatOfRect rectangles, final int size) {
		Mat dstImage = image.clone();
		for (Rect rect : rectangles.toArray()) {
			Mat imageROI = new Mat(image, rect);
			Mat dstImageROI = new Mat(dstImage, rect);
			for (int y = 0; y < imageROI.height(); y += size) {
				for (int x = 0; x < imageROI.width(); x += size) {
					int yLimit = y + size;
					if (yLimit >= imageROI.height()) {
						yLimit = imageROI.height();
					}
					int xLimit = x + size;
					if (xLimit >= imageROI.width()) {
						xLimit = imageROI.width();
					}
					double b, g, r;
					b = g = r = 0;
					int winSize = 0;
					for (int i = y; i < yLimit; i++) {
						for (int j = x; j < xLimit; j++) {
							double[] pixel = imageROI.get(j, i);
							b += pixel[0];
							g += pixel[1];
							r += pixel[2];
							winSize++;
						}
					}
					b /= winSize;
					g /= winSize;
					r /= winSize;
					for (int i = y; i < yLimit; i++) {
						for (int j = x; j < xLimit; j++) {
							dstImageROI.put(j, i, new double[] { b, g, r });
						}
					}
				}
			}
		}
		return dstImage;
	}
	
	public static Mat drawMosaicOn8Bit(Mat image, MatOfRect rectangles, final int size) {
		Mat dstImage = image.clone();
		for (Rect rect : rectangles.toArray()) {
			Mat imageROI = new Mat(image, rect);
			Mat dstImageROI = new Mat(dstImage, rect);
			for (int y = 0; y < imageROI.height(); y += size) {
				for (int x = 0; x < imageROI.width(); x += size) {
					int yLimit = y + size;
					if (yLimit >= imageROI.height()) {
						yLimit = imageROI.height();
					}
					int xLimit = x + size;
					if (xLimit >= imageROI.width()) {
						xLimit = imageROI.width();
					}
					double p = 0;
					int winSize = 0;
					for (int i = y; i < yLimit; i++) {
						for (int j = x; j < xLimit; j++) {
							double[] pixel = imageROI.get(j, i);
							p += pixel[0];
							winSize++;
						}
					}
					p /= winSize;
					for (int i = y; i < yLimit; i++) {
						for (int j = x; j < xLimit; j++) {
							dstImageROI.put(j, i, new double[] { p });
						}
					}
				}
			}
		}
		return dstImage;
	}
}
