package net.onfirenetwork.testbench.lua;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaEnv {

    private Globals globals;
    private boolean sandbox;

    public LuaEnv(boolean sandbox){
        this.sandbox = sandbox;
        globals = JsePlatform.standardGlobals();
        if(sandbox){
            globals.set("env", new LuaTable());
        }
    }

    public void add(String name, FunctionHandler handler){
        VarArgFunction fn = new VarArgFunction() {
            public Varargs invoke(Varargs varargs) {
                return handler.call(varargs);
            }
        };
        if(sandbox){
            LuaTable env = (LuaTable) globals.get("env");
            env.set(name, fn);
        }else{
            globals.set(name, fn);
        }
    }

    public void addStatic(JsonObject source){
        for(String method : source.keySet()){
            JsonArray sourceValues = source.get(method).getAsJsonArray();
            LuaValue[] values = new LuaValue[sourceValues.size()];
            for(int i=0; i<values.length; i++){
                values[i] = convertValue(sourceValues.get(i));
            }
            add(method, args -> VarArgFunction.varargsOf(values));
        }
    }

    private LuaValue convertValue(JsonElement value){
        if(value.isJsonPrimitive()){
            JsonPrimitive pValue = value.getAsJsonPrimitive();
            if(pValue.isString()){
                return LuaString.valueOf(pValue.getAsString());
            }
            if(pValue.isBoolean()){
                return LuaBoolean.valueOf(pValue.getAsBoolean());
            }
            if(pValue.isNumber()){
                return LuaNumber.valueOf(pValue.getAsDouble());
            }
        }
        if(value.isJsonArray()){
            JsonArray aValue = value.getAsJsonArray();
            LuaTable table = new LuaTable();
            for(int i=0; i<aValue.size(); i++){
                table.set(i+1, convertValue(aValue.get(i)));
            }
            return table;
        }
        return LuaNil.NIL;
    }

    public void whitelist(String method){
        LuaTable env = new LuaTable();
        env.set(method, globals.get(method));
        globals.set("env", env);
    }

    public void execute(String code){
        LuaFunction fn;
        if(sandbox){
            fn = (LuaFunction) globals.load(code, "", (LuaTable) globals.get("env"));
        }else{
            fn = (LuaFunction) globals.load(code);
        }
        fn.call();
    }

    protected Varargs result(LuaValue... values){
        return VarArgFunction.varargsOf(values);
    }

}
