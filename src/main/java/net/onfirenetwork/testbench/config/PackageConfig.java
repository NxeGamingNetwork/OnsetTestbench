package net.onfirenetwork.testbench.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class PackageConfig {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String author;
    String version;
    @SerializedName("server_scripts")
    List<String> serverScripts;
    @SerializedName("client_scripts")
    List<String> clientScripts;
    List<String> files;

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

    public static PackageConfig load(File file){
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
        return gson.fromJson(sb.toString(), PackageConfig.class);
    }

}
