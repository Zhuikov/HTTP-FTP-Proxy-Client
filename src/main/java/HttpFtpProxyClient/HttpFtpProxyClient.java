package HttpFtpProxyClient;

import javax.swing.*;
import java.awt.*;

public class HttpFtpProxyClient {

    public static void main(String[] args) {
        new HttpFtpProxyClient();
    }

    public HttpFtpProxyClient() {
        JFrame frame = new JFrame("Hello");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel labelPane = new JPanel(new GridBagLayout());

        labelPane.setLayout(new GridBagLayout());
        labelPane.setPreferredSize(new Dimension(200, 200));
        labelPane.setMaximumSize(new Dimension(200 , 200));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
//        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.VERTICAL;

        JLabel label1 = new JLabel("Label1");
//        label1.setSize(20, 100);
//        label1.setPreferredSize(new Dimension(20, 100));
        JLabel label2 = new JLabel("Label2");
//        label2.setSize(20, 100);
        JLabel label3 = new JLabel("Label3");
//        label3.setSize(20, 100);

        labelPane.add(label1, c);
        labelPane.add(label2, c);
        labelPane.add(label3, c);

        labelPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Test border title"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5))
        );

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
