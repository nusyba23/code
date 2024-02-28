
/**
 * Name : Nusyba Shifa
 * UCID : 30162709
 * WebClient Class
 * 
 * CPSC 441
 * Assignment 2
 * 
 * @author 	Majid Ghaderi
 * @version	2024
 *
 */

 import java.io.*;
import java.net.Socket;
import java.net.URL;
import javax.net.ssl.SSLSocketFactory;
import java.util.logging.Logger;

public class WebClient {

    private static final Logger logger = Logger.getLogger("WebClient");

    public WebClient() {
        // Dont need Constructor 
    }

    public void getObject(String url) {
        try {
            URL parsedUrl = new URL(url);
            String protocol = parsedUrl.getProtocol();
            String host = parsedUrl.getHost();
            int port = parsedUrl.getPort() != -1 ? parsedUrl.getPort() : (protocol.equalsIgnoreCase("http") ? 80 : 443);
            String path = parsedUrl.getPath().isEmpty() ? "/" : parsedUrl.getPath();

            Socket socket;

            if (protocol.equalsIgnoreCase("https")) {
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                socket = factory.createSocket(host, port);
            } else {
                socket = new Socket(host, port);
            }

            // Send GET request
            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
            writer.println("GET " + path + " HTTP/1.1");
            writer.println("Host: " + host);
            writer.println("Connection: close");
            writer.println(); // emty line to end of the hesder sectonn
            writer.flush();

            // reading bytes directlsy
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            FileOutputStream fos = new FileOutputStream(getFileName(path));
            boolean headerEnded = false;
            int b;
            String headerText = "";


            // process harderr bytes
            while (!headerEnded && (b = bis.read()) != -1) {
                headerText += (char) b;
                if (headerText.contains("\r\n\r\n")) {
                    headerEnded = true;
                }
            }

            // once headers done wriite diractly to filles
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            fos.close();
            bis.close();
            writer.close();
            socket.close();
            logger.info("File downloaded: " + getFileName(path));
        } catch (IOException e) {
            logger.severe("Error: " + e.getMessage());
        }
    }

    private String getFileName(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        return fileName.isEmpty() ? "index.html" : fileName;
    }
}
