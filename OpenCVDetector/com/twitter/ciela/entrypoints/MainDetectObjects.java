package com.twitter.ciela.entrypoints;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.highgui.Highgui;

import com.twitter.ciela.detector.Detector;
import com.twitter.ciela.detector.TitsDetector;
import com.twitter.ciela.util.DrawUtil;

public class MainDetectObjects {

	private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String eroSrcDirPath = "C:\\Users\\ota_kazuhiro\\Pictures\\ero_gif\\05_frames";
		String detectedDirPath = eroSrcDirPath + File.separator + "tits_detected";
		File detectedDir = new File(detectedDirPath);
		if (!detectedDir.exists()) {
			detectedDir.mkdir();
		}
		
		Detector titsDetector = new TitsDetector("resources/cascade_oppai.xml");
		for (File eroImg : getJpegOrPngFiles(eroSrcDirPath)) {
			Mat srcImg = Highgui.imread(eroImg.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_ANYCOLOR);
			MatOfRect detectedRects = titsDetector.detect(srcImg);
			srcImg = DrawUtil.drawMosaic(srcImg, detectedRects, 15);
//			srcImg = DrawUtil.drawBlur(srcImg, detectedRects);
//			DrawUtil.drawImage(srcImg, Highgui.imread("resources/miserarenaiyo.png"), detectedRects);
			String detectedImgPath = detectedDirPath + File.separator + eroImg.getName();
			Highgui.imwrite(detectedImgPath, srcImg);
			log.info("Saved detected image " + detectedImgPath + ".");
		}
	}
	
	private static File[] getJpegOrPngFiles(String srcDirectory) {
		File[] jpegFiles = new File(srcDirectory).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
			}
		});
		return jpegFiles;
	}

}
