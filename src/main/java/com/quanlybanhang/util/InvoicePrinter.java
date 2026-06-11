package com.quanlybanhang.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.quanlybanhang.model.Order;
import com.quanlybanhang.model.OrderDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Tiện ích hỗ trợ xuất và in hóa đơn ra file PDF sử dụng thư viện OpenPDF (iText).
 * Thiết lập font Arial để đảm bảo hiển thị đúng nội dung tiếng Việt có dấu.
 */
public class InvoicePrinter {
    private static DecimalFormat currencyFormatter = new DecimalFormat("#,##0 đ");
    private static SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static boolean printInvoice(Order order, List<OrderDetail> details, String customerName, String cashierName, File saveFile) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(saveFile));
            document.open();

            // Load font hỗ trợ Tiếng Việt (Arial từ Windows Fonts)
            BaseFont bf;
            try {
                bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception e) {
                // Fallback nếu không có Arial.ttf
                bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            }
            
            Font fontTitle = new Font(bf, 18, Font.BOLD);
            Font fontHeader = new Font(bf, 11, Font.BOLD);
            Font fontNormal = new Font(bf, 11, Font.NORMAL);
            Font fontItalic = new Font(bf, 10, Font.ITALIC);

            // 1. Tiêu đề cửa hàng
            Paragraph title = new Paragraph("CỬA HÀNG CÔNG NGHỆ QB-TECH", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph address = new Paragraph("Địa chỉ: 123 Đường Láng, Đống Đa, Hà Nội\nSĐT: 0987.654.321\n-----------------------------------------", fontItalic);
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);

            // 2. Tiêu đề hóa đơn
            Paragraph docTitle = new Paragraph("HÓA ĐƠN BÁN HÀNG", new Font(bf, 14, Font.BOLD));
            docTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(docTitle);

            Paragraph codePara = new Paragraph("Số hóa đơn: " + order.getCode(), fontHeader);
            codePara.setAlignment(Element.ALIGN_CENTER);
            document.add(codePara);
            
            document.add(new Paragraph(" "));

            // 3. Thông tin chung
            document.add(new Paragraph("Ngày lập hóa đơn: " + dateFormater.format(order.getOrderDate()), fontNormal));
            document.add(new Paragraph("Khách hàng: " + customerName, fontNormal));
            document.add(new Paragraph("Thu ngân: " + cashierName, fontNormal));
            document.add(new Paragraph("Phương thức thanh toán: " + order.getPaymentMethod(), fontNormal));
            document.add(new Paragraph(" "));

            // 4. Bảng chi tiết sản phẩm mua
            PdfPTable table = new PdfPTable(5); // 5 cột: STT, Tên SP, Đơn giá, S.Lượng, Thành tiền
            table.setWidthPercentage(100);
            table.setWidths(new float[]{8f, 42f, 18f, 12f, 20f});

            // Tiêu đề cột
            String[] headers = {"STT", "Tên sản phẩm", "Đơn giá", "S.L", "Thành tiền"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            int stt = 1;
            for (OrderDetail od : details) {
                table.addCell(createCell(String.valueOf(stt++), Element.ALIGN_CENTER, fontNormal));
                table.addCell(createCell(od.getProductName(), Element.ALIGN_LEFT, fontNormal));
                table.addCell(createCell(currencyFormatter.format(od.getPrice()), Element.ALIGN_RIGHT, fontNormal));
                table.addCell(createCell(String.valueOf(od.getQuantity()), Element.ALIGN_CENTER, fontNormal));
                table.addCell(createCell(currencyFormatter.format(od.getTotalPrice()), Element.ALIGN_RIGHT, fontNormal));
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // 5. Tổng cộng tiền thanh toán
            PdfPTable tableSummary = new PdfPTable(2);
            tableSummary.setWidthPercentage(45);
            tableSummary.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableSummary.setWidths(new float[]{50f, 50f});

            tableSummary.addCell(createCellNoBorder("Tổng tiền hàng:", Element.ALIGN_LEFT, fontNormal));
            tableSummary.addCell(createCellNoBorder(currencyFormatter.format(order.getTotalAmount()), Element.ALIGN_RIGHT, fontNormal));

            tableSummary.addCell(createCellNoBorder("Giảm giá:", Element.ALIGN_LEFT, fontNormal));
            tableSummary.addCell(createCellNoBorder("-" + currencyFormatter.format(order.getDiscount()), Element.ALIGN_RIGHT, fontNormal));

            tableSummary.addCell(createCellNoBorder("Khách phải trả:", Element.ALIGN_LEFT, fontHeader));
            tableSummary.addCell(createCellNoBorder(currencyFormatter.format(order.getFinalAmount()), Element.ALIGN_RIGHT, fontHeader));

            document.add(tableSummary);
            document.add(new Paragraph(" "));

            // 6. Footer hóa đơn
            Paragraph footer = new Paragraph("Cám ơn quý khách đã mua sắm tại QB-Tech!\nHẹn gặp lại quý khách!", fontItalic);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            return true;
        } catch (Exception e) {
            System.err.println("Error creating PDF invoice: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static PdfPCell createCell(String text, int align, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
    }

    private static PdfPCell createCellNoBorder(String text, int align, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(3);
        return cell;
    }
}
