package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.response.ProductoResponseDTO;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Logger logger =
            LoggerFactory.getLogger(PdfService.class);

    public byte[] generatePdfProduct(List<ProductoResponseDTO> products) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Titulo
            Font fontTitle = new Font(Font.HELVETICA, 18, Font.BOLD);

            Paragraph title = new Paragraph("LISTADO DE PRECIOS", fontTitle);

            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);

            document.add(title);

            // Fecha de generación
            Font informationFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

            Paragraph fecha = new Paragraph(
                    "Fecha de generación: " +
                            LocalDateTime.now().format(FORMATO_FECHA),
                    informationFont);

            fecha.setSpacingAfter(10);

            document.add(fecha);

            // Filtros

            // Tabla
            PdfPTable table = new PdfPTable(5);

            table.setWidthPercentage(100);

            table.setWidths(new float[]{
                    3.0f,
                    2.0f,
                    1.5f,
                    1.5f,
                    1.2f
            });

            // Encabezados
            addHeading(table, "Producto");
            addHeading(table, "Categoría");
            addHeading(table, "Minorista");
            addHeading(table, "Mayorista");
            addHeading(table, "Estado");

            for(ProductoResponseDTO product: products){
                addCell(table, product.getNombre());

                addCell(table, product.getCategoria().getNombre());

                addCell(table, formatPryce(product.getPrecioMinorista()));

                addCell(table, formatPryce(product.getPrecioMayorista()));

                addCell(table, product.getActivo() ? "Activo" : "Inactivo");
            }

            document.add(table);

            // Cantidad de productos
            Paragraph numberProducts = new Paragraph(
                    "\nTotal de productos: " + products.size(),
                    informationFont
            );

            document.add(numberProducts);

            document.close();

            return outputStream.toByteArray();

        } catch (DocumentException e) {
            //System.err.println(e.getMessage());
            logger.error("Error al preparar documento PDF", e);

            throw new ResourceNotFoundException("Error al preparar el documento PDF");
        }
    }

    private void addHeading(PdfPTable table, String text){
        Font font = new Font(
                Font.HELVETICA,
                9,
                Font.BOLD
        );

        PdfPCell cell = new PdfPCell(
                new Phrase(text, font)
        );

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);

        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text){
        Font font = new Font(
                Font.HELVETICA,
                8,
                Font.NORMAL
        );

        PdfPCell cell = new PdfPCell(
                new Phrase(
                        text != null ? text : "",
                        font
                )
        );

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);

        table.addCell(cell);
    }

    private String formatPryce(BigDecimal pryce){
        if(pryce == null){
            return "-";
        }

        return "$ " + pryce.toString();
    }


}
