package ar.edu.tp;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.graph.AsSubgraph;
import java.util.*;

/**
 * Calcula el recorrido mínimo de mantenimiento sobre las estaciones.
 * <p>
 * Aplica el algoritmo de Prim sobre el subgrafo inducido por las estaciones
 * para obtener el Árbol de Expansión Mínima (MST). El peso del MST
 * representa el costo mínimo para conectar todas las estaciones sin ciclos.
 *
 * @param g grafo completo de transporte
 * @param estaciones arreglo con los IDs de las estaciones de tranvía
 * @return peso total del MST
 */

public class GestorDeDatos {

    // --- Constantes del sistema ---
    /** Costo por kilómetro recorrido en hoverboard ($/km) */
    public static final double K1 = 2.0;
    /** Costo por tramo recorrido en tranvía ($/tramo) */
    public static final double K2 = 5.0;

    /**
    * Clase contenedora para transportar un grafo junto con sus metadatos.
    **/
    public static class GrafoTransporte {
        public Graph<String, DefaultWeightedEdge> grafo;
        public String[] estaciones; // Estaciones primarias de tranvía
        public String[][] paresOD;  // Pares Origen-Destino a evaluar
        public GrafoTransporte(Graph<String, DefaultWeightedEdge> g, String[] est, String[][] pares) {
            this.grafo = g; this.estaciones = est; this.paresOD = pares;
        }
    }

    /**
    * GRAFO 1: RED PEQUEÑA (CENTRO URBANO)
    * Crea una red de 5 estaciones de tranvía y 5 puntos de origen/destino.
    * Simula un barrio con conexiones cortas en hoverboard.
    */
    public static GrafoTransporte crearGrafo1() {
        Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // Definición de todos los vértices (estaciones + puntos de interés)
        String[] nodos = {"E1","E2","E3","E4","E5","Casa_A","Casa_B","Oficina","Hospital","Comercio"};
        for (String v : nodos) g.addVertex(v);

        // --- Conexiones HOVERBOARD (orígenes/destinos -> estaciones) ---
        // El peso es K1 * distancia en km
        agregarArista(g, "Casa_A", "E1", K1 * 1.2); 
        agregarArista(g, "Casa_A", "E2", K1 * 1.4);
        agregarArista(g, "Casa_B", "E2", K1 * 1.5);
        agregarArista(g, "Casa_B", "E3", K1 * 1.6);
        agregarArista(g, "Oficina", "E3", K1 * 0.8);
        agregarArista(g, "Oficina", "E2", K1 * 1.0);
        agregarArista(g, "Hospital", "E5", K1 * 2.0);
        agregarArista(g, "Hospital", "E4", K1 * 1.8);
        agregarArista(g, "Comercio", "E4", K1 * 1.0);
        agregarArista(g, "Comercio", "E3", K1 * 1.2);
        // --- Red de TRANVÍA principal y atajos ---
        // El peso es K2 * tramos
        agregarArista(g, "E1", "E2", K2);                 
        agregarArista(g, "E2", "E3", K2);                 
        agregarArista(g, "E3", "E4", K2);                 
        agregarArista(g, "E4", "E5", K2);                 
        agregarArista(g, "E1", "E3", K2 * 1.8);             
        agregarArista(g, "E2", "E4", K2 * 1.5);             

        String[] estaciones = {"E1","E2","E3","E4","E5"};
        String[][] pares = {{"Casa_A","Hospital"},{"Casa_B","Comercio"},
        {"Oficina","Casa_A"},{"Hospital","Comercio"},{"Casa_B","Oficina"}};
        
        return new GrafoTransporte(g, estaciones, pares);
    }

    /**
    * GRAFO 2: RED GRANDE (METRÓPOLIS)
    * Crea una red de 8 estaciones y 8 puntos de interés.
    * Simula una ciudad con más alternativas de viaje.
    */
    public static GrafoTransporte crearGrafo2() { 
        Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // Definición de todos los vértices (estaciones + puntos de interés)
        String[] nodos = {"E1","E2","E3","E4","E5","E6","E7","E8","Residencia_N","Residencia_S","Residencia_E",
        "Residencia_O","Centro_Empresas","Universidad","Parque","Estacion_Central"};
        for (String v : nodos) g.addVertex(v);

        // --- Conexiones HOVERBOARD (orígenes/destinos -> estaciones) ---

        agregarArista(g, "Residencia_N", "E1", K1 * 1.0);     
        agregarArista(g, "Residencia_N", "E2", K1 * 1.2);     
        agregarArista(g, "Residencia_S", "E5", K1 * 1.3);     
        agregarArista(g, "Residencia_S", "E4", K1 * 1.5);     
        agregarArista(g, "Residencia_E", "E4", K1 * 1.2);     
        agregarArista(g, "Residencia_E", "E3", K1 * 1.0);     
        agregarArista(g, "Residencia_O", "E7", K1 * 1.1);     
        agregarArista(g, "Residencia_O", "E6", K1 * 1.3);     
        agregarArista(g, "Centro_Empresas", "E3", K1 * 0.9);  
        agregarArista(g, "Centro_Empresas", "E2", K1 * 1.1);  
        agregarArista(g, "Universidad", "E6", K1 * 1.5);      
        agregarArista(g, "Universidad", "E7", K1 * 1.4);      
        agregarArista(g, "Parque", "E2", K1 * 0.7);           
        agregarArista(g, "Parque", "E3", K1 * 0.9);           
        agregarArista(g, "Estacion_Central", "E8", K1 * 0.5); 
        agregarArista(g, "Estacion_Central", "E7", K1 * 0.8); 
         // --- Red de TRANVÍA principal y atajos ---
        agregarArista(g, "E1", "E2", K2);                     
        agregarArista(g, "E2", "E3", K2);                     
        agregarArista(g, "E3", "E4", K2);                     
        agregarArista(g, "E4", "E5", K2);                     
        agregarArista(g, "E5", "E6", K2);                     
        agregarArista(g, "E6", "E7", K2);                     
        agregarArista(g, "E7", "E8", K2);                     
        agregarArista(g, "E1", "E3", K2 * 1.8);                 
        agregarArista(g, "E2", "E4", K2 * 1.7);                 
        agregarArista(g, "E3", "E6", K2 * 2.0);                 
        agregarArista(g, "E4", "E7", K2 * 1.9);                 
        agregarArista(g, "E5", "E8", K2 * 1.8);                 

        String[] estaciones = {"E1","E2","E3","E4","E5","E6","E7","E8"};
        String[][] pares = {{"Residencia_N","Centro_Empresas"},{"Universidad",
        "Estacion_Central"},{"Residencia_E","Parque"},{"Residencia_N","Parque"},
        {"Centro_Empresas","Universidad"}};

        return new GrafoTransporte(g, estaciones, pares);
    }

    /**
    * Helper: agrega una arista con peso al grafo.
    * Evita repetir g.addEdge() + setEdgeWeight() en todo el código.
    */
    private static void agregarArista(Graph<String, DefaultWeightedEdge> g, String a, String b, double peso) {
        var e = g.addEdge(a, b); g.setEdgeWeight(e, peso);
    }

    /**
    * PROCESAMIENTO PRINCIPAL
    * 1. Calcula la ruta de menor costo para cada par OD usando Dijkstra.
    * 2. Calcula el recorrido de mantenimiento usando Prim (MST).
    * 3. Delega la visualización al Graficador.
    */
    public static void analizarYMostrar(String nombre, GrafoTransporte gt) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" " + nombre);
        System.out.println("=".repeat(60));

        var g = gt.grafo;

        // --- PROBLEMA 1: Rutas de usuarios ---
        System.out.println("\n📍 RUTAS DE USUARIOS (Menor costo):\n");
        int rutas = 0; double suma = 0;
        for (String[] par : gt.paresOD) {

            // Dijkstra encuentra el camino más barato según los pesos K1 y K2
            var ruta = new DijkstraShortestPath<>(g).getPath(par[0], par[1]);
            if (ruta!= null) {
                System.out.printf(" %s → %s%n Camino: %s%n Costo total: $%.2f%n%n",
                    par[0], par[1], ruta.getVertexList(), ruta.getWeight());
                rutas++; suma += ruta.getWeight();
            }
        }
        System.out.printf(" Nodos: %d, Aristas: %d%n", g.vertexSet().size(), g.edgeSet().size());
        if (rutas>0) System.out.printf(" Costo promedio: $%.2f%n", suma/rutas);

        // --- PROBLEMA 2: Mantenimiento con camioneta ---
        System.out.println("\n🚐 MANTENIMIENTO (Camioneta):\n Estaciones: " + Arrays.toString(gt.estaciones));
        double costo = calcularRecorridoEstaciones(g, gt.estaciones);
        System.out.printf(" Costo aproximado: $%.2f%n", costo);

        // Delegamos la visualización al Graficador.
        Graficador.visualizar(nombre, g, gt.estaciones, gt.paresOD);
    }

    /**
    * PROBLEMA 2: Recorrido de Camioneta de Mantenimiento
    *
    * Calcula el Árbol de Expansión Mínima (MST) usando el algoritmo de Prim
    * sobre el subgrafo formado solo por las estaciones de tranvía.
    *
    * El peso del MST representa el costo mínimo para conectar todas las
    * estaciones en una ruta de mantenimiento sin ciclos.
    */    
    private static double calcularRecorridoEstaciones(Graph<String, DefaultWeightedEdge> g, String[] estaciones) {

        // Creamos un subgrafo que contiene solo las estaciones
        var sub = new AsSubgraph<>(g, new HashSet<>(Arrays.asList(estaciones)));
        // Prim nos da el árbol más barato que las conecta a todas
        return new PrimMinimumSpanningTree<>(sub).getSpanningTree().getWeight();
    }
}
