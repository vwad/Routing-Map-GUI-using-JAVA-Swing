package Utils.Others;
import java.io.OutputStream;
import java.io.PrintStream;

public class RoutingOutputStream extends OutputStream {
    private static PrintStream currentStream = System.out;

    public void setCurrentStream(PrintStream currentStream) {
        RoutingOutputStream.currentStream = currentStream;
    }

    public static PrintStream getCurrentStream() {
        return currentStream;
    }

    @Override
    public void write(int b) {
        currentStream.write(b);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        currentStream.write(buf, off, len);
    }
}
