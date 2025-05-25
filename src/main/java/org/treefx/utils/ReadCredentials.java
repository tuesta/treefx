package org.treefx.utils;

import org.treefx.utils.adt.T;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ReadCredentials {

    /**
     * Lee las credenciales de un archivo denominado "DATABASE_CREDENTIALS".
     * El archivo debe contener dos líneas en formato:
     * <pre>
     * USER=nombre_de_usuario
     * PASSWORD=contraseña
     * </pre>
     *
     * @return una instancia de {@link T} que contiene el nombre de usuario y la contraseña en formato de texto.
     * @throws Exception si ocurre un error al leer el archivo o si el formato no es válido.
     */
    public static T<String, String> read() throws Exception {
        try (BufferedReader br = Files.newBufferedReader(Path.of("DATABASE_CREDENTIALS"))) {
            String usuarioLn = br.readLine();
            String passwordLn = br.readLine();
            if (usuarioLn.startsWith("USER=") && passwordLn.startsWith("PASSWORD=")) {
                String user = usuarioLn.replace("USER=", "");
                String password = passwordLn.replace("PASSWORD=", "");
                return new T.MkT<>(user, password);
            } else throw new Exception("DATABASE_CREDENTIALS file format is invalid");
        }
    }
}
