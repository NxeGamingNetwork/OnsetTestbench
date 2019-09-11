package net.onfirenetwork.testbench.client;

import com.google.gson.JsonParser;
import net.onfirenetwork.testbench.client.webui.WebUI;
import net.onfirenetwork.testbench.lua.LuaEnv;
import org.luaj.vm2.*;

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
        add("AddRemoteEvent", args -> {
            pack.getClient().getRemoteEventSystem().addEvent(args.tojstring(1), (LuaFunction) args.arg(2));
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
        add("CallRemoteEvent", args -> {
            LuaTable varargs = args.arg(2).isnil()?new LuaTable():(LuaTable) args.arg(2);
            if(varargs.length() == 1 && varargs.get(1).istable()){
                varargs = (LuaTable) varargs.get(1);
            }
            LuaValue[] params = new LuaValue[varargs.length()+1];
            params[0] = LuaInteger.valueOf(pack.getClient().getPlayer().getId());
            for(int i=2; i<=params.length; i++){
                params[i-1] = varargs.get(i);
            }
            pack.getClient().getInstance().getServer().getRemoteEventSystem().callEvent(args.tojstring(1), params);
            return result();
        });
        add("GetPackageName", args -> result(LuaString.valueOf(pack.getName())));
        add("CreateWebUI", args -> {
            WebUI ui = pack.getClient().getWebUIManager().createWebUI(
                    args.toint(1),
                    args.toint(2),
                    args.toint(3),
                    args.toint(4)
            );
            return result(LuaValue.valueOf(ui.getId()));
        });
        add("DestroyWebUI", args -> {
            WebUI ui = pack.getClient().getWebUIManager().getWebUI(args.toint(1));
            if(ui != null)
                ui.destroy();
            return result();
        });
        add("SetWebSize", args -> {
            WebUI ui = pack.getClient().getWebUIManager().getWebUI(args.toint(1));
            if(ui != null)
                ui.setSize(args.toint(2), args.toint(3));
            return result();
        });
        add("SetWebLocation", args -> {
            WebUI ui = pack.getClient().getWebUIManager().getWebUI(args.toint(1));
            if(ui != null)
                ui.setLocation(args.toint(2), args.toint(3));
            return result();
        });
        add("LoadWebFile", args -> {
            WebUI ui = pack.getClient().getWebUIManager().getWebUI(args.toint(1));
            if(ui != null)
                ui.loadFile(args.tojstring(2));
            return result();
        });
        add("ExecuteWebJS", args -> {
            WebUI ui = pack.getClient().getWebUIManager().getWebUI(args.toint(1));
            if(ui != null)
                ui.execute(args.tojstring(2));
            return result();
        });
        add("GetPlayerId", args -> result(LuaInteger.valueOf(pack.getClient().getPlayer().getId())));
        whitelist("print");
        whitelist("setmetatable");
        whitelist("getmetatable");
    }

}
