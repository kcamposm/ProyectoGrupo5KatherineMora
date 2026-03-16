package GestionInventario.bl.entities.clientes;

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
            case 3 -> {
                if (frentePremium == null) {
                    frentePremium = nuevo;
                    finPremium = nuevo;
                } else {
                    finPremium.siguiente = nuevo;
                    finPremium = nuevo;
                }
            }
            case 2 -> {
                if (frenteAfiliado == null) {
                    frenteAfiliado = nuevo;
                    finAfiliado = nuevo;
                } else {
                    finAfiliado.siguiente = nuevo;
                    finAfiliado = nuevo;
                }
            }
            default -> {
                if (frenteBasico == null) {
                    frenteBasico = nuevo;
                    finBasico = nuevo;
                } else {
                    finBasico.siguiente = nuevo;
                    finBasico = nuevo;
                }
            }
        }
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

    public boolean estaVacia() {
        return frentePremium == null && frenteAfiliado == null && frenteBasico == null;
    }

    public void mostrarCola() {
        if (estaVacia()) {
            System.out.println("No hay clientes en espera.");
            return;
        }

        System.out.println("=== Cola de clientes ===");
        mostrarSegmento("Premium", frentePremium);
        mostrarSegmento("Afiliados", frenteAfiliado);
        mostrarSegmento("Básicos", frenteBasico);
    }

    private void mostrarSegmento(String titulo, NodoCliente frente) {
        System.out.println("-- " + titulo + " --");
        if (frente == null) {
            System.out.println("(sin clientes)");
            return;
        }

        NodoCliente actual = frente;
        int posicion = 1;

        while (actual != null) {
            System.out.println(posicion + ". " + actual.cliente);
            actual = actual.siguiente;
            posicion++;
        }
    }
}