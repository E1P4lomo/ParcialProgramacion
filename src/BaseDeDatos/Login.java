/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BaseDeDatos;
import java.sql.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class Login extends JFrame {

    JPanel panel1;
    JLabel titulo;
    JSeparator separador;
    JLabel nombre;
    JLabel contraseña;
    JTextArea textArea1;
    JTextArea textArea2;
    JButton registrar;
    JButton ingresar;

    conexion conex = new conexion();

    public Login() {
        setSize(500, 500);
        setLocationRelativeTo(null);
        iniciarComponente();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void iniciarComponente() {
        Panel();
        etiquetas();
        botones();
    }

    public void Panel() {
        panel1 = new JPanel();
        panel1.setLayout(null);
        this.getContentPane().add(panel1);
    }

    public void etiquetas() {
        titulo = new JLabel("LOGIN", SwingConstants.CENTER);
        titulo.setBounds(150, 30, 180, 40);
        titulo.setOpaque(true);
        titulo.setBackground(Color.cyan);
        titulo.setFont(new Font("Bookman Old Style", Font.BOLD, 15));
        panel1.add(titulo);

        separador = new JSeparator();
        separador.setBounds(1, 80, 500, 50);
        panel1.add(separador);

        nombre = new JLabel("Usuario: ");
        nombre.setBounds(20, 100, 80, 30);
        nombre.setOpaque(true);
        nombre.setFont(new Font("Arial Black", Font.ITALIC, 12));
        panel1.add(nombre);

        contraseña = new JLabel("Contraseña: ", SwingConstants.CENTER);
        contraseña.setBounds(20, 160, 80, 30);
        contraseña.setOpaque(true);
        contraseña.setFont(new Font("Arial Black", Font.ITALIC, 11));
        panel1.add(contraseña);
    }

    public void botones() {
        textArea1 = new JTextArea();
        textArea1.setBounds(110, 108, 150, 20);
        panel1.add(textArea1);

        textArea2 = new JTextArea();
        textArea2.setBounds(110, 168, 150, 20);
        panel1.add(textArea2);

        ingresar = new JButton("Ingresar");
        ingresar.setBounds(10, 380, 140, 40);
        ingresar.setFont(new Font("arial", Font.BOLD, 12));
        panel1.add(ingresar);

        registrar = new JButton("Registrarse");
        registrar.setBounds(330, 380, 140, 40);
        registrar.setFont(new Font("arial", Font.BOLD, 12));
        panel1.add(registrar);

        ActionListener accion = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = textArea1.getText();
                String contraseña = textArea2.getText();
                conex.insertarDatos(usuario, contraseña);
            }
        };

        registrar.addActionListener(accion);

        ActionListener accionIngreso = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = textArea1.getText();
                String contraseña = textArea2.getText();

                if (usuario.isEmpty() || contraseña.isEmpty()) {
                    System.out.println("El usuario y la contraseña no pueden estar vacíos.");
                } else if (conex.validarCredenciales(usuario, contraseña)) {
                    int usuarioId = obtenerUsuarioId(usuario);  // Debes implementar este método
                    Contactos contactos = new Contactos(usuarioId);
                    contactos.setVisible(true);
                    dispose();
                } else {
                    System.out.println("Credenciales incorrectas.");
                }
            }
        };

        ingresar.addActionListener(accionIngreso);
    }

    private int obtenerUsuarioId(String usuario) {
        try {
            String query = "SELECT id FROM registro WHERE usuario = ?";
            PreparedStatement ps = conex.conectarDB().prepareStatement(query);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener el ID del usuario: " + ex.getMessage());
        }
        return -1; // Indica que no se encontró el usuario
    }
    
    
}
