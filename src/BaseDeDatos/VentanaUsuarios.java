package BaseDeDatos;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class VentanaUsuarios extends JFrame {

    private JTextArea textArea;

    public VentanaUsuarios() {
        setTitle("Lista de Usuarios");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        mostrarUsuarios();
    }

    private void mostrarUsuarios() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/prueba", "root", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT usuario FROM registro");

            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                String usuario = resultSet.getString("usuario");
                sb.append(usuario).append("\n");
            }

            textArea.setText(sb.toString());

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    
}
