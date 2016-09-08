package cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.download.db;


import java.io.Serializable;

import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.cache.CacheMode;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.model.HttpHeaders;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.model.HttpParams;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.BaseRequest;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.DeleteRequest;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.GetRequest;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.HeadRequest;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.OptionsRequest;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.PostRequest;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.PutRequest;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：16/8/8
 * 描    述：与BaseRequest对应,主要是为了序列化
 * 修订历史：
 * ================================================
 */
public class DownloadRequest implements Serializable {

    private static final long serialVersionUID = -6883956320373276785L;

    public String method;
    public String url;
    public CacheMode cacheMode;
    public String cacheKey;
    public long cacheTime;
    public HttpParams params;
    public HttpHeaders headers;

    public static String getMethod(BaseRequest request) {
        if (request instanceof GetRequest) return "get";
        if (request instanceof PostRequest) return "post";
        if (request instanceof PutRequest) return "put";
        if (request instanceof DeleteRequest) return "delete";
        if (request instanceof OptionsRequest) return "options";
        if (request instanceof HeadRequest) return "head";
        return "";
    }

    public static BaseRequest createRequest(String url, String method) {
        if (method.equals("get")) return new GetRequest(url);
        if (method.equals("post")) return new PostRequest(url);
        if (method.equals("put")) return new PutRequest(url);
        if (method.equals("delete")) return new DeleteRequest(url);
        if (method.equals("options")) return new OptionsRequest(url);
        if (method.equals("head")) return new HeadRequest(url);
        return null;
    }
}
