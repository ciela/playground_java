package com.twitter.ciela.detector;

import java.io.FileNotFoundException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

/**
 * implementation of {@link Detector} specialized for human face
 * @author ciela
 *
 */
public class FaceDetector implements Detector {

	private CascadeClassifier detector;
	
	public FaceDetector() {
		detector = new CascadeClassifier("resources/lbpcascade_frontalface.xml");
	}
	
	public FaceDetector(String cascadeFileName) throws FileNotFoundException {
		detector = new CascadeClassifier(cascadeFileName);
	}
	
	@Override
	public MatOfRect detect(Mat srcImg) {
		MatOfRect resultRects = new MatOfRect();
		detector.detectMultiScale(srcImg, resultRects);
		return resultRects;
	}
	
}
