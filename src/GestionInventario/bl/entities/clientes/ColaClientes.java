package GestionInventario.bl.entities.clientes;

import java.util.ArrayList;
import java.util.List;

public class ColaClientes {

    private static class NodoCliente {
        private Cliente cliente;
        private NodoCliente siguiente;

        public NodoCliente(Cliente cliente) {
            this.cliente = cliente;
        }
    }

    private NodoCliente frentePremium;
    private NodoCliente finPremium;
    private NodoCliente frenteAfiliado;
    private NodoCliente finAfiliado;
    private NodoCliente frenteBasico;
    private NodoCliente finBasico;

    public void encolar(Cliente cliente) {
        if (cliente == null) {
            return;
        }

        NodoCliente nuevo = new NodoCliente(cliente);

        switch (cliente.getPrioridad()) {
            case 3:
                agregarEnColaPremium(nuevo);
                break;
            case 2:
                agregarEnColaAfiliado(nuevo);
                break;
            default:
                agregarEnColaBasico(nuevo);
                break;
        }
    }

    public Cliente verSiguiente() {
        if (frentePremium != null) {
            return frentePremium.cliente;
        }
        if (frenteAfiliado != null) {
            return frenteAfiliado.cliente;
        }
        if (frenteBasico != null) {
            return frenteBasico.cliente;
        }
        return null;
    }

    public Cliente atenderSiguiente() {
        if (frentePremium != null) {
            return desencolarPremium();
        }
        if (frenteAfiliado != null) {
            return desencolarAfiliado();
        }
        if (frenteBasico != null) {
            return desencolarBasico();
        }
        return null;
    }

    public boolean estaVacia() {
        return frentePremium == null && frenteAfiliado == null && frenteBasico == null;
    }

    public List<Cliente> obtenerClientesEnOrden() {
        List<Cliente> clientes = new ArrayList<Cliente>();
        agregarClientesDesde(frentePremium, clientes);
        agregarClientesDesde(frenteAfiliado, clientes);
        agregarClientesDesde(frenteBasico, clientes);
        return clientes;
    }

    private void agregarEnColaPremium(NodoCliente nuevo) {
        if (frentePremium == null) {
            frentePremium = nuevo;
            finPremium = nuevo;
        } else {
            finPremium.siguiente = nuevo;
            finPremium = nuevo;
        }
    }

    private void agregarEnColaAfiliado(NodoCliente nuevo) {
        if (frenteAfiliado == null) {
            frenteAfiliado = nuevo;
            finAfiliado = nuevo;
        } else {
            finAfiliado.siguiente = nuevo;
            finAfiliado = nuevo;
        }
    }

    private void agregarEnColaBasico(NodoCliente nuevo) {
        if (frenteBasico == null) {
            frenteBasico = nuevo;
            finBasico = nuevo;
        } else {
            finBasico.siguiente = nuevo;
            finBasico = nuevo;
        }
    }

    private Cliente desencolarPremium() {
        Cliente cliente = frentePremium.cliente;
        frentePremium = frentePremium.siguiente;
        if (frentePremium == null) {
            finPremium = null;
        }
        return cliente;
    }

    private Cliente desencolarAfiliado() {
        Cliente cliente = frenteAfiliado.cliente;
        frenteAfiliado = frenteAfiliado.siguiente;
        if (frenteAfiliado == null) {
            finAfiliado = null;
        }
        return cliente;
    }

    private Cliente desencolarBasico() {
        Cliente cliente = frenteBasico.cliente;
        frenteBasico = frenteBasico.siguiente;
        if (frenteBasico == null) {
            finBasico = null;
        }
        return cliente;
    }

    private void agregarClientesDesde(NodoCliente frente, List<Cliente> clientes) {
        NodoCliente actual = frente;
        while (actual != null) {
            clientes.add(actual.cliente);
            actual = actual.siguiente;
        }
    }
}