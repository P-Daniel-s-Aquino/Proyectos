package ar.edu.tp;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.graph.AsSubgraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import java.util.*;

/**
 * SISTEMA DE TRANSPORTE COMBINADO (HOVERBOARD + TRANVÍA)
 * 
 * Problema: Calcular rutas de menor costo para usuarios y recorrido óptimo
 * para camioneta de mantenimiento en estaciones de tranvía.
 * 
 * Costo = K1 * km_hoverboard + K2 * estaciones_tranvia
 */
public class App {
    
    // Constantes del sistema
    private static final double K1 = 2.0;  // $/km en hoverboard
    private static final double K2 = 5.0;  // $/tramo en tranvía
    
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("java.awt.headless", "false");

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE TRANSPORTE HOVERBOARD + TRANVÍA      ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        // ========== GRAFO 1: RED PEQUEÑA (CENTRO URBANO) ==========
        procesarGrafo("RED PEQUEÑA - CENTRO URBANO", crearGrafo1());
        
        // ========== GRAFO 2: RED GRANDE (METROPOLIS) ==========
        procesarGrafo("RED GRANDE - METROPOLIS", crearGrafo2());
    }

    /**
     * Procesa un grafo: calcula rutas de usuarios y recorrido de camioneta
     */
    private static void procesarGrafo(String nombre, GrafoTransporte gt) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + nombre);
        System.out.println("=".repeat(60));
        
        Graph<String, DefaultWeightedEdge> g = gt.grafo;
        String[] estaciones = gt.estaciones;
        String[][] pares = gt.paresOD;
        
        // 1. Resolver rutas de usuarios
        System.out.println("\n📍 RUTAS DE USUARIOS (Menor costo):\n");
        
        for (String[] par : pares) {
            String origen = par[0];
            String destino = par[1];
            
            DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = 
                new DijkstraShortestPath<>(g);
            var ruta = dijkstra.getPath(origen, destino);
            
            if (ruta != null) {
                System.out.printf("  %s → %s%n", origen, destino);
                System.out.printf("    Camino: %s%n", ruta.getVertexList());
                System.out.printf("    Costo total: $%.2f%n%n", ruta.getWeight());
            }
        }
        
        // 2. Recorrido de camioneta (viajante simplificado)
        System.out.println("\n🚐 MANTENIMIENTO (Camioneta):\n");
        System.out.println("  Estaciones a visitar: " + Arrays.toString(estaciones));
        double costo = calcularRecorridoEstaciones(g, estaciones);
        System.out.printf("  Costo aproximado del recorrido: $%.2f%n", costo);
        
        // 3. Visualizar
        visualizarGrafo(nombre, g, estaciones, pares);
    }

    /**
     * GRAFO 1: RED PEQUEÑA (5 nodos principales + 5 secundarios)
     */
    private static GrafoTransporte crearGrafo1() {
        Graph<String, DefaultWeightedEdge> g = 
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        
        String[] nodos = {
            // Estaciones primarias (TRANVÍA)
            "E1", "E2", "E3", "E4", "E5",
            // Nodos secundarios (ORÍGENES/DESTINOS)
            "Casa_A", "Casa_B", "Oficina", "Hospital", "Comercio"
        };
        
        for (String v : nodos) g.addVertex(v);
        
        // Conexiones: ORÍGENES -> ESTACIONES (hoverboard)
        agregarArista(g, "Casa_A", "E1", K1 * 1.2);      // 1.2 km
        agregarArista(g, "Casa_B", "E2", K1 * 1.5);      // 1.5 km
        agregarArista(g, "Oficina", "E3", K1 * 0.8);     // 0.8 km
        agregarArista(g, "Hospital", "E5", K1 * 2.0);    // 2.0 km
        agregarArista(g, "Comercio", "E4", K1 * 1.0);    // 1.0 km
        
        // Red de tranvía (estaciones - tranvía)
        agregarArista(g, "E1", "E2", K2);                // 1 tramo
        agregarArista(g, "E2", "E3", K2);                // 1 tramo
        agregarArista(g, "E3", "E4", K2);                // 1 tramo
        agregarArista(g, "E4", "E5", K2);                // 1 tramo
        agregarArista(g, "E1", "E3", K2 * 1.8);          // 1.8 tramos (directo)
        agregarArista(g, "E2", "E4", K2 * 1.5);          // 1.5 tramos (directo)
        
        String[] estaciones = {"E1", "E2", "E3", "E4", "E5"};
        String[][] pares = {
            {"Casa_A", "Hospital"},
            {"Casa_B", "Comercio"},
            {"Oficina", "Casa_A"}
        };
        
        return new GrafoTransporte(g, estaciones, pares);
    }

    /**
     * GRAFO 2: RED GRANDE (8 nodos principales + 8 secundarios)
     */
    private static GrafoTransporte crearGrafo2() {
        Graph<String, DefaultWeightedEdge> g = 
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        
        String[] nodos = {
            // Estaciones primarias (TRANVÍA)
            "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8",
            // Nodos secundarios (ORÍGENES/DESTINOS)
            "Residencia_N", "Residencia_S", "Residencia_E", "Residencia_O",
            "Centro_Empresas", "Universidad", "Parque", "Estacion_Central"
        };
        
        for (String v : nodos) g.addVertex(v);
        
        // Conexiones: ORÍGENES -> ESTACIONES (hoverboard)
        agregarArista(g, "Residencia_N", "E1", K1 * 1.0);
        agregarArista(g, "Residencia_S", "E5", K1 * 1.3);
        agregarArista(g, "Residencia_E", "E4", K1 * 1.2);
        agregarArista(g, "Residencia_O", "E7", K1 * 1.1);
        agregarArista(g, "Centro_Empresas", "E3", K1 * 0.9);
        agregarArista(g, "Universidad", "E6", K1 * 1.5);
        agregarArista(g, "Parque", "E2", K1 * 0.7);
        agregarArista(g, "Estacion_Central", "E8", K1 * 0.5);
        
        // Red de tranvía (estaciones - tranvía)
        // Ruta principal: E1-E2-E3-E4-E5-E6-E7-E8
        agregarArista(g, "E1", "E2", K2);
        agregarArista(g, "E2", "E3", K2);
        agregarArista(g, "E3", "E4", K2);
        agregarArista(g, "E4", "E5", K2);
        agregarArista(g, "E5", "E6", K2);
        agregarArista(g, "E6", "E7", K2);
        agregarArista(g, "E7", "E8", K2);
        
        // Atajos (tranvía directo)
        agregarArista(g, "E1", "E3", K2 * 1.8);
        agregarArista(g, "E2", "E4", K2 * 1.7);
        agregarArista(g, "E3", "E6", K2 * 2.0);
        agregarArista(g, "E4", "E7", K2 * 1.9);
        agregarArista(g, "E5", "E8", K2 * 1.8);
        
        String[] estaciones = {"E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8"};
        String[][] pares = {
            {"Residencia_N", "Centro_Empresas"},
            {"Universidad", "Estacion_Central"},
            {"Residencia_E", "Parque"}
        };
        
        return new GrafoTransporte(g, estaciones, pares);
    }

    /**
     * PROBLEMA 2: Recorrido de Camioneta de Mantenimiento
     * 
     * Calcula el costo del árbol de expansión mínima usando el
     * Algoritmo de Prim sobre las estaciones de tranvía.
     * 
     * Este resultado representa el costo mínimo para conectar todas
     * las estaciones en una red de mantenimiento.
     */
    private static double calcularRecorridoEstaciones(
            Graph<String, DefaultWeightedEdge> g, String[] estaciones) {
        if (estaciones.length == 0) return 0.0;

        var estacionSet = new HashSet<>(Arrays.asList(estaciones));
        var subgrafo = new AsSubgraph<>(g, estacionSet);

        var prim = new PrimMinimumSpanningTree<>(subgrafo);
        return prim.getSpanningTree().getWeight();
    }

    /**
     * Visualiza el grafo usando GraphStream
     */
    private static void visualizarGrafo(String nombre, 
            Graph<String, DefaultWeightedEdge> g, 
            String[] estaciones,
            String[][] pares) {
        
        SingleGraph gs = new SingleGraph(nombre);
        configurarEstilo(gs, estaciones);
        
        // Agregar nodos al grafo visual
        for (String v : g.vertexSet()) {
            Node n = gs.addNode(v);
            n.setAttribute("ui.label", v);
            
            // Marcar estaciones primarias
            if (Arrays.asList(estaciones).contains(v)) {
                n.setAttribute("ui.class", "estacion");
            } else {
                n.setAttribute("ui.class", "destino");
            }
        }
        
        // Agregar aristas al grafo visual
        for (DefaultWeightedEdge e : g.edgeSet()) {
            String a = g.getEdgeSource(e);
            String b = g.getEdgeTarget(e);
            Edge ge = gs.addEdge(a + "-" + b, a, b, false);
            ge.setAttribute("ui.label", 
                String.format("$%.1f", g.getEdgeWeight(e)));
        }
        
        gs.display();
    }

    /**
     * Configura el estilo visual del grafo
     */
    private static void configurarEstilo(SingleGraph gs, String[] estaciones) {
        gs.setAttribute("ui.stylesheet", 
            "node.estacion { " +
            "  size: 45px; " +
            "  fill-color: #FF6B6B; " +
            "  text-size: 16px; " +
            "  text-style: bold; " +
            "  text-color: #000000; " +
            "  stroke-mode: plain; " +
            "  stroke-color: #8B0000; " +
            "  stroke-width: 3px; " +
            "} " +
            "node.destino { " +
            "  size: 35px; " +
            "  fill-color: #4ECDC4; " +
            "  text-size: 14px; " +
            "  text-style: bold; " +
            "  text-color: #000000; " +
            "  stroke-mode: plain; " +
            "  stroke-color: #004d4d; " +
            "  stroke-width: 2px; " +
            "} " +
            "edge { " +
            "  fill-color: #333333; " +
            "  size: 3px; " +
            "  text-size: 14px; " +
            "  text-style: bold; " +
            "  text-color: #000000; " +
            "  text-alignment: along; " +
            "  text-background-color: #FFFFFF; " +
            "  text-background-mode: rounded-box; " +
            "} " +
            "edge.ruta { " +
            "  fill-color: #FFD700; " +
            "  size: 5px; " +
            "  text-color: #000000; " +
            "}"
        );
    }

    /**
     * Helper: agregar arista con peso
     */
    private static void agregarArista(Graph<String, DefaultWeightedEdge> g, 
                                      String a, String b, double peso) {
        var e = g.addEdge(a, b);
        g.setEdgeWeight(e, peso);
    }

    /**
     * Clase interna para encapsular info del grafo
     */
    private static class GrafoTransporte {
        Graph<String, DefaultWeightedEdge> grafo;
        String[] estaciones;      // Estaciones primarias (tranvía)
        String[][] paresOD;       // Pares origen-destino
        
        GrafoTransporte(Graph<String, DefaultWeightedEdge> g, 
                       String[] est, String[][] pares) {
            this.grafo = g;
            this.estaciones = est;
            this.paresOD = pares;
        }
    }
}