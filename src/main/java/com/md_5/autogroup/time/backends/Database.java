package com.md_5.autogroup.time.backends;

public interface Database {

    String connectionString = "";

    public void init() throws Exception;

    public void load(String player) throws Exception;

    public void add(String player) throws Exception;

    public void update(String player) throws Exception;

    public void save() throws Exception;
}
