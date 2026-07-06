package com.parkingoffice.service;

import com.parkingoffice.model.Movimiento;
import com.parkingoffice.model.TipoVehiculo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartService {

    public BufferedImage generarGraficoOcupacionTemporal(List<Movimiento> movimientos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<LocalDate, Long> ocupacionPorDia = new HashMap<>();

        for (Movimiento mov : movimientos) {
            LocalDate fecha = mov.getFechaIngreso().toLocalDate();
            ocupacionPorDia.merge(fecha, 1L, Long::sum);
        }

        java.util.SortedMap<LocalDate, Long> sorted = new java.util.TreeMap<>(ocupacionPorDia);
        for (Map.Entry<LocalDate, Long> entry : sorted.entrySet()) {
            dataset.addValue(entry.getValue(), "Ocupacion", entry.getKey().toString());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Ocupacion de Espacios por Dia",
                "Fecha",
                "Cantidad de Movimientos",
                dataset
        );

        return chart.createBufferedImage(800, 400);
    }

    public BufferedImage generarGraficoRecaudacionDiaria(List<Movimiento> movimientos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<LocalDate, BigDecimal> recaudacionPorDia = new HashMap<>();

        for (Movimiento mov : movimientos) {
            if ("FINALIZADO".equals(mov.getEstado()) && mov.getTotalPagar() != null) {
                LocalDate fecha = mov.getFechaSalida().toLocalDate();
                recaudacionPorDia.merge(fecha, mov.getTotalPagar(), BigDecimal::add);
            }
        }

        java.util.SortedMap<LocalDate, BigDecimal> sorted = new java.util.TreeMap<>(recaudacionPorDia);
        for (Map.Entry<LocalDate, BigDecimal> entry : sorted.entrySet()) {
            dataset.addValue(entry.getValue(), "Recaudacion", entry.getKey().toString());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Recaudacion Diaria",
                "Fecha",
                "Monto ($)",
                dataset
        );

        return chart.createBufferedImage(800, 400);
    }

    public BufferedImage generarGraficoDistribucionTiposVehiculo(List<Movimiento> movimientos) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        Map<String, Long> distribucion = new HashMap<>();
        for (Movimiento mov : movimientos) {
            TipoVehiculo tipo = mov.getVehiculo().getTipoVehiculo();
            String nombreTipo = tipo != null ? tipo.getNombre() : "Desconocido";
            distribucion.merge(nombreTipo, 1L, Long::sum);
        }

        for (Map.Entry<String, Long> entry : distribucion.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribucion por Tipo de Vehiculo",
                dataset,
                true,
                true,
                false
        );

        return chart.createBufferedImage(600, 400);
    }

    public void guardarGrafico(BufferedImage imagen, String rutaSalida) throws IOException {
        File archivo = new File(rutaSalida);
        archivo.getParentFile().mkdirs();
        ChartUtils.saveChartAsPNG(archivo, new JFreeChart(null), imagen.getWidth(), imagen.getHeight());
    }
}