package cn.link.excel.easyexcel.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;

import java.io.InputStream;

/**
 * @author Link
 * @version 1.0
 * @date 2020/11/3 11:45
 */
@ContentRowHeight(100)
public class ImageData {

    @ExcelProperty(value = {"byte"})
    @ColumnWidth(20)
    private byte[] imageByteArray;

    @ExcelProperty(value = {"in"})
    @ColumnWidth(20)
    private InputStream inputStream;

    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
