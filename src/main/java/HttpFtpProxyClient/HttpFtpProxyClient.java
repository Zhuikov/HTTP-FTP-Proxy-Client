package HttpFtpProxyClient;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class HttpFtpProxyClient {

    static public final String proxyAddress = "127.0.0.1";
    static public final int proxyPort = 7500;

    static public final String contentLength = "Content-Length: ";
    static private ProxyClientGUI proxyClientGUI = new ProxyClientGUI();

    static class ResponseStructure {
        private String headers = null;
        private ArrayList<Character> body = null;

        public ResponseStructure() {}

        public String getHeaders() {
            return headers;
        }

        public ArrayList<Character> getBody() {
            return body;
        }

        public void setHeaders(String headers) {
            this.headers = headers;
        }

        public void setBody(ArrayList<Character> body) {
            this.body = body;
        }
    }

    public static void main(String[] args) {
        new HttpFtpProxyClient();
    }

    public HttpFtpProxyClient() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame(proxyClientGUI);
            proxyClientGUI.addPanel(frame.getMainPanel(),LoginFrame.frameKey);
            proxyClientGUI.showPanel(LoginFrame.frameKey);

//            ServerFrame serverFrame = new ServerFrame(proxyClientGUI, "ftp.funet.ru", "13213");
//            proxyClientGUI.addPanel(serverFrame.getMainPanel(), ServerFrame.frameKey);
//            proxyClientGUI.showPanel(ServerFrame.frameKey);
        });
    }

    public static ResponseStructure makeRequest(ResponseStructure request) throws IOException {
        Socket socket = new Socket(proxyAddress, proxyPort);
        sendRequest(socket, request);
        return readResponse(socket);
    }

    private static void sendRequest(Socket socket, ResponseStructure request) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write(request.getHeaders().getBytes());
        if (request.getBody() != null)
            for (char c : request.getBody())
                os.write(c);
    }

    private static ResponseStructure readResponse(Socket socket) throws IOException {

        ResponseStructure responseStructure = new ResponseStructure();
        InputStream is = socket.getInputStream();

        String line;
        ArrayList<String> headers = new ArrayList<>();
        while (true) {
            line = readString(is);
            if (line.isEmpty()) break;
            headers.add(line);
        }

        String[] firstLine = headers.get(0).split(" ");
        responseStructure.setHeaders(firstLine[1]);

        int bodyLength = 0;
        for (String s : headers) {
            if (s.length() > contentLength.length() &&
                    s.substring(0, contentLength.length()).equals(contentLength)) {
                bodyLength = Integer.parseInt(s.substring(contentLength.length()));
                break;
            }
        }

        int readBytes = 0;
        ArrayList<Character> bodyData = new ArrayList<>();
        while (readBytes < bodyLength) {
            bodyData.add((char) is.read());
            readBytes++;
        }

        responseStructure.setBody(bodyData);

        socket.close();
        return responseStructure;
    }

    static private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        char value;

        while (true) {
            value = (char)is.read();
            if (value == '\n') break;
            sb.append(value);
        }

        return sb.toString();
    }
}
