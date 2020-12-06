package be4rjp.sclat.server;

import be4rjp.sclat.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EquipmentClient {
    private List<String> commands = new ArrayList<>();
    
    private final String host;
    private final int port;
    
    private final BukkitRunnable task;
    
    public EquipmentClient(String host, int port, List<String> commands) {
        this.host = host;
        this.port = port;
        this.commands = commands;
        this.task = new BukkitRunnable(){
            @Override
            public void run() {
                Socket cSocket = null;
                PrintWriter writer = null;
                BufferedReader reader = null;
                
                try {
                    //System.out.println("test");
                    //IPアドレスとポート番号を指定してクライアント側のソケットを作成
                    cSocket = new Socket(host, port);
                    
                    //クライアント側からサーバへの送信用
                    writer = new PrintWriter(cSocket.getOutputStream(), true);
                    
                    //サーバ側からの受取用
                    reader = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
                    
                    //命令送信ループ
                    String cmd = null;
                    while (true) {
                        if (commands.size() != 0) {
                            
                            cmd = commands.get(0);
                            
                            //送信用の文字を送信
                            writer.println(cmd);
                            
                            //stopの入力でループを抜ける
                            if (cmd.equals("stop")) {
                                break;
                            }
                            
                            //サーバ側からの受取の結果を表示
                            //System.out.println("result：" + reader.readLine());
                            
                            commands.remove(0);
                        }else{
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                        if (cSocket != null) {
                            cSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Client is stopped!");
                }
            }
        };
    }
    
    public void startClient(){this.task.runTaskAsynchronously(Main.getPlugin());}
    
    public void addCommand(String command){
        commands.add(command);
    }
}
