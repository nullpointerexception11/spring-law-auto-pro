package com.lawauto.backend.petition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

@Component
public class PetitionDocumentRenderer {

    public byte[] renderDocx(PetitionDraftExportService.ExportPayload payload) {
        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            addParagraph(document, payload.title(), true);
            addParagraph(document, "Generated At: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), false);
            addParagraph(document, "Case ID: " + payload.caseId(), false);
            addParagraph(document, "", false);
            for (PetitionDraftExportService.TemplateSection section : payload.sections()) {
                addParagraph(document, section.title(), true);
                addParagraph(document, section.content(), false);
                addParagraph(document, "", false);
            }
            document.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public byte[] renderPdf(PetitionDraftExportService.ExportPayload payload) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                stream.beginText();
                stream.newLineAtOffset(50, 780);
                stream.showText(safe(payload.title()));
                stream.endText();

                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                stream.beginText();
                stream.newLineAtOffset(50, 760);
                stream.showText("Case ID: " + payload.caseId());
                stream.newLineAtOffset(0, -16);
                stream.showText("Generated At: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                for (PetitionDraftExportService.TemplateSection section : payload.sections()) {
                    stream.newLineAtOffset(0, -24);
                    stream.showText(safeTruncated(section.title(), 140));
                    stream.newLineAtOffset(0, -14);
                    stream.showText(safeTruncated(section.content(), 220));
                }
                stream.endText();
            }
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void addParagraph(XWPFDocument document, String text, boolean title) {
        XWPFParagraph paragraph = document.createParagraph();
        var run = paragraph.createRun();
        run.setBold(title);
        run.setFontSize(title ? 16 : 11);
        run.setText(text == null ? "" : text);
    }

    private String safe(String value) {
        return value == null ? "" : value.replaceAll("[\\r\\n\\t]+", " ");
    }

    private String safeTruncated(String value, int max) {
        String cleaned = safe(value);
        if (cleaned.length() <= max) return cleaned;
        return cleaned.substring(0, max);
    }
}
