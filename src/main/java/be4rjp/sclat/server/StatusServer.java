package be4rjp.sclat.server;

import be4rjp.sclat.Main;
import be4rjp.sclat.MessageType;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.SoundType;
import be4rjp.sclat.data.ServerStatus;
import be4rjp.sclat.manager.PlayerReturnManager;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.manager.RankMgr;
import be4rjp.sclat.manager.ServerStatusManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
    
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    
                    if(cmd.equals("stop")){
                        socket.close();
                        System.out.println("Socket closed.");
                        break;
                    }
                    
                    System.out.println(cmd);

                    String args[] = cmd.split(" ");
                    
                    if(args[0].equals("return") && args.length == 2){
                        if(args[1].length() == 36){
                            PlayerReturnManager.addPlayerReturn(args[1]);
                        }
                    }

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
                                            RankMgr.addPlayerRankPoint(args[3], Integer.parseInt(args[2]));
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
                        case "started":{
                            if (args.length == 3) {
                                for(ServerStatus ss : ServerStatusManager.serverList){
                                    if(ss.getServerName().equals(args[1])) {
                                        ss.setRunningMatch(true);
                                        ss.setWaitingEndTime(0);
                                        ss.setMatchStartTime(Long.parseLong(args[2]));
                                    }
                                }
                            }
                            break;
                        }
                        case "cd":{
                            if (args.length == 3) {
                                for(ServerStatus ss : ServerStatusManager.serverList){
                                    if(ss.getServerName().equals(args[1])) {
                                        ss.setWaitingEndTime(Long.parseLong(args[2]));
                                        Sclat.sendMessage(ss.getDisplayName() + "§aの試合待機が開始されました！", MessageType.ALL_PLAYER);
                                        Sclat.sendMessage("§a§l" + (ss.getWaitingEndTime() - (System.currentTimeMillis() / 1000)) + "§b秒後に開始されます", MessageType.ALL_PLAYER);
                                        Main.getPlugin().getServer().getOnlinePlayers().forEach(player -> Sclat.playGameSound(player, SoundType.SUCCESS));
                                    }
                                }
                            }
                            break;
                        }
                        case "cdc":{
                            if (args.length == 2) {
                                for(ServerStatus ss : ServerStatusManager.serverList){
                                    if(ss.getServerName().equals(args[1])) {
                                        ss.setWaitingEndTime(0);
                                    }
                                }
                            }
                            break;
                        }
                        case "stopped":{
                            if (args.length == 2) {
                                for(ServerStatus ss : ServerStatusManager.serverList){
                                    if(ss.getServerName().equals(args[1])) {
                                        ss.setRunningMatch(false);
                                        ss.setMatchStartTime(0);
                                    }
                                }
                            }
                            break;
                        }
                        case "restart":{
                            if (args.length == 2) {
                                for(ServerStatus ss : ServerStatusManager.serverList){
                                    if(ss.getServerName().equals(args[1])) {
                                        ss.setRestartingServer(true);
                                    }
                                }
                            }
                            break;
                        }
                        case "restarted":{
                            if (args.length == 2) {
                                for(ServerStatus ss : ServerStatusManager.serverList){
                                    if(ss.getServerName().equals(args[1])) {
                                        ss.setRestartingServer(false);
                                    }
                                }
                            }
                            break;
                        }
                        case "map":{
                            if (args.length == 3) {
                                for(ServerStatus ss : ServerStatusManager.serverList){
                                    if(ss.getServerName().equals(args[1])) {
                                        ss.setMapName(args[2]);
                                    }
                                }
                            }
                            break;
                        }
                        case "tutorial":{
                            if (args.length == 2) {
                                PlayerStatusMgr.setTutorialState(args[1], 1);
                            }
                            break;
                        }
                    }
                }else{break;}
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
