package net.onfirenetwork.testbench.client;

import com.google.gson.JsonParser;
import net.onfirenetwork.testbench.lua.LuaEnv;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.InputStreamReader;

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
            LuaTable varargs = args.arg(2).isnil()?new LuaTable():(LuaTable) args.arg(2);
            if(varargs.length() == 1 && varargs.get(1).istable()){
                varargs = (LuaTable) varargs.get(1);
            }
            LuaValue[] params = new LuaValue[varargs.length()];
            for(int i=1; i<=params.length; i++){
                params[i-1] = varargs.get(i);
            }
            pack.getClient().getLocalEventSystem().callEvent(args.tojstring(1), params);
            return result();
        });
        add("GetPackageName", args -> result(LuaString.valueOf(pack.getName())));
    }

}
