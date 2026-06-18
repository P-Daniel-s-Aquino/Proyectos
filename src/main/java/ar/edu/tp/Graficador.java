package ar.edu.tp;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import java.util.Arrays;

// Clase encargada únicamente de dibujar el grafo en pantalla
// Usa GraphStream, por eso no tiene lógica de cálculo, solo visualización
public class Graficador {

    // Método principal para mostrar un grafo
    // Recibe el nombre de la ventana, el grafo de JGraphT, las estaciones y los pares OD (aunque pares no se usa aquí, se deja por compatibilidad)
    public static void visualizar(String nombre, Graph<String, DefaultWeightedEdge> g, String[] estaciones, String[][] pares) {
        
        // Creamos un grafo de GraphStream con el nombre que le pasamos
        SingleGraph gs = new SingleGraph(nombre);

        // Definimos el estilo CSS para los nodos y aristas
        gs.setAttribute("ui.stylesheet", """
        node.estacion {
            size: 45px;
            fill-color: #FF6B6B;      // rojo para estaciones de tranvía
            text-size: 16px;
            text-style: bold;
            text-color: #000000;
            stroke-mode: plain;
            stroke-color: #8B0000;    // borde más oscuro
            stroke-width: 3px;
        }
        
        node.destino {
            size: 35px;
            fill-color: #4ECDC4;      // turquesa para casas, oficinas, etc.
            text-size: 14px;
            text-style: bold;
            text-color: #000000;
            stroke-mode: plain;
            stroke-color: #004d4d;
            stroke-width: 2px;
        }
        
        edge {
            fill-color: #333333;      // gris oscuro para las aristas
            size: 3px;
            text-size: 14px;
            text-style: bold;
            text-color: #000000;
            text-alignment: along;    // texto sigue la línea
            text-background-color: #FFFFFF;
            text-background-mode: rounded-box; // cajita blanca detrás del peso
        }
        
        edge.ruta {
            fill-color: #FFD700;      // dorado, por si querés resaltar una ruta
            size: 5px;
        }
        """);

        // Agregamos todos los vértices del grafo JGraphT al grafo visual
        for (String v : g.vertexSet()) {
            Node n = gs.addNode(v); 
            n.setAttribute("ui.label", v); // mostramos el nombre encima del nodo
            
            // Si el vértice está en la lista de estaciones, le damos clase "estacion", si no, "destino"
            // Esto hace que tome el estilo rojo o turquesa definido arriba
            n.setAttribute("ui.class", Arrays.asList(estaciones).contains(v) ? "estacion" : "destino");
        }

        // Agregamos todas las aristas con su peso como etiqueta
        for (var e : g.edgeSet()) {
            String a = g.getEdgeSource(e);
            String b = g.getEdgeTarget(e);
            
            // El ID de la arista debe ser único, usamos "A-B"
            Edge ge = gs.addEdge(a + "-" + b, a, b, false);
            
            // Mostramos el costo con formato $X.X
            ge.setAttribute("ui.label", String.format("$%.1f", g.getEdgeWeight(e)));
        }

        // Finalmente mostramos la ventana interactiva
        // GraphStream abre un JFrame con Swing
        gs.display();
    }
}