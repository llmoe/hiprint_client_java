package com.llmoe.print;

import com.llmoe.print.common.utils.PixelPaper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.junit.Test;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

/**
 * @author llmoe
 * @date 2020/6/11 12:41
 */
public class TestDemo {
    @Test
    public void getUrl() {
        try {
            PDDocument document = PDDocument.load(new File("C:\\Users\\llmoe\\Desktop\\test\\20201104112505705.pdf"));
            PrinterJob job = PrinterJob.getPrinterJob();

            PDFPrintable printable = new PDFPrintable(document, Scaling.SCALE_TO_FIT, false, 0, true);
            HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            // 设置打印格式

//            pras.add(OrientationRequested.);
            PixelPaper pixelPaper = new PixelPaper(72, 70, 70);

            pras.add(new MediaPrintableArea(5, 5, pixelPaper.getWidth(), pixelPaper.getHeight(), MediaPrintableArea.MM));

            PageFormat pageFormat = job.defaultPage();
            Paper loPaper = pageFormat.getPaper();
            loPaper.setSize(pixelPaper.getWidth(), pixelPaper.getHeight());
            loPaper.setImageableArea(5, 5, pixelPaper.getWidth(), pixelPaper.getHeight());
            pageFormat.setPaper(loPaper);

            //job.setPrintable(printable);
            job.setPrintable(printable, pageFormat);
            //打印 数量
            job.setCopies(1);
            job.setJobName(new File("C:\\Users\\llmoe\\Desktop\\test\\20201104112505705.pdf").getName());

            //job.print(pras);
            job.print();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void htmlToPdf() {
//        boolean success = HtmlToPdf.create()
//                .object(HtmlToPdfObject.forHtml("n"))
//                .convert("/Users/llmoe/Desktop/test.pdf");
    }


    @Test
    public void getUrl2() {
////        System.out.println(FileUtil.getUserHomeDir());
////        System.out.println(FileUtil.getUserHomePath());
//        String homePath = FileUtil.getUserHomePath();
//        boolean exist = FileUtil.exist(homePath + File.separator + "print_server.ini");
//        System.out.println("exist = " + exist);
//        JSONObject jsonObject = JSONUtil.readJSONObject(new File(homePath + File.separator + "print_server.ini"), CharsetUtil.CHARSET_UTF_8);
//        System.out.println("jsonObject = " + jsonObject.toString());
    }


    @Test
    public void testPdf() throws IOException, InterruptedException {
//        Pdf pdf = new Pdf();
//        pdf.addPageFromString("");
//// Save the PDF
//        pdf.saveAs("/Users/llmoe/Desktop/output.pdf");
    }
}
