package net.onfirenetwork.testbench.client;

import lombok.Getter;
import net.onfirenetwork.testbench.Instance;
import net.onfirenetwork.testbench.EventSystem;
import net.onfirenetwork.testbench.client.player.Player;
import net.onfirenetwork.testbench.client.webui.WebUIManager;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;

import java.util.HashMap;
import java.util.Map;

public class Client {

    @Getter
    private Instance instance;
    private Map<String, ClientPackage> packageMap = new HashMap<>();
    @Getter
    private EventSystem localEventSystem = new EventSystem();
    @Getter
    private EventSystem remoteEventSystem = new EventSystem();
    @Getter
    private WebUIManager webUIManager;
    @Getter
    private Player player;

    public Client(Instance instance, int id, String name){
        this.instance = instance;
        this.player = new Player(this, id, name);
        for(String packageName : instance.getServerConfig().getPackages()){
            packageMap.put(packageName, new ClientPackage(packageName, this));
        }
        webUIManager = new WebUIManager(this);
    }

    public void start(){
        for(String packageName : instance.getServerConfig().getPackages()){
            packageMap.get(packageName).run();
        }
        localEventSystem.callEvent("OnPackageStart");
    }

    public void stop(){
        instance.getServer().getLocalEventSystem().callEvent("OnPlayerQuit", LuaInteger.valueOf(player.getId()));
        instance.getClients().remove(this);
        localEventSystem.callEvent("OnPackageStop");
    }

    public void tick(float delta){
        localEventSystem.callEvent("OnGameTick", LuaNumber.valueOf(delta));
    }

}
