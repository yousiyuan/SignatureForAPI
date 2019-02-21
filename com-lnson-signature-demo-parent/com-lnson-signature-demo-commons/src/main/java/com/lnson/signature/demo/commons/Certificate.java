package com.lnson.signature.demo.commons;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

/**
 * 实现开放API接口签名验证
 * 参考链接：https://www.jianshu.com/p/ad410836587a
 */
public class Certificate {

    /**
     * Certificate构造对象，存储签名相关的参数
     */
    private CertificateBuilder property;
    /**
     * 假设允许客户端和服务端最多能存在15分钟的时间差，同时追踪记录在服务端的nonce集合。
     * 当有新的请求进入时，
     * 首先检查携带的timestamp是否在15分钟内，如超出时间范围，则拒绝，
     * 然后查询携带的nonce，如存在已有集合，则拒绝。否则，记录该nonce，并删除集合内时间戳大于15分钟的nonce
     * （可以使用redis的expire，新增nonce的同时设置它的超时失效时间为15分钟）。
     */
    private String timestamp;
    /**
     * 唯一的随机字符串，用来标识每个被签名的请求
     */
    private String nonce;
    /**
     * 签名
     */
    private String sign;

    private Certificate(CertificateBuilder builder) {
        this.property = builder;
    }

    /**
     * 获取最终请求参数列表
     * 注意：外部不要将timestamp、nonce、accessKey和sign这4个参数添加到方法参数params集合中
     */
    public String getUrlParamsWithSign(final LinkedHashMap<String, String> params) {
        this.sign = getSign(params);

        //6、最终请求Url参数
        params.put("timestamp", this.timestamp);
        params.put("nonce", this.nonce);
        params.put("sign", this.sign);
        params.put("accessKey", property.accessKey);

        List<String> urlParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String strParam = MessageFormat.format("{0}={1}", entry.getKey(), entry.getValue());
            urlParams.add(strParam);
        }
        return String.join("&", urlParams);
    }

    /**
     * 获取签名
     * 注意：外部不要将timestamp、nonce、accessKey和sign这4个参数添加到方法参数params集合中
     */
    public String getSign(final LinkedHashMap<String, String> params) {
        if (this.sign == null || "".equals(this.sign))
            this.sign = createSign(params);
        return this.sign;
    }

    /**
     * 创建签名
     * 注意：外部不要将timestamp、nonce、accessKey和sign这4个参数添加到方法参数params集合中
     */
    private String createSign(final LinkedHashMap<String, String> params) {
        //1、除去空值请求参数
        LinkedHashMap<String, String> newRequestParams = removeEmptyParam(params);

        //2、按照请求参数名的字母升序排列非空请求参数（包含AccessKey)，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串
        newRequestParams.put("AccessKey", property.accessKey);
        String strUrlParams = parseUrlString(newRequestParams);

        //3、生成当前时间戳timestamp=now和唯一随机字符串nonce=random
        strUrlParams = addUniqueParam(strUrlParams);

        //4、最后拼接上Secretkey得到字符串stringSignTemp
        String stringSignTemp = MessageFormat.format("{0}&SecretKey={1}", strUrlParams, property.secretKey);

        //5、对stringSignTemp进行MD5运算，并将得到的字符串所有字符转换为大写，得到sign值
        return DigestUtils.md5Hex(stringSignTemp.getBytes(property.charset)).toUpperCase();
    }

    /**
     * 移除空请求参数
     */
    private LinkedHashMap<String, String> removeEmptyParam(final LinkedHashMap<String, String> params) {
        LinkedHashMap<String, String> newParams = new LinkedHashMap<>();
        if (params == null || params.size() <= 0) {
            return newParams;
        }
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null || value.equals(""))
                continue;
            newParams.put(key, value);
        }
        return newParams;
    }

    /**
     * 使用URL键值对的格式（即key1=value1&key2=value2…）将参数列表拼接成字符串
     */
    private String parseUrlString(final Map<String, String> requestMap) {
        List<String> keyList = new ArrayList<>(requestMap.keySet());
        Collections.sort(keyList);

        List<String> entryList = new ArrayList<>();
        for (String key : keyList) {
            String value = requestMap.get(key);
            try {
                value = URLEncoder.encode(value, property.charset.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            entryList.add(MessageFormat.format("{0}={1}", key, value));
        }
        return String.join("&", entryList);
    }

    /**
     * timestamp+nonce方案标识每个被签名的请求
     */
    private String addUniqueParam(final String urlParams) {
        if (property.requestTimestamp == null)
            this.timestamp = String.valueOf(System.currentTimeMillis());
        else
            this.timestamp = property.requestTimestamp;
        if (property.requestNonce == null)
            this.nonce = UUID.randomUUID().toString().replaceAll("-", "");
        else
            this.timestamp = property.requestNonce;
        return MessageFormat.format("{0}&timestamp={1}&nonce={2}", urlParams, this.timestamp, this.nonce);
    }

    public static class CertificateBuilder {

        /**
         * 公匙
         */
        private String accessKey;

        /**
         * 私匙
         */
        private String secretKey;

        /**
         * 字符编码
         */
        private Charset charset = StandardCharsets.UTF_8;

        /**
         * 服务端接收到的请求参数
         */
        private String requestTimestamp;

        /**
         * 服务端接收到的请求参数
         */
        private String requestNonce;

        public CertificateBuilder setAccessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public CertificateBuilder setSecretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public CertificateBuilder setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public CertificateBuilder setRequestTimestamp(String requestTimestamp) {
            this.requestTimestamp = requestTimestamp;
            return this;
        }

        public CertificateBuilder setRequestNonce(String requestNonce) {
            this.requestNonce = requestNonce;
            return this;
        }

        public Certificate builder() {
            return new Certificate(this);
        }

    }

}
