package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;

import java.util.function.Consumer;

/**
 * La clase ZipListStrict es una implementación de una lista enlazada doblemente navegable.
 * Permite recorrer sus nodos en ambas direcciones (anterior y posterior) y proporciona métodos
 * para agregar, eliminar, y modificar elementos de manera eficiente. Este enfoque utiliza la
 * estructura Maybe para manejar valores nulos y controlar el flujo de los nodos, mejorando
 * la seguridad frente a errores.
 *
 * @param <a> El tipo de elementos que contiene la lista.
 */
public class ZipListStrict<a> implements ZipList<a> {
    private Maybe<NodeLinkList<a>> mNode;
    private int size;
    private int index;
    private NodeLinkList<a> head = null;
    private NodeLinkList<a> last = null;

    /**
     * Obtiene el nodo cabeza de la lista.
     *
     * @return El nodo cabeza de la lista.
     */
    public NodeLinkList<a> getHead() {
        return head;
    }

    /**
     * Obtiene el último nodo de la lista.
     *
     * @return El último nodo de la lista.
     */
    public NodeLinkList<a> getLast() {
        return last;
    }

    /**
     * Devuelve el número de elementos que contiene la lista.
     *
     * @return El tamaño de la lista.
     */
    public int size() {
        return this.size;
    }

    /**
     * Obtiene el índice actual en la lista.
     *
     * @return El índice actual.
     */
    public int getIx() {
        return this.index;
    }

    /**
     * Establece el valor actual del nodo en la posición actual.
     *
     * @param a El valor a establecer en el nodo actual.
     */
    @Override
    public void setCurrent(a a) {
        switch (this.mNode) {
            case Maybe.Nothing() -> {
            }
            case Maybe.Just(NodeLinkList<a> node) -> node.setCurrent(a);
        }
    }

    /**
     * Constructor que inicializa una lista vacía.
     */
    public ZipListStrict() {
        this.mNode = new Maybe.Nothing<>();
        this.size = 0;
        this.index = 0;
    }

    /**
     * Obtiene el índice de un nodo específico en la lista.
     *
     * @param node El nodo cuyo índice se quiere obtener.
     * @return El índice del nodo, o 0 si no se encuentra.
     */
    public int getIndex(NodeLinkList<a> node) {
        if (this.head == null) return 0;
        this.toStart();

        do {
            if (this.mNode.fromJust() == node) return this.index;
        } while (this.next());

        return 0;
    }

    /**
     * Obtiene el contenedor Maybe del nodo actual.
     *
     * @return Un Maybe que contiene el nodo actual.
     */
    public Maybe<NodeLinkList<a>> getMNode() {
        return this.mNode;
    }

    /**
     * Extrae el elemento contenido en el nodo actual de la lista.
     *
     * @return Un Maybe que contiene el valor actual si existe, o Nothing si la lista está vacía.
     */
    @Override
    public Maybe<a> extract() {
        return switch (this.mNode) {
            case Maybe.Nothing() -> new Maybe.Nothing<>();
            case Maybe.Just(NodeLinkList<a> node) -> new Maybe.Just<>(node.getCurrent());
        };
    }

    /**
     * Crea una nueva lista vacía sin elementos.
     *
     * @param <x> El tipo de elementos que contendrá la nueva lista.
     * @return Una nueva instancia vacía de ZipListStrict.
     */
    @Override
    public <x> ZipList<x> empty() {
        return new ZipListStrict<>();
    }

    /**
     * Avanza al siguiente nodo en la lista si es posible.
     *
     * @return true si se movió al siguiente nodo, false en caso contrario.
     */
    @Override
    public boolean next() {
        switch (this.mNode) {
            case Maybe.Nothing() -> {
                return false;
            }
            case Maybe.Just(NodeLinkList<a> node) -> {
                switch (node.getAfter()) {
                    case Maybe.Nothing() -> {
                        return false;
                    }
                    case Maybe.Just(NodeLinkList<a> ignored) -> {
                        this.mNode = node.getAfter();
                        this.index++;
                        return true;
                    }
                }
            }
        }
    }

    /**
     * Verifica si existe un nodo siguiente en la lista.
     *
     * @return true si existe un nodo siguiente, false en caso contrario.
     */
    public boolean hasNext() {
        switch (this.mNode) {
            case Maybe.Nothing() -> {
                return false;
            }
            case Maybe.Just(NodeLinkList<a> node) -> {
                return node.getAfter().isJust();
            }
        }
    }

    /**
     * Retrocede al nodo anterior en la lista si es posible.
     *
     * @return true si se movió al nodo anterior, false en caso contrario.
     */
    @Override
    public boolean prev() {
        switch (this.mNode) {
            case Maybe.Nothing() -> {
                return false;
            }
            case Maybe.Just(NodeLinkList<a> node) -> {
                switch (node.getBefore()) {
                    case Maybe.Nothing() -> {
                        return false;
                    }
                    case Maybe.Just(NodeLinkList<a> ignored) -> {
                        this.mNode = node.getBefore();
                        this.index--;
                        return true;
                    }
                }
            }
        }
    }

    /**
     * Verifica si existe un nodo anterior en la lista.
     *
     * @return true si existe un nodo anterior, false en caso contrario.
     */
    public boolean hasPrev() {
        switch (this.mNode) {
            case Maybe.Nothing() -> {
                return false;
            }
            case Maybe.Just(NodeLinkList<a> node) -> {
                return node.getBefore().isJust();
            }
        }
    }

    /**
     * Inserta un nuevo nodo con el valor especificado a la derecha del nodo actual.
     *
     * @param val El valor que tendrá el nuevo nodo.
     */
    public void insertR(a val) {
        switch (this.mNode) {
            case Maybe.Nothing() -> {
                var newNode = new NodeLinkList<>(val);
                this.head = newNode;
                this.last = newNode;
                this.mNode = new Maybe.Just<>(newNode);
                this.size = 1;
                this.index = 1;
            }
            case Maybe.Just(NodeLinkList<a> node) -> {
                var newNode = new NodeLinkList<>(this.mNode, val, node.getAfter());
                var mnewNode = new Maybe.Just<>(newNode);
                node.setAfter(mnewNode);
                this.mNode = mnewNode;
                this.size++;
                this.index++;
                if (this.size == this.index) this.last = newNode;
            }
        }
    }

    /**
     * Inserta un nuevo nodo con el valor especificado al final de la lista, manteniendo el índice original.
     *
     * @param val El valor que tendrá el nuevo nodo.
     */
    public void insert(a val) {
        int ix = this.index;
        if (ix != 0) this.toEnd();
        insertR(val);
        this.to(ix);
    }

    /**
     * Elimina el nodo actual de la lista y ajusta los punteros y el índice en consecuencia.
     *
     * @return Un Maybe que contiene el valor del nodo eliminado, o Nothing si la lista está vacía.
     */
    public Maybe<a> deleteCurrent() {
        return switch (this.mNode) {
            case Maybe.Nothing() -> new Maybe.Nothing<>();
            case Maybe.Just(NodeLinkList<a> node) -> {
                switch (node.getBefore()) {
                    case Maybe.Nothing() -> {
                        switch (node.getAfter()) {
                            case Maybe.Nothing() -> {
                                this.head = null;
                                this.last = null;
                                this.size = 0;
                                this.index = 0;
                                this.mNode = new Maybe.Nothing<>();
                            }
                            case Maybe.Just(NodeLinkList<a> nodeAfter) -> {
                                this.head = nodeAfter;
                                this.size--;
                                nodeAfter.setBefore(new Maybe.Nothing<>());
                                this.mNode = node.getAfter();
                            }
                        }
                    }
                    case Maybe.Just(NodeLinkList<a> nodeBefore) -> {
                        switch (node.getAfter()) {
                            case Maybe.Nothing() -> {
                                this.last = nodeBefore;
                                this.size--;
                                this.index--;
                                nodeBefore.setAfter(new Maybe.Nothing<>());
                                this.mNode = node.getBefore();
                            }
                            case Maybe.Just(NodeLinkList<a> nodeAfter) -> {
                                nodeBefore.setAfter(node.getAfter());
                                this.size--;
                                this.index--;
                                this.mNode = node.getBefore();
                            }
                        }
                    }
                }
                yield new Maybe.Just<>(node.getCurrent());
            }
        };
    }

    
    /**
     * Elimina el nodo en la posición especificada por el índice.
     * Ajusta los punteros y actualiza el índice después de la eliminación.
     *
     * @param i El índice del nodo que se desea eliminar.
     * @return Un Maybe que contiene el valor del nodo eliminado, o Nothing si la lista está vacía o el índice no existe.
     */
    public Maybe<a> delete(int i) {
        int ix = this.index;
        this.to(i);
        Maybe<a> deleted = new Maybe.Nothing<>();
        if (this.index == i) deleted = this.deleteCurrent();
        this.to(ix);
        return deleted;
    }

    /**
     * Mueve el cursor de la lista al índice especificado.
     * Selecciona el camino más eficiente para moverse hacia el índice objetivo.
     *
     * @param i El índice al que se desea mover el cursor.
     * @return El índice actual después de completar el movimiento.
     */
    public int to(int i) {
        if (this.size == 0) return 0;
        if (i < 1) i = 1;
        if (i > this.size) i = this.size;

        int closenessToHead = i;
        int closenessToLast = this.size - i;
        int closenessToCurrent = Math.abs(this.index - i);

        if (closenessToHead < closenessToLast && closenessToHead < closenessToCurrent) {
            this.toStart();
        } else if (closenessToLast < closenessToCurrent) {
            this.toEnd();
        }

        while (i != this.index) {
            if (this.index < i) this.next();
            else this.prev();
        }

        return this.index;
    }

    /**
     * Mueve el cursor de la lista al nodo inicial (cabeza).
     * Establece el índice actual como 1.
     */
    private void toStart() {
        this.index = 1;
        this.mNode = new Maybe.Just<>(this.head);
    }

    /**
     * Mueve el cursor de la lista al último nodo.
     * Establece el índice actual como el tamaño de la lista.
     */
    private void toEnd() {
        this.index = this.size;
        this.mNode = new Maybe.Just<>(this.last);
    }

    /**
     * Aplica una función especificada a cada elemento de la lista.
     * Recorre toda la lista desde el inicio hasta el final y vuelve la posición al inicio.
     *
     * @param k Una función que se aplicará a cada elemento de la lista.
     */
    public void mapM(Consumer<a> k) {
        if (size == 0) return;

        toStart();
        do {
            k.accept(this.mNode.fromJust().getCurrent());
        }
        while (this.next());
        this.toStart();
    }


    /**
     * Muestra la información del nodo actual.
     * <p>
     * Si la lista está vacía, imprime "--". En cambio, si el nodo actual existe,
     * se llama al método `show` del nodo para mostrar su contenido.
     * </p>
     */
    @Override
    public void show() {
        switch (this.mNode) {
            case Maybe.Nothing() -> System.out.println("--");
            case Maybe.Just(NodeLinkList<a> node) -> node.show();
        }
    }
}