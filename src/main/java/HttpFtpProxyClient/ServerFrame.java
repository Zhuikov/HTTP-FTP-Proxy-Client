package HttpFtpProxyClient;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ServerFrame {

    public static String frameKey = "server";

    private final ProxyClientGUI proxyClientGUI;
    private final JPanel mainPanel = new JPanel();
    private final JList list = new JList();
    private final JButton downloadButton = new JButton("Download");
    private final JButton uploadButton = new JButton("Upload");
    private final JLabel pathLabel = new JLabel("Path: ");
    private final JLabel currentDirLabel = new JLabel("/");
    private final Dimension buttonSize = new Dimension(150, 25);

    public ServerFrame(ProxyClientGUI proxyClientGUI) {

        this.proxyClientGUI = proxyClientGUI;

        // setting list panel
        JPanel listPanel = new JPanel();
        list.setPreferredSize(new Dimension(470, 350));
        list.setFont(new Font("monospaced", Font.PLAIN, 10));
        listPanel.add(list);

        // setting panel with "download" and "upload" buttons
        downloadButton.setPreferredSize(buttonSize);
        uploadButton.setPreferredSize(buttonSize);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        buttonsPanel.setPreferredSize(new Dimension(200, 350));
        buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(25, 0, 0, 0);
        buttonsPanel.add(makeASCIIBinaryPanel(downloadButton), c);
        GridBagConstraints c1 = new GridBagConstraints();
        c1.anchor = GridBagConstraints.CENTER;
        c1.weighty = 1.0;
        c1.gridx = 1;
        c1.gridy = 0;
        c1.insets = new Insets(-60, 0 , 0, 0);
        buttonsPanel.add(makeASCIIBinaryPanel(uploadButton), c1);

        // setting path panel
        JPanel pathPanel = new JPanel();
        pathLabel.setFont(ProxyClientGUI.labelFont);
        currentDirLabel.setFont(ProxyClientGUI.labelFont);
        pathPanel.add(pathLabel);

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(pathPanel, constraintsPathPanel());
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
        c.gridy = 1;
        c.gridx = 1;
        c.insets = new Insets(3, 10, 1, 10);

        return c;
    }

    private JPanel makeASCIIBinaryPanel(JButton button) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(180, 70));
        panel.setBorder(BorderFactory.createDashedBorder(null));
        JRadioButton ascii = new JRadioButton("ASCII");
        JRadioButton binary = new JRadioButton("BINARY");
        ButtonGroup bg = new ButtonGroup();
        bg.add(ascii);
        bg.add(binary);
        ascii.setSelected(true);

        panel.setLayout(new GridBagLayout());
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

    public void updateCurrentPath(ArrayList<Character> currentPath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : currentPath) {
            stringBuilder.append(c);
        }
        currentDirLabel.setText(stringBuilder.toString());
    }

    public void updateList(ArrayList<Character> ftpResponse) {
        StringBuilder stringList = new StringBuilder();
        for (Character c : ftpResponse) {
            stringList.append(c);
        }
        String[] lines = stringList.toString().split("\n");

        list.setListData(lines);
    }
}