package br.ufg.inf.ubicare.ubiloc.domain;

import com.orm.SugarRecord;

/**
 * Created by anapaula on 12/09/17.
 */

public class User extends SugarRecord<User> {
    private String key;

    public User() {}

    public User(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
