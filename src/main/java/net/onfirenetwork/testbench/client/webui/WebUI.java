package net.onfirenetwork.testbench.client.webui;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.onfirenetwork.testbench.web.command.*;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebUI {

    WebUIManager manager;
    int id;

    public void create(int x, int y, int width, int height){
        if(manager.getClient().getInstance().getEmulator() == null)
            return;
        if(manager.getClient().getPlayer().getId() != 1)
            return;
        manager.getClient().getInstance().getEmulator().sendCommand(new WebUICreateCommand(id, x, y, width, height));
    }

    public void loadFile(String url){
        if(manager.getClient().getInstance().getEmulator() == null)
            return;
        if(manager.getClient().getPlayer().getId() != 1)
            return;
        manager.getClient().getInstance().getEmulator().sendCommand(new WebUILoadCommand(id, url));
    }

    public void execute(String code){
        if(manager.getClient().getInstance().getEmulator() == null)
            return;
        if(manager.getClient().getPlayer().getId() != 1)
            return;
        manager.getClient().getInstance().getEmulator().sendCommand(new WebUIExecuteCommand(id, code));
    }

    public void destroy(){
        manager.getWebUIs().remove(this);
        if(manager.getClient().getInstance().getEmulator() == null)
            return;
        if(manager.getClient().getPlayer().getId() != 1)
            return;
        manager.getClient().getInstance().getEmulator().sendCommand(new WebUIDestroyCommand(id));
    }

    public void setSize(int width, int height){
        if(manager.getClient().getInstance().getEmulator() == null)
            return;
        if(manager.getClient().getPlayer().getId() != 1)
            return;
        manager.getClient().getInstance().getEmulator().sendCommand(new WebUISizeCommand(id, width, height));
    }

    public void setLocation(int x, int y){
        if(manager.getClient().getInstance().getEmulator() == null)
            return;
        if(manager.getClient().getPlayer().getId() != 1)
            return;
        manager.getClient().getInstance().getEmulator().sendCommand(new WebUILocationCommand(id, x, y));
    }

}
