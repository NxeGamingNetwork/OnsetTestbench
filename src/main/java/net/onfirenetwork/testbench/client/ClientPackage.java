package net.onfirenetwork.testbench.client;

import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
public class ClientPackage {

    private String name;
    private Client client;
    private ClientPackageEnv env;

    public ClientPackage(String name, Client client){
        this.name = name;
        this.client = client;
        this.env = new ClientPackageEnv(this);
    }

    public void run(){
        for(String fileName : client.getInstance().getPackageConfigs().get(name).getClientScripts()){
            env.execute(loadFile(fileName));
        }
    }

    private File getPackageFolder(){
        return new File(client.getInstance().getServerFolder(), name);
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
