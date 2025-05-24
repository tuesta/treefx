package org.treefx.model;

import org.treefx.model.ziplist.ZipListStrict;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.Movement;
import org.treefx.utils.adt.T;
import javafx.geometry.Point2D;

import java.sql.*;
import java.util.LinkedList;

/**
 * Maneja la conexión con la base de datos y proporciona métodos para interactuar con las tablas y datos.
 */
public class ConnectionDB {
    private Maybe<Connection> mconnection;

    public boolean success() {
        return mconnection.isJust();
    }

    /**
     * Convierte una cadena en formato SQL POINT a un objeto Point2D.
     *
     * @param positionRAW la representación en formato POINT (longitud, latitud) como cadena.
     * @return un objeto Point2D correspondiente a las coordenadas del punto.
     */
    public Point2D toPoint2D(String positionRAW) {
        // Formato: POINT(longitud latitud)
        String[] coordinates = positionRAW
                .replace("POINT(", "")
                .replace(")", "")
                .split(" ");
        double x = Double.parseDouble(coordinates[0]);
        double y = Double.parseDouble(coordinates[1]);
        return new Point2D(x, y);
    }

    /**
     * Obtiene todos los nodos raíz de la base de datos.
     *
     * @return una lista enlazada con las raíces, cada una representada por su ID y nombre.
     */
    public LinkedList<T<Integer, String>> getAllRoots() {
        var roots = new LinkedList<T<Integer, String>>();

        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var statement = connection.createStatement();
                     var resultSet = statement.executeQuery("SELECT node_id, name FROM roots")
                    )
                {
                    while (resultSet.next())
                        roots.add(new T.MkT<>(resultSet.getInt("node_id"), resultSet.getString("name")));
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        }

        return roots;
    }

    /**
     * Inserta un nodo raíz en la base de datos.
     *
     * @param name el nombre del nodo raíz que se desea insertar.
     * @return el ID del nodo raíz recién insertado, o -1 si ocurre un error.
     */
    public int insertRoot(String name) {
        int id = -1;

        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var callableStatement = connection.prepareCall("{call InsertRootNode(?, ?, GeomFromText(?), ?, ?)}"))
                {
                    callableStatement.setString("node_name", "");
                    callableStatement.setString("node_imgURL", "");
                    callableStatement.setString("node_position", "Point("+ "100 100" +")");
                    callableStatement.setString("root_name", name);
                    callableStatement.registerOutParameter("new_root_id", java.sql.Types.INTEGER);
                    callableStatement.execute();

                    id = callableStatement.getInt("new_root_id");
                } catch (SQLException e) { System.err.println(e); }
            }
        }

        return id;
    }

    /**
     * Elimina un nodo raíz de la base de datos.
     *
     * @param root_id el ID del nodo raíz que se desea eliminar.
     * @return -1 si ocurre un error.
     */
    public int removeRoot(int root_id) {
        int id = -1;

        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var pstmt = connection.prepareStatement("DELETE FROM roots WHERE node_id = ?"))
                {
                    pstmt.setInt(1, root_id);
                    pstmt.executeUpdate();
                } catch (SQLException e) { System.err.println("error: " + e); }
            }
        }

        return id;
    }

    /**
     * Inserta un nodo hijo en la base de datos.
     *
     * @param position la posición del nodo hija en formato Point2D.
     * @param parentId el ID del nodo padre.
     * @return el ID del nodo hijo recién insertado, o -1 si ocurre un error.
     */
    public int insertChild(Point2D position, int parentId) {
        int id = -1;

        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var callableStatement = connection.prepareCall("{call InsertChildNode(?, ?, GeomFromText(?), ?, ?)}"))
                {
                    callableStatement.setString("node_name", "");
                    callableStatement.setString("node_imgURL", "");
                    callableStatement.setString("node_position", "Point("+ position.getX() + " " + position.getY() +")");
                    callableStatement.setInt("parent_id", parentId);
                    callableStatement.registerOutParameter("new_node_id", java.sql.Types.INTEGER);
                    callableStatement.execute();

                    id = callableStatement.getInt("new_node_id");
                } catch (SQLException e) { System.err.println(e); }
            }
        }

        return id;
    }

    /**
     * Actualiza la información de la posición de un nodo en la base de datos.
     *
     * @param id       el ID del nodo que se desea actualizar.
     * @param position la nueva posición del nodo en formato Point2D.
     */
    public void updateNodeInfo(int id, Point2D position) {
        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var pstmt = connection.prepareStatement("UPDATE node SET position = GeomFromText(?) WHERE id = ?"))
                {
                    pstmt.setString(1, "Point("+ position.getX() + " " + position.getY() +")");
                    pstmt.setInt(2, id);
                    pstmt.executeUpdate();
                } catch (SQLException e) { System.err.println(e); }
            }
        }
    }

    /**
     * Actualiza el nombre y la URL de la imagen de un nodo en la base de datos.
     *
     * @param id       el ID del nodo que se desea actualizar.
     * @param name     el nuevo nombre del nodo.
     * @param imageURL la nueva URL de la imagen del nodo.
     */
    public void updateNodeInfo(int id, String name, String imageURL) {
        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var pstmt = connection.prepareStatement("""
                UPDATE node
                SET
                    name = ?,
                    imgURL = ?
                WHERE id = ?
                """))
                {
                    pstmt.setString(1, name);
                    pstmt.setString(2, imageURL);
                    pstmt.setInt(3, id);
                    pstmt.executeUpdate();
                } catch (SQLException e) { System.err.println(e); }
            }
        }
    }

    /**
     * Obtiene la información de un nodo a partir de su ID.
     *
     * @param id el ID del nodo cuyo información se desea obtener.
     * @return un objeto NodeInfo conteniendo los datos del nodo, o null si no se encuentra.
     */
    public NodeInfo getNodeInfo(int id) {
        NodeInfo nodeInfo = null;

        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var pstmt = connection.prepareStatement("""
                    SELECT id, name, imgURL, AsText(position) AS position FROM node
                    WHERE id = ?
                    """))
                {
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String imgURL = rs.getString("imgURL");
                        String positionRAW = rs.getString("position");

                        nodeInfo = new NodeInfo(id, name, imgURL, toPoint2D(positionRAW), getChildrenMoves(id));
                    } else System.out.println("node no encontrado con id: " + id + " en la tabla node");
                } catch (Exception e) { System.err.println(e); }


            }
        }

        return nodeInfo;
    }

    /**
     * Inserta un conjunto de movimientos espaciales para un nodo específico.
     *
     * @param node_id         el ID del nodo al cual se le agregan movimientos espaciales.
     * @param movementInSpace un objeto que contiene los movimientos espaciales y la posición.
     */
    public void insertMovementInSpace(int node_id, MovementInSpace movementInSpace) {
        var movements = movementInSpace.getMovements();
        var pos = movementInSpace.getPos();

        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var pstmt = connection.prepareStatement("""
                    INSERT INTO node_positions (position, node_id, movements)
                    VALUES (GeomFromText(?), ?, ?)
                    """))
                {
                    pstmt.setString(1, "Point("+ pos.getX() + " " + pos.getY() +")");
                    pstmt.setInt(2, node_id);

                    String movementsRAW = "";
                    for (Movement movement : movements) movementsRAW += Movement.show(movement) + " ";
                    if (!movementsRAW.isEmpty()) movementsRAW.trim();

                    pstmt.setString(3, movementsRAW);
                    pstmt.executeUpdate();
                } catch (SQLException e) { throw new RuntimeException(e); }
            }
        }
    }

    /**
     * Obtiene los movimientos espaciales asociados a un nodo hijo específico.
     *
     * @param id el ID del nodo hijo para el cual se obtienen los movimientos.
     * @return una lista enlazada de los movimientos espaciales.
     */
    public LinkedList<MovementInSpace> getChildrenMoves(int id) {
        LinkedList<MovementInSpace> movementsInSpace = new LinkedList<>();

        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var pstmt = connection.prepareStatement(
                        "SELECT AsText(position) as position, movements, node_id FROM node_positions WHERE node_id = ?"))
                {
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {
                        String positionRAW = rs.getString("position");
                        String movementsRAW = rs.getString("movements");

                        String[] movementsStr = movementsRAW.split(" ");
                        LinkedList<Movement> movements = new LinkedList<>();

                        for (String movementStr : movementsStr) {
                            movements.add(Movement.read(movementStr));
                        }

                        MovementInSpace movementInSpace = new MovementInSpace(toPoint2D(positionRAW), movements);
                        movementsInSpace.add(movementInSpace);
                    }

                } catch (Exception e) { System.err.println(e); }
            }
        }
        return movementsInSpace;
    }

    /**
     * Rellena un ZipTree con información jerárquica de un nodo y sus hijos.
     *
     * @param zipTree el árbol ZipTree que se completará con nodos.
     * @param id      el ID del nodo raíz del árbol jerárquico.
     * @return el árbol ZipTree completamente rellenado.
     */
    public ZipTreeStrict<NodeInfo> getZipTreeGO(ZipTreeStrict<NodeInfo> zipTree, int id) {
        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try (var pstmt = connection.prepareStatement("""
                                SELECT
                                    child.id AS child_id,
                                    child.name AS child_name,
                                    child.imgURL AS child_imgURL,
                                    AsText(child.position) AS child_position
                                FROM node_hierarchy AS hierarchy
                                INNER JOIN node AS child ON hierarchy.child_node_id = child.id
                                WHERE hierarchy.parent_node_id = ?
                                ORDER BY child.id;
                                """))
                {
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        int childId = rs.getInt("child_id");
                        String childName = rs.getString("child_name");
                        String childImgURL = rs.getString("child_imgURL");
                        String childPositionRAW = rs.getString("child_position");

                        NodeInfo newChild = new NodeInfo(childId, childName, childImgURL, toPoint2D(childPositionRAW), getChildrenMoves(childId));
                        zipTree.insertChild(newChild);
                    }
                } catch (SQLException e) { System.err.println(e); }

                ZipListStrict<T<NodeInfo, TreeCtxStrict<NodeInfo>>> children = zipTree.getCtx().getChildren();
                children.mapM(t -> {
                    var childIx = t.fst().getId();
                    zipTree.setCtx(t.snd());
                    getZipTreeGO(zipTree, childIx);
                    zipTree.toFather();
                });
            }
        }

        return zipTree;
    }

    /**
     * Obtiene un árbol jerárquico ZipTree a partir de un nodo raíz específico.
     *
     * @param id el ID del nodo raíz del árbol ZipTree.
     * @return el árbol ZipTree generado.
     */
    public ZipTreeStrict<NodeInfo> getZipTree(int id) {
        ZipTreeStrict<NodeInfo> zipTree = new ZipTreeStrict<>(this.getNodeInfo(id));
        return this.getZipTreeGO(zipTree, id);
    }

    /**
     * Cierra la conexión con la base de datos si está establecida.
     */
    public void close() {
        switch (mconnection) {
            case Maybe.Nothing() -> System.out.println("Conexion no establecida");
            case Maybe.Just(Connection connection) -> {
                try { connection.close(); } catch (SQLException e) { System.err.println(e); }
            }
        }
    }

    /**
     * Constructor que inicializa la conexión con la base de datos dado un conjunto de parámetros.
     *
     * @param host el host del servidor de base de datos.
     * @param port el puerto del servidor de base de datos.
     * @param user el nombre de usuario para la conexión.
     * @param pass la contraseña para la conexión.
     * @param bd   el nombre de la base de datos.
     */
    public ConnectionDB(String host, String port, String user, String pass, String bd) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + bd + "?useSSL=false&serverTimezone=UTC";
        try {
            // Cargar el driver JDBC (opcional con JDBC 4.0+ pero recomendado)
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.mconnection = new Maybe.Just<>(DriverManager.getConnection(url, user, pass));
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
            this.mconnection = new Maybe.Nothing<>();
        }
    }
}