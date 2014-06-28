package com.twitter.ciela.detector;

import java.util.HashSet;
import java.util.Set;


public class DetectorContainer {

	private Set<Detector> detectors;
	
	public DetectorContainer() {
		detectors = new HashSet<>();
	}
}
