package net.onfirenetwork.testbench.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.onfirenetwork.testbench.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class InstanceConfig {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SerializedName("server_folder")
    String serverFolder = ".";
    @SerializedName("ui_enabled")
    boolean uiEnabled = false;
    @SerializedName("ui_port")
    int uiPort = 2255;

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

    public static InstanceConfig load(File file){
        if(!file.exists())
            return new InstanceConfig();
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
        return gson.fromJson(sb.toString(), InstanceConfig.class);
    }

}
