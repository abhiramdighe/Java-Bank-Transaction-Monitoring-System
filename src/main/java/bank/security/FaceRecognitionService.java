package bank.security;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_videoio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class FaceRecognitionService {
    private static final Logger logger = LoggerFactory.getLogger(FaceRecognitionService.class);

    // Captures a single frame from the default webcam
    public static byte[] captureFaceData() {
        try (OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0)) { // 0 = default webcam
            grabber.setImageWidth(640);
            grabber.setImageHeight(480);
            grabber.start();

            // warm up webcam
            for (int i = 0; i < 5; i++) {
                grabber.grab();
            }

            Frame frame = grabber.grab();
            if (frame != null) {
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage image = converter.getBufferedImage(frame);
                    if (image != null) {
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                            ImageIO.write(image, "jpg", baos);
                            grabber.stop();
                            return baos.toByteArray();
                        }
                    }
                }
            }
            grabber.stop();
        } catch (Exception e) {
            logger.error("Webcam capture failed. Ensure you have a webcam attached.", e);
        }
        return new byte[0]; // Empty array if failed
    }

    // TODO: Integrate OpenCV LBPHFaceRecognizer or similar for real face recognition
    // For now, fallback to basic check: both images must be non-empty
    public static boolean matchFace(byte[] storedData, byte[] liveData) {
        if (storedData == null || liveData == null) return false;
        if (storedData.length < 100 || liveData.length < 100) return false;
        // TODO: Replace with real face recognition algorithm
        return true;
    }
}
