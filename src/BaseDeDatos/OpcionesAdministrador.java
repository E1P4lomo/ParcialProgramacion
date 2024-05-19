package BaseDeDatos;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.mindrot.jbcrypt.BCrypt;

public class OpcionesAdministrador extends JFrame {

    private conexion conex;
    private int usuarioId;
    private JTextField usuarioField;
    private JPasswordField contraseñaField;
    private JTextField dniField;
    private JTextField nombreField;
    private JTextField apellidoField;
    private JTextField correoField;
    private JTextField direccionField;
    private JTextField localidadField;
    private JButton actualizarContraseñaButton;
    private JButton cargarDatosButton;
    private JButton actualizarDatosButton;

    public OpcionesAdministrador(int usuarioId) {
        this.usuarioId = usuarioId;
        this.conex = new conexion();

        setTitle("Opciones Administrador");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        usuarioField = new JTextField(10);
        usuarioField.setEditable(true);
        contraseñaField = new JPasswordField(10);
        dniField = new JTextField(10);
        nombreField = new JTextField(10);
        apellidoField = new JTextField(10);
        correoField = new JTextField(10);
        direccionField = new JTextField(10);
        localidadField = new JTextField(10);

        panel.add(new JLabel("Usuario:"));
        panel.add(usuarioField);

        panel.add(new JLabel("Contraseña:"));
        panel.add(contraseñaField);

        panel.add(new JLabel("DNI:"));
        panel.add(dniField);

        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);

        panel.add(new JLabel("Apellido:"));
        panel.add(apellidoField);

        panel.add(new JLabel("Correo Electrónico:"));
        panel.add(correoField);

        panel.add(new JLabel("Dirección:"));
        panel.add(direccionField);

        panel.add(new JLabel("Localidad:"));
        panel.add(localidadField);

        cargarDatosButton = new JButton("Cargar Datos");
        panel.add(cargarDatosButton);

        actualizarDatosButton = new JButton("Actualizar Datos");
        panel.add(actualizarDatosButton);

        actualizarContraseñaButton = new JButton("Actualizar Contraseña");
        panel.add(actualizarContraseñaButton);

        JButton cerrarSesionButton = new JButton("Cerrar Sesión");
        panel.add(cerrarSesionButton);

        add(panel);

        cargarDatosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDatosUsuario();
            }
        });

        actualizarDatosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarDatosUsuario();
            }
        });

        actualizarContraseñaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarContraseña();
            }
        });

        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });
    }

    public void cargarDatosUsuario() {
        ResultSet rs = conex.obtenerDatosUsuario(usuarioId);
        try {
            if (rs.next()) {
                usuarioField.setText(rs.getString("usuario"));
                contraseñaField.setText("********"); // Mostrar una representación de la contraseña encriptada
                dniField.setText(rs.getString("dni"));
                nombreField.setText(rs.getString("nombre"));
                apellidoField.setText(rs.getString("apellido"));
                correoField.setText(rs.getString("correo_electronico"));
                direccionField.setText(rs.getString("direccion"));
                localidadField.setText(rs.getString("localidad"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos del usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarDatosUsuario() {
        String usuario = usuarioField.getText();
        String dni = dniField.getText();
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String correo = correoField.getText();
        String direccion = direccionField.getText();
        String localidad = localidadField.getText();

        // Validar que los campos no estén vacíos
        if (usuario.isEmpty() || dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || direccion.isEmpty() || localidad.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar el formato del correo electrónico
        if (!validarCorreo(correo)) {
            JOptionPane.showMessageDialog(null, "El correo electrónico debe tener un formato válido (ejemplo@dominio.com).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Actualizar los datos del usuario en la base de datos
        conex.actualizarUsuario(usuarioId, dni, nombre, apellido, correo, direccion, localidad);
        JOptionPane.showMessageDialog(null, "Datos actualizados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void actualizarContraseña() {
        String nuevaContraseña = JOptionPane.showInputDialog(null, "Ingrese la nueva contraseña:", "Actualizar Contraseña", JOptionPane.PLAIN_MESSAGE);
        if (nuevaContraseña != null && !nuevaContraseña.isEmpty()) {
            // Actualizar la contraseña del usuario en la base de datos
            conex.actualizarContraseña(usuarioId, nuevaContraseña);
            JOptionPane.showMessageDialog(null, "Contraseña actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void cerrarSesion() {
        dispose(); // Cierra la ventana actual
        Login login = new Login(); // Vuelve al inicio de sesión
        login.setVisible(true);
    }

    private boolean validarCorreo(String correo) {
        return correo.matches("^[\\w-\\.]+@[\\w-\\.]+\\.[a-z]{2,3}$");
    }
}