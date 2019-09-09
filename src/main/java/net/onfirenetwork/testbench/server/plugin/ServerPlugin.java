package net.onfirenetwork.testbench.server.plugin;

import net.onfirenetwork.testbench.server.ServerPackageEnv;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;

public interface ServerPlugin {

    void enable();
    void disable();
    void enablePackage(String packageName, ServerPackageEnv env);
    void disablePackage(String packageName, ServerPackageEnv env);

    default Varargs result(LuaValue... values){
        return VarArgFunction.varargsOf(values);
    }

}
