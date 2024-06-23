package BaseDeDatos;

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import org.mindrot.jbcrypt.BCrypt;

public class Contactos extends JFrame {

    private conexion conex;
    private int usuarioId;

    public Contactos(int usuarioId) {
        this.usuarioId = usuarioId;
        this.conex = new conexion();

        setTitle("Contactos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable tablaContactos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tablaContactos);

        // Botones CRUD
        JButton agregar = new JButton("Agregar");
        JButton actualizar = new JButton("Actualizar");
        JButton eliminar = new JButton("Eliminar");
        JButton opcionesAdmin = new JButton("Opciones de Administrador");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(agregar);
        buttonPanel.add(actualizar);
        buttonPanel.add(eliminar);
        buttonPanel.add(opcionesAdmin);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Agregar ActionListener al botón Opciones de Administrador
        opcionesAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarOpcionesAdministrador();
            }
        });

        // Listener para cargar contactos al abrir la ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                cargarContactos(tablaContactos);
            }
        });

        // Agregar listener
        agregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarContacto();
                cargarContactos(tablaContactos);
            }
        });

        // Actualizar listener
        actualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaContactos.getSelectedRow();
                if (selectedRow >= 0) {
                    actualizarContacto(tablaContactos, selectedRow);
                    cargarContactos(tablaContactos);
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un contacto para actualizar.");
                }
            }
        });

        // Eliminar listener
        eliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaContactos.getSelectedRow();
                if (selectedRow >= 0) {
                    eliminarContacto(tablaContactos, selectedRow);
                    cargarContactos(tablaContactos);
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un contacto para eliminar.");
                }
            }
        });

    }

    private void cargarContactos(JTable tablaContactos) {
        ResultSet rs = conex.obtenerContactos(usuarioId);
        try {
            // Supongamos que utilizamos DefaultTableModel para llenar la tabla
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "DNI", "Nombre", "Apellido", "Correo Electrónico", "Dirección", "Localidad"}, 0);
            while (rs.next()) {
                int id = rs.getInt("id");
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String correoElectronico = rs.getString("correo_electronico");
                String direccion = rs.getString("direccion");
                String localidad = rs.getString("localidad");
                model.addRow(new Object[]{id, dni, nombre, apellido, correoElectronico, direccion, localidad});
            }
            tablaContactos.setModel(model);
        } catch (SQLException ex) {
            System.out.println("Error al cargar los contactos: " + ex.getMessage());
        }
    }

    private void agregarContacto() {
        JTextField dniField = new JTextField(10);
        JTextField nombreField = new JTextField(10);
        JTextField apellidoField = new JTextField(10);
        JTextField correoField = new JTextField(10);
        JTextField direccionField = new JTextField(10);
        JTextField localidadField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 2));
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

        int result = JOptionPane.showConfirmDialog(null, panel, "Agregar Contacto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String dni = dniField.getText();
            String nombre = nombreField.getText();
            String apellido = apellidoField.getText();
            String correo = correoField.getText();
            String direccion = direccionField.getText();
            String localidad = localidadField.getText();

            if (!validarDNI(dni)) {
                JOptionPane.showMessageDialog(null, "El DNI debe tener exactamente 8 caracteres numéricos.");
                return;
            }

            if (!validarCorreo(correo)) {
                JOptionPane.showMessageDialog(null, "El correo electrónico debe tener un formato válido (ejemplo@dominio.com).");
                return;
            }

            conex.insertarContacto(dni, nombre, apellido, correo, direccion, localidad, usuarioId);
        }
    }

    private void actualizarContacto(JTable tablaContactos, int selectedRow) {
        DefaultTableModel model = (DefaultTableModel) tablaContactos.getModel();
        int id = (int) model.getValueAt(selectedRow, 0);
        String dni = (String) model.getValueAt(selectedRow, 1);
        String nombre = (String) model.getValueAt(selectedRow, 2);
        String apellido = (String) model.getValueAt(selectedRow, 3);
        String correo = (String) model.getValueAt(selectedRow, 4);
        String direccion = (String) model.getValueAt(selectedRow, 5);
        String localidad = (String) model.getValueAt(selectedRow, 6);

        JTextField dniField = new JTextField(dni, 10);
        JTextField nombreField = new JTextField(nombre, 10);
        JTextField apellidoField = new JTextField(apellido, 10);
        JTextField correoField = new JTextField(correo, 10);
        JTextField direccionField = new JTextField(direccion, 10);
        JTextField localidadField = new JTextField(localidad, 10);

        JPanel panel = new JPanel(new GridLayout(0, 2));
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

        int result = JOptionPane.showConfirmDialog(null, panel, "Actualizar Contacto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            dni = dniField.getText();
            nombre = nombreField.getText();
            apellido = apellidoField.getText();
            correo = correoField.getText();
            direccion = direccionField.getText();
            localidad = localidadField.getText();

            if (!validarDNI(dni)) {
                JOptionPane.showMessageDialog(null, "El DNI debe tener exactamente 8 caracteres numéricos.");
                return;
            }

            if (!validarCorreo(correo)) {
                JOptionPane.showMessageDialog(null, "El correo electrónico debe tener un formato válido (ejemplo@dominio.com).");
                return;
            }

            conex.actualizarContacto(id, dni, nombre, apellido, correo, direccion, localidad);
        }
    }

    private boolean validarDNI(String dni) {
        return dni.matches("\\d{8}");
    }

    private boolean validarCorreo(String correo) {
        return correo.matches("^[\\w-\\.]+@[\\w-\\.]+\\.[a-z]{2,3}$");
    }

    private void eliminarContacto(JTable tablaContactos, int selectedRow) {
        DefaultTableModel model = (DefaultTableModel) tablaContactos.getModel();
        int id = (int) model.getValueAt(selectedRow, 0);
        int result = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar este contacto?", "Eliminar Contacto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            conex.eliminarContacto(id);
        }
    }

    private void mostrarOpcionesAdministrador() {
        JFrame frame = new JFrame("Opciones de Administrador");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        frame.add(panel, BorderLayout.CENTER);

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

        JButton actualizarBtn = new JButton("Actualizar");
        JButton eliminarBtn = new JButton("Eliminar");
        JButton salirBtn = new JButton("Salir");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(actualizarBtn);
        buttonPanel.add(eliminarBtn);
        buttonPanel.add(salirBtn);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Cargar los datos del usuario
        cargarDatosUsuario(usuarioField, contraseñaField, dniField, nombreField, apellidoField, correoField, direccionField, localidadField);

        // Actualizar usuario
        actualizarBtn.addActionListener(new ActionListener() {
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

                // Validar campos antes de actualizar
                if (!validarDNI(dni) || !validarCorreo(correo)) {
                    return;
                }

                // Si la contraseña no está vacía, hashearla antes de actualizar
                if (!contraseña.isEmpty()) {
                    String hashContraseña = BCrypt.hashpw(contraseña, BCrypt.gensalt());
                    conex.actualizarUsuario(usuarioId, usuario, hashContraseña, dni, nombre, apellido, correo, direccion, localidad);
                } else {

                    conex.actualizarUsuario(usuarioId, usuario, contraseña, dni, nombre, apellido, correo, direccion, localidad);
                }
            }
        });

        // Eliminar usuario
        eliminarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar este usuario?", "Eliminar Usuario", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    conex.eliminarUsuario(usuarioId);
                    frame.dispose();
                    dispose();
                    new Login().setVisible(true);
                }
            }
        });

        // Salir y volver al login
        salirBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                dispose();
                new Login().setVisible(true);
            }
        });

        frame.setVisible(true);
    }

    private void cargarDatosUsuario(JTextField usuarioField, JPasswordField contraseñaField, JTextField dniField, JTextField nombreField, JTextField apellidoField, JTextField correoField, JTextField direccionField, JTextField localidadField) {
        ResultSet rs = conex.obtenerDatosUsuario(usuarioId);
        try {
            if (rs.next()) {
                usuarioField.setText(rs.getString("usuario"));
                contraseñaField.setText(rs.getString("password"));
                dniField.setText(rs.getString("dni"));
                nombreField.setText(rs.getString("nombre"));
                apellidoField.setText(rs.getString("apellido"));
                correoField.setText(rs.getString("correo"));
                direccionField.setText(rs.getString("direccion"));
                localidadField.setText(rs.getString("localidad"));
            }
        } catch (SQLException ex) {
            System.out.println("Error al cargar los datos del usuario: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Contactos(1).setVisible(true); // Cambia '1' por el ID de usuario que desees probar
            }
        });
    }
}
