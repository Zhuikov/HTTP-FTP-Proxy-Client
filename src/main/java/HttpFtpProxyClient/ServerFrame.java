package HttpFtpProxyClient;


import javax.swing.*;
import java.awt.*;

public class ServerFrame {

    public static String frameKey = "server";

    private final ProxyClientGUI proxyClientGUI;
    private final JPanel mainPanel = new JPanel();
    private final JList list = new JList();
    private final JButton downloadButton = new JButton("Download");
    private final JButton uploadButton = new JButton("Upload");
    private final Dimension buttonSize = new Dimension(150, 25);

    public ServerFrame(ProxyClientGUI proxyClientGUI) {

        this.proxyClientGUI = proxyClientGUI;

        JPanel listPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();
        JPanel pathPanel = new JPanel();

        list.setPreferredSize(new Dimension(400, 350));
        listPanel.add(list);

        downloadButton.setPreferredSize(buttonSize);
        uploadButton.setPreferredSize(buttonSize);
        buttonsPanel.setLayout(new GridLayout(2, 1));
        buttonsPanel.setPreferredSize(new Dimension(200, 350));
        buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        GridBagConstraints c = new GridBagConstraints();
//        c.anchor = GridBagConstraints.NORTH;
//        c.fill = GridBagConstraints.VERTICAL;
//        c.weighty = 1.0;
//        c.gridx = 1;
//        c.gridy = 0;
//        c.insets = new Insets(25, 0, 0, 0);
        buttonsPanel.add(makeASCIIBinaryPanel(downloadButton));
//        GridBagConstraints c1 = new GridBagConstraints();
//        c1.anchor = GridBagConstraints.NORTH;
//        c1.weighty = 1.0;
//        c1.gridx = 1;
//        c1.gridy = 1;
//        c1.insets = new Insets(10, 0 , 0, 0);
        buttonsPanel.add(makeASCIIBinaryPanel(uploadButton));

        mainPanel.setLayout(new GridBagLayout());

        mainPanel.add(new Label("PATH"), constraintsPathPanel());
        mainPanel.add(listPanel, constraintsListPanel());
        mainPanel.add(buttonsPanel, constraintsButtonPanel());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private GridBagConstraints constraintsListPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.gridy = 1;
        c.gridx = 0;
        c.insets = new Insets(3, 6, 1 ,10);

        return c;
    }

    private GridBagConstraints constraintsPathPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(3, 6, 10, 10);

        return c;
    }

    private GridBagConstraints constraintsButtonPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.gridy = 1;
        c.gridx = 1;
        c.insets = new Insets(3, 10, 1, 10);

        return c;
    }

    private JPanel makeASCIIBinaryPanel(JButton button) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JRadioButton ascii = new JRadioButton("ASCII");
        JRadioButton binary = new JRadioButton("BINARY");
        ButtonGroup bg = new ButtonGroup();
        bg.add(ascii);
        bg.add(binary);
        ascii.setSelected(true);

        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(100, 50));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 5, 10);
        panel.add(ascii, c);

        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 10, 5, 0);
        panel.add(binary, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(button, c);

        return panel;
    }
}
