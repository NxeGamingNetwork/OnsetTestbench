package net.onfirenetwork.testbench.web;

import com.google.gson.*;
import lombok.Getter;
import net.onfirenetwork.testbench.Instance;
import net.onfirenetwork.testbench.web.command.WebUICommand;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import spark.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;

@WebSocket
public class WebUIEmulator {

    private Instance instance;
    @Getter
    private int port;
    private WebUIListener listener;
    private Service service;
    private Session session = null;

    public WebUIEmulator(Instance instance, int port, WebUIListener listener) {
        this.instance = instance;
        this.port = port;
        this.listener = listener;
    }

    public void start(){
        service = Service.ignite();
        service.staticFileLocation("webui");
        service.webSocket("/socket", this);
        service.port(port);
        for (String packageName : instance.getServerConfig().getPackages()) {
            File packageFolder = new File(instance.getServerFolder(), packageName);
            for (String fileName : instance.getPackageConfigs().get(packageName).getFiles()) {
                File file = new File(packageFolder, fileName);
                boolean source = isSource(fileName);
                service.get("/asset/" + packageName + "/" + fileName, (request, response) -> loadAsset(file, source));
            }
        }
        service.redirect.get("/", "/index.html");
        service.init();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String json) {
        Gson gson = new Gson();
        JsonObject eventJson = gson.fromJson(json, JsonObject.class);
        String eventName = eventJson.get("name").getAsString();
        if (eventName.equals("callevent")) {
            JsonArray paramsJson = eventJson.get("data").getAsJsonObject().get("params").getAsJsonArray();
            LuaValue[] params = new LuaValue[paramsJson.size()];
            for (int i = 0; i < params.length; i++) {
                params[i] = luaFromJson(paramsJson.get(i));
            }
            listener.onEvent(eventJson.get("data").getAsJsonObject().get("name").getAsString(), params);
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session){
        if(this.session != null){
            try {
                session.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        this.session = session;
        listener.onReady();
    }

    private LuaValue luaFromJson(JsonElement json){
        if(json.isJsonPrimitive()){
            JsonPrimitive jsonP = json.getAsJsonPrimitive();
            if(jsonP.isNumber()){
                return LuaNumber.valueOf(jsonP.getAsDouble());
            }
            if(jsonP.isBoolean()){
                return LuaBoolean.valueOf(jsonP.getAsBoolean());
            }
            if(jsonP.isString()){
                return LuaString.valueOf(jsonP.getAsString());
            }
        }
        return LuaValue.NIL;
    }

    public void sendCommand(WebUICommand event){
        if(session == null)
            return;
        JsonObject json = new JsonObject();
        Gson gson = new Gson();
        json.addProperty("name", event.getName());
        json.add("data", gson.toJsonTree(event));
        try {
            session.getRemote().sendString(gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isSource(String fileName){
        if(fileName.endsWith(".js"))
            return true;
        if(fileName.endsWith(".html"))
            return true;
        if(fileName.endsWith(".htm"))
            return true;
        if(fileName.endsWith(".css"))
            return true;
        return false;
    }

    private byte[] loadAsset(File file, boolean replaceAssetPath){
        try {
            return loadAsset(new FileInputStream(file), replaceAssetPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] loadAsset(InputStream stream, boolean replaceAssetPath){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while (stream.available() > 0){
                byte[] data = new byte[Math.min(stream.available(), 4096)];
                stream.read(data);
                baos.write(data);
            }
            stream.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        if(!replaceAssetPath)
            return baos.toByteArray();
        String asset = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        return asset.replace("http://asset/", "http://localhost:"+service.port()+"/asset/").getBytes(StandardCharsets.UTF_8);
    }

    public void stop() {
        service.stop();
    }

}
