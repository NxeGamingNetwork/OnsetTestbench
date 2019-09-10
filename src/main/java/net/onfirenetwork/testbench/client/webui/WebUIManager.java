package net.onfirenetwork.testbench.client.webui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.onfirenetwork.testbench.client.Client;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class WebUIManager {

    Client client;
    List<WebUI> webUIs = new ArrayList<>();
    int nextId = 1;

    public WebUIManager(Client client){
        this.client = client;
    }

    public WebUI createWebUI(int x, int y, int width, int height){
        int id = nextId;
        nextId++;
        WebUI webUI = new WebUI(this, id);
        webUIs.add(webUI);
        webUI.create(x, y, width, height);
        return webUI;
    }

    public WebUI getWebUI(int id){
        for(WebUI ui : getWebUIs()){
            if(ui.getId() == id)
                return ui;
        }
        return null;
    }

}
