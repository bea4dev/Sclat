package be4rjp.sclat;

/**
 *
 * from https://github.com/ldilley/minestat/blob/master/Java/me/dilley/MineStat.java
 */
import java.io.*;
import java.net.*;

public class MineStat
{
    public static final byte NUM_FIELDS = 6;     // expected number of fields returned from server after query
    public static final int DEFAULT_TIMEOUT = 5; // default TCP socket connection timeout in seconds
    
    /**
     * Hostname or IP address of the Minecraft server
     */
    private String address;
    
    /**
     * Port number the Minecraft server accepts connections on
     */
    private int port;
    
    /**
     * TCP socket connection timeout in milliseconds
     */
    private int timeout;
    
    /**
     * Is the server up? (true or false)
     */
    private boolean serverUp;
    
    /**
     * Message of the day from the server
     */
    private String motd;
    
    /**
     * Minecraft version the server is running
     */
    private String version;
    
    /**
     * Current number of players on the server
     */
    private String currentPlayers;
    
    /**
     * Maximum player capacity of the server
     */
    private String maximumPlayers;
    
    /**
     * Ping time to server in milliseconds
     */
    private long latency;
    
    public MineStat(String address, int port)
    {
        this(address, port, DEFAULT_TIMEOUT);
    }
    
    public MineStat(String address, int port, int timeout)
    {
        setAddress(address);
        setPort(port);
        setTimeout(timeout);
        refresh();
    }
    
    /**
     * Refresh state of the server
     * @return <code>true</code>; <code>false</code> if the server is down
     */
    public boolean refresh()
    {
        String[] serverData;
        String rawServerData;
        try
        {
            //Socket clientSocket = new Socket(getAddress(), getPort());
            Socket clientSocket = new Socket();
            long startTime = System.currentTimeMillis();
            clientSocket.connect(new InetSocketAddress(getAddress(), getPort()), timeout);
            setLatency(System.currentTimeMillis() - startTime);
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            byte[] payload = {(byte) 0xFE, (byte) 0x01};
            //dos.writeBytes("\u00FE\u0001");
            dos.write(payload, 0, payload.length);
            rawServerData = br.readLine();
            clientSocket.close();
        }
        catch(Exception e)
        {
            serverUp = false;
            //e.printStackTrace();
            return serverUp;
        }
        
        if(rawServerData == null)
            serverUp = false;
        else
        {
            serverData = rawServerData.split("\u0000\u0000\u0000");
            if(serverData != null && serverData.length >= NUM_FIELDS)
            {
                serverUp = true;
                setVersion(serverData[2].replace("\u0000", ""));
                setMotd(serverData[3].replace("\u0000", ""));
                setCurrentPlayers(serverData[4].replace("\u0000", ""));
                setMaximumPlayers(serverData[5].replace("\u0000", ""));
            }
            else
                serverUp = false;
        }
        return serverUp;
    }
    
    public String getAddress()
    {
        return address;
    }
    
    public void setAddress(String address)
    {
        this.address = address;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public int getTimeout()
    {
        return timeout * 1000;         // milliseconds
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout = timeout * 1000; // milliseconds
    }
    
    public String getMotd()
    {
        return motd;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public String getCurrentPlayers()
    {
        return currentPlayers;
    }
    
    public String getMaximumPlayers()
    {
        return maximumPlayers;
    }
    
    public long getLatency()
    {
        return latency;
    }
    
    public void setLatency(long latency)
    {
        this.latency = latency;
    }
    
    public void setMaximumPlayers(String maximumPlayers)
    {
        this.maximumPlayers = maximumPlayers;
    }
    
    public void setCurrentPlayers(String currentPlayers)
    {
        this.currentPlayers = currentPlayers;
    }
    
    public void setMotd(String motd)
    {
        this.motd = motd;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public boolean isServerUp()
    {
        return serverUp;
    }
}
