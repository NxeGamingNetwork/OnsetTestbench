package net.onfirenetwork.testbench.server;

import com.google.gson.JsonParser;
import net.onfirenetwork.testbench.lua.LuaEnv;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ServerPackageEnv extends LuaEnv {

    private ServerPackage pack;

    public ServerPackageEnv(ServerPackage pack){
        super(false);
        this.pack = pack;
        addStatic(new JsonParser().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("static/common.json"))).getAsJsonObject());
        addStatic(new JsonParser().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("static/server.json"))).getAsJsonObject());
        add("ServerExit", args -> {
            pack.getServer().getInstance().stop();
            return result();
        });
        add("AddEvent", args -> {
            pack.getServer().getLocalEventSystem().addEvent(args.tojstring(1), (LuaFunction) args.arg(2));
            return result();
        });
        add("CallEvent", args -> {
            List<LuaValue> params = new ArrayList<>();
            for(int i=2; !args.isnil(i); i++){
                params.add(args.arg(i));
            }
            pack.getServer().getLocalEventSystem().callEvent(args.tojstring(1), params.toArray(new LuaValue[0]));
            return result();
        });
        add("GetPackageName", args -> result(LuaString.valueOf(pack.getName())));
    }

}
