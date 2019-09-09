package net.onfirenetwork.testbench.server;

import com.google.gson.JsonParser;
import net.onfirenetwork.testbench.lua.LuaEnv;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
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
            LuaTable varargs = args.arg(2).isnil()?new LuaTable():(LuaTable) args.arg(2);
            if(varargs.length() == 1 && varargs.get(1).istable()){
                varargs = (LuaTable) varargs.get(1);
            }
            LuaValue[] params = new LuaValue[varargs.length()];
            for(int i=1; i<=params.length; i++){
                params[i-1] = varargs.get(i);
            }
            pack.getServer().getLocalEventSystem().callEvent(args.tojstring(1), params);
            return result();
        });
        add("GetPackageName", args -> result(LuaString.valueOf(pack.getName())));
    }

}
