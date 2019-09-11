package net.onfirenetwork.testbench.client.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.onfirenetwork.testbench.client.Client;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Player {

    Client client;
    int id;
    String steamId = null;
    String name;

    public Player(Client client, int id, String name){
        this.client = client;
        this.id = id;
        this.name = name;
    }

    public void auth(String steamId){
        this.steamId = steamId;
    }

}
