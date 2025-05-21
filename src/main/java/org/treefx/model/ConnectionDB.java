package org.treefx.model;

import org.treefx.utils.adt.Maybe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que gestiona la conexión a una base de datos MySQL.
 * Utiliza el patrón Maybe para manejar la posibilidad de una conexión fallida.
 */
public class ConnectionDB
{
    /** Conexión a la base de datos envuelta en un Maybe para manejo seguro */
    private Maybe<Connection> mconnection;

    /**
     * Verifica si la conexión a la base de datos se estableció exitosamente.
     *
     * @return true si la conexión está establecida, false en caso contrario
     */
    public boolean success() {
        return this.mconnection.isJust();
    }

    /**
     * Constructor que establece una conexión a la base de datos MySQL.
     * Si la conexión falla, se almacena un Maybe.Nothing.
     *
     * @param host dirección del servidor de base de datos
     * @param port puerto de conexión
     * @param user nombre de usuario
     * @param pass contraseña
     * @param bd nombre de la base de datos
     */
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