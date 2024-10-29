package com.footballfixture;

public class Launcher {
    public static void main(String[] args) {
        try {
            Main.main(args);
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
