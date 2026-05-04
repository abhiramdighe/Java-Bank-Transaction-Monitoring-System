
import org.bytedeco.javacv.OpenCVFrameGrabber;
public class test {
    public static void main(String[] args) throws Exception {
        System.out.println("Trying 0 (Default)");
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); 
        grabber.start();
        System.out.println("Frame grabbed? " + (grabber.grab() != null));
        grabber.stop();
    }
}

