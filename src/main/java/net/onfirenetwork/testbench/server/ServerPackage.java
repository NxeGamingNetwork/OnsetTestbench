package net.onfirenetwork.testbench.server;

import lombok.Getter;
import net.onfirenetwork.testbench.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
public class ServerPackage {

    private String name;
    private Server server;
    private ServerPackageEnv env;

    public ServerPackage(String name, Server server){
        this.name = name;
        this.server = server;
        this.env = new ServerPackageEnv(this);
    }

    public void run(){
        for(String fileName : server.getInstance().getPackageConfigs().get(name).getServerScripts()){
            env.execute(loadFile(fileName));
        }
    }

    private File getPackageFolder(){
        return new File(server.getInstance().getServerFolder(), name);
    }

    private String loadFile(String fileName){
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(new File(getPackageFolder(), fileName));
            while (fis.available() > 0){
                byte[] data = new byte[Math.min(fis.available(), 4096)];
                fis.read(data);
                sb.append(new String(data, StandardCharsets.UTF_8));
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
