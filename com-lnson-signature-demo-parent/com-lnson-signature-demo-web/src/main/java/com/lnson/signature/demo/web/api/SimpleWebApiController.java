package com.lnson.signature.demo.web.api;

import com.lnson.signature.demo.commons.Certificate;
import com.lnson.signature.demo.entity.User;
import com.lnson.signature.demo.web.controller.HomeController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 注解@ResponseBody使用说明：
 *
 * 注解@ResponseBody是作用在方法上的，@ResponseBody 表示该方法的返回结果直接写入 HTTP response body 中，
 * 一般在异步获取数据时使用【也就是AJAX】，在使用 @RequestMapping后，返回值通常解析为跳转路径， 但是加上 @ResponseBody
 * 后返回结果不会被解析为跳转路径，而是直接写入 HTTP response body 中。 比如异步获取 json 数据，加上 @ResponseBody
 * 后，会直接返回 json 数据。
 *
 * ==================================================================================================================
 *
 * 注解@RequestBody使用说明：
 *
 * 注解@RequestBody是作用在形参列表上，用于将前台发送过来固定格式的数据【xml 格式或者 json等】封装为对应的 JavaBean 对象，
 * 封装时使用到的一个对象是系统默认配置的 HttpMessageConverter进行解析，然后封装到形参上。
 *
 * ==================================================================================================================
 *
 * 注解@RequestParam使用说明：
 *
 * 注解@RequestParam修饰方法中形参，获取请求中特定的请求参数值并赋值给形参，同时可以对特定的请求参数进行验证、设置默认值等等
 */
@Controller
@RequestMapping(value = "api/simple")
public class SimpleWebApiController {

    private final static Logger logger = LogManager.getLogger(HomeController.class);

    // 测试链接===》》http://localhost/sign/api/simple/query.do?cid=3029
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    User queryBrandById(Long cid) {
        logger.debug(cid);
        return new User();
    }

    // 测试链接===》》http://localhost/sign/api/simple/query2.do?cid=3029
    @RequestMapping(value = "/query2", method = {RequestMethod.GET})
    public @ResponseBody
    User queryBrand(@RequestParam(value = "cid") Long id) {
        logger.debug(id);
        return new User();
    }

    // POST MAN 工具测试
    // 测试链接===》》http://localhost/sign/api/simple/query1.do
    // 参数 {"cid":3029}
    @RequestMapping(value = "/query1", method = {RequestMethod.POST})
    public @ResponseBody
    User queryBrandById(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        Long cid = Long.valueOf(map.get("cid").toString());
        logger.debug(cid);
        return new User();
    }

    //测试链接===》》http://localhost/sign/api/simple/sign.do
    @RequestMapping(value = "/sign", method = {RequestMethod.GET})
    public @ResponseBody
    String testSign() {
        //http://api.test.com/test?name=hello&home=world&work=java
        String httpUrl = "http://api.test.com/test";
        Certificate cert = new Certificate
                .CertificateBuilder()
                .setAccessKey("access")
                .setSecretKey("secret")
                .setCharset(StandardCharsets.UTF_8)
                //.setRequestNonce("参数的唯一标识")
                //.setRequestTimestamp("12454654745")
                .builder();
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("name", "hello");
        param.put("home", "world");
        param.put("work", "java");

        String urlParamsWithSign = cert.getUrlParamsWithSign(param);
        String sign = cert.getSign(param);
        logger.info(urlParamsWithSign);
        logger.info(sign);

        httpUrl += "?" + urlParamsWithSign;

        return httpUrl;
    }

}
