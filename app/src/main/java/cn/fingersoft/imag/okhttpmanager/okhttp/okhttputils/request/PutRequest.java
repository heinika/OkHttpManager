package cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request;


import java.io.IOException;
import java.util.List;

import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.model.HttpParams;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.utils.HttpUtils;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.utils.OkLogger;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/16
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PutRequest extends BaseBodyRequest<PutRequest> {

    public PutRequest(String url) {
        super(url);
    }

    @Override
    protected Request generateRequest(RequestBody requestBody) {
        try {
            headers.put("Content-Length", String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            OkLogger.e(e);
        }
        Request.Builder requestBuilder = HttpUtils.appendHeaders(headers);
        return requestBuilder.put(requestBody).url(url).tag(tag).build();
    }

    @Override
    public PutRequest addFileWrapperParams(String key, List<HttpParams.FileWrapper> fileWrappers) {
        params.putFileWrapperParams(key, fileWrappers);
        return this;
    }
}