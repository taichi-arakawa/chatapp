import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

public class client {
  public static void main(String[] args) {
    // クライアントソケットを生成
    try (Socket socket = new Socket("localhost", 10000);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // キーボード入力用のリーダーの作成
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {

      // 氏名入力
      System.out.print("NAME>");
      String name = keyboard.readLine();
      writer.println("give me logs");

      Thread clientThread = new Thread(() -> {
        while (true) {
          String input;
          try {
            input = keyboard.readLine();
            writer.println(name + ":" + input);
            if (input.equals("exit")) {
              break;
            }
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      });
      clientThread.start();

      // 「exit」を入力するまで繰り返し
      while (true) {
        System.out.println(reader.readLine());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
