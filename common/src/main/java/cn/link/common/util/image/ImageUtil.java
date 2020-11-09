package cn.link.common.util.image;


import com.alibaba.excel.util.StringUtils;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.math.BigDecimal;


/**
 * 图片工具类，完成图片的截取
 */
public class ImageUtil {

    /**
     * 字节流输出流
     */
    public static final ByteArrayOutputStream BOS = new ByteArrayOutputStream();

    /**
     * Thumbnails 根据指定大小和指定精度压缩图片
     *
     * @param srcPath      源图片地址
     * @param desPath      目标图片地址
     * @param desFileSize  指定图片大小，单位kb
     * @param accuracy     精度，递归压缩的比率，建议小于0.9
     * @param desMaxWidth  目标最大宽度
     * @param desMaxHeight 目标最大高度
     * @return 目标文件路径
     */
    public static String commpressPicForScale(String srcPath, String desPath,
                                              long desFileSize, double accuracy, int desMaxWidth, int desMaxHeight) {
        if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(desPath)) {
            return null;
        }
        if (!new File(srcPath).exists()) {
            return null;
        }
        try {
            File srcFile = new File(srcPath);
            long srcFileSize = srcFile.length();
            System.out.println("源图片：" + srcPath + "，大小：" + srcFileSize / 1024
                    + "kb");
            //获取图片信息
            BufferedImage bim = ImageIO.read(srcFile);
            int srcWidth = bim.getWidth();
            int srcHeight = bim.getHeight();

            //先转换成jpg
            Thumbnails.Builder builder = Thumbnails.of(srcFile).outputFormat("jpg");

            // 指定大小（宽或高超出会才会被缩放）
            if (srcWidth > desMaxWidth || srcHeight > desMaxHeight) {
                builder.size(desMaxWidth, desMaxHeight);
            } else {
                //宽高均小，指定原大小
                builder.size(srcWidth, srcHeight);
            }

            // 写入到内存
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //字节输出流（写入到内存）
            builder.toOutputStream(baos);

            // 递归压缩，直到目标文件大小小于desFileSize
            byte[] bytes = commpressPicCycle(baos.toByteArray(), desFileSize, accuracy);

            // 输出到文件
            File desFile = new File(desPath);
            FileOutputStream fos = new FileOutputStream(desFile);
            fos.write(bytes);
            fos.close();

            System.out.println("目标图片：" + desPath + "，大小" + desFile.length() / 1024 + "kb");
            System.out.println("图片压缩完成！");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return desPath;
    }

    private static byte[] commpressPicCycle(byte[] bytes, long desFileSize, double accuracy) throws IOException {
        // File srcFileJPG = new File(desPath);
        long srcFileSizeJPG = bytes.length;
        // 2、判断大小，如果小于500kb，不压缩；如果大于等于500kb，压缩
        if (srcFileSizeJPG <= desFileSize * 1024) {
            return bytes;
        }
        // 计算宽高
        BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
        int srcWdith = bim.getWidth();
        int srcHeigth = bim.getHeight();
        int desWidth = new BigDecimal(srcWdith).multiply(
                new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(srcHeigth).multiply(
                new BigDecimal(accuracy)).intValue();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //字节输出流（写入到内存）
        Thumbnails.of(new ByteArrayInputStream(bytes)).size(desWidth, desHeight).outputQuality(accuracy).toOutputStream(baos);
        return commpressPicCycle(baos.toByteArray(), desFileSize, accuracy);
    }


    /**
     * 实现图像的等比缩放
     *
     * @param source  原图
     * @param targetW 目标宽度
     * @param targetH 目标高度
     * @return
     */
    private static BufferedImage resize(BufferedImage source, int targetW,
                                        int targetH) {
        // targetW，targetH分别表示目标长和宽
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) targetW / source.getWidth();
        double sy = (double) targetH / source.getHeight();
        // 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
        // 则将下面的if else语句注释即可
        if (sx < sy) {
            sx = sy;
            targetW = (int) (sx * source.getWidth());
        } else {
            sy = sx;
            targetH = (int) (sy * source.getHeight());
        }
        // handmade
        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
                    targetH);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else {
            target = new BufferedImage(targetW, targetH, type);
        }
        Graphics2D g = target.createGraphics();
        // smoother than exlax:
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;

    }

    /**
     * 实现图像的等比缩放和缩放后的截取, 处理成功返回true, 否则返回false
     *
     * @param inFilePath  要截取文件的路径
     * @param outFilePath 截取后输出的路径
     * @param width       要截取宽度
     * @param hight       要截取的高度
     * @throws Exception
     */
    public static boolean compress(String inFilePath, String outFilePath,
                                   int width, int hight) {
        boolean ret = false;
        File file = new File(inFilePath);
        File saveFile = new File(outFilePath);
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            ret = compress(in, saveFile, width, hight);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ret = false;
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * 实现图像的等比缩放和缩放后的截取, 处理成功返回true, 否则返回false
     *
     * @param in       要截取文件流
     * @param saveFile 截取后输出的路径
     * @param width    要截取宽度
     * @param height   要截取的高度
     * @throws Exception
     */
    public static boolean compress(InputStream in, File saveFile,
                                   int width, int height) {
//     boolean ret = false;
        BufferedImage srcImage = null;
        try {
            srcImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (width > 0 || height > 0) {
            // 原图的大小
            int sw = srcImage.getWidth();
            int sh = srcImage.getHeight();
            // 如果原图像的大小小于要缩放的图像大小，直接将要缩放的图像复制过去
            if (sw > width && sh > height) {
                srcImage = resize(srcImage, width, height);
            } else {
                String fileName = saveFile.getName();
                String formatName = fileName.substring(fileName
                        .lastIndexOf('.') + 1);
                try {
                    ImageIO.write(srcImage, formatName, saveFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        // 缩放后的图像的宽和高
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();
        // 如果缩放后的图像和要求的图像宽度一样，就对缩放的图像的高度进行截取
        if (w == width) {
            // 计算X轴坐标
            int x = 0;
            int y = h / 2 - height / 2;
            try {
                saveSubImage(srcImage, new Rectangle(x, y, width, height), saveFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // 否则如果是缩放后的图像的高度和要求的图像高度一样，就对缩放后的图像的宽度进行截取
        else if (h == height) {
            // 计算X轴坐标
            int x = w / 2 - width / 2;
            int y = 0;
            try {
                saveSubImage(srcImage, new Rectangle(x, y, width, height), saveFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * 实现图像的等比缩放和缩放后的截取，返回该图片的字节流
     *
     * @param in         要截取文件流
     * @param suffixName 图片后缀名
     * @param width      指定图片压缩后的宽度
     * @param height     指定图片压缩后的宽度
     * @return
     */
    public static byte[] compress(InputStream in, String suffixName,
                                  int width, int height) {

        //压缩前先清空流中的数据
        BOS.reset();
        BufferedImage srcImage = null;

        try {
            srcImage = ImageIO.read(in);
        } catch (IOException e) {
            return null;
        }

        if (width > 0 || height > 0) {
            // 原图的大小
            int sw = srcImage.getWidth();
            int sh = srcImage.getHeight();

            // 如果原图像的大小小于要缩放的图像大小，直接不缩放，返回原图像
            if (sw < width || sh < height) {
                try {
                    ImageIO.write(srcImage, suffixName, BOS);
                    return BOS.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            srcImage = resize(srcImage, width, height);

        }

        // 缩放后的图像的宽和高
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();
        // 如果缩放后的图像和要求的图像宽度一样，就对缩放的图像的高度进行截取
        if (w == width) {
            // 计算X轴坐标
            int x = 0;
            int y = h / 2 - height / 2;
            try {
                return saveSubImage(srcImage, new Rectangle(x, y, width, height), suffixName, BOS);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        // 否则如果是缩放后的图像的高度和要求的图像高度一样，就对缩放后的图像的宽度进行截取
        else if (h == height) {
            // 计算X轴坐标
            int x = w / 2 - width / 2;
            int y = 0;
            try {
                return saveSubImage(srcImage, new Rectangle(x, y, width, height), suffixName, BOS);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;

    }

    /**
     * 实现图像的等比缩放和缩放后的截取, 处理成功返回true, 否则返回false
     *
     * @param in         图片输入流
     * @param saveFile   压缩后的图片输出流
     * @param proportion 压缩比
     * @throws Exception
     */
    public static boolean compress(InputStream in, File saveFile, int proportion) {
        // 检查参数有效性
        if (null == in || null == saveFile || proportion < 1) {
            return false;
        }

        BufferedImage srcImage = null;
        try {
            srcImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 原图的大小
        int width = srcImage.getWidth() / proportion;
        int hight = srcImage.getHeight() / proportion;

        srcImage = resize(srcImage, width, hight);

        // 缩放后的图像的宽和高
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();
        // 如果缩放后的图像和要求的图像宽度一样，就对缩放的图像的高度进行截取
        if (w == width) {
            // 计算X轴坐标
            int x = 0;
            int y = h / 2 - hight / 2;
            try {
                saveSubImage(srcImage, new Rectangle(x, y, width, hight), saveFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // 否则如果是缩放后的图像的高度和要求的图像高度一样，就对缩放后的图像的宽度进行截取
        else if (h == hight) {
            // 计算X轴坐标
            int x = w / 2 - width / 2;
            int y = 0;
            try {
                saveSubImage(srcImage, new Rectangle(x, y, width, hight), saveFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * 实现缩放后的截图,写入指定文件中
     *
     * @param image          缩放后的图像
     * @param subImageBounds 要截取的子图的范围
     * @param subImageFile   要保存的文件
     * @throws IOException
     */
    private static void saveSubImage(BufferedImage image,
                                     Rectangle subImageBounds, File subImageFile) throws IOException {
        if (subImageBounds.x < 0 || subImageBounds.y < 0
                || subImageBounds.width - subImageBounds.x > image.getWidth()
                || subImageBounds.height - subImageBounds.y > image.getHeight()) {
            //LoggerUtil.error(ImageHelper.class, "Bad subimage bounds");
            return;
        }
        BufferedImage subImage = image.getSubimage(subImageBounds.x, subImageBounds.y, subImageBounds.width, subImageBounds.height);
        String fileName = subImageFile.getName();
        String formatName = fileName.substring(fileName.lastIndexOf('.') + 1);
        ImageIO.write(subImage, formatName, subImageFile);
    }

    /**
     * 实现缩放后的截图,返回该截图的字节数组
     *
     * @param image          缩放后的图像
     * @param subImageBounds 要截取的子图的范围
     * @param bos            将图片写入的字节流
     * @throws IOException
     */
    private static byte[] saveSubImage(BufferedImage image,
                                       Rectangle subImageBounds, String suffixName, ByteArrayOutputStream bos) throws IOException {
        if (subImageBounds.x < 0 || subImageBounds.y < 0
                || subImageBounds.width - subImageBounds.x > image.getWidth()
                || subImageBounds.height - subImageBounds.y > image.getHeight()) {
            //LoggerUtil.error(ImageHelper.class, "Bad subimage bounds");
            return null;
        }
        BufferedImage subImage = image.getSubimage(subImageBounds.x, subImageBounds.y, subImageBounds.width, subImageBounds.height);
        ImageIO.write(subImage, suffixName, bos);
        return bos.toByteArray();

    }

}