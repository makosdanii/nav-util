package com.pizzadelivery.server.services;

import com.pizzadelivery.server.data.entities.Edge;
import com.pizzadelivery.server.data.repositories.EdgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EdgeService {
    private EdgeRepository edgeRepository;

    @Autowired
    public EdgeService(EdgeRepository edgeRepository) {
        this.edgeRepository = edgeRepository;
    }

    public Edge findEdge(int id) {
        return edgeRepository.findById(id).orElseThrow();
    }

    public Iterable<Edge> loadEdges() {
        Iterable<Edge> all = edgeRepository.findAll();
        loadGraph();
        return all;
    }

    public ArrayList<ArrayList<Edge>> loadGraph() {
        Iterable<Edge> all = edgeRepository.findAll();
        ArrayList<ArrayList<Edge>> graph = new ArrayList<>();
        all.forEach(edge -> {
            var adjacency = new ArrayList<Edge>(List.of(edge));
            edge.getMapsById().forEach(id -> adjacency.add(id.getEdgeByNeighbourId()));
            graph.add(adjacency);
        });

        graph.stream().sorted(new Comparator<ArrayList<Edge>>() {
            @Override
            public int compare(ArrayList<Edge> o1, ArrayList<Edge> o2) {
                return o1.get(0).compareTo(o2.get(0));
            }
        }).toList();

        return graph;
    }
}
