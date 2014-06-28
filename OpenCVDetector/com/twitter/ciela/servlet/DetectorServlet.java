package com.twitter.ciela.servlet;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

import com.twitter.ciela.detector.Detector;
import com.twitter.ciela.detector.FaceDetector;
import com.twitter.ciela.detector.TitsDetector;
import com.twitter.ciela.util.ConvertUtil;
import com.twitter.ciela.util.DrawUtil;

/**
 * abstract servlet which detects objects from posted image data
 * @author ciela
 *
 */
@WebServlet(name="DetectorServlet", urlPatterns={"/detectImg"})
@MultipartConfig(location="/", maxFileSize=20848820, maxRequestSize=418018841, fileSizeThreshold=1048576)
public class DetectorServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private static final long serialVersionUID = 1L;

	private Map<String, Detector> detectorsMap;
	
	public DetectorServlet() throws FileNotFoundException {
		super();
		detectorsMap = new HashMap<String, Detector>();
		log.info("Loaded face detector.");
		detectorsMap.put("face", new FaceDetector());
		log.info("Loaded tits detector.");
		detectorsMap.put("tits", new TitsDetector());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.write("GET method is not supported!!!".getBytes());
		outputStream.flush();
		outputStream.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String typeToDetect = req.getParameter("type_to_detect");
		Part imgToDetectPart = req.getPart("img_to_detect");
		
		// if gif image, specified to animation gif
		boolean isGif = getFilename(imgToDetectPart).endsWith(".gif");
		if (isGif) {
			ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
			reader.setInput(ImageIO.createImageInputStream(imgToDetectPart.getInputStream()));
			List<BufferedImage> imagesToDetect = new ArrayList<>();
			int pageNum = reader.getNumImages(true);
			log.info("this gif has " + pageNum + " pages.");
			for (int i = 0; i < pageNum; i++) {
				imagesToDetect.add(detect(reader.read(i), typeToDetect));
			}
			resp.setContentType("image/gif");
			ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
			ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(resp.getOutputStream());
			writer.setOutput(imageOutputStream);
			writer.prepareWriteSequence(null);
			for (BufferedImage imageToDetect : imagesToDetect) {
				writer.writeToSequence(new IIOImage(imageToDetect, null, null), null);
			}
			writer.endWriteSequence();
			imageOutputStream.flush();
			imageOutputStream.close();
		} else {
			BufferedImage imageToDetect = ImageIO.read(imgToDetectPart.getInputStream());
			
			log.info("image received...");
			BufferedImage detectedImage = detect(imageToDetect, typeToDetect);
			
			resp.setContentType("image/png");
			ImageIO.write(detectedImage, "png", resp.getOutputStream());
		}
	}
	
	private BufferedImage detect(BufferedImage toDetect, String typeToDetect) {		
		Detector detector = detectorsMap.get(typeToDetect);

		Mat mat = ConvertUtil.bufferedImageToMat(toDetect);
		log.info("converted bufferedimage type " + toDetect.getType() + " to mat channel " + mat.channels() + ".");
		
		log.info("detecting...");
		MatOfRect detectedRects = detector.detect(mat);
		
		log.info("drawing...");
		mat = mat.channels() == 1 ? DrawUtil.drawMosaicOn8Bit(mat, detectedRects, 10) : DrawUtil.drawMosaic(mat, detectedRects, 10);
		
		BufferedImage matToBufferedImage = ConvertUtil.matToBufferedImage(mat);
		log.info("converted mat channel " + mat.channels() + " to bufferedimage type " + matToBufferedImage.getType() + ".");
		return matToBufferedImage;
	}
	
	 private String getFilename(Part part) {
		for (String cd : part.getHeader("Content-Disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
    }
}
	