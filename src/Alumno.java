public class Alumno {
    private int id;
    private String nombre;
    private String email;
    private String telefono;

    // Constructor
    public Alumno(int id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
    @Override
    public String toString() {
        return "id->" + id +
                ", nombre->'" + nombre + '\'' +
                ", email->'" + email + '\'' +
                ", telefono->'" + telefono ;
    }
}
