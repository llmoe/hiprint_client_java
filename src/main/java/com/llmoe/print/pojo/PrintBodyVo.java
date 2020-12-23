package com.llmoe.print.pojo;

import lombok.Data;

/**
 * 打印内容
 *
 * @author llmoe
 * @date 2020/6/8 10:22
 */
@Data
public class PrintBodyVo {
    /**
     * 打印时间
     */
    private String printTime;

    /**
     * 打印机名称
     */
    private String printName;

    /**
     * 打印数量
     */
    private String printNum;

    /**
     * 打印机模板
     */
    private String printTemplate;

    /**
     * 操作员
     */
    private String printUser;

    public PrintBodyVo(String printTime, String printName, String printNum, String printTemplate, String printUser) {
        this.printTime = printTime;
        this.printName = printName;
        this.printNum = printNum;
        this.printTemplate = printTemplate;
        this.printUser = printUser;
    }

    public PrintBodyVo() {
    }
}
