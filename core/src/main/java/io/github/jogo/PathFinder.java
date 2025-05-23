
package io.github.jogo;

import com.badlogic.gdx.math.Vector2;
import io.github.jogo.Screens.*;

import java.util.*;

public class PathFinder {

    private static class Node implements Comparable<Node> {
        Vector2 position;
        Node parent;
        float gCost;
        float hCost;

        Node(Vector2 position, Node parent, float gCost, float hCost) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
        }

        float fCost() {
            return gCost + hCost;
        }

        @Override
        public int compareTo(Node other) {
            return Float.compare(this.fCost(), other.fCost());
        }
    }

    public static List<Vector2> findPath(World world, Vector2 start, Vector2 end) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        HashSet<Vector2> closedSet = new HashSet<>();

        openSet.add(new Node(start, null, 0, heuristic(start, end)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.position.epsilonEquals(end, 0.1f)) {
                return reconstructPath(current);
            }

            closedSet.add(current.position);

            for (Vector2 neighbor : getNeighbors(current.position, world)) {
                if (closedSet.contains(neighbor)) continue;

                float tentativeG = current.gCost + 1;

                boolean inOpenSet = false;
                for (Node node : openSet) {
                    if (node.position.equals(neighbor)) {
                        inOpenSet = true;
                        if (tentativeG < node.gCost) {
                            node.gCost = tentativeG;
                            node.parent = current;
                        }
                        break;
                    }
                }

                if (!inOpenSet) {
                    openSet.add(new Node(neighbor, current, tentativeG, heuristic(neighbor, end)));
                }
            }
        }

        return null;
    }

    private static List<Vector2> reconstructPath(Node node) {
        List<Vector2> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.position);
            node = node.parent;
        }
        return path;
    }

    private static float heuristic(Vector2 a, Vector2 b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // Manhattan distance
    }

    private static List<Vector2> getNeighbors(Vector2 pos, World world) {
        List<Vector2> neighbors = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};

        for (int[] dir : directions) {
            int newX = (int) pos.x + dir[0];
            int newY = (int) pos.y + dir[1];
            if (world.isWalkable(newX, newY)) {
                neighbors.add(new Vector2(newX, newY));
            }
        }

        return neighbors;
    }
}
