import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
public class RemoveBOM {
    public static void main(String[] args) throws Exception {
        Path p = Paths.get("src/main/java/bank/ui/UserDashboard.java");
        byte[] bytes = Files.readAllBytes(p);
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            byte[] newBytes = new byte[bytes.length - 3];
            System.arraycopy(bytes, 3, newBytes, 0, newBytes.length);
            Files.write(p, newBytes);
            System.out.println("BOM removed");
        }
    }
}
