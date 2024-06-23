package BaseDeDatos;

import java.sql.*;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

public class conexion {

    // Objeto Connection para manejar la conexión con la base de datos
    Connection SQLconexion;

    public conexion() {
        // Datos de conexión
        String host = "localhost";
        String puerto = "3306";
        String nameDB = "prueba";
        String usuario = "root";
        String pass = "";
        String drive = "com.mysql.cj.jdbc.Driver";
        String databaseURL = "jdbc:mysql://" + host + ":" + puerto + "/" + nameDB + "?useSSL=false";

        try {
            // Cargar el driver de MySQL
            Class.forName(drive);
            // Establecer la conexión con la base de datos
            SQLconexion = DriverManager.getConnection(databaseURL, usuario, pass);
            System.out.println("Base de datos conectada");
        } catch (Exception ex) {
            System.out.println("Error al conectar con la base de datos");
        }
    }

    // Retornar la conexión establecida
    public Connection conectarDB() {
        return SQLconexion;
    }

    // Verificar si un usuario existe en la base de datos
    public boolean usuarioExiste(String usuario) {
        try {
            String query = "SELECT * FROM registro WHERE usuario = ?";
            PreparedStatement ps = conectarDB().prepareStatement(query);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // Devuelve true si hay al menos una fila (es decir, el usuario existe)
        } catch (SQLException ex) {
            System.out.println("Error al verificar si el usuario existe: " + ex.getMessage());
            return false;
        }
    }

    // Validar las credenciales del usuario
    public boolean validarCredenciales(String usuario, String password) {
        try {
            String query = "SELECT password FROM registro WHERE usuario = ?";
            PreparedStatement ps = conectarDB().prepareStatement(query);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashContraseñaAlmacenada = rs.getString("password");
                return BCrypt.checkpw(password, hashContraseñaAlmacenada);  // Comparar la contraseña ingresada con la encriptada
            }
        } catch (SQLException ex) {
            System.out.println("Error al validar usuario: " + ex.getMessage());
        }
        return false;
    }

    // Insertar un nuevo usuario en la base de datos
    public void insertarDatos(String usuario, String password) {
        if (usuarioExiste(usuario)) {
            JOptionPane.showMessageDialog(null, "El usuario ya existe. Por favor, elija otro nombre de usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // No insertar si el usuario ya existe
        }

        // El usuario no existe, proceder con la inserción
        try {
            String hashContraseña = BCrypt.hashpw(password, BCrypt.gensalt());  // Encriptar la contraseña
            String query = "INSERT INTO registro (usuario, password) VALUES (?, ?)";
            PreparedStatement ps = conectarDB().prepareStatement(query);
            ps.setString(1, usuario);
            ps.setString(2, hashContraseña);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            System.out.println("Error al insertar datos: " + ex.getMessage());
        }
    }

    public ResultSet obtenerDatosUsuario(int usuarioId) {
        try {
            String query = "SELECT * FROM registro WHERE id = ?";
            PreparedStatement ps = conectarDB().prepareStatement(query);
            ps.setInt(1, usuarioId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            System.out.println("Error al obtener datos del usuario: " + ex.getMessage());
            return null;
        }
    }
    
    public String obtenerNombreUsuario(int usuarioId) {
    try {
        String query = "SELECT usuario FROM registro WHERE id = ?";
        PreparedStatement ps = SQLconexion.prepareStatement(query);
        ps.setInt(1, usuarioId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("usuario");
        }
    } catch (SQLException ex) {
        System.out.println("Error al obtener el nombre de usuario: " + ex.getMessage());
    }
    return null;
}
    public void actualizarDatosUsuario(int usuarioId, String dni, String nombre, String apellido, String correo, String direccion, String localidad) {
        try {
            String query = "UPDATE registro SET dni = ?, nombre = ?, apellido = ?, correo = ?, direccion = ?, localidad = ? WHERE id = ?";
            PreparedStatement ps = conectarDB().prepareStatement(query);
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, correo);
            ps.setString(5, direccion);
            ps.setString(6, localidad);
            ps.setInt(7, usuarioId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error al actualizar datos del usuario: " + ex.getMessage());
        }
    }

    public void eliminarUsuario(int usuarioId) {
        try {
            // Preparar la consulta SQL para eliminar al usuario
            String query = "DELETE FROM registro WHERE id = ?";
            PreparedStatement ps = conectarDB().prepareStatement(query);
            ps.setInt(1, usuarioId);

            // Ejecutar la consulta
            int rowsAffected = ps.executeUpdate();

            // Verificar si se eliminó el usuario correctamente
            if (rowsAffected > 0) {
                System.out.println("Usuario eliminado exitosamente.");
            } else {
                System.out.println("No se pudo eliminar el usuario.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al eliminar el usuario: " + ex.getMessage());
        }
    }

    // Insertar un nuevo contacto en la base de datos
    public void insertarContacto(String dni, String nombre, String apellido, String correoElectronico, String direccion, String localidad, int usuarioId) {
        try {
            String query = "INSERT INTO contactos (dni, nombre, apellido, correo_electronico, direccion, localidad, usuario_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, correoElectronico);
            ps.setString(5, direccion);
            ps.setString(6, localidad);
            ps.setInt(7, usuarioId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Datos insertados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al insertar los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Obtener contactos de un usuario específico
    public ResultSet obtenerContactos(int usuarioId) {
        try {
            String query = "SELECT * FROM contactos WHERE usuario_id = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setInt(1, usuarioId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener los contactos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Actualizar un contacto existente
    public void actualizarContacto(int id, String dni, String nombre, String apellido, String correoElectronico, String direccion, String localidad) {
        try {
            String query = "UPDATE contactos SET dni = ?, nombre = ?, apellido = ?, correo_electronico = ?, direccion = ?, localidad = ? WHERE id = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, correoElectronico);
            ps.setString(5, direccion);
            ps.setString(6, localidad);
            ps.setInt(7, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Contacto actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar el contacto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el contacto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Eliminar un contacto
    public void eliminarContacto(int id) {
        try {
            String query = "DELETE FROM contactos WHERE id = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Contacto eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar el contacto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el contacto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  public void actualizarUsuario(int usuarioId, String usuario, String contraseña, String dni, String nombre, String apellido, String correo, String direccion, String localidad) {
    try {
        // Verificar si el nuevo nombre de usuario ya existe
        if ((!usuarioExiste(usuario) || obtenerNombreUsuario(usuarioId).equals(usuario))) {
            String query;
            if (!contraseña.isEmpty()) {
                query = "UPDATE registro SET usuario = ?, password = ?, dni = ?, nombre = ?, apellido = ?, correo = ?, direccion = ?, localidad = ? WHERE id = ?";
            } else {
                query = "UPDATE registro SET usuario = ?, dni = ?, nombre = ?, apellido = ?, correo = ?, direccion = ?, localidad = ? WHERE id = ?";
            }
            PreparedStatement ps = conectarDB().prepareStatement(query);
            ps.setString(1, usuario);
            if (!contraseña.isEmpty()) {
                ps.setString(2, contraseña);
                ps.setString(3, dni);
                ps.setString(4, nombre);
                ps.setString(5, apellido);
                ps.setString(6, correo);
                ps.setString(7, direccion);
                ps.setString(8, localidad);
                ps.setInt(9, usuarioId);
            } else {
                ps.setString(2, dni);
                ps.setString(3, nombre);
                ps.setString(4, apellido);
                ps.setString(5, correo);
                ps.setString(6, direccion);
                ps.setString(7, localidad);
                ps.setInt(8, usuarioId);
            }
            int rowsAffected = ps.executeUpdate();
            // Mostrar mensaje dependiendo del resultado de la actualización
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Usuario actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "El usuario ya existe. Por favor, elija otro nombre de usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        System.out.println("Error al actualizar usuario: " + ex.getMessage());
    }
}


    // Actualizar la contraseña de un usuario
    public void actualizarContraseña(int usuarioId, String nuevaContraseña) {
        try {
            String query = "UPDATE registro SET password = ? WHERE id = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setString(1, nuevaContraseña);
            ps.setInt(2, usuarioId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Contraseña actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar la contraseña: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Obtener el ID del usuario actual
    public int obtenerUsuarioId(String usuario) {
        try {
            String query = "SELECT id FROM registro WHERE usuario = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener el ID del usuario: " + ex.getMessage());
        }
        return -1; // Retorna -1 si no se encuentra el usuario
    }
 
    public ResultSet obtenerDatosUsuario(String usuario) {
    ResultSet rs = null;
    try {
        String query = "SELECT * FROM registro WHERE usuario = ?";
        PreparedStatement ps = conectarDB().prepareStatement(query);
        ps.setString(1, usuario);
        rs = ps.executeQuery();
    } catch (SQLException ex) {
        System.out.println("Error al obtener los datos del usuario: " + ex.getMessage());
    }
    return rs;
}

}
