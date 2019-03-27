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
    private JRadioButton downloadAscii = new JRadioButton("ASCII");
    private JRadioButton downloadBinary = new JRadioButton("BINARY");
    private JRadioButton uploadAscii = new JRadioButton("ASCII");
    private JRadioButton upldoadBinary = new JRadioButton("BINARY");
    private final JLabel currentDirLabel = new JLabel("/");

    public ServerFrame(ProxyClientGUI proxyClientGUI, String serverAddress, String loginPassword) {

        this.proxyClientGUI = proxyClientGUI;
        this.serverAddress = serverAddress;
        this.loginPassword = loginPassword;

        // setting list panel
        JPanel listPanel = new JPanel();
        list.setFont(new Font("monospaced", Font.PLAIN, 10));
        list.addMouseListener(forwardDirAction());
        JScrollPane menuScrollPane = new JScrollPane(list);
        menuScrollPane.setPreferredSize(new Dimension(470, 350));
        listPanel.add(menuScrollPane);

        updatePath("/");
        updateList(currentDir);

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

        JButton downloadButton = new JButton("Download");
        downloadButton.setPreferredSize(ProxyClientGUI.buttonSize);
        downloadButton.addActionListener(e -> downloadAction());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.anchor = GridBagConstraints.NORTH;
        c1.weighty = 1.0;
        c1.gridx = 1;
        c1.gridy = 0;
        c1.insets = new Insets(54, 0, 0, 0);
        buttonsPanel.add(makeASCIIBinaryPanel(downloadButton, downloadAscii, downloadBinary), c1);

        JButton uploadButton = new JButton("Upload");
        uploadButton.setPreferredSize(ProxyClientGUI.buttonSize);
        uploadButton.addActionListener(e -> uploadAction());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = GridBagConstraints.CENTER;
        c2.weighty = 1.0;
        c2.gridx = 1;
        c2.gridy = 0;
        c2.insets = new Insets(2, 0 , 0, 0);
        buttonsPanel.add(makeASCIIBinaryPanel(uploadButton, uploadAscii, upldoadBinary), c2);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(ProxyClientGUI.buttonSize);
        deleteButton.addActionListener(e -> deleteAction());
        GridBagConstraints c3 = new GridBagConstraints();
        c3.anchor = GridBagConstraints.CENTER;
        c3.weighty = 1.0;
        c3.gridx = 1;
        c3.gridy = 0;
        c3.insets = new Insets(130, 0, 0, 0);
        buttonsPanel.add(deleteButton, c3);

        // setting path panel
        JPanel pathPanel = new JPanel();
        JLabel pathLabel = new JLabel("Path: ");
        pathLabel.setFont(ProxyClientGUI.labelFont);
        currentDirLabel.setFont(ProxyClientGUI.labelFont);
        pathPanel.add(pathLabel);
        pathPanel.add(currentDirLabel);

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(pathPanel, constraintsPathPanel());
        mainPanel.add(listPanel, constraintsListPanel());
        mainPanel.add(buttonsPanel, constraintsButtonPanel());
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
                        System.out.println("Current dir before forward update = " + currentDir);
                        updatePath(currentDir + newDir + '/');
                        updateList(currentDir);
                        System.out.println("Current dir after  forward update = " + currentDir);
                    }
                }
            }
        };
    }

    // todo выбирать имя файла куда сохранять
    private void downloadAction() {

        if (list.isSelectionEmpty())
            return;

        if (list.getSelectedValue().toString().charAt(0) != '-')
            return;

        HttpFtpProxyClient.ResponseStructure retrRequest =
                makeRetrRequest(currentDir + getFileName(list.getSelectedValue().toString()),
                        downloadAscii.isSelected() ? 'A' : 'I');
        JFileChooser chooser = new JFileChooser("/home/artem/Desktop");
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
            HttpFtpProxyClient.ResponseStructure response = HttpFtpProxyClient.makeRequest(retrRequest);
            for (char c : response.getBody()) {
                outputStream.write(c);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot save file");
            return;
        }

        JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Saved!");
    }

    private void uploadAction() {

        JFileChooser chooser = new JFileChooser("/home/artem/Documents/proxyTest");
        chooser.setDialogTitle("Choose a file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (chooser.showSaveDialog(proxyClientGUI.getFrame()) != JFileChooser.APPROVE_OPTION)
            return;

        // open and upload file
        ArrayList<Character> body = new ArrayList<>();
        int input;
        HttpFtpProxyClient.ResponseStructure storResponse;
        System.out.println("Uploading: " + chooser.getSelectedFile().getAbsolutePath());
        try {
            FileInputStream inputStream = new FileInputStream(chooser.getSelectedFile().getAbsolutePath());
            while ((input = inputStream.read()) != -1)
                body.add((char) input);
            inputStream.close();
            HttpFtpProxyClient.ResponseStructure request = makeStorRequest(currentDir + chooser.getSelectedFile().getName(),
                    body, uploadAscii.isSelected() ? 'A' : 'I');
            storResponse = HttpFtpProxyClient.makeRequest(request);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot upload file");
            e.printStackTrace();
            return;
        }

        if (storResponse.getHeaders().equals("200")) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Uploaded!");
        } else {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Error " + storResponse.getHeaders());
        }

        updateList(currentDir);
    }

    // изменяет метку currentPath, currentDir и запрашивает list
    private void backDirAction() {

        if (currentDir.equals("/"))
            return;

        String previousDir = currentDir.substring(0, currentDir.lastIndexOf('/'));
        previousDir = previousDir.substring(0, previousDir.lastIndexOf('/')) + '/';

        updatePath(previousDir);
        updateList(currentDir);
    }

    private void deleteAction() {

        if (list.isSelectionEmpty())
            return;

        //todo delete directories
        HttpFtpProxyClient.ResponseStructure deleRequest =
                makeDeleRequest(currentDir + getFileName(list.getSelectedValue().toString()));
        HttpFtpProxyClient.ResponseStructure deleResponse;

        try {
            deleResponse = HttpFtpProxyClient.makeRequest(deleRequest);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Error while sending request");
            return;
        }

        if (deleResponse.getHeaders().equals("200")) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Deleted");
        } else {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot delete file");
        }

        updateList(currentDir);
    }

    private void updateList(String dir) {
        HttpFtpProxyClient.ResponseStructure listRequest = makeListRequest(dir);
        HttpFtpProxyClient.ResponseStructure listResponse;
        try {
            listResponse = HttpFtpProxyClient.makeRequest(listRequest);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Error request for list");
            return;
        }

        if (listResponse.getHeaders().equals("200"))
            updateList(listResponse.getBody());
        else
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot update list");
    }

    private void updatePath(String newDir) {
        HttpFtpProxyClient.ResponseStructure request = makeCwdRequest(newDir);
        HttpFtpProxyClient.ResponseStructure response;
        try {
            response = HttpFtpProxyClient.makeRequest(request);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Error request for cwd");
            return;
        }

        if (response.getHeaders().equals("200")) {
            currentDir = newDir;
            currentDirLabel.setText(newDir);
        } else {
            JOptionPane.showMessageDialog(proxyClientGUI.getFrame(), "Cannot update path");
        }
    }

    private String getFileName(String line) {
        return line.substring(line.lastIndexOf(' ') + 1, line.length() - 1);
    }

    private HttpFtpProxyClient.ResponseStructure makeRetrRequest(String file, char type) {
        HttpFtpProxyClient.ResponseStructure responseStructure = new HttpFtpProxyClient.ResponseStructure();
        responseStructure.setHeaders("GET " + serverAddress + "/file" + file + "?type=\"" + type + "\" HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n");

        return responseStructure;
    }

    private HttpFtpProxyClient.ResponseStructure makeListRequest(String dir) {
        HttpFtpProxyClient.ResponseStructure responseStructure = new HttpFtpProxyClient.ResponseStructure();
        responseStructure.setHeaders("GET " + serverAddress + "/file" + dir + "?type=\"A\" HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n");

        return responseStructure;
    }

    private HttpFtpProxyClient.ResponseStructure makeCwdRequest(String dir) {

        HttpFtpProxyClient.ResponseStructure responseStructure = new HttpFtpProxyClient.ResponseStructure();
        responseStructure.setHeaders("GET " + serverAddress + "/cwd?dir=\"" + dir + "\" HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n");

        return responseStructure;
    }

    private HttpFtpProxyClient.ResponseStructure makeStorRequest(String filePlace, ArrayList<Character> body, char type) {

        HttpFtpProxyClient.ResponseStructure responseStructure = new HttpFtpProxyClient.ResponseStructure();
        responseStructure.setHeaders("PUT " + serverAddress + "/file" + filePlace + "?type=\"" + type + "\" HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + '\n' +
                HttpFtpProxyClient.contentLength + body.size() + "\n\n");
        responseStructure.setBody(body);

        return responseStructure;
    }

    private HttpFtpProxyClient.ResponseStructure makeDeleRequest(String filePlace) {

        HttpFtpProxyClient.ResponseStructure responseStructure = new HttpFtpProxyClient.ResponseStructure();
        responseStructure.setHeaders("DELETE " + serverAddress + "/file" + filePlace + " HTTP/1.1\n" +
                "Host: " + HttpFtpProxyClient.proxyAddress +
                "\nAuthorization: Basic " + loginPassword + "\n\n");
        return responseStructure;
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
