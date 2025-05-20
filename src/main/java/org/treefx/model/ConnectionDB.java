package org.treefx.model;

import org.treefx.model.ziplist.ZipListStrict;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;
import javafx.geometry.Point2D;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class ConnectionDB
{
    private Maybe<Connection> mconnection;

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
                        String positionRAW = rs.getString("position"); // Formato: POINT(longitud latitud)

                        // Extraer coordenadas del POINT
                        String[] coordinates = positionRAW
                                .replace("POINT(", "")
                                .replace(")", "")
                                .split(" ");
                        double x = Double.parseDouble(coordinates[0]);
                        double y = Double.parseDouble(coordinates[1]);

                        nodeInfo = new NodeInfo(id, name, imgURL, new Point2D(x, y), new LinkedList<>());
                    } else System.out.println("node no encontrado con id: " + id + " en la tabla node");
                } catch (Exception e) { System.err.println(e); }


            }
        }

        return nodeInfo;
    }

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

                        // Extraer coordenadas del POINT
                        String[] childPosition = childPositionRAW
                                .replace("POINT(", "")
                                .replace(")", "")
                                .split(" ");
                        double x = Double.parseDouble(childPosition[0]);
                        double y = Double.parseDouble(childPosition[1]);

                        NodeInfo newChild = new NodeInfo(childId, childName, childImgURL, new Point2D(x, y), new LinkedList<>());
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

    public ZipTreeStrict<NodeInfo> getZipTree(int id) {
        ZipTreeStrict<NodeInfo> zipTree = new ZipTreeStrict<>(this.getNodeInfo(id));
        return this.getZipTreeGO(zipTree, id);
    }

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