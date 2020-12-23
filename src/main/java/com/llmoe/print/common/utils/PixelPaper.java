package com.llmoe.print.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 以像素定义纸张大小
 *
 * @author zhouhao
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixelPaper {

    /**
     * 宽度
     */
    private int width;

    /**
     * 高度
     */
    private int height;

    /**
     * 将毫米转为像素，算法为:dpi/25.4/mm
     * https://blog.csdn.net/qwaszx523/article/details/78779895
     *
     * @param dpi    DPI,每英寸的像素数量
     * @param width  以毫米定义的纸张宽
     * @param height 以毫米定义的纸张高
     */
    public PixelPaper(double dpi, int width, int height) {
        //1英寸=25.4毫米
        BigDecimal decimal = BigDecimal.valueOf(dpi)
                .divide(BigDecimal.valueOf(25.4), 5, RoundingMode.CEILING);

        setHeight(decimal.multiply(BigDecimal.valueOf(height)).intValue());

        setWidth(decimal.multiply(BigDecimal.valueOf(width)).intValue());
    }
}
