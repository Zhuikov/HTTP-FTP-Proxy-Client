package HttpFtpProxyClient;


import javax.swing.*;
import java.awt.*;

public class ProxyClientGUI {

    public static final Font labelFont = new Font("Serif", Font.PLAIN, 16);

    private final JFrame frame = new JFrame("Http-Ftp Proxy");
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardLayoutPanel = new JPanel(cardLayout);

    public ProxyClientGUI() {
        frame.setSize(700, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(cardLayoutPanel);
    }

    public void addPanel(JPanel panel, String key) {
        cardLayoutPanel.add(panel, key);
    }

    public void showPanel(String key) {
        cardLayout.show(cardLayoutPanel, key);
        frame.setVisible(true);
    }

    public void setFrameTitle(String name) {
        frame.setTitle(name);
    }

    public JFrame getFrame() {
        return frame;
    }
}
