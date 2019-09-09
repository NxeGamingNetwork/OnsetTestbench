package net.onfirenetwork.testbench.server;

import lombok.Getter;
import net.onfirenetwork.testbench.Instance;
import net.onfirenetwork.testbench.LocalEventSystem;
import net.onfirenetwork.testbench.server.plugin.MariaDBPlugin;
import net.onfirenetwork.testbench.server.plugin.ServerPlugin;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaValue;

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
    private LocalEventSystem localEventSystem = new LocalEventSystem();

    public Server(Instance instance){
        this.instance = instance;
        if(instance.getConfig().getPlugins().contains("mariadb")){
            plugins.add(new MariaDBPlugin());
        }
        for(String packageName : instance.getConfig().getPackages()){
            packageMap.put(packageName, new ServerPackage(packageName, this));
        }
    }

    public void start(){
        started = true;
        for(ServerPlugin plugin : plugins){
            plugin.enable();
            for(String packageName : instance.getConfig().getPackages()){
                plugin.enablePackage(packageName, packageMap.get(packageName).getEnv());
            }
        }
        for(String packageName : instance.getConfig().getPackages()){
            packageMap.get(packageName).run();
        }
    }

    public void stop(){
        started = false;
        for(ServerPlugin plugin : plugins){
            for(String packageName : instance.getConfig().getPackages()){
                plugin.disablePackage(packageName, packageMap.get(packageName).getEnv());
            }
            plugin.disable();
        }
    }

    public void tick(float delta){
        localEventSystem.callEvent("OnGameTick", LuaNumber.valueOf(delta));
    }

}
