package Components;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

import DataBase.User;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class EmailSender {
    private static final String EMAIL = "colectivproiect@gmail.com"; // Replace with your verified email
    private static String SENDGRID_KEY; // Replace with your new SendGrid API key test

    public static void sendEmail(User u, BufferedImage QRCode, byte[] receipt) throws IOException, WriterException {
        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/sendgridkey.txt"))){
            SENDGRID_KEY = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Email from = new Email(EMAIL);
        Email to = new Email(u.getEmail());
        String subject = "Your BookNgo Reservation Receipt";
        String bodyContent = "Dear " + u.getFirst_name() + ",\n\n"
                + "Thank you for choosing our hotel. Please find attached the receipt for your recent payment and a QR code for your reservation.\n\n"
                + "Best regards,\n"
                + "BookNGo";
        // Create the content and attachments
        Content content = new Content("text/plain", bodyContent);
        Mail mail = new Mail(from, subject, to, content);

        // Attach QR code
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(QRCode, "png", baos);
        byte[] qrBytes = baos.toByteArray();
        String encodedQR = Base64.getEncoder().encodeToString(qrBytes);
        Attachments qrCodeAttachment = new Attachments();
        qrCodeAttachment.setContent(encodedQR);
        qrCodeAttachment.setType("image/png");
        qrCodeAttachment.setFilename("qrCode.png");
        qrCodeAttachment.setDisposition("attachment");
        qrCodeAttachment.setContentId("Image");
        mail.addAttachments(qrCodeAttachment);

        // Attach PDF receipt
        String encodedPDF = Base64.getEncoder().encodeToString(receipt);
        Attachments pdfAttachment = new Attachments();
        pdfAttachment.setContent(encodedPDF);
        pdfAttachment.setType("application/pdf");
        pdfAttachment.setFilename("receipt.pdf");
        pdfAttachment.setDisposition("attachment");
        pdfAttachment.setContentId("Pdf");
        mail.addAttachments(pdfAttachment);
        SendGrid sg = new SendGrid(SENDGRID_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            // Log the response for debugging
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            System.out.println("Response Headers: " + response.getHeaders());

            if (response.getStatusCode() != 202) {
                System.out.println("Error: Failed to send email.");
            } else {
                System.out.println("Email sent successfully.");
            }
        } catch (IOException ex) {
            System.out.println("IOException occurred: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static byte[] generatePDFReceipt(User u) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Receipt"));
        document.add(new Paragraph("Name: " + u.getFirst_name() + " " + u.getLast_name()));
        document.add(new Paragraph("Email: " + u.getEmail()));
        // Add more receipt details as needed

        document.close();
        return baos.toByteArray();
    }

    private static byte[] generateQRCode(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}
