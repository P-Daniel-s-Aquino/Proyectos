package ar.edu.tp;

public class App {
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("java.awt.headless", "false");

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println(  "║ SISTEMA DE TRANSPORTE HOVERBOARD + TRANVÍA         ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        var g1 = GestorDeDatos.crearGrafo1();
        GestorDeDatos.analizarYMostrar("RED PEQUEÑA - CENTRO URBANO", g1);

        var g2 = GestorDeDatos.crearGrafo2();
        GestorDeDatos.analizarYMostrar("RED GRANDE - METROPOLIS", g2);
    }
}
