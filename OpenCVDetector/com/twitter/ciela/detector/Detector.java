package com.twitter.ciela.detector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

/**
 * detector interface
 * @author ciela
 *
 */
public interface Detector {

	/**
	 * detect objects from source image
	 * @param srcImg original source image
	 * @return {@link MatOfRect} of detected areas 
	 */
	MatOfRect detect(Mat srcImg);
	
}
