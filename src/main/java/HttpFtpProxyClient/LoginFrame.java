package HttpFtpProxyClient;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Base64;

public class LoginFrame {

    public final static String frameKey = "login";

    private final JPanel mainPanel = new JPanel();
    private final ProxyClientGUI proxyClientGUI;

    private final JTextField userText = new JTextField(10);
    private final JPasswordField passText = new JPasswordField(10);

    static private final String customItem = "*custom*";
    static private final String[] serversText = { customItem, "ftp.funet.fi", "ftp.sunet.se" };
    private JTextField serverCustomText = new JTextField(10);
    private JComboBox serverBox = new JComboBox(serversText);

    private final JLabel userLabel = new JLabel("User: ");
    private final JLabel passLabel = new JLabel("Password: ");
    private final JLabel serverLabel = new JLabel("Server: ");
    private final JButton connectButton = new JButton("Connect");


    public JPanel getMainPanel() {
        return mainPanel;
    }

    public LoginFrame(ProxyClientGUI proxyClientGUI) {

        this.proxyClientGUI = proxyClientGUI;

        // Make the login form panel
        JPanel loginForm = new JPanel();
        loginForm.setLayout(new GridBagLayout());

        userLabel.setFont(ProxyClientGUI.labelFont);
        passLabel.setFont(ProxyClientGUI.labelFont);
        serverLabel.setFont(ProxyClientGUI.labelFont);
        serverCustomText.setText("192.168.0.27");
        serverCustomText.setEnabled(false);
        serverBox.addActionListener(e -> {
            if (serverBox.getSelectedItem().toString().equals(customItem)) {
                serverCustomText.setEnabled(true);
            } else {
                serverCustomText.setText("");
                serverCustomText.setEnabled(false);
            }
        });

        addLabel(loginForm, 0, userLabel);
        addComponent(loginForm, 0, userText);
        userText.setText("artem");
        addLabel(loginForm, 1, passLabel);
        addComponent(loginForm, 1, passText);
        passText.setText("artem");
        addLabel(loginForm, 2, serverLabel);
        addComponent(loginForm, 2, serverBox);
        addComponent(loginForm, 3, serverCustomText);

        addConnectButton(loginForm, connectButton);
        connectButton.addActionListener(e -> connectAction());


        // make the label panel.
        JPanel labelPanel = new JPanel();
        JLabel topLabel = new JLabel("<html><h1 style=\"color: rgb(25, 116, 210)\">" +
                "<strong><i>HTTP-FTP Proxy Client</i></strong></h1><hr></html>");
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        labelPanel.add(topLabel, c);

        // login panel and label panel to main panel
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(-50);
        mainPanel.setLayout(borderLayout);
        mainPanel.add(labelPanel, BorderLayout.NORTH);
        mainPanel.add(loginForm, BorderLayout.CENTER);
    }

    private void addLabel(JPanel panel, int pos, JLabel label) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = pos;
        panel.add(label, c);
    }

    private void addComponent(JPanel panel, int pos, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 40;
        c.gridx = 1;
        c.gridy = pos;
        panel.add(field, c);
    }

    private void addConnectButton(JPanel panel, JButton button) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 3, 3, 3);
        c.ipadx = 40;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        panel.add(button, c);
    }

    private void connectAction() {

        final String selectedServer = serverBox.getSelectedItem().toString();
        final String serverAddress = (!selectedServer.equals(customItem)) ? selectedServer : serverCustomText.getText();

        if (serverAddress.isEmpty() || userText.getText().isEmpty() || passText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "All fields must be filled");
            return;
        }

        final String loginPassword = Base64.getEncoder().encodeToString(
                (userText.getText() + ':' + passText.getText()).getBytes()
        );

        HttpFtpProxyClient.ResponseStructure checkAuth = new HttpFtpProxyClient.ResponseStructure();
        checkAuth.setHeaders("GET " + serverAddress + "/auth HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n");
        try {
            HttpFtpProxyClient.ResponseStructure response = HttpFtpProxyClient.makeRequest(checkAuth);
            if (!response.getHeaders().equals("200")) {
                JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Auth error");
                return;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Error check auth");
        }


        ServerFrame serverFrame = new ServerFrame(proxyClientGUI, serverAddress, loginPassword);
        proxyClientGUI.addPanel(serverFrame.getMainPanel(), ServerFrame.frameKey);
        proxyClientGUI.showPanel(ServerFrame.frameKey);
        proxyClientGUI.setFrameTitle(serverAddress);
    }
}
