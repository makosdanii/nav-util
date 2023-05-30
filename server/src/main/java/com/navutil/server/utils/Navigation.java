package com.navutil.server.utils;

import java.util.*;

/**
 * class responsible for navigation tasks amongst edges
 */
public class Navigation {
    /**
    List<ArrayList<Edge>> graph;
    Edge[] edges;
    Map<Car, Edge[]> routes = new HashMap<>();

    public Navigation(EdgeService edgeService) {
        graph = edgeService.loadGraph();
        edges = graph.stream().map(list -> list.get(0)).toArray(Edge[]::new);
    }

    public List<ArrayList<Edge>> getGraph() {
        return graph;
    }

    public void setGraph(List<ArrayList<Edge>> graph) {
        this.graph = graph;
        edges = graph.stream().map(list -> list.get(0)).toArray(Edge[]::new);
    }

    public int getDistance(Car fromItsPosition) {
        return Arrays.stream(routes.get(fromItsPosition))
                .reduce(0, (acc, curr) -> acc + curr.getEdgeWeight(), Integer::sum);
    }

    public Edge[] getRoute(Car fromItsPosition) {
        return routes.get(fromItsPosition);
    }

    public Integer navigate(Car car, Edge current, Edge target) {
        Edge[] result = dijkstraShortestPath(current, target);
        routes.put(car, result);

        return Arrays.stream(result).reduce(0, (acc, curr) -> acc + curr.getEdgeWeight(), Integer::sum);
    }

    public Edge[] dijkstraShortestPath(Edge current, Edge target) {
        if (current.equals(target)) {
            return new Edge[]{current};
        }
        if (graph == null) throw new IllegalArgumentException("Unloaded graph");

        int[] distance = new int[edges.length];
        int[] parent = new int[edges.length];
        for (int j = 0; j < edges.length; j++) {
            distance[j] = Integer.MAX_VALUE;
            parent[j] = 0;
        }

        Edge node = current.clone();
        int index = node.getId() - 1;
        node.setEdgeWeight(0);

        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(edges.length, new Edge());
        priorityQueue.add(node);
        distance[index] = 0;

        while (!priorityQueue.isEmpty() && distance[index] < Integer.MAX_VALUE) {
            node = priorityQueue.remove();
            index = node.getId() - 1;

            for (int i = 1; i < graph.get(index).size(); i++) {
                int adjacentIdx = graph.get(index).get(i).getId() - 1;
                Edge adjacent = edges[adjacentIdx];
                int edgeDist = adjacent.getEdgeWeight();
                int newDist = distance[index] + edgeDist;

                // If the new distance is lesser in the cost
                if (newDist < distance[adjacentIdx]) {
                    distance[adjacentIdx] = newDist;
                    parent[adjacentIdx] = index;

                    // Adding the current node to the priority queue priorityQueue
                    adjacent = adjacent.clone();
                    adjacent.setEdgeWeight(distance[adjacentIdx]);
                    priorityQueue.add(adjacent);
                }
            }
        }

        ArrayList<Edge> path = new ArrayList<>();
        assemble(parent, path, current.getId(), target.getId() - 1);
        path.add(target);

        return path.toArray(Edge[]::new);
    }

    private void assemble(int[] parentNodes, ArrayList<Edge> path, int from, int to) {
        Edge node = edges[parentNodes[to]];
        if (node.getId() == from) {
            path.add(node);
            return;
        }

        assemble(parentNodes, path, from, node.getId() - 1);
        path.add(node);
    }
    */
}
