import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MultiClientServer {
    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(10000);
            System.out.println("サーバーが起動しました。クライアントからの接続を待ちます...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("クライアントと接続しました。");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter output;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                output = new PrintWriter(clientSocket.getOutputStream(), true);

                String clientData;
                while ((clientData = input.readLine()) != null) {
                    System.out.println(clientData);

                    // ブロードキャスト
                    broadcast(clientData);
                }

                clientSocket.close();
                System.out.println("クライアントとの接続を終了しました。");

                // スレッドをリストから削除
                clientHandlers.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            output.println(message);
        }
    }

    private static void broadcast(String message) {
        // System.out.println(clientHandlers);
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }
}
