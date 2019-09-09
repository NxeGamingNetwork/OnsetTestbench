package net.onfirenetwork.testbench.client;

import com.google.gson.JsonParser;
import net.onfirenetwork.testbench.lua.LuaEnv;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClientPackageEnv extends LuaEnv {

    private ClientPackage pack;

    public ClientPackageEnv(ClientPackage pack){
        super(true);
        this.pack = pack;
        addStatic(new JsonParser().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("static/common.json"))).getAsJsonObject());
        addStatic(new JsonParser().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("static/client.json"))).getAsJsonObject());
        add("AddEvent", args -> {
            pack.getClient().getLocalEventSystem().addEvent(args.tojstring(1), (LuaFunction) args.arg(2));
            return result();
        });
        add("CallEvent", args -> {
            List<LuaValue> params = new ArrayList<>();
            for(int i=2; !args.isnil(i); i++){
                params.add(args.arg(i));
            }
            pack.getClient().getLocalEventSystem().callEvent(args.tojstring(1), params.toArray(new LuaValue[0]));
            return result();
        });
        add("GetPackageName", args -> result(LuaString.valueOf(pack.getName())));
    }

}
