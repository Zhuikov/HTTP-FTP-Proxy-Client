package HttpFtpProxyClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class HttpFtpProxyClient {

    static public final String proxyAddress = "127.0.0.1";
    static public final int proxyPort = 7500;

    static private final String contentLength = "Content-Length: ";
    static private ProxyClientGUI proxyClientGUI = new ProxyClientGUI();

    static class DataAndCode {
        private String code = null;
        private ArrayList<Character> data = null;

        public DataAndCode() {}

        public String getCode() {
            return code;
        }

        public ArrayList<Character> getData() {
            return data;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setData(ArrayList<Character> data) {
            this.data = data;
        }
    }

    public static void main1(String[] args) {
        String st = "IURII:GAGARIN";
        byte[] decBytes = Base64.getEncoder().encode(st.getBytes());
        String dec = new String(decBytes, StandardCharsets.UTF_8);
        System.out.println(dec);
        byte[] encBytes = Base64.getDecoder().decode(dec.getBytes());
        String enc = new String(encBytes, StandardCharsets.UTF_8);
        System.out.println(enc);

    }

    public static void main(String[] args) {
        new HttpFtpProxyClient();
    }

    public HttpFtpProxyClient() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame(proxyClientGUI);
            proxyClientGUI.addPanel(frame.getMainPanel(),LoginFrame.frameKey);
            proxyClientGUI.showPanel(LoginFrame.frameKey);

//            ServerFrame serverFrame = new ServerFrame(proxyClientGUI);
//            proxyClientGUI.addPanel(serverFrame.getMainPanel(), ServerFrame.frameKey);
//            proxyClientGUI.showPanel(ServerFrame.frameKey);
        });
    }

    public static void send(Socket socket, String request) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write(request.getBytes());
    }

    //todo убрать DataAndCode, возвращать ArrayList
    public static DataAndCode readResponse(Socket socket) throws IOException {

        DataAndCode dataAndCode = new DataAndCode();
        InputStream is = socket.getInputStream();

//        System.out.println("readResponse: before headers");
        String line;
        ArrayList<String> headers = new ArrayList<>();
        while (true) {
//            System.out.println("\treadResponse: readString");
            line = readString(is);
//            System.out.println("response line: " + line);
            if (line.isEmpty()) break;
            headers.add(line);
        }
//        System.out.println("readResponse: after headers");

        String[] firstLine = headers.get(0).split(" ");
        dataAndCode.setCode(firstLine[1]);

        int bodyLength = 0;
        for (String s : headers) {
            if (s.length() > contentLength.length() &&
                    s.substring(0, contentLength.length()).equals(contentLength)) {
                bodyLength = Integer.parseInt(s.substring(contentLength.length()));
                break;
            }
        }

//        System.out.println("readResponse: before read data");
        int readBytes = 0;
        ArrayList<Character> bodyData = new ArrayList<>();
        while (readBytes < bodyLength) {
            bodyData.add((char) is.read());
            readBytes++;
        }
//        System.out.println("readResponse: after read data");

        System.out.println("Body len = " + bodyLength + "\n read = " + readBytes);

        dataAndCode.setData(bodyData);

        return dataAndCode;
    }

    static private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        char value;

        while (true) {
//            System.out.println("\treadString");
            value = (char)is.read();
            if (value == '\n') break;
            sb.append(value);
        }

        return sb.toString();
    }
}
