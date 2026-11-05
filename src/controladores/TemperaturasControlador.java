package controladores;

import modelos.Temperatura;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import servicios.TemperaturasServicio;
import vistas.TemperaturasVista;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TemperaturasControlador {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TemperaturasVista vista;
    private List<Temperatura> temperaturas;

    public TemperaturasControlador(TemperaturasVista vista) {
        this.vista = vista;

        this.vista.setGraficarClick(evento -> graficar());
        this.vista.setBuscarExtremosClick(evento -> buscarExtremos());
        cargarDatos();
    }

    private void cargarDatos() {
        String nombreArchivo = System.getProperty("user.dir") + "/src/datos/Temperaturas.csv";
        temperaturas = TemperaturasServicio.getDatos(nombreArchivo);

        if (temperaturas.isEmpty()) {
            vista.mostrarMensaje("No se pudieron cargar datos desde:\n" + nombreArchivo);
            return;
        }

        // Fechas iniciales de los selectores: primer y último día con datos
        var fechaMin = TemperaturasServicio.getFechaMinima(temperaturas);
        var fechaMax = TemperaturasServicio.getFechaMaxima(temperaturas);
        fechaMin.ifPresent(vista::setFechaDesde);
        fechaMax.ifPresent(vista::setFechaHasta);
        fechaMin.ifPresent(vista::setFechaConsulta);
    }

    private void graficar() {
        LocalDate desde = vista.getFechaDesde();
        LocalDate hasta = vista.getFechaHasta();

        if (desde.isAfter(hasta)) {
            vista.mostrarMensaje("La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            return;
        }

        var datosFiltrados = TemperaturasServicio.filtrarPorRango(temperaturas, desde, hasta);
        var promedios = TemperaturasServicio.getPromedioPorCiudad(datosFiltrados);

        if (promedios.isEmpty()) {
            vista.mostrarMensaje("No hay datos de temperatura en el rango seleccionado.");
            return;
        }

        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        promedios.forEach((ciudad, promedio) -> datos.addValue(promedio, "Promedio", ciudad));

        JFreeChart graficador = ChartFactory.createBarChart(
                "Temperatura promedio por ciudad (" + desde.format(FORMATO_FECHA)
                        + " - " + hasta.format(FORMATO_FECHA) + ")",
                "Ciudad",
                "Temperatura promedio (°C)",
                datos,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        BarRenderer renderer = (BarRenderer) graficador.getCategoryPlot().getRenderer();
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00")));
        renderer.setDefaultItemLabelsVisible(true);

        ChartPanel renderizador = new ChartPanel(graficador);
        renderizador.setPreferredSize(new Dimension(500, 270));

        vista.mostrarGrafica(renderizador);
        vista.activarPestaña(0);
    }

    private void buscarExtremos() {
        LocalDate fecha = vista.getFechaConsulta();

        var datosFecha = TemperaturasServicio.filtrarPorFecha(temperaturas, fecha);
        var masCalurosa = TemperaturasServicio.getCiudadMasCalurosa(datosFecha);
        var menosCalurosa = TemperaturasServicio.getCiudadMenosCalurosa(datosFecha);

        String fechaTexto = fecha.format(FORMATO_FECHA);

        String resultado = masCalurosa
                .flatMap(mas -> menosCalurosa.map(menos -> String.format(
                        "<html><h3>Fecha: %s</h3>"
                                + "<p>Ciudad más calurosa: <b>%s</b> (%.1f °C)</p>"
                                + "<p>Ciudad menos calurosa: <b>%s</b> (%.1f °C)</p></html>",
                        fechaTexto,
                        mas.getCiudad(), mas.getTemperatura(),
                        menos.getCiudad(), menos.getTemperatura())))
                .orElse("<html><h3>No hay datos de temperatura para la fecha " + fechaTexto + "</h3></html>");

        vista.mostrarExtremos(resultado);
        vista.activarPestaña(1);
    }
}
