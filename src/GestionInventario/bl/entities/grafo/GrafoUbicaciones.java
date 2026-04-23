package GestionInventario.bl.entities.grafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * GrafoUbicaciones es una clase que representa un grafo no dirigido ponderado
 * para gestionar ubicaciones y las distancias entre ellas.
 * 
 * Esta clase implementa la estructura de datos de grafo utilizando una lista de adyacencia
 * y proporciona funcionalidades para:
 * - Agregar ubicaciones (nodos) al grafo
 * - Conectar ubicaciones con distancias (aristas ponderadas)
 * - Encontrar la ruta más corta entre dos ubicaciones usando el algoritmo de Dijkstra
 * - Verificar si existe una ruta entre dos ubicaciones
 * - Generar una representación textual del mapa
 * 
 * El grafo es no dirigido, lo que significa que si existe una conexión de A a B
 * con distancia d, automáticamente existe una conexión de B a A con la misma distancia.
 */
public class GrafoUbicaciones {

    private final Map<String, Map<String, Double>> adyacencias;

    public GrafoUbicaciones() {
        this.adyacencias = new LinkedHashMap<String, Map<String, Double>>();
    }

    public boolean agregarUbicacion(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        String claveExistente = resolverClave(nombre);
        if (claveExistente != null) {
            return false;
        }

        adyacencias.put(nombre.trim(), new LinkedHashMap<String, Double>());
        return true;
    }

    public boolean existeUbicacion(String nombre) {
        return resolverClave(nombre) != null;
    }

    public boolean agregarConexion(String origen, String destino, double distancia) {
        if (distancia <= 0) {
            return false;
        }

        String claveOrigen = resolverClave(origen);
        String claveDestino = resolverClave(destino);

        if (claveOrigen == null || claveDestino == null) {
            return false;
        }

        if (claveOrigen.equalsIgnoreCase(claveDestino)) {
            return false;
        }

        adyacencias.get(claveOrigen).put(claveDestino, distancia);
        adyacencias.get(claveDestino).put(claveOrigen, distancia);

        return true;
    }

    public List<String> obtenerUbicaciones() {
        List<String> ubicaciones = new ArrayList<String>(adyacencias.keySet());
        Collections.sort(ubicaciones, String.CASE_INSENSITIVE_ORDER);
        return ubicaciones;
    }

    public ResultadoRuta obtenerCaminoMasCorto(String origen, String destino) {
        String claveOrigen = resolverClave(origen);
        String claveDestino = resolverClave(destino);

        if (claveOrigen == null || claveDestino == null) {
            return new ResultadoRuta();
        }

        if (claveOrigen.equalsIgnoreCase(claveDestino)) {
            List<String> camino = new ArrayList<String>();
            camino.add(claveOrigen);
            return new ResultadoRuta(true, camino, 0);
        }

        Map<String, Double> distancias = new HashMap<String, Double>();
        Map<String, String> anteriores = new HashMap<String, String>();

        for (String ubicacion : adyacencias.keySet()) {
            distancias.put(ubicacion, Double.POSITIVE_INFINITY);
        }

        distancias.put(claveOrigen, 0.0);

        PriorityQueue<NodoDistancia> cola = new PriorityQueue<NodoDistancia>(new Comparator<NodoDistancia>() {
            @Override
            public int compare(NodoDistancia a, NodoDistancia b) {
                return Double.compare(a.distancia, b.distancia);
            }
        });

        cola.add(new NodoDistancia(claveOrigen, 0.0));

        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();

            if (actual.distancia > distancias.get(actual.nombre)) {
                continue;
            }

            if (actual.nombre.equalsIgnoreCase(claveDestino)) {
                break;
            }

            Map<String, Double> vecinos = adyacencias.get(actual.nombre);
            for (Map.Entry<String, Double> vecino : vecinos.entrySet()) {
                String nombreVecino = vecino.getKey();
                double peso = vecino.getValue();
                double nuevaDistancia = distancias.get(actual.nombre) + peso;

                if (nuevaDistancia < distancias.get(nombreVecino)) {
                    distancias.put(nombreVecino, nuevaDistancia);
                    anteriores.put(nombreVecino, actual.nombre);
                    cola.add(new NodoDistancia(nombreVecino, nuevaDistancia));
                }
            }
        }

        if (distancias.get(claveDestino) == Double.POSITIVE_INFINITY) {
            return new ResultadoRuta();
        }

        List<String> camino = new ArrayList<String>();
        String paso = claveDestino;

        while (paso != null) {
            camino.add(paso);
            paso = anteriores.get(paso);
        }

        Collections.reverse(camino);

        return new ResultadoRuta(true, camino, distancias.get(claveDestino));
    }

    public boolean hayRuta(String origen, String destino) {
        return obtenerCaminoMasCorto(origen, destino).isExisteCamino();
    }

    public String obtenerRepresentacionMapa() {
        StringBuilder sb = new StringBuilder();

        if (adyacencias.isEmpty()) {
            sb.append("El grafo no contiene ubicaciones.\n");
            return sb.toString();
        }

        List<String> ubicaciones = obtenerUbicaciones();

        for (String ubicacion : ubicaciones) {
            sb.append(ubicacion).append(" -> ");

            Map<String, Double> vecinos = adyacencias.get(ubicacion);
            if (vecinos == null || vecinos.isEmpty()) {
                sb.append("(sin conexiones)");
            } else {
                int contador = 0;
                for (Map.Entry<String, Double> entrada : vecinos.entrySet()) {
                    sb.append(entrada.getKey())
                            .append(" (")
                            .append(String.format("%.2f", entrada.getValue()))
                            .append(" km)");

                    contador++;
                    if (contador < vecinos.size()) {
                        sb.append(", ");
                    }
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public void cargarMapaBase() {
        agregarUbicacion("San Jose");
        agregarUbicacion("Heredia");
        agregarUbicacion("Cartago");
        agregarUbicacion("Alajuela");
        agregarUbicacion("Guanacaste");
        agregarUbicacion("Limón");
        agregarUbicacion("Puntarenas");

        agregarConexion("San Jose", "Heredia", 13.0);
        agregarConexion("Heredia", "Cartago", 36.5);
        agregarConexion("Cartago", "Alajuela", 44.0);
        agregarConexion("Alajuela", "Guanacaste", 216.0);
        agregarConexion("Guanacaste", "Limón", 312.0);
        agregarConexion("Limón", "Puntarenas", 208.0);
        agregarConexion("Puntarenas", "San Jose", 95.0);
    }

    private String resolverClave(String nombre) {
        if (nombre == null) {
            return null;
        }

        String buscada = nombre.trim();

        for (String clave : adyacencias.keySet()) {
            if (clave.equalsIgnoreCase(buscada)) {
                return clave;
            }
        }

        return null;
    }

    private static class NodoDistancia {
        private String nombre;
        private double distancia;

        public NodoDistancia(String nombre, double distancia) {
            this.nombre = nombre;
            this.distancia = distancia;
        }
    }
}
