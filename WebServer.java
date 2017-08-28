import javax.print.attribute.standard.RequestingUserName;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by koga on 2017/07/28.
 */
public class WebServer {

    static String rootPath = "/Users/koga/Desktop";
    static Socket s;
    static ServerSocket ss;

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try {
            ss = new ServerSocket(port);
            System.out.println("Web Server Started at port: " + ss.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {              //to separate each client's thread
            try {
                s = ss.accept();
            } catch (IOException e){
                e.printStackTrace();
            }
            ServerThread serverThread = new ServerThread();
            Thread server = new Thread(serverThread);
            server.start();
        }
    }

    static void getFile(Socket s, String filePath) throws IOException{
        PrintStream out = new PrintStream(s.getOutputStream(), true);
        File file = new File(filePath);

        try {
            FileInputStream fis = new FileInputStream(file);
//            FileReader r = new FileReader(file);
//            BufferedReader br = new BufferedReader(r);
            int fileLength = (int) file.length();
            if (fileLength > 0) {
            byte buff[] = new byte[fileLength];
//            String line;
//            while ((line = br.readLine()) != null){
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Content-Length: " + fileLength);
            out.println();

            fis.read(buff);
            out.write(buff, 0, fileLength);
            out.flush();
            } else {
                out.println("HTTP/1.1 404 Not Found");
            }
//            }
        }catch (FileNotFoundException e){
            out.println("HTTP/1.1 404 Not Found");
        }
    }
}

class ServerThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            try{
            BufferedReader in = new BufferedReader(new InputStreamReader(WebServer.s.getInputStream(), "UTF-8"));
            String line = in.readLine();
            String requestPath;
            if (line != null) {
                requestPath = WebServer.rootPath + line.split(" ")[1];
                if (requestPath.endsWith("/")) {
                    requestPath += "index.html";
                }
                System.out.println("Request " + requestPath);
                WebServer.getFile(WebServer.s, requestPath);
            }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
