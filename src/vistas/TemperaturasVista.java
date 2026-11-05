package vistas;

import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TemperaturasVista extends JFrame {

    private JSpinner spnDesde, spnHasta, spnFechaConsulta;
    private JTabbedPane tpTemperaturas;
    private JPanel pnlGrafica;
    private JLabel lblExtremos;

    private JButton btnGraficar, btnBuscarExtremos;

    public TemperaturasVista() {

        setTitle("Temperaturas por Ciudad");
        setSize(800, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Panel superior: rango de fechas + gráfica
        JPanel pnlRango = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlRango.add(new JLabel("Desde"));
        spnDesde = crearSelectorFecha();
        pnlRango.add(spnDesde);
        pnlRango.add(new JLabel("Hasta"));
        spnHasta = crearSelectorFecha();
        pnlRango.add(spnHasta);
        btnGraficar = new JButton("Graficar promedios");
        pnlRango.add(btnGraficar);

        // Panel superior: fecha específica + ciudades extremas
        JPanel pnlFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlFecha.add(new JLabel("Fecha"));
        spnFechaConsulta = crearSelectorFecha();
        pnlFecha.add(spnFechaConsulta);
        btnBuscarExtremos = new JButton("Ciudad más y menos calurosa");
        pnlFecha.add(btnBuscarExtremos);

        JPanel pnlControles = new JPanel();
        pnlControles.setLayout(new BoxLayout(pnlControles, BoxLayout.Y_AXIS));
        pnlControles.add(pnlRango);
        pnlControles.add(pnlFecha);

        pnlGrafica = new JPanel(new BorderLayout());
        JScrollPane spGrafica = new JScrollPane(pnlGrafica);

        lblExtremos = new JLabel("Seleccione una fecha y presione el botón.");
        lblExtremos.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel pnlExtremos = new JPanel(new BorderLayout());
        pnlExtremos.add(lblExtremos, BorderLayout.CENTER);

        tpTemperaturas = new JTabbedPane();
        tpTemperaturas.addTab("Gráfica", spGrafica);
        tpTemperaturas.addTab("Ciudad más / menos calurosa", pnlExtremos);

        getContentPane().add(pnlControles, BorderLayout.NORTH);
        getContentPane().add(tpTemperaturas, BorderLayout.CENTER);
    }

    private JSpinner crearSelectorFecha() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        spinner.setPreferredSize(new Dimension(110, 25));
        return spinner;
    }

    private LocalDate aLocalDate(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void asignarFecha(JSpinner spinner, LocalDate fecha) {
        spinner.setValue(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    // getters

    public LocalDate getFechaDesde() {
        return aLocalDate(spnDesde);
    }

    public LocalDate getFechaHasta() {
        return aLocalDate(spnHasta);
    }

    public LocalDate getFechaConsulta() {
        return aLocalDate(spnFechaConsulta);
    }

    // setters

    public void setFechaDesde(LocalDate fecha) {
        asignarFecha(spnDesde, fecha);
    }

    public void setFechaHasta(LocalDate fecha) {
        asignarFecha(spnHasta, fecha);
    }

    public void setFechaConsulta(LocalDate fecha) {
        asignarFecha(spnFechaConsulta, fecha);
    }

    public void setGraficarClick(ActionListener evento) {
        btnGraficar.addActionListener(evento);
    }

    public void setBuscarExtremosClick(ActionListener evento) {
        btnBuscarExtremos.addActionListener(evento);
    }

    // metodos publicos

    public void activarPestaña(int posicion) {
        if (posicion >= 0 && posicion < tpTemperaturas.getTabCount()) {
            tpTemperaturas.setSelectedIndex(posicion);
        }
    }

    public void mostrarGrafica(ChartPanel renderizadorGrafica) {
        pnlGrafica.removeAll();
        pnlGrafica.add(renderizadorGrafica, BorderLayout.CENTER);
        pnlGrafica.revalidate();
        pnlGrafica.repaint();
    }

    public void mostrarExtremos(String textoHtml) {
        lblExtremos.setText(textoHtml);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
