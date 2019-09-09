package net.onfirenetwork.testbench;

import java.io.File;

public class OnsetTestbench {

    public static void main(String[] args){
        File folder = new File(".");
        if(args.length > 0){
            folder = new File(String.join(" ", args));
        }
        if(!(new File(folder, "server_config.json")).exists()){
            System.out.println("No server_config.json found!");
            return;
        }
        Instance instance = new Instance(folder);
        instance.start();
    }

}
