package net.onfirenetwork.testbench;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.onfirenetwork.testbench.client.Client;
import net.onfirenetwork.testbench.config.PackageConfig;
import net.onfirenetwork.testbench.config.ServerConfig;
import net.onfirenetwork.testbench.server.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Instance {

    File serverFolder;
    ServerConfig config;
    Server server;
    List<Client> clients = new ArrayList<>();
    Map<String, PackageConfig> packageConfigs = new HashMap<>();
    boolean exitRequested = false;

    public Instance(File serverFolder){
        this.serverFolder = serverFolder;
        this.config = ServerConfig.load(new File(serverFolder, "server_config.json"));
        for(String packageName : config.getPackages()){
            packageConfigs.put(packageName, PackageConfig.load(new File(new File(getServerFolder(), packageName),"package.json")));
        }
        this.server = new Server(this);
    }

    public void start(){
        new Thread(()->{
            server.start();
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
        Client client = new Client(this);
        clients.add(client);
        return client;
    }

}
