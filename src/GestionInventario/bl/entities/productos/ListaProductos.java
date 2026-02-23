package GestionInventario.bl.entities.productos;

import java.time.Duration;
import java.util.Date;

public class ListaProductos {


    //Atributos
    private  Producto primero;

    //metodos
    //Constructor
    public ListaProductos() {
        this.primero = null;
    }

    public Producto getPrimero() {
        return primero;
    }
    public void setPrimero(Producto primero) {
        this.primero = primero;
    }

    //Operaciones

    //--------------------------CREATE-------------------------
    public void insertarNodoInicio(String nombre, double precio, int cantidadJugadores, Duration duracionJuego, int edadMinima, String categoria,String imagenProducto){
        Producto nuevo = new Producto(nombre,precio,cantidadJugadores,duracionJuego,edadMinima,categoria,imagenProducto);
        nuevo.setSiguiente(primero); //preservar la lista mediante la conservacion de la referencia al 1
        setPrimero(nuevo); //Insercion del nuevo nodo como primero en la lista
    }

    private boolean estaVacia(){
        return primero == null;
    }

    public void insertarNodoFinal(String nombre, double precio, int cantidadJugadores, Duration duracionJuego, int edadMinima, String categoria,String imagenProducto){
        Producto nuevo = new Producto(nombre,precio,cantidadJugadores,duracionJuego,edadMinima,categoria,imagenProducto);
        if (estaVacia()){
            setPrimero(nuevo);
            return;
        }

        Producto temp = primero;

        while (temp.getSiguiente() != null){
            temp = temp.getSiguiente();
            temp.setSiguiente(nuevo); // pasar la referencia de temp al siguiente nodo
        }

        temp.setSiguiente(nuevo);//Ponerle al ultimo nodo el nuevo como siguiente
    }



    //--------------------------READ-------------------------

    public Producto buscar(String nombre){
        if (estaVacia()){
            System.out.println("La lista esta vacia");
            return null;
        }

        Producto temp = primero;
        while ( temp != null && !temp.getNombre().equals(nombre)){
            temp = temp.getSiguiente();
        }

        if (temp == null){
            System.out.println("El nombre no se encontro en la lista");
        } else  {
            System.out.println("El nombre se encontro en la lista");
        }

        return temp;
    }

    public  void  mostrarLista(){
        if (estaVacia()){
            System.out.println("La lista esta vacia");
            return;
        }

        Producto temp = primero;
        while (temp != null){
            System.out.println(temp);
            temp = temp.getSiguiente();
        }
    }



    //--------------------------DELETE-------------------------

    public Producto eliminarNodo(String nombre){
        if (estaVacia()) return null;

        Producto temp = primero;
        Producto anteriorTemp = temp;

        while ( temp != null && !temp.getNombre().equals(nombre)){
            anteriorTemp = temp; //poner al dia al anterior con respecto al temporar alntes de moverlo
            temp = temp.getSiguiente(); // pasar la referencia de temp al siguiente nodo
        }

        if (temp == null){
            System.out.println("El nombre no se encontro en la lista");
        } else  {
            System.out.println("El nombre se elimino de la lista");
            anteriorTemp.setSiguiente(temp.getSiguiente()); //conectamos al anterior del temporal con su siguiente
        }

        return temp;
    }




}
