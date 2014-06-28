package com.twitter.ciela.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ConvertUtil {

	public static BufferedImage matToBufferedImage(Mat mat) {
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
	
	public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
		byte[] pixels = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
		int type = bufferedImage.getType() == BufferedImage.TYPE_3BYTE_BGR ? CvType.CV_8UC3 : CvType.CV_8UC1;
		Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), type);
		mat.put(0, 0, pixels);
		return mat;
	}
	
}
