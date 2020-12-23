package com.llmoe.print.service.impl;

import cn.hutool.core.util.StrUtil;
import com.llmoe.print.common.config.SysConfig;
import com.llmoe.print.common.utils.HtmlToPdfPrintUtil;
import com.llmoe.print.pojo.PrinterDTO;
import com.llmoe.print.service.PrinterService;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.ArrayList;
import java.util.List;

/**
 * @author llmoe
 * @date 2020/6/11 09:29
 */
@Service
public class PrinterServiceImpl implements PrinterService {

    @Override
    public List<PrinterDTO> getPrinterList() {

        List<PrintService> deviceList = HtmlToPdfPrintUtil.getDeviceList();
        List<PrinterDTO> printer = new ArrayList<>();
        String defaultPrinter = SysConfig.getConfig().getStr("defaultPrinter");
        if (StrUtil.isBlank(defaultPrinter)) {
            defaultPrinter = this.getDefaultPrinter();
        }
        for (PrintService printService : deviceList) {
            PrinterDTO dto = new PrinterDTO();
            //判断是不是默认打印机
            dto.setIsDefault(StrUtil.isNotBlank(defaultPrinter) && printService.getName().equals(defaultPrinter));
            dto.setName(printService.getName());
            dto.setStatus(0);
            dto.setDescription("");
            printer.add(dto);
        }
        return printer;
    }

    /**
     * 获取默认打印机
     *
     * @return 默认打印机名称
     */
    @Override
    public String getDefaultPrinter() {
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        if (printService == null) {
            return "";
        }
        return printService.getName();
    }

    /**
     * 验证打印机是否存在
     *
     * @param printerName 打印机名称
     * @return 是否存在
     */
    @Override
    public boolean verificationPrinter(String printerName) {
        //获取当前服务器打印机列表
        List<PrintService> list = HtmlToPdfPrintUtil.getDeviceList();
        for (PrintService p : list) {
            if (p.getName().equals(printerName)) {
                return true;
            }
        }
        return false;
    }
}
