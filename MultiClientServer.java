import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileReader;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultiClientServer {
    private static String filename;

    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(10000);){

            System.out.println("サーバーが起動しました。クライアントからの接続を待ちます...");

            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            filename = date.format(formatter);

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

                    if (clientData.equals("give me logs")){
                        
                        // logの読み込み
                        try {
                            File file = new File("./"+filename+".txt");

                        
                        FileReader fileReader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        String logs;
                        while ((logs = bufferedReader.readLine()) != null) {
                            //System.out.print(logs);
                            sendMessage(logs);
                        }
                    
                        // 4.最後にファイルを閉じてリソースを開放する
                        //fileReader.close();
                        bufferedReader.close();
                    
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        
                        
                        continue;
                    }
                    

                    //
                    try{
                        File file = new File("./"+filename+".txt");
                
                        //if (checkBeforeWritefile(file)){
                        FileWriter filewriter = new FileWriter(file, true);
          
                        filewriter.write(clientData+"\r\n");
                        //filewriter.write("ddd\r\n");
                
                        filewriter.close();
                        //}else{
                        //  System.out.println("ファイルに書き込めません");
                            //}
                        }catch(IOException e){
                            System.out.println(e);
                        }

                    // ブロードキャスト
                    broadcast(clientData);

                    // 会話の記録を残す

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
