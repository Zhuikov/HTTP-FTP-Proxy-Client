package HttpFtpProxyClient;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Base64;

public class LoginFrame {

    private JFrame frame = new JFrame("Login");
    private JTextField userText = new JTextField(10);
    private JPasswordField passText = new JPasswordField(10);

    static private final String customItem = "*custom*";
    static private final String[] serversText = { "ftp.funet.fi", "ftp.sunet.se", customItem };
    private JTextField serverCustomText = new JTextField(10);
    private JComboBox serverBox = new JComboBox(serversText);

    private JLabel userLabel = new JLabel("User: ");
    private JLabel passLabel = new JLabel("Password: ");
    private JLabel serverLabel = new JLabel("Server: ");

    private JButton connectButton = new JButton("Connect");

    public LoginFrame() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel loginForm = new JPanel();
        loginForm.setLayout(new GridBagLayout());

        userLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        passLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        serverLabel.setFont(new Font("Serif", Font.PLAIN, 16));
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
        userText.setText("anonymous");
        addLabel(loginForm, 1, passLabel);
        addComponent(loginForm, 1, passText);
        passText.setText("PASSWORD");
        addLabel(loginForm, 2, serverLabel);
        addComponent(loginForm, 2, serverBox);
        addComponent(loginForm, 3, serverCustomText);

        addConnectButton(loginForm, connectButton);
        connectButton.addActionListener(e -> connectAction());

        JLabel topLabel = new JLabel("<html><h1 style=\"color: rgb(25, 116, 210)\">" +
                "<strong><i>HTTP-FTP Proxy Client</i></strong></h1><hr></html>");
        JPanel labelPanel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        labelPanel.add(topLabel, c);

        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(-50);
        frame.setLayout(borderLayout);
        frame.add(labelPanel, BorderLayout.NORTH);
        frame.add(loginForm, BorderLayout.CENTER);

        frame.setSize(800, 480);
        frame.setResizable(false);
        frame.setVisible(true);
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

    // запрашивает list / у указанного сервера.
    private void connectAction() {

        final String selectedServer = serverBox.getSelectedItem().toString();
        final String serverAddress = (!selectedServer.equals(customItem)) ? selectedServer : serverCustomText.getText();
        // проверить пустые поля
        final String loginPassword = Base64.getEncoder().encodeToString(
                (userText.getText() + ':' + passText.getText()).getBytes()
        );
        String request = "GET " + serverAddress + "/ HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress + '\n' +
                "Authorization: Basic " + loginPassword + "\n\n";

        HttpFtpProxyClient.DataAndCode response;
        try {
            response = HttpFtpProxyClient.send(request);
            System.out.println(response.getCode());
            System.out.println(response.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
