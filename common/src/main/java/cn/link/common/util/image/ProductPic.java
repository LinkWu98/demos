package cn.link.common.util.image;

import java.io.File;

/**
 * 用于商品图片的业务处理对象
 *
 * 图片缓存
 * 图片地址修复
 *
 * @author Link
 * @version 1.0
 * @date 2020/11/10 18:57
 */
public class ProductPic {

    /**
     * 商品表主键
     */
    private Integer id;

    /**
     * 图片直接使用 http请求 还是 直接流式传输
     * true http请求 | false 直接流式传输
     */
    private boolean requestFlag;

    /**
     * 图片对象
     */
    private File file;

    /**
     * 图片地址
     */
    private String mainPicId;

    public ProductPic() {
    }

    public ProductPic(boolean requestFlag, File file) {
        this.requestFlag = requestFlag;
        this.file = file;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isRequestFlag() {
        return requestFlag;
    }

    public void setRequestFlag(boolean requestFlag) {
        this.requestFlag = requestFlag;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMainPicId() {
        return mainPicId;
    }

    public void setMainPicId(String mainPicId) {
        this.mainPicId = mainPicId;
    }
}
