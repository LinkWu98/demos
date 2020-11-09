package cn.link.common.excel.easyexcel;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * EasyExcel 自定义样式
 *
 * @author Link
 * @version 1.0
 * @date 2020/11/3 13:41
 */
public class StyleStrategy {

    /**
     * 头策略
     */
    private static WriteCellStyle headWriteCellStyle = new WriteCellStyle();

    /**
     * 内容策略
     */
    private static WriteCellStyle contentWriteCellStyle = new WriteCellStyle();

    /**
     * 字体策略
     */
    private static WriteFont writeFont = new WriteFont();

    public static HorizontalCellStyleStrategy commonStrategy() {

        // 头的策略
        headWriteCellStyle = new WriteCellStyle();
        // 背景设置为红色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 20);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 背景绿色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 20);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

    }

}
