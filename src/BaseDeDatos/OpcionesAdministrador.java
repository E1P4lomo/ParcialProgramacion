/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BaseDeDatos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.mindrot.jbcrypt.BCrypt;

public class OpcionesAdministrador extends JFrame {

    private conexion conex;
    private int usuarioId;

    public OpcionesAdministrador(int usuarioId) {
        this.usuarioId = usuarioId;
        this.conex = new conexion();

        setTitle("Opciones Administrador");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField usuarioField = new JTextField(10);
        JPasswordField contraseñaField = new JPasswordField(10);
        JTextField dniField = new JTextField(10);
        JTextField nombreField = new JTextField(10);
        JTextField apellidoField = new JTextField(10);
        JTextField correoField = new JTextField(10);
        JTextField direccionField = new JTextField(10);
        JTextField localidadField = new JTextField(10);

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

        JButton guardar = new JButton("Guardar");
        panel.add(guardar);

        add(panel);

        guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = usuarioField.getText();
                String contraseña = new String(contraseñaField.getPassword());
                String dni = dniField.getText();
                String nombre = nombreField.getText();
                String apellido = apellidoField.getText();
                String correo = correoField.getText();
                String direccion = direccionField.getText();
                String localidad = localidadField.getText();

                if (usuario.isEmpty() || contraseña.isEmpty() || dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || direccion.isEmpty() || localidad.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos.");
                    return;
                }

                if (!validarDNI(dni)) {
                    JOptionPane.showMessageDialog(null, "El DNI debe tener exactamente 8 caracteres numéricos.");
                    return;
                }

                if (!validarCorreo(correo)) {
                    JOptionPane.showMessageDialog(null, "El correo electrónico debe tener un formato válido (ejemplo@dominio.com).");
                    return;
                }

                String contraseñaEncriptada = BCrypt.hashpw(contraseña, BCrypt.gensalt());

                // Aquí irían las llamadas a métodos de la clase `conexion` para actualizar la información del usuario y sus datos personales
                conex.actualizarUsuario(usuarioId, usuario, contraseñaEncriptada, dni, nombre, apellido, correo, direccion, localidad);

                JOptionPane.showMessageDialog(null, "Datos actualizados correctamente.");
            }
        });
    }

    private boolean validarDNI(String dni) {
        return dni.matches("\\d{8}");
    }

    private boolean validarCorreo(String correo) {
        return correo.matches("^[\\w-\\.]+@[\\w-\\.]+\\.[a-z]{2,3}$");
    }

}
