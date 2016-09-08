package cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.callback;

import android.support.annotation.Nullable;

import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.BaseRequest;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：抽象的回调接口
 * 修订历史：
 * ================================================
 */
public abstract class AbsCallback<T> {

    /** 请求网络开始前，UI线程 */
    public void onBefore(BaseRequest request) {
    }

    /** 拿到响应后，将数据转换成需要的格式，子线程中执行，可以是耗时操作 */
    public abstract T parseNetworkResponse(Response response) throws Exception;

    /** 对返回数据进行操作的回调， UI线程 */
    public abstract void onResponse(boolean isFromCache, T t, Request request, @Nullable Response response);

    /** 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程 */
    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
        if (e != null) e.printStackTrace();
    }

    /** 请求网络结束后，UI线程 */
    public void onAfter(boolean isFromCache, @Nullable T t, Call call, @Nullable Response response, @Nullable Exception e) {
    }

    /**
     * Post执行上传过程中的进度回调，get请求不回调，UI线程
     *
     * @param currentSize  当前上传的字节数
     * @param totalSize    总共需要上传的字节数
     * @param progress     当前上传的进度
     * @param networkSpeed 当前上传的速度   字节/秒
     */
    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    }

    /**
     * 执行下载过程中的进度回调，UI线程
     *
     * @param currentSize  当前下载的字节数
     * @param totalSize    总共需要下载的字节数
     * @param progress     当前下载的进度
     * @param networkSpeed 当前下载的速度   字节/秒
     */
    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    }

    public static final AbsCallback CALLBACK_DEFAULT = new AbsCallback() {

        @Override
        public Response parseNetworkResponse(Response response) throws Exception {
            return response;
        }

        @Override
        public void onResponse(boolean isFromCache, Object data, Request request, Response response) {
        }
    };
}