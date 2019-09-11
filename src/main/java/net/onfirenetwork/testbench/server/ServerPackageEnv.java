package net.onfirenetwork.testbench.server;

import com.google.gson.JsonParser;
import net.onfirenetwork.testbench.client.Client;
import net.onfirenetwork.testbench.lua.LuaEnv;
import org.luaj.vm2.*;

import java.io.InputStreamReader;

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
        add("AddRemoteEvent", args -> {
            pack.getServer().getRemoteEventSystem().addEvent(args.tojstring(1), (LuaFunction) args.arg(2));
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
        add("CallRemoteEvent", args -> {
            Client client = pack.getServer().getInstance().getClient(args.toint(1));
            if(client == null)
                return result();
            LuaTable varargs = args.arg(3).isnil()?new LuaTable():(LuaTable) args.arg(3);
            if(varargs.length() == 1 && varargs.get(1).istable()){
                varargs = (LuaTable) varargs.get(1);
            }
            LuaValue[] params = new LuaValue[varargs.length()];
            for(int i=1; i<=params.length; i++){
                params[i-1] = varargs.get(i);
            }
            client.getRemoteEventSystem().callEvent(args.tojstring(2), params);
            return result();
        });
        add("GetAllPlayers", args -> {
            LuaTable playerIds = new LuaTable();
            int i = 1;
            for(Client client : pack.getServer().getInstance().getClients()){
                playerIds.set(i, LuaInteger.valueOf(client.getPlayer().getId()));
                i++;
            }
            return result(playerIds);
        });
        add("GetPlayerName", args -> {
            Client client = pack.getServer().getInstance().getClient(args.toint(1));
            if(client == null)
                return result(LuaNil.NIL);
            return result(LuaString.valueOf(client.getPlayer().getName()));
        });
        add("GetPlayerSteamId", args -> {
            Client client = pack.getServer().getInstance().getClient(args.toint(1));
            if(client == null)
                return result(LuaNil.NIL);
            return result(LuaString.valueOf(client.getPlayer().getSteamId()));
        });
        add("GetPackageName", args -> result(LuaString.valueOf(pack.getName())));
    }

}
