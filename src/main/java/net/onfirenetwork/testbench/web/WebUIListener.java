package net.onfirenetwork.testbench.web;

import org.luaj.vm2.LuaValue;

public interface WebUIListener {
    void onReady();
    void onEvent(String name, LuaValue... params);
    void onClose();
}
