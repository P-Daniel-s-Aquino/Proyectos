# 🚇 Sistema de Transporte Hoverboard + Tranvía

**Proyecto de Grafos y Algoritmos** - Modelización de red de transporte combinado.

---

## 📋 ¿Qué necesita instalar la otra persona?

### **Requisitos Obligatorios:**

1. **Java JDK 11 o superior**
   - Descargar: https://www.oracle.com/java/technologies/downloads/
   - Verificar: `java -version`

2. **Apache Maven 3.6+**
   - Descargar: https://maven.apache.org/download.cgi
   - Verificar: `mvn -version`

3. **Git (opcional, para clonar)**
   - Descargar: https://git-scm.com/

---

> Si descargas el proyecto como ZIP, primero descomprímelo y luego entra en la carpeta `Transporte`.

```
### **Clonar desde Git**

```bash
git clone <URL_DEL_REPOSITORIO>
cd Transporte
mvn clean compile
mvn exec:java -Dexec.mainClass="ar.edu.tp.App"
```

---

## 📦 Estructura del Proyecto

```
Transporte/
├── pom.xml                          # Configuración Maven + dependencias
├── README.md                        # Este archivo
├── RESUMEN.md                       # Documentación detallada
└── src/
    └── main/java/ar/edu/tp/
        └── App.java                 # Código principal
```

---

## 🎯 ¿Qué hace el proyecto?

**Resuelve 2 problemas sobre grafos:**

### **Problema 1: Ruta de Menor Costo**
- Calcula la ruta más barata para viajar de un origen a un destino
- Considera: K1 × km_hoverboard + K2 × tramos_tranvía
- Usa: **Algoritmo de Dijkstra**

### **Problema 2: Recorrido de Mantenimiento (TSP)**
- Calcula el costo mínimo para conectar todas las estaciones de tranvía
- Usa: **Algoritmo de Prim** para árbol de expansión mínima (MST)
- Tipo: **Red de mantenimiento óptima** sobre las estaciones primarias

---

## 📊 Grafos Incluidos

1. **RED PEQUEÑA** (Centro Urbano)
   - 5 estaciones de tranvía
   - 5 puntos de origen/destino
   - 13 conexiones

2. **RED GRANDE** (Metropolis)
   - 8 estaciones de tranvía
   - 8 puntos de origen/destino
   - 22 conexiones

---

## 🎨 Visualización

El programa abre **2 ventanas gráficas interactivas** con:
- 🔴 Nodos rojos = Estaciones de tranvía
- 🟦 Nodos azules = Puntos de origen/destino
- Etiquetas con costos en pesos
- Interactividad: arrastra nodos, zoom, pausa

---

## 📝 Constantes Configurables

En `App.java` línea 15-16:
```java
private static final double K1 = 2.0;  // $/km en hoverboard
private static final double K2 = 5.0;  // $/tramo en tranvía
```

Cambiar estos valores modifica automáticamente los costos calculados.

---

## 🔧 Solución de Problemas

### **Error: "Java not found"**
```bash
# Installar Java
# Windows: Descargar desde oracle.com
# Linux (Ubuntu/Debian):
sudo apt install openjdk-11-jdk

# Verificar:
java -version
```

### **Error: "Maven not found"**
```bash
# Installar Maven
# Descargar y agregar al PATH
# Verificar:
mvn -version
```

### **Error: "Cannot find symbol" al compilar**
```bash
# Limpiar y recompilar
mvn clean compile
```

### **Las ventanas gráficas no aparecen**
```bash
# Agregar variables de entorno
export DISPLAY=:0
mvn exec:java -Dexec.mainClass="ar.edu.tp.App"
```

---

## 📚 Documentación Detallada

<details>
<summary><b>📖 HAGA CLIC AQUÍ PARA VER DOCUMENTACIÓN TÉCNICA</b></summary>

### **Dos Grafos Completos** 🌐

#### **GRAFO 1: RED PEQUEÑA (Centro Urbano)**
- 5 Estaciones de Tranvía: E1, E2, E3, E4, E5
- 5 Puntos de Origen/Destino: Casa_A, Casa_B, Oficina, Hospital, Comercio
- 13 Aristas: Conexiones hoverboard + red de tranvía
- Rutas directas: E1→E3 y E2→E4 (atajos)

#### **GRAFO 2: RED GRANDE (Metropolis)**
- 8 Estaciones de Tranvía: E1-E8
- 8 Puntos de Origen/Destino: Residencias, Centro Empresas, Universidad, Parque, Estación Central
- 22 Aristas: Conexiones y múltiples atajos

### **Problemas Resueltos** ✅

**Problema 1: Ruta de Menor Costo**
- Utiliza **Dijkstra** para encontrar ruta más barata
- Fórmula: `K1 * km_hoverboard + K2 * tramos_tranvia`

**RED PEQUEÑA (3 rutas):**
1. Casa_A → Hospital: $23.90
2. Casa_B → Comercio: $12.50
3. Oficina → Casa_A: $13.00

**RED GRANDE (3 rutas):**
1. Residencia_N → Centro_Empresas: $12.80
2. Universidad → Estación_Central: $14.00
3. Residencia_E → Parque: $12.30

**Problema 2: Recorrido Camioneta**
- RED PEQUEÑA: 5 estaciones, costo $20.00
- RED GRANDE: 8 estaciones, costo $35.00

### **Líneas Clave del Código** 💻

#### **Imports - Librerías**
```java
// JGraphT: Crear grafos y calcular rutas óptimas
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

// GraphStream: Visualizar grafos gráficamente
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
```

#### **Creación del Grafo**
```java
// Crear grafo no dirigido con pesos
Graph<String, DefaultWeightedEdge> g = 
    new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

// Agregar nodos
for (String v : nodos) 
    g.addVertex(v);

// Agregar aristas con costo
agregarArista(g, "Casa_A", "E1", K1 * 1.2);
```

#### **Algoritmo Dijkstra**
```java
// Crear algoritmo
DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = 
    new DijkstraShortestPath<>(g);

// Calcular ruta más barata
var ruta = dijkstra.getPath(origen, destino);

// Obtener costo total
System.out.println("Costo: $" + ruta.getWeight());
```

#### **Visualización GraphStream**
```java
// Crear grafo visual
SingleGraph gs = new SingleGraph("Transporte");

// Agregar nodos visibles
Node n = gs.addNode(v);
n.setAttribute("ui.label", v);

// Agregar aristas visibles
Edge ge = gs.addEdge(a + "-" + b, a, b, false);
ge.setAttribute("ui.label", "$" + costo);

// Mostrar ventana
gs.display();
```

### **Resumen de Librerías**

| Librería | Función | Uso |
|----------|---------|-----|
| **JGraphT** | Grafos + Dijkstra | Cálculos |
| **GraphStream** | Visualización gráfica | Mostrar grafo |
| **Java Util** | Arrays, Strings | Estructuras |

### **Tabla de Métodos**

```
App.java
├── main()                          // Punto de entrada
├── procesarGrafo()                 // Procesa un grafo completo
├── crearGrafo1()                   // RED PEQUEÑA (5+5 nodos)
├── crearGrafo2()                   // RED GRANDE (8+8 nodos)
├── calcularRecorridoEstaciones()   // Problema 2: Camioneta
├── visualizarGrafo()               // GraphStream
├── configurarEstilo()              // Estilos CSS
└── agregarArista()                 // Helper para agregar aristas
```

> 📝 **Ver `RESUMEN.md`** para documentación completa con explicaciones línea por línea

</details>

---

---

## 📈 Cómo Extender el Proyecto

1. **Agregar más grafos**: Modificar `crearGrafo1()` y `crearGrafo2()`
2. **Cambiar pares O/D**: Editar arrays `pares` en cada grafo
3. **Algoritmo TSP óptimo**: Usar `TravelingSalesmanProblem` de JGraphT
4. **Interfaz gráfica**: Agregar JFrame para entrada dinámica
5. **API REST**: Exponer cálculos como servicio web

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

**Maven las descargará automáticamente** cuando ejecutes `mvn compile`.

---

## ✅ Checklist para Compartir

- [x] Compilable con `mvn clean compile`
- [x] Ejecutable con `mvn exec:java`
- [x] Código comentado y legible
- [x] README con instrucciones
- [x] Documentación detallada (RESUMEN.md)
- [x] Visualización gráfica funcional
- [x] 2 grafos distintos
- [x] 3 pares O/D por grafo (6 total)
- [x] Problema 1 y 2 resueltos

---

## 💬 Soporte

Si alguien tiene problemas:
1. Verificar que Java y Maven están instalados: `java -version` y `mvn -version`
2. Ejecutar: `mvn clean compile`
3. Si falla, enviar el mensaje de error completo
4. Revisar este README en la sección "Solución de Problemas"

**¡Listo para compartir!** 🚀
