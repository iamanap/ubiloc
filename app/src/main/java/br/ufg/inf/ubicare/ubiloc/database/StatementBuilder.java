package br.ufg.inf.ubicare.ubiloc.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

public class StatementBuilder {
    private String sql;
    private SQLiteDatabase database;
    private SQLiteStatement statement;
    private static StatementBuilder instance;

    private StatementBuilder(SQLiteDatabase database) {
        this.sql = "";
        this.database = database;
    }

    public static StatementBuilder from(@NonNull SQLiteDatabase database) {
        if (instance == null) {
            instance = new StatementBuilder(database);
        }
        return instance;
    }

    public SQLiteStatement build(@NonNull String sql) {
        if (!this.sql.equals(sql)) {
            this.sql = sql;
            this.statement = database.compileStatement(sql);
        }
        return statement;
    }
}

