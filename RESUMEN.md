# 🚇 SISTEMA DE TRANSPORTE HOVERBOARD + TRANVÍA
## Documentación del Código Mejorado

---

## 🎯 Problemas Resueltos

### **Problema 1: Ruta de Menor Costo para Usuarios**
Utiliza **Dijkstra** para encontrar la ruta más barata considerando:
- Costo hoverboard: `K1 * distancia_km`
- Costo tranvía: `K2 * cantidad_tramos`

**3 Pares por Grafo:**

**RED PEQUEÑA:**
1. Casa_A → Hospital: $23.90 (6 nodos, múltiples estaciones)
2. Casa_B → Comercio: $12.50 (ruta corta)
3. Oficina → Casa_A: $13.00 (ida y vuelta)

**RED GRANDE:**
1. Residencia_N → Centro_Empresas: $12.80 (usando atajo E1→E3)
2. Universidad → Estación_Central: $14.00 (ruta larga)
3. Residencia_E → Parque: $12.30 (ruta óptima)

### **Problema 2: Recorrido de Camioneta de Mantenimiento**
Calcula el costo del árbol de expansión mínima para conectar **todas las estaciones**:
- RED PEQUEÑA: 5 estaciones, costo $20.00
- RED GRANDE: 8 estaciones, costo $35.00

---

## 📊 Visualización en GraphStream

El código abre **2 ventanas gráficas** interactivas:

### **Elementos Visuales:**
- 🔴 **Nodos Rojos Grandes**: Estaciones de Tranvía (E1, E2, etc.)
- 🟦 **Nodos Azules Pequeños**: Puntos de Origen/Destino
- **Etiquetas en aristas**: Costo en dólares ($)
- **Grosor de línea**: Proporcional al costo

### **Interactividad:**
- Arrastra nodos para reorganizar
- Zoom con rueda del ratón
- Pausa y resume la visualización

---

## 💻 Constantes Configurables

```java
private static final double K1 = 2.0;  // $/km en hoverboard
private static final double K2 = 5.0;  // $/tramo en tranvía
```

Puedes cambiar estos valores para ajustar el modelo económico.

---

## 🔧 Estructura del Código

```
App.java
├── main()                          // Punto de entrada
├── procesarGrafo()                 // Procesa un grafo completo
├── crearGrafo1()                   // RED PEQUEÑA
├── crearGrafo2()                   // RED GRANDE
├── calcularRecorridoEstaciones()   // Problema 2: Camioneta
├── visualizarGrafo()               // GraphStream
├── configurarEstilo()              // Estilos CSS
└── agregarArista()                 // Helper para agregar aristas
```

---

## 📝 Líneas Clave del Código (Con Librerías)

### **Imports y Configuración Inicial**

```java
// Línea 3-6: LIBRERÍA JGRAPHT (cálculos de grafos)
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
// → JGraphT: librería para crear grafos y calcular rutas óptimas

// Línea 7-9: LIBRERÍA GRAPHSTREAM (visualización)
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
// → GraphStream: librería para mostrar grafos gráficamente
```

---

### **Creación del Grafo (Líneas 54-58)**

```java
// Línea 54: JGraphT - Crear grafo vacío con pesos
Graph<String, DefaultWeightedEdge> g = 
    new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
// String: los nodos son texto (nombres de estaciones)
// DefaultWeightedEdge: las aristas tienen peso (costo)
// SimpleWeightedGraph: grafo no dirigido (puedes ir en ambas direcciones)

// Línea 59-60: Agregar nodos al grafo
for (String v : nodos) 
    g.addVertex(v);
// → addVertex(): JGraphT - agrega un nodo al grafo

// Línea 62-70: Agregar aristas con peso
agregarArista(g, "Casa_A", "E1", K1 * 1.2);
// → agregarArista(): función helper que:
//   1. Crea una arista entre 2 nodos
//   2. Define su peso (costo) con setEdgeWeight()
```

---

### **Cálculo de Rutas (Líneas 92-95)**

```java
// Línea 92: JGraphT - Algoritmo Dijkstra
DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = 
    new DijkstraShortestPath<>(g);

// Línea 93: Calcular ruta más barata
var ruta = dijkstra.getPath(origen, destino);
// → getPath(): JGraphT - busca la ruta de MENOR COSTO
//   Retorna una lista de nodos en el orden óptimo

// Línea 97: Obtener el costo total
System.out.println("Costo total: $" + ruta.getWeight());
// → getWeight(): JGraphT - suma de todos los pesos de las aristas
```

---

### **Visualización Gráfica (Líneas 166-190)**

```java
// Línea 166: GraphStream - Crear grafo visual
SingleGraph gs = new SingleGraph("Transporte");
// → SingleGraph: grafo no dirigido que se muestra en ventana gráfica

// Línea 168-173: Configurar estilos CSS
gs.setAttribute("ui.stylesheet", 
    "node.estacion { size: 45px; fill-color: #FF6B6B; ... }");
// → setAttribute(): GraphStream - aplica estilos visuales CSS
//   Controla color, tamaño, fuente, bordes, etc.

// Línea 174-176: Agregar nodos al grafo visual
for (String v : g.vertexSet()) {
    Node n = gs.addNode(v);
    n.setAttribute("ui.label", v);
}
// → addNode(): GraphStream - crea nodo visible
// → setAttribute("ui.label"): GraphStream - agrega etiqueta visible

// Línea 177-183: Agregar aristas al grafo visual
for (DefaultWeightedEdge e : g.edgeSet()) {
    Edge ge = gs.addEdge(a + "-" + b, a, b, false);
    ge.setAttribute("ui.label", String.format("$%.1f", ...));
}
// → addEdge(): GraphStream - crea arista visible
// → setAttribute(): GraphStream - agrega costo como etiqueta

// Línea 185: Mostrar ventana gráfica
gs.display();
// → display(): GraphStream - abre la ventana interactiva
```

---

### **Clase Interna (Líneas 263-272)**

```java
private static class GrafoTransporte {
    Graph<String, DefaultWeightedEdge> grafo;
    String[] estaciones;
    String[][] paresOD;
    
    GrafoTransporte(Graph<String, DefaultWeightedEdge> g, 
                   String[] est, String[][] pares) {
        this.grafo = g;          // JGraphT graph
        this.estaciones = est;   // Array de strings
        this.paresOD = pares;    // Pares origen-destino
    }
}
// → Clase para empaquetar la información de cada grafo
//   Organiza: grafo (JGraphT) + estaciones + pares de prueba
```

---

## 🏗️ Resumen de Librerías

| Librería | Uso | Líneas |
|----------|-----|--------|
| **JGraphT** | Crear grafo + Dijkstra | 3-6, 54-95 |
| **GraphStream** | Visualizar grafo | 7-9, 166-185 |
| **Java Util** | Arrays, listas | 10, 258-272 |

---

## 📦 Dependencias

```xml
<dependency>
    <groupId>org.jgrapht</groupId>
    <artifactId>jgrapht-core</artifactId>
    <version>1.5.1</version>
</dependency>
<dependency>
    <groupId>org.graphstream</groupId>
    <artifactId>gs-core</artifactId>
    <version>2.0</version>
</dependency>
```

---

## 🚀 Ejecución

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="ar.edu.tp.App"
```
---

**Asignatura**: Programacón Orientada a Objetos II
