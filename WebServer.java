import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by koga on 2017/07/28.
 */
public class WebServer {

    static String rootPath = "/Users/koga/Desktop/Files";
    static Socket s;
    static ServerSocket ss;

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try {
            ss = new ServerSocket(port);
            System.out.println("<< Web Server Started at port: " + ss.getLocalPort() + " >>");
            System.out.println("<< Access http://localhost:" + ss.getLocalPort() + " to try it out! >>");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {              //to separate each client's thread (server will always listen)
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

        //404error
        File errorfile = new File(rootPath + "/404.html");
        FileInputStream errorfis = new FileInputStream(errorfile);
        int errorFileLength = (int) errorfile.length();
        byte errorbuff[] = new byte[errorFileLength];

        try {
            if (file.exists()) {            //confirms if file exists
            FileInputStream fis = new FileInputStream(file);
            int fileLength = (int) file.length();
            byte buff[] = new byte[fileLength];

            //http header
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Content-Length: " + fileLength);
            out.println();

            //response
            fis.read(buff);
            out.write(buff, 0, fileLength);
            out.flush();
            System.out.println("Returned: " + ServerThread.requestPath);

            } else {
                //when file was not found
                //header
                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-Type: text/html");
                out.println("Content-Length: " + errorFileLength);
                out.println();

                //response
                errorfis.read(errorbuff);
                out.write(errorbuff, 0, errorFileLength);
                out.flush();
                System.out.println("Returned: 404 Not Found");
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}

class ServerThread implements Runnable {        //Thread
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
