package net.onfirenetwork.testbench.client;

import lombok.Getter;

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

}
