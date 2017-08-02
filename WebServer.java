import javax.print.attribute.standard.RequestingUserName;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by koga on 2017/07/28.
 */
public class WebServer {

    private static String rootPath = "/Users/koga/Desktop";

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Web Server Started at port: " + ss.getLocalPort());
            Socket s = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            String line = in.readLine();
            String requestPath;
            requestPath = rootPath + line.split(" ")[1];
            if (requestPath.endsWith("/")){
                requestPath += "index.html";
            }
//            while ((line = in.readLine()) != null && !line.isEmpty()){
//                System.out.println(line);
//            }
            System.out.println(requestPath);
            getFile(s,requestPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getFile(Socket s, String filePath) throws IOException{
        PrintStream out = new PrintStream(s.getOutputStream(), true);
        File file = new File(filePath);

        try{
            FileInputStream fis = new FileInputStream(file);
//            FileReader r = new FileReader(file);
//            BufferedReader br = new BufferedReader(r);
            int fileLength = (int) file.length();
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
//            }
        }catch (FileNotFoundException e){
            out.println("404 Not Found");
        }
    }
}
