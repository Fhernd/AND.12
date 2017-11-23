package co.ortizol.models;

/**
 * Representa una entidad con los datos de un contacto.
 */
public class Contacto {
    /**
     * Representa el nombre del contacto.
     */
    private String nombre;
    /**
     * Representa el teléfono del contacto.
     */
    private String telefono;

    /**
     * Constructor de un Contacto sin valores de inicialización.
     */
    public Contacto() {
    }

    /**
     * Crea un conteacto con su nombre y teléfono.
     * @param nombre Nombre del contacto.
     * @param telefono Teléfono del contacto.
     */
    public Contacto(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    /**
     * Obtiene el nombre del contacto.
     * @return Nombre del contacto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el número de teléfono del contacto.
      * @return Número de teléfono del contacto.
     */
    public String getTelefono() {
        return telefono;
    }
}
