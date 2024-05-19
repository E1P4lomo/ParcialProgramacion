/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BaseDeDatos;

import java.sql.*;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

public class conexion {

    Connection SQLconexion;

    public conexion() {
        String host = "localhost";
        String puerto = "3306";
        String nameDB = "prueba";
        String usuario = "root";
        String pass = "";
        String drive = "com.mysql.cj.jdbc.Driver";
        String databaseURL = "jdbc:mysql://" + host + ":" + puerto + "/" + nameDB + "?useSSL=false";

        try {
            Class.forName(drive);
            SQLconexion = DriverManager.getConnection(databaseURL, usuario, pass);
            System.out.println("Base de datos conectada");
        } catch (Exception ex) {
            System.out.println("Error al conectar con la base de datos");
        }
    }

    public Connection conectarDB() {
        return SQLconexion;
    }

    public boolean usuarioExiste(String usuario) {
        try {
            String query = "SELECT COUNT(*) FROM registro WHERE usuario = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error al verificar si el usuario existe: " + ex.getMessage());
        }
        return false;
    }

    public boolean validarCredenciales(String usuario, String contraseña) {
        try {
            // Preparar la consulta SQL
            String query = "SELECT password FROM registro WHERE usuario = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setString(1, usuario);

            // Ejecutar la consulta
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                // Verificar la contraseña ingresada con el hash almacenado
                return PasswordHashing.checkPassword(contraseña, hashedPassword);
            } else {
                // No se encontró el usuario en la base de datos
                return false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al validar credenciales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            return false;
        }
    }

    public void insertarDatos(String usuario, String contraseña) {
        // Hashear la contraseña antes de insertarla en la base de datos
        String hashedPassword = PasswordHashing.hashPassword(contraseña);

        try {
            // Preparar la consulta SQL
            String query = "INSERT INTO registro (usuario, password) VALUES (?, ?)";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setString(1, usuario);
            ps.setString(2, hashedPassword);

            // Ejecutar la consulta
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

    public void eliminarContacto(int id) {
        try {
            String query = "DELETE FROM contactos WHERE id = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
               JOptionPane.showMessageDialog(null, "Contacto Eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
               JOptionPane.showMessageDialog(null, "Error al Eliminar el contacto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al Eliminar el contacto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarUsuario(int usuarioId, String usuario, String contraseña, String dni, String nombre, String apellido, String correo, String direccion, String localidad) {
    try {
        // Actualizar los datos del usuario en la tabla "registro"
        String query = "UPDATE registro SET usuario = ?, password = ? WHERE id = ?";
        PreparedStatement ps = this.SQLconexion.prepareStatement(query);
        ps.setString(1, usuario);
        ps.setString(2, contraseña);
        ps.setInt(3, usuarioId);
        ps.executeUpdate();

        // Actualizar los datos personales en la tabla "datos_personales"
        query = "UPDATE datos_personales SET dni = ?, nombre = ?, apellido = ?, correo_electronico = ?, direccion = ?, localidad = ? WHERE usuario_id = ?";
        ps = this.SQLconexion.prepareStatement(query);
        ps.setString(1, dni);
        ps.setString(2, nombre);
        ps.setString(3, apellido);
        ps.setString(4, correo);
        ps.setString(5, direccion);
        ps.setString(6, localidad);
        ps.setInt(7, usuarioId);
        ps.executeUpdate();

        // Mostrar un mensaje de éxito
        JOptionPane.showMessageDialog(null, "Datos actualizados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        // Mostrar un mensaje de error si ocurre una excepción SQL
        JOptionPane.showMessageDialog(null, "Error al actualizar los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public ResultSet obtenerDatosUsuario(int usuarioId) {
        try {
            String query = "SELECT * FROM registro WHERE id = ?";
            PreparedStatement ps = SQLconexion.prepareStatement(query);
            ps.setInt(1, usuarioId);
            return ps.executeQuery();
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al obtener los datos del usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

   public void actualizarContraseña(int usuarioId, String nuevaContraseña) {
    try {
        // Hashear la nueva contraseña antes de actualizarla
        String contraseñaEncriptada = BCrypt.hashpw(nuevaContraseña, BCrypt.gensalt());
        String query = "UPDATE registro SET password = ? WHERE id = ?";
        PreparedStatement ps = SQLconexion.prepareStatement(query);
        ps.setString(1, contraseñaEncriptada);
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

    public void cerrarConexion() {
        try {
            if (SQLconexion != null) {
                SQLconexion.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al cerrar la conexión: " + ex.getMessage());
        }
    }

    void actualizarUsuario(int usuarioId, String dni, String nombre, String apellido, String correo, String direccion, String localidad) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
