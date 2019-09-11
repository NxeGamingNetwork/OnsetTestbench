package net.onfirenetwork.testbench;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSystem {

    private Map<String, List<LuaFunction>> handlerMap = new HashMap<>();

    public void addEvent(String eventName, LuaFunction handler){
        List<LuaFunction> handlers;
        if(handlerMap.containsKey(eventName)){
            handlers = handlerMap.get(eventName);
        }else{
            handlers = new ArrayList<>();
        }
        handlers.add(handler);
        handlerMap.put(eventName, handlers);
    }

    public void callEvent(String eventName, LuaValue... params){
        if(!handlerMap.containsKey(eventName))
            return;
        List<LuaFunction> handlers = handlerMap.get(eventName);
        for(LuaFunction handler : handlers){
            if(params.length == 0)
                handler.call();
            if(params.length == 1)
                handler.call(params[0]);
            if(params.length == 2)
                handler.call(params[0], params[1]);
            if(params.length == 3)
                handler.call(params[0], params[1], params[2]);
        }
    }

}
