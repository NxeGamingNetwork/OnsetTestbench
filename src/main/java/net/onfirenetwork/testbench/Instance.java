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
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            join("1234", "Jan");
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
        new Thread(client::start).start();
        return client;
    }

}
