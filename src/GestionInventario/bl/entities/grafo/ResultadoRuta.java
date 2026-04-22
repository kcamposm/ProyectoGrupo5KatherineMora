package GestionInventario.bl.entities.grafo;

import java.util.ArrayList;
import java.util.List;

public class ResultadoRuta {

    private boolean existeCamino;
    private List<String> camino;
    private double distanciaTotal;

    public ResultadoRuta() {
        this.existeCamino = false;
        this.camino = new ArrayList<String>();
        this.distanciaTotal = -1;
    }

    public ResultadoRuta(boolean existeCamino, List<String> camino, double distanciaTotal) {
        this.existeCamino = existeCamino;
        this.camino = camino;
        this.distanciaTotal = distanciaTotal;
    }

    public boolean isExisteCamino() {
        return existeCamino;
    }

    public void setExisteCamino(boolean existeCamino) {
        this.existeCamino = existeCamino;
    }

    public List<String> getCamino() {
        return camino;
    }

    public void setCamino(List<String> camino) {
        this.camino = camino;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(double distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }

    public String caminoComoTexto() {
        if (!existeCamino || camino == null || camino.isEmpty()) {
            return "Sin ruta disponible";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camino.size(); i++) {
            sb.append(camino.get(i));
            if (i < camino.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}