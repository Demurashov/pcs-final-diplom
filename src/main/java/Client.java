import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Client {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8989;
    private static String[] words = {"за", "бизнес", "микросервис", "на", "смысл", "план", "паттерн", "к", "также"};

    public static void main(String[] args) {
        Gson gsn = new Gson();
        for (String item : words) {
            try (Socket socket = new Socket(HOST, PORT);
                 PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(socket.getInputStream())))) {
                if (item != null) {
                    printWriter.println(item);
                } else {
                    printWriter.println("Q");
                    break;
                }
                String strIn = bufferedReader.readLine();
                List<PageEntry> pageEntryList = gsn.fromJson(strIn, new TypeToken<List<PageEntry>>() {}.getType());
                //System.out.println("Получено сообщение от сервера: " + strIn);
                System.out.println("Получено сообщение от сервера: ");
                pageEntryList.forEach(a-> System.out.println(a));
            } catch (Exception exception) {
                exception.getStackTrace();
            }
        }
    }
}

