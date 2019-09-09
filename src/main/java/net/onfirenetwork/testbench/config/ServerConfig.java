package net.onfirenetwork.testbench.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ServerConfig {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SerializedName("servername")
    String serverName;
    @SerializedName("gamemode")
    String gameMode;
    @SerializedName("website_url")
    String websiteUrl;
    @SerializedName("ipaddress")
    String ipAddress;
    int port;
    @SerializedName("maxplayers")
    int maxPlayers;
    String password;
    int timeout;
    @SerializedName("iplimit")
    int ipLimit;
    @SerializedName("masterserver")
    boolean masterServer;
    List<String> plugins;
    List<String> packages;
    @SerializedName("stream_distance")
    float streamDistance;
    @SerializedName("stream_update_rate")
    float streamUpdateRate;
    boolean voice;
    @SerializedName("voice_sample_rate")
    int voiceSampleRate;
    @SerializedName("voice_distance")
    float voiceDistance;
    @SerializedName("voice_spatialization")
    boolean voiceSpatialization;

    public void save(File file){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(gson.toJson(this).getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerConfig load(File file){
        if(!file.exists())
            return null;
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            while(fis.available() > 0){
                byte[] data = new byte[Math.min(fis.available(),4096)];
                fis.read(data);
                sb.append(new String(data, StandardCharsets.UTF_8));
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(sb.toString(), ServerConfig.class);
    }

}
