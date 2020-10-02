package be4rjp.sclat.server;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.manager.PlayerStatusMgr;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StatusServer extends Thread {

    private ServerSocket sSocket = null;

    //private List<String> commands = new ArrayList<>();

    private final int port;

    public StatusServer(int port){
        this.port = port;
    }

    public void run(){
        try{
            //ソケットを作成
            sSocket = new ServerSocket(port);
            System.out.println("Waiting for status client...");

            //クライアントからの要求待ち
            while (true) {
                Socket socket = sSocket.accept();
                new EchoThread(socket).start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if (sSocket!=null)
                    sSocket.close();
                System.out.println("Status server is stopped!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


//非同期スレッド
class EchoThread extends Thread {

    private Socket socket;

    public EchoThread(Socket socket) {
        this.socket = socket;
        System.out.println("Connected " + socket.getRemoteSocketAddress());
    }

    public void run() {
        try {
            System.out.println("Waiting for commands...");
            //クライアントからの受取用
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //サーバーからクライアントへの送信用
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String cmd = null;
            //命令受け取り用ループ
            while (true) {
                if((cmd = reader.readLine()) != null) {
                    System.out.println(cmd);

                    String args[] = cmd.split(" ");

                    switch (args[0]) {
                        case "get": {
                            if (args.length == 3) {

                            }
                            break;
                        }
                        case "add": { //add [statusName] [number] [uuid]
                            if (args.length == 4) {
                                if (Sclat.isNumber(args[2]) && args[3].length() == 36) {
                                    switch (args[1]) {
                                        case "money":
                                            PlayerStatusMgr.addMoney(args[3], Integer.parseInt(args[2]));
                                            break;
                                        case "rank":
                                            PlayerStatusMgr.addRank(args[3], Integer.parseInt(args[2]));
                                            break;
                                        case "level":
                                            PlayerStatusMgr.addLv(args[3], Integer.parseInt(args[2]));
                                            break;
                                        case "kill":
                                            PlayerStatusMgr.addKill(args[3], Integer.parseInt(args[2]));
                                            break;
                                        case "paint":
                                            PlayerStatusMgr.addPaint(args[3], Integer.parseInt(args[2]));
                                            break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {}
            System.out.println("Disconnected " + socket.getRemoteSocketAddress());
        }
    }

}
