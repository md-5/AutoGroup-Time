package com.md_5.autogroup;

public class Errors {

    public static void classNotFound() {
        AutoGroup.logger.severe("SQLite Driver not found!");
    }

    public static void SQLException() {
        AutoGroup.logger.severe("AutoGroup: Something went wrong with the database. Please try restarting, otherwise enable debug in the config.");
    }
}