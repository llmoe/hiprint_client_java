package com.llmoe.print.service;

import com.llmoe.print.pojo.PrinterDTO;

import java.util.List;

/**
 * 打印机接口
 *
 * @author llmoe
 * @date 2020/6/11 09:26
 */
public interface PrinterService {

    /**
     * 获取打印机列表
     *
     * @return 打印机列表
     */
    public List<PrinterDTO> getPrinterList();

    /**
     * 获取本地默认打印机
     *
     * @return 打印机
     */
    public String getDefaultPrinter();


    /**
     * 验证打印机是否存在
     *
     * @return 结果
     */
    public boolean verificationPrinter(String printerName);
}
