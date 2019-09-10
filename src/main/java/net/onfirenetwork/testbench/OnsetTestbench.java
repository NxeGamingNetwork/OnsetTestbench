package net.onfirenetwork.testbench;

import java.io.File;

public class OnsetTestbench {

    public static void main(String[] args){
        Instance instance = new Instance(new File("bench.json"));
        instance.start();
    }

}
