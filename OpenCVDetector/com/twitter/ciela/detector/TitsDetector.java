package com.twitter.ciela.detector;

import java.io.FileNotFoundException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

/**
 * implementation of {@link Detector} specialized for human tits
 * @author ciela
 *
 */
public class TitsDetector implements Detector {

	private CascadeClassifier detector;
	
	public TitsDetector() throws FileNotFoundException {
		this("resources/cascade_oppai.xml");
	}
	
	public TitsDetector(String cascadeFileName) throws FileNotFoundException {
		detector = new CascadeClassifier(cascadeFileName);
	}
	
	@Override
	public MatOfRect detect(Mat srcImg) {
		MatOfRect resultRects = new MatOfRect();
		detector.detectMultiScale(srcImg, resultRects);
		return resultRects;
	}

}
