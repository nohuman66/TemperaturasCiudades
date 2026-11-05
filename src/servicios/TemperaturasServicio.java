package servicios;

import modelos.Temperatura;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemperaturasServicio {

    public static List<Temperatura> getDatos(String nombreArchivo) {
        var formatoFecha = DateTimeFormatter.ofPattern("d/M/yyyy");
        try (Stream<String> lineasArchivo = Files.lines(Paths.get(nombreArchivo), StandardCharsets.UTF_8)) {
            return lineasArchivo.skip(1)
                    .filter(linea -> !linea.isBlank())
                    .map(linea -> linea.split(","))
                    .map(textos -> new Temperatura(textos[0].trim(),
                            LocalDate.parse(textos[1].trim(), formatoFecha),
                            Double.parseDouble(textos[2].trim())
                    ))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static Optional<LocalDate> getFechaMinima(List<Temperatura> temperaturas) {
        return temperaturas.stream()
                .map(Temperatura::getFecha)
                .min(Comparator.naturalOrder());
    }

    public static Optional<LocalDate> getFechaMaxima(List<Temperatura> temperaturas) {
        return temperaturas.stream()
                .map(Temperatura::getFecha)
                .max(Comparator.naturalOrder());
    }

    public static List<Temperatura> filtrarPorRango(List<Temperatura> temperaturas,
                                                    LocalDate desde, LocalDate hasta) {
        return temperaturas.stream()
                .filter(t -> !t.getFecha().isBefore(desde) &&
                        !t.getFecha().isAfter(hasta)
                )
                .collect(Collectors.toList());
    }

    public static List<Temperatura> filtrarPorFecha(List<Temperatura> temperaturas, LocalDate fecha) {
        return temperaturas.stream()
                .filter(t -> t.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

 
    public static Map<String, Double> getPromedioPorCiudad(List<Temperatura> temperaturas) {
        return temperaturas.stream()
                .collect(Collectors.groupingBy(Temperatura::getCiudad,
                        TreeMap::new,
                        Collectors.averagingDouble(Temperatura::getTemperatura)));
    }

    public static Optional<Temperatura> getCiudadMasCalurosa(List<Temperatura> temperaturasFecha) {
        return temperaturasFecha.stream()
                .max(Comparator.comparingDouble(Temperatura::getTemperatura));
    }

    public static Optional<Temperatura> getCiudadMenosCalurosa(List<Temperatura> temperaturasFecha) {
        return temperaturasFecha.stream()
                .min(Comparator.comparingDouble(Temperatura::getTemperatura));
    }
}
