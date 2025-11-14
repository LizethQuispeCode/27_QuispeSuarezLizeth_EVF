package org.example.view;

import controller.IncidenciaController;
import model.Incidencia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame {
    private IncidenciaController controller = new IncidenciaController();

    // componentes
    private JTextField txtId = new JTextField(5);
    private JComboBox<String> cbTipo;
    private JTextField txtAula = new JTextField(10);
    private JTextField txtFecha = new JTextField(10); // formato yyyy-MM-dd
    private JComboBox<String> cbEstado;
    private JTextArea taDescripcion = new JTextArea(3, 20);

    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField txtBuscarTipo = new JTextField(10);
    private JTextField txtBuscarAula = new JTextField(10);
    private JComboBox<String> cbBuscarEstado;

    public MainFrame() {
        super("Sistema de Registro de Incidencias Técnicas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        cbTipo = new JComboBox<>(new String[]{"PC", "Proyector", "Conectividad", "Impresora", "Otro"});
        cbEstado = new JComboBox<>(new String[]{"Pendiente", "Procesando", "Resuelto"});
        cbBuscarEstado = new JComboBox<>(new String[]{"", "Pendiente", "Procesando", "Resuelto"});

        buildUI();
        cargarTabla();
        setVisible(true);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; form.add(new JLabel("ID:"), g);
        g.gridx=1; form.add(txtId, g); txtId.setEditable(false);

        g.gridx=0; g.gridy=1; form.add(new JLabel("Tipo*:"), g);
        g.gridx=1; form.add(cbTipo, g);

        g.gridx=0; g.gridy=2; form.add(new JLabel("Aula*:"), g);
        g.gridx=1; form.add(txtAula, g);

        g.gridx=0; g.gridy=3; form.add(new JLabel("Fecha* (yyyy-mm-dd):"), g);
        g.gridx=1; form.add(txtFecha, g);

        g.gridx=0; g.gridy=4; form.add(new JLabel("Estado*:"), g);
        g.gridx=1; form.add(cbEstado, g);

        g.gridx=0; g.gridy=5; form.add(new JLabel("Descripción:"), g);
        g.gridx=1; form.add(new JScrollPane(taDescripcion), g);

        JPanel botones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        botones.add(btnGuardar); botones.add(btnNuevo); botones.add(btnActualizar); botones.add(btnEliminar);

        btnGuardar.addActionListener(e -> guardar());
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"ID","Tipo","Aula","Fecha","Estado","Descripción"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2) cargarSeleccionEnFormulario();
            }
        });

        // Buscador
        JPanel buscador = new JPanel();
        buscador.setBorder(BorderFactory.createTitledBorder("Buscar"));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListarTodos = new JButton("Listar todos");
        JButton btnReportePendientes = new JButton("Reporte: Pendientes");
        JButton btnExportCSV = new JButton("Exportar CSV");

        buscador.add(new JLabel("Tipo:")); buscador.add(txtBuscarTipo);
        buscador.add(new JLabel("Aula:")); buscador.add(txtBuscarAula);
        buscador.add(new JLabel("Estado:")); buscador.add(cbBuscarEstado);
        buscador.add(btnBuscar); buscador.add(btnListarTodos); buscador.add(btnReportePendientes); buscador.add(btnExportCSV);

        btnBuscar.addActionListener(e -> buscar());
        btnListarTodos.addActionListener(e -> cargarTabla());
        btnReportePendientes.addActionListener(e -> reportePendientes());
        btnExportCSV.addActionListener(e -> exportarCSV());

        // disposición general
        JPanel left = new JPanel(new BorderLayout());
        left.add(form, BorderLayout.NORTH);
        left.add(botones, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, new JScrollPane(table));
        split.setDividerLocation(350);

        split.setResizeWeight(0.3);
        split.setDividerLocation(300);


        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(split, BorderLayout.CENTER);
        getContentPane().add(buscador, BorderLayout.NORTH);
    }

    private void guardar() {
        try {
            Incidencia i = leerFormularioSinId();
            if (!i.isValid()) { JOptionPane.showMessageDialog(this, "Complete los campos obligatorios (*)"); return; }
            controller.crearIncidencia(i);
            JOptionPane.showMessageDialog(this, "Incidencia registrada (ID=" + i.getId() + ")");
            cargarTabla();
            limpiarFormulario();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    private void actualizar() {
        try {
            String idText = txtId.getText();
            if (idText == null || idText.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione una incidencia para actualizar (doble click)."); return; }
            Incidencia i = leerFormularioConId();
            controller.actualizarIncidencia(i);
            JOptionPane.showMessageDialog(this, "Actualizado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        try {
            String idText = txtId.getText();
            if (idText == null || idText.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione una incidencia para eliminar (doble click)."); return; }
            int id = Integer.parseInt(idText);
            int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar incidencia ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                controller.eliminarIncidencia(id);
                cargarTabla();
                limpiarFormulario();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
        }
    }

    private Incidencia leerFormularioSinId() {
        String tipo = cbTipo.getSelectedItem().toString();
        String aula = txtAula.getText().trim();
        LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
        String estado = cbEstado.getSelectedItem().toString();
        String desc = taDescripcion.getText().trim();
        return new Incidencia(tipo, aula, fecha, estado, desc);
    }

    private Incidencia leerFormularioConId() {
        int id = Integer.parseInt(txtId.getText().trim());
        Incidencia i = leerFormularioSinId();
        i.setId(id);
        return i;
    }

    private void limpiarFormulario() {
        txtId.setText("");
        cbTipo.setSelectedIndex(0);
        txtAula.setText("");
        txtFecha.setText(LocalDate.now().toString());
        cbEstado.setSelectedIndex(0);
        taDescripcion.setText("");
    }

    private void cargarSeleccionEnFormulario() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        txtId.setText(tableModel.getValueAt(r,0).toString());
        cbTipo.setSelectedItem(tableModel.getValueAt(r,1).toString());
        txtAula.setText(tableModel.getValueAt(r,2).toString());
        txtFecha.setText(tableModel.getValueAt(r,3).toString());
        cbEstado.setSelectedItem(tableModel.getValueAt(r,4).toString());
        taDescripcion.setText(tableModel.getValueAt(r,5) != null ? tableModel.getValueAt(r,5).toString() : "");
    }

    private void cargarTabla() {
        try {
            List<Incidencia> lista = controller.listarTodas();
            poblarTabla(lista);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar tabla: " + ex.getMessage());
        }
    }

    private void poblarTabla(List<Incidencia> lista) {
        tableModel.setRowCount(0);
        for (Incidencia i : lista) {
            tableModel.addRow(new Object[]{
                    i.getId(), i.getTipo(), i.getAula(), i.getFecha().toString(), i.getEstado(), i.getDescripcion()
            });
        }
    }

    private void buscar() {
        try {
            String tipo = txtBuscarTipo.getText().trim();
            String aula = txtBuscarAula.getText().trim();
            String estado = cbBuscarEstado.getSelectedItem() == null ? "" : cbBuscarEstado.getSelectedItem().toString();
            if ("".equals(estado)) estado = "";
            List<Incidencia> lista = controller.buscar(tipo, aula, estado);
            poblarTabla(lista);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error en búsqueda: " + ex.getMessage());
        }
    }

    private void reportePendientes() {
        try {
            List<Incidencia> lista = controller.listarPorEstado("Pendiente");
            poblarTabla(lista);
            JOptionPane.showMessageDialog(this, "Reporte: incidencias con estado Pendiente mostradas en la tabla.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + ex.getMessage());
        }
    }

    private void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
        try (java.io.PrintWriter pw = new java.io.PrintWriter(path)) {
            // cabecera
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                pw.print(tableModel.getColumnName(c));
                if (c < tableModel.getColumnCount()-1) pw.print(",");
            }
            pw.println();
            // filas
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object val = tableModel.getValueAt(r,c);
                    String s = val == null ? "" : val.toString().replaceAll("\"", "\"\"");
                    if (s.contains(",") || s.contains("\"") || s.contains("\n")) s = "\"" + s + "\"";
                    pw.print(s);
                    if (c < tableModel.getColumnCount()-1) pw.print(",");
                }
                pw.println();
            }
            JOptionPane.showMessageDialog(this, "CSV exportado a: " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exportando CSV: " + ex.getMessage());
        }
    }
}
