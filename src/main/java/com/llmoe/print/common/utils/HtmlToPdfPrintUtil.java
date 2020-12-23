package com.llmoe.print.common.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.layout.font.FontProvider;
import com.llmoe.print.common.BusinessException;
import com.llmoe.print.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 根据HTML模板 PDF进行打印
 *
 * @author llmoe
 * @date 2020/2/28 20:29
 */
@Slf4j
public class HtmlToPdfPrintUtil {

    private static final String PDF = ".pdf";
    //换算像素的基础值
    private static final Double MM = 2.833;

    private static ConverterProperties properties = null;

    //使用静态代码块，只加载一次足矣,节省资源
    static {
        //解决iText生成PDF中文乱码问题
        properties = new ConverterProperties();
        FontProvider fontProvider = new FontProvider();
        //添加系统字体库
        fontProvider.addSystemFonts();
        fontProvider.addDirectory(getFontsDataDir());
        fontProvider.addStandardPdfFonts();
        properties.setFontProvider(fontProvider);
        properties.setCharset("UTF-8");
    }

    /**
     * 根据html模板生成并打印PDF
     *
     * @param content      打印模板HTML
     * @param printerName  打印机名称
     * @param printNum     打印数量
     * @param selectedItem 打印模式
     * @param height       模板高度
     * @param dpi          DPI,每英寸的像素数量
     * @param width        模板宽度
     */
    public static void htmlPrint(String content, String printerName, Integer printNum, Integer dpi, Integer height, Integer width, String selectedItem) {
        //输出PDF路径
        String outFilePath = null;
        try {
            long old = System.currentTimeMillis();

            //输出PDF路径
            outFilePath = getOutputFilePath();

            //使用iText的最新版本 pdf2Html 转换Html为PDF文档
            HtmlConverter.convertToPdf(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), new FileOutputStream(outFilePath), properties);

            //检查和获取打印机
            PrintService printService = HtmlToPdfPrintUtil.anExaminationPrint(printerName);

            //打印PDF
            printPDF(outFilePath, printService, printNum, dpi, height, width, selectedItem);

            long now = System.currentTimeMillis();

            log.info("打印耗时：" + ((now - old) / 1000.0) + " s");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("打印失败：" + e.getMessage());
        } finally {
            //删除生成PDF文件
            FileUtil.del(outFilePath);
        }
    }

    /**
     * 检查打印机
     *
     * @param printerName 打印机名称
     */
    public static PrintService anExaminationPrint(String printerName) {
        //获取当前服务器打印机列表
        List<PrintService> list = getDeviceList();
        PrintService printService = null;
        for (PrintService p : list) {
            if (p.getName().equals(printerName)) {
                printService = p;
                break;
            }
        }
        //检验用户选择的打印机是否在打印机列表
        if (printService == null) {
            throw new BusinessException("找不到打印机啊啊");
        }
        return printService;
    }

    /**
     * 获取PDF文档输出流
     *
     * @return 路径地址
     */
    public static String getOutputFilePath() {
        String dataDir = FileUtil.getTmpDirPath();
        String fileName = DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN) + PDF;
        return dataDir + fileName;
    }

    /**
     * 获取全部打印设备信息
     *
     * @return 返回全部能用的打印服务的List
     */
    public static List<PrintService> getDeviceList() {
        // 构建打印请求属性集
        HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        // 设置打印格式，因为未确定类型，所以选择autosense
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        // 查找所有的可用的打印服务
        PrintService[] printService = PrintServiceLookup.lookupPrintServices(flavor, pras);
        return Arrays.asList(printService);
    }

    /**
     * 打印pdf的方法，使用PDFBOX
     *
     * @param printNum 打印数量
     * @param file     要打印的文件
     * @param service  打印设备
     * @param height   模板高
     * @param width    模板宽
     * @param dpi      DPI,每英寸的像素数量
     */
    private static void printPDF(String file, PrintService service, Integer printNum, Integer dpi, Integer height, Integer width, String selectedItem) throws IOException {
        PDDocument document = null;
        try {
            log.info("尺寸 = " + height + "," + width);
            log.info("DPI = " + dpi);
            log.info("打印模式 = " + selectedItem);
            log.info("打印数量 = " + printNum);
            log.info("打印机 = " + service.getName());

            //加载文档
            document = PDDocument.load(new File(file));
            PDFPrintable printable = null;
            //设置纸张
            if (Constants.SCALE_TO_FIT.equalsIgnoreCase(selectedItem)) {
                printable = new PDFPrintable(document, Scaling.SCALE_TO_FIT, false, 0, true);
            } else if (Constants.ACTUAL_SIZE.equalsIgnoreCase(selectedItem)) {
                printable = new PDFPrintable(document, Scaling.ACTUAL_SIZE, false, 0, true);
            } else if (Constants.SHRINK_TO_FIT.equalsIgnoreCase(selectedItem)) {
                printable = new PDFPrintable(document, Scaling.SHRINK_TO_FIT, false, 0, true);
            } else if (Constants.STRETCH_TO_FIT.equalsIgnoreCase(selectedItem)) {
                printable = new PDFPrintable(document, Scaling.STRETCH_TO_FIT, false, 0, true);
            } else {
                printable = new PDFPrintable(document, Scaling.SCALE_TO_FIT, false, 0, true);
            }

            //尺寸计算
            PixelPaper pixelPaper = new PixelPaper(dpi, width, height);

            //创建打印任务
            PrinterJob job = PrinterJob.getPrinterJob();
            //设置打印机器
            job.setPrintService(service);
            PageFormat pageFormat = job.defaultPage();
            Paper loPaper = pageFormat.getPaper();
            //如果设置自定义模式
            if (Constants.CUSTOMIZE.equalsIgnoreCase(selectedItem) && !height.equals(0) && !width.equals(0)) {
                //设置打印尺寸
                loPaper.setSize(pixelPaper.getWidth(), pixelPaper.getHeight());
                loPaper.setImageableArea(5, 5, pixelPaper.getWidth(), pixelPaper.getHeight());
            }else {
                loPaper.setSize(loPaper.getWidth(), loPaper.getHeight());
                loPaper.setImageableArea(5, 5, loPaper.getWidth(), loPaper.getHeight());
            }
            pageFormat.setPaper(loPaper);
            //打印 数量
            job.setCopies(printNum);
            job.setJobName(new File(file).getName());
            job.setPrintable(printable, pageFormat);
            //开始打印
            job.print();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("操作打印机打印失败：" + e.getMessage());
        } finally {
            log.info("关闭doc");
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * 获取资源文件目录的地址
     *
     * @return 地址
     */
    private static String getFontsDataDir() {
        File webRoot = FileUtil.getWebRoot();
        webRoot = new File(webRoot, "fonts");
        return webRoot.toString() + File.separator;
    }


}
