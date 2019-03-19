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

        StringBuilder sb = new StringBuilder();
        if (sb.toString().isEmpty()) {
            System.out.println("EMPTY");
        } else System.out.println("NO");
    }

    public static void main(String[] args) {
        new HttpFtpProxyClient();
    }

    public HttpFtpProxyClient() {
        SwingUtilities.invokeLater(() -> {
//            LoginFrame frame = new LoginFrame(proxyClientGUI);
//            proxyClientGUI.addPanel(frame.getMainPanel(),LoginFrame.frameKey);
//            proxyClientGUI.showPanel(LoginFrame.frameKey);

            ServerFrame serverFrame = new ServerFrame(proxyClientGUI);
            proxyClientGUI.addPanel(serverFrame.getMainPanel(), ServerFrame.frameKey);
            proxyClientGUI.showPanel(ServerFrame.frameKey);
        });
    }

    public static void send(Socket socket, String request) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write(request.getBytes());
//        os.close();
    }

    public static DataAndCode readResponse(Socket socket) throws IOException {

        DataAndCode dataAndCode = new DataAndCode();
        InputStream is = socket.getInputStream();

        String line;
        ArrayList<String> headers = new ArrayList<>();
        while (true) {
            line = readString(is);
            System.out.println("response line: " + line);
            if (line.isEmpty()) break;
            headers.add(line);
        }

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

//        byte[] body = new byte[bodyLength];
        int readBytes = 0;
//        while (readBytes < bodyLength) {
//            readBytes += is.read(body);
//        }

        ArrayList<Character> bodyData = new ArrayList<>();
        while (readBytes++ < bodyLength) {
            bodyData.add((char) is.read());
        }

        System.out.println("Body len = " + bodyLength);
//        is.read(body);

//        String bodyString = new String(body);
//        ArrayList<Character> bodyData = new ArrayList<>();
//        for (char c : bodyString.toCharArray()) {
//            bodyData.add(c);
//        }

        dataAndCode.setData(bodyData);

        return dataAndCode;
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

    class MenuPane extends JPanel {
        public MenuPane() {
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
//            gbc.anchor = GridBagConstraints.NORTH;
//
//            add(new JLabel("<html><h1><strong><i>Krisko Beatz Quiz</i></strong></h1><hr></html>"), gbc);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttons = new JPanel(new GridBagLayout());
            buttons.add(new JButton("Start"), gbc);
            buttons.add(new JButton("Show scores"), gbc);
            buttons.add(new JButton("Help"), gbc);
            buttons.add(new JButton("Exit"), gbc);

            gbc.weighty = 1;
            add(buttons, gbc);
        }
    }
}
