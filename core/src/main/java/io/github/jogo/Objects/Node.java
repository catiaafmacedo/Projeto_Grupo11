package io.github.jogo.Objects;

import io.github.jogo.Interfaces.INode;

public class Node implements INode {
    public int x, y;
    public Node parent;
    public int g, f;

    public Node(int x, int y, Node parent, int g, int f) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.g = g;
        this.f = f;
    }
}
