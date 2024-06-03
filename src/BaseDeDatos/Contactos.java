package BaseDeDatos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

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
        // Crear ventana de opciones del administrador
        JFrame ventanaAdmin = new JFrame("Opciones de Administrador");
        ventanaAdmin.setSize(400, 300);
        ventanaAdmin.setLocationRelativeTo(this);
        ventanaAdmin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear panel para los campos de usuario
        JPanel panelUsuario = new JPanel(new GridLayout(0, 2));
        JLabel lblUsuario = new JLabel("Usuario:");
        JTextField txtUsuario = new JTextField(10);
        JLabel lblContrasena = new JLabel("Contraseña:");
        JTextField txtContrasena = new JTextField(10);
        JLabel lblDni = new JLabel("DNI:");
        JTextField txtDni = new JTextField(10);
        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField(10);
        JLabel lblApellido = new JLabel("Apellido:");
        JTextField txtApellido = new JTextField(10);
        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        JTextField txtCorreo = new JTextField(10);
        JLabel lblDireccion = new JLabel("Dirección:");
        JTextField txtDireccion = new JTextField(10);
        JLabel lblLocalidad = new JLabel("Localidad:");
        JTextField txtLocalidad = new JTextField(10);

        // Agregar más campos según la estructura de tu usuario
        // Agregar campos al panel
        panelUsuario.add(lblUsuario);
        panelUsuario.add(txtUsuario);
        panelUsuario.add(lblContrasena);
        panelUsuario.add(txtContrasena);
        panelUsuario.add(lblDni);
        panelUsuario.add(txtDni);
        panelUsuario.add(lblNombre);
        panelUsuario.add(txtNombre);
        panelUsuario.add(lblApellido);
        panelUsuario.add(txtApellido);
        panelUsuario.add(lblCorreo);
        panelUsuario.add(txtCorreo);
        panelUsuario.add(lblDireccion);
        panelUsuario.add(txtDireccion);
        panelUsuario.add(lblLocalidad);
        panelUsuario.add(txtLocalidad);
        // Agregar más campos al panel según sea necesario

        // Botones de acción
        JButton btnActualizar = new JButton("Actualizar Datos");
        JButton btnCambiarUsuario = new JButton("Cambiar Usuario/Contraseña");
        JButton btnEliminarUsuario = new JButton("Eliminar Usuario");
        JButton btnGuardarCambios = new JButton("Guardar Cambios");
        
        
        
        
       btnActualizar.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Lógica para actualizar los datos del usuario
        String dni = txtDni.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String correo = txtCorreo.getText();
        String direccion = txtDireccion.getText();
        String localidad = txtLocalidad.getText();
        
        // Verificar si alguna variable está vacía antes de llamar al método actualizarUsuario
        if (!dni.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty() && !correo.isEmpty() && !direccion.isEmpty() && !localidad.isEmpty()) {
            try {
                conex.actualizarUsuario(usuarioId, null, null, dni, nombre, apellido, correo, direccion, localidad);
                JOptionPane.showMessageDialog(null, "Datos actualizados correctamente.");
            } catch (Exception ex) {
                System.out.println("Error al actualizar los datos del usuario: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error: Todos los campos son obligatorios. No se puede actualizar.");
            System.out.println("Error: Todos los campos son obligatorios. No se puede actualizar.");
        }
    }
});
        btnCambiarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para cambiar usuario y contraseña
                // Implementar la ventana para cambiar usuario y contraseña
            }
        });

        btnEliminarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para eliminar usuario
                int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar su cuenta?", "Confirmación", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    // Implementar la eliminación del usuario de la base de datos
                    conex.eliminarUsuario(usuarioId);
                    // Cerrar la ventana de opciones del administrador
                    ventanaAdmin.dispose();
                    // Cerrar la ventana de contactos
                    dispose();
                    // Implementar la redirección a la ventana de login u otra acción apropiada
                }
            }
        });

        // Crear panel para los botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(0, 1));
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCambiarUsuario);
        panelBotones.add(btnEliminarUsuario);

        // Crear panel principal para la ventana de administrador
        JPanel panelAdmin = new JPanel(new BorderLayout());
        panelAdmin.add(panelUsuario, BorderLayout.CENTER);
        panelAdmin.add(panelBotones, BorderLayout.SOUTH);

        // Agregar panel principal a la ventana de administrador
        ventanaAdmin.add(panelAdmin);

        // Mostrar ventana de administrador
        ventanaAdmin.setVisible(true);
    }



}
