package org.treefx.model;

import org.treefx.utils.adt.Maybe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB
{
    private Maybe<Connection> mconnection;

    public boolean success() {
        return this.mconnection.isJust();
    }

    public ConnectionDB(String host, String port, String user, String pass, String bd) {
        String thost = "sql7.freesqldatabase.com";
        String tport = "3306";
        String tuser = "sql7776456";
        String tpass = "KnGGaAQpPR";
        String tbd = "sql7776456";
        String turl = "jdbc:mysql://" + thost + ":" + tport + "/" + tbd + "?useSSL=false&serverTimezone=UTC";
        try {
            // Cargar el driver JDBC (opcional con JDBC 4.0+ pero recomendado)
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.mconnection = new Maybe.Just<>(DriverManager.getConnection(turl, tuser, tpass));
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
            this.mconnection = new Maybe.Nothing<>();
        }
    }
}