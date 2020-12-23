package com.llmoe.print.pojo;

import lombok.Data;

/**
 * 打印机实体类
 *
 * @author llmoe
 * @date 2020/6/6 10:21
 */
@Data
public class PrinterDTO {

    /**
     * 打印机名称
     */
    private String name;

    /**
     * 打印机描述
     */
    private String description;

    /**
     * 是否默认
     */
    private Boolean isDefault;

    /**
     * 状态
     */
    private Integer status;

    @Override
    public String toString() {
        if (isDefault) {
            return name + "(默认)";
        }
        return name;
    }
}
