package net.onfirenetwork.testbench.server;

import lombok.Getter;
import net.onfirenetwork.testbench.Instance;
import net.onfirenetwork.testbench.EventSystem;
import net.onfirenetwork.testbench.server.plugin.MariaDBPlugin;
import net.onfirenetwork.testbench.server.plugin.ServerPlugin;
import org.luaj.vm2.LuaNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    @Getter
    private Instance instance;
    private List<ServerPlugin> plugins = new ArrayList<>();
    private Map<String, ServerPackage> packageMap = new HashMap<>();
    @Getter
    private boolean started = false;
    @Getter
    private EventSystem localEventSystem = new EventSystem();
    @Getter
    private EventSystem remoteEventSystem = new EventSystem();

    public Server(Instance instance){
        this.instance = instance;
        if(instance.getServerConfig().getPlugins().contains("mariadb")){
            plugins.add(new MariaDBPlugin());
        }
        for(String packageName : instance.getServerConfig().getPackages()){
            packageMap.put(packageName, new ServerPackage(packageName, this));
        }
    }

    public void start(){
        started = true;
        for(ServerPlugin plugin : plugins){
            plugin.enable();
            for(String packageName : instance.getServerConfig().getPackages()){
                plugin.enablePackage(packageName, packageMap.get(packageName).getEnv());
            }
        }
        for(String packageName : instance.getServerConfig().getPackages()){
            packageMap.get(packageName).run();
        }
        localEventSystem.callEvent("OnPackageStart");
    }

    public void stop(){
        started = false;
        for(ServerPlugin plugin : plugins){
            for(String packageName : instance.getServerConfig().getPackages()){
                plugin.disablePackage(packageName, packageMap.get(packageName).getEnv());
            }
            plugin.disable();
        }
        localEventSystem.callEvent("OnPackageStop");
    }

    public void tick(float delta){
        localEventSystem.callEvent("OnGameTick", LuaNumber.valueOf(delta));
    }

}
