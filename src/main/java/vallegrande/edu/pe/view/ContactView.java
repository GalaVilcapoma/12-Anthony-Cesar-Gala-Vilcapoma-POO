package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Vista principal de la Agenda MVC Swing con diseño moderno.
 */
public class ContactView extends JFrame {
    private final ContactController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    public ContactView(ContactController controller) {
        super("Agenda MVC Swing - Vallegrande");
        this.controller = controller;
        initUI();
        loadContacts();
        showWelcomeMessage();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // maximizada

        // Colores del tema oscuro
        Color backgroundColor = new Color(33, 33, 33);
        Color panelColor = new Color(44, 44, 44);
        Color headerColor = new Color(25, 118, 210);
        Color textColor = Color.WHITE;

        // Fuente base
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Panel principal
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(backgroundColor);
        setContentPane(contentPanel);

        // Mensaje de bienvenida
        JLabel welcomeLabel = new JLabel("¡Bienvenido a tu Agenda!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(headerColor);
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Tabla de contactos
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(baseFont);
        table.setRowHeight(30);
        table.setForeground(textColor);
        table.setBackground(panelColor);
        table.getTableHeader().setFont(baseFont.deriveFont(Font.BOLD, 18f));
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonsPanel.setBackground(backgroundColor);

        JButton addBtn = new RoundedButton("Agregar", new Color(25, 118, 210));
        addBtn.setIcon(new ImageIcon("icons/add.png"));

        JButton deleteBtn = new RoundedButton("Eliminar", new Color(244, 67, 54));
        deleteBtn.setIcon(new ImageIcon("icons/delete.png"));

        buttonsPanel.add(addBtn);
        buttonsPanel.add(deleteBtn);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Eventos botones
        addBtn.addActionListener(e -> showAddContactDialog());
        deleteBtn.addActionListener(e -> deleteSelectedContact());
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
            showToast("Contacto agregado con éxito", new Color(76, 175, 80));
        }
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showToast("Seleccione un contacto para eliminar", new Color(244, 67, 54));
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar este contacto?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
            showToast("Contacto eliminado correctamente", new Color(76, 175, 80));
        }
    }

    // Notificación tipo toast
    private void showToast(String message, Color bgColor) {
        JWindow toast = new JWindow();
        toast.setLayout(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        toast.add(label);
        toast.pack();
        toast.setLocationRelativeTo(this);
        toast.setVisible(true);

        new Timer(2000, e -> toast.dispose()).start();
    }

    // Mensaje de bienvenida al iniciar
    private void showWelcomeMessage() {
        showToast("Bienvenido a tu Agenda", new Color(25, 118, 210));
    }

    /**
     * Botón redondeado con hover
     */
    static class RoundedButton extends JButton {
        private final Color baseColor;

        public RoundedButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setHorizontalTextPosition(SwingConstants.RIGHT);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { setBackground(baseColor.darker()); }
                @Override public void mouseExited(MouseEvent e) { setBackground(baseColor); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground() != null ? getBackground() : baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
