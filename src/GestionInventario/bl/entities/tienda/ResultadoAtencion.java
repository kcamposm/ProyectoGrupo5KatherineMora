package GestionInventario.bl.entities.tienda;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.grafo.ResultadoRuta;

public class ResultadoAtencion {

    private boolean atendido;
    private String mensaje;
    private Cliente cliente;
    private ResultadoRuta ruta;

    public ResultadoAtencion(boolean atendido, String mensaje, Cliente cliente, ResultadoRuta ruta) {
        this.atendido = atendido;
        this.mensaje = mensaje;
        this.cliente = cliente;
        this.ruta = ruta;
    }

    public boolean isAtendido() {
        return atendido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public ResultadoRuta getRuta() {
        return ruta;
    }
}