package ucv.codelab.service.writer;

import java.io.File;
import java.io.IOException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import ucv.codelab.util.PopUp;

public class MakePdf {

    @SuppressWarnings("ConvertToTryWithResources")
    public static void make(File ubicacion, int idOrden) throws IOException {
        // Inicializar el escritor PDF
        PdfWriter pdfWriter = new PdfWriter(ubicacion);
        // Inicializar el documento PDF
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        // Inicializar el documento para agregar contenido
        Document document = new Document(pdfDocument);

        // TODO llenar datos del documento

        document.close();
        pdfDocument.close();

        PopUp.informacion("PDF creado", "PDF creado con exito en la ubicacion indicada");
    }
}