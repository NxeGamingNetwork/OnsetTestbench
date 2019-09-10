package net.onfirenetwork.testbench.client;

import lombok.Getter;
import net.onfirenetwork.testbench.Instance;
import net.onfirenetwork.testbench.LocalEventSystem;
import net.onfirenetwork.testbench.client.webui.WebUIManager;
import org.luaj.vm2.LuaNumber;

import java.util.HashMap;
import java.util.Map;

public class Client {

    @Getter
    private Instance instance;
    private Map<String, ClientPackage> packageMap = new HashMap<>();
    @Getter
    private LocalEventSystem localEventSystem = new LocalEventSystem();
    @Getter
    private WebUIManager webUIManager;

    public Client(Instance instance){
        this.instance = instance;
        for(String packageName : instance.getServerConfig().getPackages()){
            packageMap.put(packageName, new ClientPackage(packageName, this));
        }
        webUIManager = new WebUIManager(this);
    }

    public void start(){
        for(String packageName : instance.getServerConfig().getPackages()){
            packageMap.get(packageName).run();
        }
    }

    public void tick(float delta){
        localEventSystem.callEvent("OnGameTick", LuaNumber.valueOf(delta));
    }

}
