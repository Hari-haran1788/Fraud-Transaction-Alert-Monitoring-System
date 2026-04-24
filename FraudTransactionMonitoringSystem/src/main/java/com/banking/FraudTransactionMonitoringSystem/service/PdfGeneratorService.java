package com.banking.FraudTransactionMonitoringSystem.service;

import com.banking.FraudTransactionMonitoringSystem.model.Transaction;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class PdfGeneratorService {

    public void export(HttpServletResponse response, String username, List<Transaction> transactions) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();


        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        Paragraph title = new Paragraph("SecureTrust Elite - E-Statement", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);


        Paragraph userDetail = new Paragraph("\nAccount Holder: " + username + "\nGenerated on: " + java.time.LocalDateTime.now() + "\n\n");
        document.add(userDetail);


        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);


        table.addCell("Date");
        table.addCell("Description");
        table.addCell("Type");
        table.addCell("Amount");


        for (Transaction txn : transactions) {
            table.addCell(txn.getTimestamp().toLocalDate().toString());
            table.addCell(txn.getDescription());
            table.addCell(txn.getType());
            table.addCell("₹" + txn.getAmount());
            }

        document.add(table);
        document.close();
    }
}

