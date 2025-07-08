package ucv.codelab.service.writer;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.io.image.ImageDataFactory;

import ucv.codelab.util.PopUp;
import ucv.codelab.model.Cliente;
import ucv.codelab.model.Orden;
import ucv.codelab.model.Producto;
import ucv.codelab.model.SubOrden;
import ucv.codelab.repository.ClienteRepository;
import ucv.codelab.repository.OrdenRepository;
import ucv.codelab.repository.ProductoRepository;
import ucv.codelab.repository.SubOrdenRepository;
import ucv.codelab.util.Personalizacion;

public class MakePdf {

    private static DecimalFormat formato = new DecimalFormat("#.00");

    @SuppressWarnings("ConvertToTryWithResources")
    public static void make(File ubicacion, int idOrden) throws IOException {
        // Inicializar el escritor PDF
        PdfWriter pdfWriter = new PdfWriter(ubicacion);
        // Inicializar el documento PDF
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        // Inicializar el documento para agregar contenido
        Document document = new Document(pdfDocument);

        // Crear fuentes
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont regularFont = PdfFontFactory.createFont("Helvetica");

        // Título principal
        Paragraph titulo = new Paragraph("BOLETA ELECTRONICA")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(titulo);

        try {
            OrdenRepository ordenRepo = new OrdenRepository();
            Orden orden = ordenRepo.findById(idOrden).get();

            ClienteRepository clienteRepo = new ClienteRepository();
            Cliente cliente = clienteRepo.findById(orden.getIdCliente()).get();

            SubOrdenRepository subOrdenRepo = new SubOrdenRepository();
            List<SubOrden> subordenes = subOrdenRepo.findByOrden(idOrden);

            ProductoRepository productoRepo = new ProductoRepository();
            HashMap<Integer, Producto> productos = new HashMap<>();
            for (SubOrden suborden : subordenes) {
                productos.put(suborden.getIdProducto(), productoRepo.findById(suborden.getIdProducto()).get());
            }

            // Información de la factura
            createInfoSection(document, regularFont, orden);

            // Información del cliente y empresa
            createClienteEmpresaSection(document, regularFont, boldFont, cliente);

            // Tabla de productos
            createProductTable(document, regularFont, boldFont, subordenes, productos);

            // Total
            createTotalSection(document, boldFont, subordenes, productos);

            PopUp.informacion("PDF creado", "PDF creado con exito en la ubicacion indicada");
        } catch (Exception e) {
            PopUp.error("PDF no generado", "Ocurrio un error al cargar los datos para el PDF");
        }

        document.close();
        pdfDocument.close();
    }

    private static void createInfoSection(Document document, PdfFont font, Orden orden) {
        // Crear tabla para información de factura
        Table infoTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        infoTable.setMarginBottom(20);

        // Columna izquierda
        Cell leftCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph("Fecha de compra: " + orden.getFechaOrden()).setFont(font))
                .add(new Paragraph("Número de factura: " + orden.getIdOrden()).setFont(font));

        // Columna derecha con logo
        Cell rightCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.RIGHT);

        try {
            // Obtener el logo desde Personalizacion (JavaFX Image)
            javafx.scene.image.Image fxImage = Personalizacion.getLogo();

            if (fxImage != null) {
                // Convertir JavaFX Image a BufferedImage manualmente
                BufferedImage bufferedImage = convertFXImageToBufferedImage(fxImage);

                // Convertir BufferedImage a byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "PNG", baos);
                byte[] imageBytes = baos.toByteArray();

                // Crear iText Image
                Image logo = new Image(ImageDataFactory.create(imageBytes));

                // Ajustar tamaño del logo (opcional)
                logo.setWidth(80);
                logo.setHeight(60);

                rightCell.add(logo);
            } else {
                // Si no hay logo, usar emoji como fallback
                rightCell.add(new Paragraph("🛒").setFontSize(40));
            }
        } catch (Exception e) {
            // En caso de error, usar emoji como fallback
            rightCell.add(new Paragraph("🛒").setFontSize(40));
        }

        infoTable.addCell(leftCell);
        infoTable.addCell(rightCell);
        document.add(infoTable);
    }

    private static BufferedImage convertFXImageToBufferedImage(javafx.scene.image.Image fxImage) {
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = fxImage.getPixelReader();

        // Leer los píxeles de la imagen JavaFX
        int[] pixels = new int[width * height];
        pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getIntArgbInstance(), pixels, 0, width);

        // Establecer los píxeles en la BufferedImage
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

        return bufferedImage;
    }

    private static void createClienteEmpresaSection(Document document, PdfFont regularFont, PdfFont boldFont,
            Cliente cliente) {
        // Crear tabla para cliente y empresa
        Table clienteEmpresaTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
        clienteEmpresaTable.setWidth(UnitValue.createPercentValue(100));
        clienteEmpresaTable.setMarginBottom(20);

        // Información del cliente
        Cell clienteCell = new Cell()
                .setBorder(new SolidBorder(new DeviceRgb(200, 200, 200), 1))
                .setPadding(10)
                .add(new Paragraph("Cliente: " + cliente.getNombreCliente()).setFont(boldFont).setFontSize(14))
                .add(new Paragraph("DNI: " + cliente.getDniCliente()).setFont(regularFont));

        // Información de la empresa
        Cell empresaCell = new Cell()
                .setBorder(new SolidBorder(new DeviceRgb(200, 200, 200), 1))
                .setPadding(10)
                .add(new Paragraph(
                        Personalizacion.getEmpresaActual().getNombreEmpresa()).setFont(boldFont).setFontSize(14))
                .add(new Paragraph(
                        "RUC: " + Personalizacion.getEmpresaActual().getRuc()).setFont(regularFont))
                .add(new Paragraph(
                        "Ubicación: " + Personalizacion.getEmpresaActual().getUbicacion()).setFont(regularFont))
                .add(new Paragraph(
                        "Email: " + Personalizacion.getEmpresaActual().getEmailEmpresa()).setFont(regularFont));

        clienteEmpresaTable.addCell(clienteCell);
        clienteEmpresaTable.addCell(empresaCell);
        document.add(clienteEmpresaTable);
    }

    private static void createProductTable(Document document, PdfFont regularFont, PdfFont boldFont,
            List<SubOrden> subordenes, HashMap<Integer, Producto> productos) {
        // Crear tabla de productos
        Table productTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1, 3, 1, 1.5f, 1.5f }));
        productTable.setWidth(UnitValue.createPercentValue(100));
        productTable.setMarginBottom(20);

        // Headers
        String[] headers = { "Nro.", "ID Producto", "Descripción", "Cantidad", "Precio Unitario", "Precio" };
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(new DeviceRgb(240, 240, 240))
                    .setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1))
                    .setPadding(5);
            productTable.addHeaderCell(headerCell);
        }

        // Datos de productos
        String[][] filas = new String[subordenes.size()][6];
        for (int i = 0; i < filas.length; i++) {
            int contador = i + 1;
            Producto p = productos.get(subordenes.get(i).getIdProducto());
            double subTotal = p.getPrecio() * subordenes.get(i).getCantidad();
            filas[i] = new String[] { contador + "", p.getIdProducto() + "", p.getNombreProducto(),
                    subordenes.get(i).getCantidad() + "", formato.format(p.getPrecio()), formato.format(subTotal) };
        }

        for (String[] fila : filas) {
            for (int i = 0; i < fila.length; i++) {
                Cell cell = new Cell()
                        .add(new Paragraph(fila[i]).setFont(regularFont))
                        .setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1))
                        .setPadding(5);

                if (i == 0 || i == 1 || i == 3) { // Centrar números
                    cell.setTextAlignment(TextAlignment.CENTER);
                } else if (i == 4 || i == 5) { // Alinear precios a la derecha
                    cell.setTextAlignment(TextAlignment.RIGHT);
                }

                productTable.addCell(cell);
            }
        }

        document.add(productTable);
    }

    private static void createTotalSection(Document document, PdfFont boldFont,
            List<SubOrden> subordenes, HashMap<Integer, Producto> productos) {
        // Calcula el total
        double total = 0;
        for (SubOrden subOrden : subordenes) {
            total += subOrden.getCantidad() * productos.get(subOrden.getIdProducto()).getPrecio();
        }

        // Crear sección de total
        Paragraph txtTotal = new Paragraph("Precio Total: S/ " + formato.format(total))
                .setFont(boldFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBackgroundColor(new DeviceRgb(240, 240, 240))
                .setPadding(10)
                .setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1));

        document.add(txtTotal);
    }
}