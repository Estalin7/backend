package com.grupoagil.proyectoagil.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.grupoagil.proyectoagil.dto.PagoHistorialDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfService {

    public byte[] generarHistorialPagosPDF(List<PagoHistorialDTO> lista) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            document.add(new Paragraph(
                "HISTORIAL DE PAGOS",
                new Font(Font.HELVETICA, 18, Font.BOLD)
            ));

            document.add(new Paragraph(" ")); // espacio

            // Tabla
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            table.addCell("ID Pago");
            table.addCell("Fecha");
            table.addCell("Cliente");
            table.addCell("Método");
            table.addCell("Pedido");
            table.addCell("Total (S/)");

            for (PagoHistorialDTO p : lista) {
                table.addCell(String.valueOf(p.getIdPago()));
                table.addCell(p.getFecha().toString());
                table.addCell(p.getCliente());
                table.addCell(p.getMetodoPago());
                table.addCell(String.valueOf(p.getIdPedido()));
                table.addCell(p.getPrecioFinal().toString());
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }

        return out.toByteArray();
    }
}
