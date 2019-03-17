package HttpFtpProxyClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HttpFtpProxyClient {

    public static void main(String[] args) {
        new HttpFtpProxyClient();
    }

    public HttpFtpProxyClient() {
        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Test");
//            frame.add(new LoginFrame());
            new LoginFrame();
//            frame.setSize(800, 480);
//            frame.setResizable(false);
//            frame.pack();
//            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
        });
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

    public HttpFtpProxyClient(int q) {
        JFrame frame = new JFrame("Hello");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel labelPane = new JPanel(new GridBagLayout());

        labelPane.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.VERTICAL;

        JLabel label1 = new JLabel("Label1");
        JLabel label2 = new JLabel("Label2");
        JLabel label3 = new JLabel("Label3");

        JButton but1 = new JButton("Test");
        but1.setPreferredSize(new Dimension(150, 30));
        JButton but2 = new JButton("Vere long test button with text");

        labelPane.add(label1, c);
        labelPane.add(but1, c);
        labelPane.add(but2, c);

        labelPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        c.weighty = 1;
        frame.add(labelPane);

//        JPanel all = new JPanel();
//        BorderLayout borderLayout = new BorderLayout();
//        all.setLayout(borderLayout);
//        all.add(labelPane, BorderLayout.CENTER);

//        add(all);

        frame.setSize(800, 480);
        frame.setResizable(false);
        frame.setVisible(true);
    }

}
