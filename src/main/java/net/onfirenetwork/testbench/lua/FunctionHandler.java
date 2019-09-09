package net.onfirenetwork.testbench.lua;

import org.luaj.vm2.Varargs;

public interface FunctionHandler {
    Varargs call(Varargs args);
}
