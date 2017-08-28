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
            if (file.exists()) {            //confirms if file exists
            FileInputStream fis = new FileInputStream(file);
            int fileLength = (int) file.length();
            byte buff[] = new byte[fileLength];
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Content-Length: " + fileLength);
            out.println();

            fis.read(buff);
            out.write(buff, 0, fileLength);
            out.flush();
                System.out.println("Returned: " + ServerThread.requestPath);
            } else {
                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-Type: text/html");
                out.println();
                try (InputStream fis
                             = new BufferedInputStream(new FileInputStream(rootPath + "/404.html"))) {
                    int ch;
                    while ((ch = fis.read()) != -1) {
                        out.write(ch);
                    }
                    System.out.println("Returned: 404 Not Found");
                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}

class ServerThread implements Runnable {
    static String requestPath;
    @Override
    public void run() {
        while (true) {
            try{
            BufferedReader in = new BufferedReader(new InputStreamReader(WebServer.s.getInputStream(), "UTF-8"));
            String line = in.readLine();
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
