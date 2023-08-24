package org.example;

import org.example.worker.*;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equalsIgnoreCase("start")) {
            new GameSimulation().startSimulation();
        } else {
            System.out.println("Usage: java GameSimulation start");
        }
    }
}