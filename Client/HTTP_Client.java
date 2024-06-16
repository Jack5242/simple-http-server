import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class HTTP_Client {

    private JFrame frame;
    private JTextField urlField;
    private JTextArea responseArea;
    private JTextArea postDataArea;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                HTTP_Client window = new HTTP_Client();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public HTTP_Client() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JLabel lblUrl = new JLabel("URL:");
        panel.add(lblUrl);

        urlField = new JTextField();
        panel.add(urlField);
        urlField.setColumns(40);

        JButton btnFetch = new JButton("Get");
        btnFetch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchFile();
            }
        });
        panel.add(btnFetch);

        JButton btnPost = new JButton("Post");
        btnPost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                postData();
            }
        });
        panel.add(btnPost);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteFile();
            }
        });
        panel.add(btnDelete);

        responseArea = new JTextArea();
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(responseArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        postDataArea = new JTextArea(5, 40);
        postDataArea.setBorder(BorderFactory.createTitledBorder("POST Data"));
        frame.getContentPane().add(postDataArea, BorderLayout.SOUTH);
    }

    private void fetchFile() {
        String urlString = urlField.getText();
        try {
            URL url = new URL(urlString);
            String hostname = url.getHost();
            int port = url.getPort() == -1 ? 8080 : url.getPort();
            String path = url.getPath();

            try (Socket socket = new Socket(hostname, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET " + path + " HTTP/1.1");
                out.println("Host: " + hostname);
                out.println("Connection: Close");
                out.println();
                out.flush();

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                responseArea.setText(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseArea.setText("Error fetching file.");
        }
    }

    private void postData() {
        String urlString = urlField.getText();
        String postData = postDataArea.getText();
        try {
            URL url = new URL(urlString);
            String hostname = url.getHost();
            int port = url.getPort() == -1 ? 8080 : url.getPort();
            String path = url.getPath();

            try (Socket socket = new Socket(hostname, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("POST " + path + " HTTP/1.1");
                out.println("Host: " + hostname);
                out.println("Content-Type: text/plain");
                out.println("Content-Length: " + postData.length());
                out.println();
                out.println(postData);
                out.flush();

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                responseArea.setText(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseArea.setText("Error posting data.");
        }
    }

    private void deleteFile() {
        String urlString = urlField.getText();
        try {
            URL url = new URL(urlString);
            String hostname = url.getHost();
            int port = url.getPort() == -1 ? 8080 : url.getPort();
            String path = url.getPath();

            try (Socket socket = new Socket(hostname, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("DELETE " + path + " HTTP/1.1");
                out.println("Host: " + hostname);
                out.println("Connection: Close");
                out.println();
                out.flush();

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                responseArea.setText(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseArea.setText("Error fetching file.");
        }
    }
}
