package net.onfirenetwork.testbench;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.onfirenetwork.testbench.client.Client;
import net.onfirenetwork.testbench.config.InstanceConfig;
import net.onfirenetwork.testbench.config.PackageConfig;
import net.onfirenetwork.testbench.config.ServerConfig;
import net.onfirenetwork.testbench.server.Server;
import net.onfirenetwork.testbench.web.WebUIEmulator;
import net.onfirenetwork.testbench.web.WebUIListener;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Instance {

    File serverFolder;
    InstanceConfig config;
    ServerConfig serverConfig;
    Server server;
    List<Client> clients = new ArrayList<>();
    Map<String, PackageConfig> packageConfigs = new HashMap<>();
    boolean exitRequested = false;
    @Setter
    WebUIEmulator emulator = null;
    int nextPlayerId = 1;
    boolean connectionRequestCancelled = false;

    public Instance(File configFile){
        config = InstanceConfig.load(configFile);
        serverFolder = new File(config.getServerFolder());
        this.serverConfig = ServerConfig.load(new File(serverFolder, "server_config.json"));
        for(String packageName : serverConfig.getPackages()){
            packageConfigs.put(packageName, PackageConfig.load(new File(new File(getServerFolder(), packageName),"package.json")));
        }
        this.server = new Server(this);
        if(config.isUiEnabled()){
            emulator = new WebUIEmulator(this, config.getUiPort(), new WebUIListener() {
                public void onReady() {
                    internalStart();
                }
                public void onClose() {
                    Client client = getClient(1);
                    if(client == null)
                        return;
                }
                public void onEvent(String name, LuaValue... params) {
                    if(getClients().size() > 0){
                        getClients().get(0).getLocalEventSystem().callEvent(name, params);
                    }
                }
            });
        }
    }

    public void start(){
        if(emulator == null){
            internalStart();
            return;
        }
        System.out.println("Navigate to \"http://localhost:"+emulator.getPort()+"/index.html\" to start the server!");
        emulator.start();
    }

    private void internalStart(){
        new Thread(()->{
            server.start();
            for(InstanceConfig.ConfigClient configClient : config.getClients()){
                join(configClient.getSteamId(), configClient.getName());
            }
            long startTime = 0;
            while (!exitRequested){
                float delta = ((float)(System.currentTimeMillis() - startTime))/1000f;
                startTime = System.currentTimeMillis();
                server.tick(delta);
                for(Client client : clients){
                    client.tick(delta);
                }
                try {
                    Thread.sleep(16-(System.currentTimeMillis()-startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop(){
        exitRequested = true;
        server.stop();
    }

    public Client join(String steamId, String username){
        if(!server.isStarted())
            return null;
        server.getLocalEventSystem().callEvent("OnClientConnectionRequest", LuaString.valueOf("127.0.0.1"), LuaInteger.valueOf(30000+(new Random()).nextInt(10000)));
        if(connectionRequestCancelled){
            connectionRequestCancelled = false;
            return null;
        }
        int id = nextPlayerId;
        nextPlayerId++;
        Client client = new Client(this, id, username);
        clients.add(client);
        server.getLocalEventSystem().callEvent("OnPlayerServerAuth", LuaInteger.valueOf(id));
        client.getPlayer().auth(steamId);
        server.getLocalEventSystem().callEvent("OnPlayerSteamAuth", LuaInteger.valueOf(id));
        new Thread(client::start).start();
        server.getLocalEventSystem().callEvent("OnPlayerJoin", LuaInteger.valueOf(id));
        return client;
    }

    public Client getClient(int id){
        for(Client client : clients){
            if(client.getPlayer().getId() == id)
                return client;
        }
        return null;
    }

}
