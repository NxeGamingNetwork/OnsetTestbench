package net.onfirenetwork.testbench.client;

import lombok.Getter;
import net.onfirenetwork.testbench.Instance;
import net.onfirenetwork.testbench.LocalEventSystem;
import org.luaj.vm2.LuaNumber;

import java.util.HashMap;
import java.util.Map;

public class Client {

    @Getter
    private Instance instance;
    private Map<String, ClientPackage> packageMap = new HashMap<>();
    @Getter
    private LocalEventSystem localEventSystem = new LocalEventSystem();

    public Client(Instance instance){
        this.instance = instance;
    }

    public void tick(float delta){
        localEventSystem.callEvent("OnGameTick", LuaNumber.valueOf(delta));
    }

}
