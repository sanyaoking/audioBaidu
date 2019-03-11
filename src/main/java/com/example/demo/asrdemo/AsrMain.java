package com.example.demo.asrdemo;



import com.example.demo.common.ConnUtil;
import com.example.demo.common.DemoException;
import com.example.demo.common.TokenHolder;
import com.example.demo.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsrMain {
    /*由于语音识别需要调用getUserMedia接口，
    而这个接口是需要https://协议下的网站才能访问的
    所以我们需要给我们的网站配置https从而来使用这个接口。！！！！*/
    public static void main(String[] args) throws IOException, DemoException {
        AsrMain demo = new AsrMain(null);
        // 填写下面信息
        String result = demo.run();
        System.out.println("识别结束：结果是：");
        System.out.println(result);
    }
    public AsrMain(MultipartFile mf){
        this.mfile = mf;
    }

    public MultipartFile mfile;
    //  填写网页上申请的appkey 如 $apiKey="g8eBUMSokVB1BHGmgxxxxxx"
    private final String appKey = "Ojwk9VudXPDZPfGVnLnyeeF5";

    // 填写网页上申请的APP SECRET 如 $secretKey="94dc99566550d87f8fa8ece112xxxxx"
    private final String secretKey = "huxeHKGG0IOrpGFvuq2gNwgbawadmyk4";

    // 需要识别的文件
    private final String filename = "16k_test.pcm";

    // 文件格式
    private final String format = "pcm";

    //  1537 表示识别普通话，使用输入法模型。1536表示识别普通话，使用搜索模型。 其它语种参见文档
    private final int dev_pid = 1537;
    //用于区分用户使用
    private String cuid = "1234567JAVA";
 
 // 采样率固定值
    private final int rate = 16000;//16000;
	
    public boolean methodRaw = false; // false以json方式上传音频文件

    private final String url = "http://vop.baidu.com/server_api"; // 可以改为https

    public String run() throws IOException, DemoException {
        TokenHolder holder = new TokenHolder(appKey, secretKey, TokenHolder.ASR_SCOPE);
        holder.resfresh();
        String token = holder.getToken();
        String result = null;
        if (methodRaw) {
            result = runRawPostMethod(token);
        } else {
            result = runJsonPostMethod(token);
        }
        return result;
    }

    private String runRawPostMethod(String token) throws IOException, DemoException {
        String url2 = url + "?cuid=" + ConnUtil.urlEncode(cuid) + "&dev_pid=" + dev_pid + "&token=" + token;
        //System.out.println(url2);
        byte[] content = getFileContent(filename);
        HttpURLConnection conn = (HttpURLConnection) new URL(url2).openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("Content-Type", "audio/" + format + "; rate=" + rate);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.getOutputStream().write(content);
        conn.getOutputStream().close();
        String result = ConnUtil.getResponseString(conn);
        return result;
    }

    public String runJsonPostMethod(String token) throws DemoException, IOException {

        byte[] content = getFileContent(filename);
        String speech = base64Encode(content);

        JSONObject params = new JSONObject();
        params.put("dev_pid", dev_pid);
        params.put("format", format);
        params.put("rate", rate);
        params.put("token", token);
        params.put("cuid", cuid);
        params.put("channel", "1");
//        params.put("lan", "zh");
        params.put("len", content.length);
        params.put("speech", speech);

        // System.out.println(params.toString());
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setDoOutput(true);
        conn.getOutputStream().write(params.toString().getBytes());
        conn.getOutputStream().close();
        String result = ConnUtil.getResponseString(conn);
        return result;
    }

    private byte[] getFileContent(String filename) throws DemoException, IOException {
        if(this.mfile==null) {
            File file = new File(filename);
            if (!file.canRead()) {
                System.err.println("文件不存在或者不可读: " + file.getAbsolutePath());
                throw new DemoException("file cannot read: " + file.getAbsolutePath());
            }
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
//            return ConnUtil.getInputStreamContent(mfile.getInputStream());
                return ConnUtil.getInputStreamContent(is);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            return ConnUtil.getInputStreamContent(mfile.getInputStream());
        }
    }

    private String base64Encode(byte[] content) {
        /**
         Base64.Encoder encoder = Base64.getEncoder(); // JDK 1.8  推荐方法
         String str = encoder.encodeToString(content);
         **/

        char[] chars = Base64Util.encode(content); // 1.7 及以下，不推荐，请自行跟换相关库
        String str = new String(chars);
//        System.out.print(str.length());
        return str;
    }

}
