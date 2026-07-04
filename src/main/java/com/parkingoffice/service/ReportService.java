package com.parkingoffice.service;

import com.parkingoffice.core.SessionManager;
import com.parkingoffice.model.Movimiento;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.math.BigDecimal;

public class ReportService {

    public void generarTicketEntrada(Movimiento movimiento, String outputPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("PARKING OFFICE - TICKET DE ENTRADA");
                contentStream.endText();

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 650);
                contentStream.setLeading(20f);
                contentStream.showText("ID Movimiento: " + movimiento.getId());
                contentStream.newLine();
                contentStream.showText("Placa: " + movimiento.getVehiculo().getPlaca());
                contentStream.newLine();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                contentStream.showText("Fecha/Hora Ingreso: " + movimiento.getFechaIngreso().format(formatter));
                contentStream.newLine();
                contentStream.showText("Operador: " + movimiento.getUsuarioIngreso().getNombreCompleto());
                contentStream.endText();
            }

            document.save(new File(outputPath));
        }
    }

    public void generarComprobanteSalida(Movimiento movimiento, String outputPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("PARKING OFFICE - COMPROBANTE DE PAGO");
                contentStream.endText();

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 650);
                contentStream.setLeading(20f);
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                contentStream.showText("Placa: " + movimiento.getVehiculo().getPlaca());
                contentStream.newLine();
                contentStream.showText("Ingreso: " + movimiento.getFechaIngreso().format(formatter));
                contentStream.newLine();
                contentStream.showText("Salida: " + movimiento.getFechaSalida().format(formatter));
                contentStream.newLine();
                contentStream.showText("Total a Pagar: $" + movimiento.getTotalPagar());
                contentStream.newLine();
                contentStream.showText("Atendido por: " + movimiento.getUsuarioSalida().getNombreCompleto());
                contentStream.endText();
            }

            document.save(new File(outputPath));
        }
    }

    public void generarReporteGeneral(LocalDate inicio, LocalDate fin, int totalVehiculos, BigDecimal totalRecaudado, String outputPath) throws IOException {
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Solo el administrador puede generar reportes generales.");
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("PARKING OFFICE - REPORTE DE CAJA");
                contentStream.endText();

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 650);
                contentStream.setLeading(20f);
                
                contentStream.showText("Período: " + inicio.toString() + " a " + fin.toString());
                contentStream.newLine();
                contentStream.showText("Total Vehículos Atendidos: " + totalVehiculos);
                contentStream.newLine();
                contentStream.showText("Total Recaudado: $" + totalRecaudado.toString());
                contentStream.endText();
            }

            document.save(new File(outputPath));
        }
    }
}
