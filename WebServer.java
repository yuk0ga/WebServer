import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by koga on 2017/07/28.
 */
public class WebServer {

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Web Server Started at port: " + ss.getLocalPort());
            Socket s = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
