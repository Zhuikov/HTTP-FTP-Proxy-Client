package HttpFtpProxyClient;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;

public class ServerFrame {

    public static String frameKey = "server";
    private String currentDir = "/";

    private final ProxyClientGUI proxyClientGUI;
    private final String serverAddress;
    private final String loginPassword;
    private final JPanel mainPanel = new JPanel();
    private final JList list = new JList();
    private final JButton downloadButton = new JButton("Download");
    JRadioButton downloadAscii = new JRadioButton("ASCII");
    JRadioButton downloadBinary = new JRadioButton("BINARY");
    private final JButton uploadButton = new JButton("Upload");
    JRadioButton uploadAscii = new JRadioButton("ASCII");
    JRadioButton upldoadBinary = new JRadioButton("BINARY");
    private final JLabel pathLabel = new JLabel("Path: ");
    private final JLabel currentDirLabel = new JLabel("/");
    private final Dimension buttonSize = new Dimension(150, 25);

    public ServerFrame(ProxyClientGUI proxyClientGUI, String serverAddress, String loginPassword) {

        this.proxyClientGUI = proxyClientGUI;
        this.serverAddress = serverAddress;
        this.loginPassword = loginPassword;

        // setting list panel
        JPanel listPanel = new JPanel();
        list.setFont(new Font("monospaced", Font.PLAIN, 10));
        JScrollPane menuScrollPane = new JScrollPane(list);
        list.addMouseListener(forwardDirAction());

        menuScrollPane.setPreferredSize(new Dimension(470, 350));
        listPanel.add(menuScrollPane);

        // setting panel with "download" and "upload" buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        buttonsPanel.setPreferredSize(new Dimension(200, 350));
        buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> backDirAction());
        GridBagConstraints c0 = new GridBagConstraints();
        c0.anchor = GridBagConstraints.NORTH;
        c0.weighty = 1.0;
        c0.gridx = 1;
        c0.gridy = 0;
        c0.insets = new Insets(15, 0, 0, 110);
        buttonsPanel.add(backButton, c0);

        downloadButton.setPreferredSize(buttonSize);
        downloadButton.addActionListener(e -> downloadAction());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(54, 0, 0, 0);
        buttonsPanel.add(makeASCIIBinaryPanel(downloadButton, downloadAscii, downloadBinary), c);

        uploadButton.setPreferredSize(buttonSize);
        uploadButton.addActionListener(e -> uploadAction());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.anchor = GridBagConstraints.CENTER;
        c1.weighty = 1.0;
        c1.gridx = 1;
        c1.gridy = 0;
        c1.insets = new Insets(2, 0 , 0, 0);
        buttonsPanel.add(makeASCIIBinaryPanel(uploadButton, uploadAscii, upldoadBinary), c1);

        // setting path panel
        JPanel pathPanel = new JPanel();
        pathLabel.setFont(ProxyClientGUI.labelFont);
        currentDirLabel.setFont(ProxyClientGUI.labelFont);
        pathPanel.add(pathLabel);
        pathPanel.add(currentDirLabel);

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(pathPanel, constraintsPathPanel());
        mainPanel.add(listPanel, constraintsListPanel());
        mainPanel.add(buttonsPanel, constraintsButtonPanel());
    }

    public void updateCurrentPath(String newPath) {
        currentDir = newPath;
        currentDirLabel.setText(currentDir);
    }

    public void updateCurrentPath(ArrayList<Character> currentPath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : currentPath) {
            stringBuilder.append(c);
        }
        currentDir = stringBuilder.toString();
        currentDirLabel.setText(currentDir);
    }

    public void updateList(ArrayList<Character> ftpResponse) {
        StringBuilder stringList = new StringBuilder();
        for (Character c : ftpResponse) {
            stringList.append(c);
        }
        String[] lines = stringList.toString().split("\n");

        list.setListData(lines);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }


    private MouseListener forwardDirAction() {

        return new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    String element = list.getSelectedValue().toString();
                    System.out.println(element);
                    if (element.charAt(0) == 'd') {
                        String newDir = getFileName(element);
                        String listRequest = makeListRequest(currentDir + newDir);
                        String cwdRequest  = makeCwdRequest(newDir);

                        HttpFtpProxyClient.DataAndCode listResponse;
                        HttpFtpProxyClient.DataAndCode cwdResponse;
                        try {
                            listResponse = HttpFtpProxyClient.makeRequest(listRequest);
                            cwdResponse = HttpFtpProxyClient.makeRequest(cwdRequest);
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), e.getMessage());
                            return;
                        }

                        if (listResponse.getData() != null) {
                            updateList(listResponse.getData());
                        } else {
                            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot get list data");
                            return;
                        }

                        if (cwdResponse.getCode().equals("200")) {
                            currentDir += newDir + '/';
                            currentDirLabel.setText(currentDir);
                        } else {
                            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot change directory");
                            return;
                        }
                    }
                }
            }
        };

    }

    // todo выбирать имя файла куда сохранять
    private void downloadAction() {

        if (list.getSelectedValue().toString().charAt(0) != '-')
            return;

        String retrRequest = makeRetrRequest(currentDir + getFileName(list.getSelectedValue().toString()),
                downloadAscii.isSelected() ? 'A' : 'I');
        JFileChooser chooser = new JFileChooser("/home/artem/Documents/proxyTest");
        chooser.setDialogTitle("Choose a directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        String downloadPath;
        if (chooser.showSaveDialog(proxyClientGUI.getFrame()) == JFileChooser.APPROVE_OPTION) {
            downloadPath = chooser.getSelectedFile().getPath();
            System.out.println(downloadPath);
        } else
            return;

        try (OutputStream outputStream = new FileOutputStream(downloadPath + "/" +
                getFileName(list.getSelectedValue().toString()))) {
            HttpFtpProxyClient.DataAndCode response = HttpFtpProxyClient.makeRequest(retrRequest);
            for (char c : response.getData()) {
                outputStream.write(c);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot save file");
            return;
        }

        JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Successfully saved!");
    }

    private void uploadAction() {

        JFileChooser chooser = new JFileChooser("/home/artem/Documents/proxyTest");
        chooser.setDialogTitle("Choose a file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (chooser.showSaveDialog(proxyClientGUI.getFrame()) != JFileChooser.APPROVE_OPTION)
            return;

        ArrayList<Character> body = new ArrayList<>();
        int input;
        HttpFtpProxyClient.DataAndCode response;
        System.out.println(chooser.getSelectedFile().getAbsolutePath());
        try (FileInputStream inputStream = new FileInputStream(chooser.getSelectedFile().getAbsolutePath())) {
            while ((input = inputStream.read()) != -1)
                body.add((char) input);
            inputStream.close();
            String request = makeStorRequest(currentDir, body, uploadAscii.isSelected() ? 'A' : 'I');
            response = HttpFtpProxyClient.makeRequest(request);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot upload file");
            e.printStackTrace();
            return;
        }

        if (response.getCode().equals("200")) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Uploaded!");
        } else {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Error " + response.getCode());
        }
    }

    // изменяет метку currentPath, currentDir и запрашивает list
    private void backDirAction() {

        if (currentDir.equals("/"))
            return;

        String tempDir = currentDir;
        currentDir = currentDir.substring(0, currentDir.lastIndexOf('/'));
        currentDir = currentDir.substring(0, currentDir.lastIndexOf('/'));
        currentDir += '/';

        String listRequest = makeListRequest(currentDir);
        String cwdRequest = makeCwdRequest(currentDir);

        HttpFtpProxyClient.DataAndCode listResponse;
        HttpFtpProxyClient.DataAndCode cwdResponse;
        try {
            listResponse = HttpFtpProxyClient.makeRequest(listRequest);
            cwdResponse = HttpFtpProxyClient.makeRequest(cwdRequest);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), e.getMessage());
            return;
        }

        if (listResponse.getData() != null) {
            updateList(listResponse.getData());
        } else {
            currentDir = tempDir;
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot get list data");
            return;
        }

        if (cwdResponse.getCode().equals("200")) {
            currentDirLabel.setText(currentDir);
        } else {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot change directory");
            return;
        }

    }

    private String getFileName(String line) {
        return line.substring(line.lastIndexOf(' ') + 1, line.length() - 1);
    }

    private String makeRetrRequest(String file, char type) {
        return "GET " + serverAddress + "/file" + file + "?type=\"" + type + "\" HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n";
    }

    private String makeListRequest(String dir) {

        return "GET " + serverAddress + "/file" + dir + "/?type=\"A\" HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n";
    }

    private String makeCwdRequest(String dir) {

        return "GET " + serverAddress + "/cwd?dir=\"" + dir + "\" HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n";
    }

    private String makeStorRequest(String filePlace, ArrayList<Character> body, char type) {
        String request = "PUT " + serverAddress + "/file" + filePlace + "?type=\"" + type + "\" HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n";
        StringBuilder sb = new StringBuilder(request);
        for (char c : body) {
            sb.append(c);
        }
        return sb.toString();
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

    private JPanel makeASCIIBinaryPanel(JButton button, JRadioButton ascii, JRadioButton binary) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(180, 70));
        panel.setBorder(BorderFactory.createDashedBorder(null));
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

}
