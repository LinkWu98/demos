package cn.link.demo.thirdparty.cos;

import cn.link.demo.thirdparty.cos.service.CosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tool.link.common.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Link50
 * @version 1.0
 * @date 2020/12/1 14:39
 */
@RestController
@RequestMapping("/cos")
public class CosController {

    @Autowired
    CosService cosService;

    /**
     * 创建 bucket
     *
     * @return
     */
    @GetMapping("/upload")
    public Response<Object> upload() {

        cosService.uploadFile();

        return Response.succeed();
    }

    /**
     * 创建 bucket
     *
     * @return
     */
    @GetMapping("/download")
    public Response<Object> download() throws IOException {

        cosService.downloadFile();

        return Response.succeed();
    }

    public static void main(String[] args) {




    }

}
