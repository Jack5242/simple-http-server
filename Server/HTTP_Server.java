import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class HTTP_Server {

    private static final int PORT = 8080;
    private static final String ROOT_DIRECTORY = ".";

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {

                String requestLine = in.readLine();
                if (requestLine == null) {
                    return;
                }

                String[] tokens = requestLine.split(" ");
                String method = tokens[0];
                String filePath = tokens[1];

                if (method.equals("GET")) {
                    handleGetRequest(filePath, out);
                } else if (method.equals("POST")) {
                    handlePostRequest(filePath, in, out);
                } else if (method.equals("DELETE")) {
                    handleDeleteRequest(filePath, out);
                } else {
                    out.println("HTTP/1.1 501 Not Implemented");
                    out.println();
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleGetRequest(String filePath, PrintWriter out) throws IOException {
            File file = new File(ROOT_DIRECTORY + filePath);
            if (file.exists() && !file.isDirectory()) {
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Length: " + file.length());
                out.println();
                out.flush();
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        clientSocket.getOutputStream().write(buffer, 0, bytesRead);
                    }
                }
            } else {
                out.println("HTTP/1.1 404 Not Found");
                out.println();
            }
        }

        private void handlePostRequest(String filePath, BufferedReader in, PrintWriter out) throws IOException {
            StringBuilder body = new StringBuilder();
            String line;
            int contentLength = 0;

            // Read headers to get Content-Length
            while (!(line = in.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            // Read the body content based on Content-Length
            for (int i = 0; i < contentLength; i++) {
                body.append((char) in.read());
            }

            // Write the POST data to a file
            File file = new File(ROOT_DIRECTORY + filePath);
            try (FileWriter fileWriter = new FileWriter(file, true)) { // Append mode
                fileWriter.write(body.toString());
            }

            // Respond to the client
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println();
            out.println("Received POST data and wrote to " + filePath);
        }

        private void handleDeleteRequest(String filePath, PrintWriter out) throws IOException {
            File file = new File(ROOT_DIRECTORY + filePath);
            if (file.exists() && !file.isDirectory()) {
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Length: " + file.length());
                out.println();
                out.flush();
                file.delete();
            } else {
                out.println("HTTP/1.1 204 No Content");
                out.println();
            }
        }
    }

    
}
