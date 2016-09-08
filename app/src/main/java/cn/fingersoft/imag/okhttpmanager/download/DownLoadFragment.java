package cn.fingersoft.imag.okhttpmanager.download;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.fingersoft.imag.okhttpmanager.R;
import cn.fingersoft.imag.okhttpmanager.download.Bean.ApkInfo;
import cn.fingersoft.imag.okhttpmanager.download.list.DownLoadAdapter;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.download.DownloadInfo;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.download.DownloadManager;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.download.DownloadService;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.OkHttpUtils;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.GetRequest;
import cn.fingersoft.imag.okhttpmanager.utils.ConvertUtils;

import static cn.fingersoft.imag.okhttpmanager.download.list.DownLoadAdapter.DELETE_TASK;


/**
 * Created by heinika on 16-9-1.
 */

public class DownLoadFragment extends Fragment implements DownLoadAdapter.OnItemLongClickListener,View.OnClickListener{
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private List<DownloadInfo> mDownloadList;
    private TextView textViewTargetPath;
    public DownLoadAdapter getAdapter() {
        return mAdapter;
    }
    private DownLoadAdapter mAdapter;
    private DownloadManager downloadManager;
    private List<ApkInfo> apks;
    int num = 0;
    public Button buttonDelete;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_download);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new DownLoadAdapter(getActivity());
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        downloadManager = DownloadService.getDownloadManager();
        buttonDelete = (Button) view.findViewById(R.id.button_delete_task);
        buttonDelete.setOnClickListener(this);
        initApksData();
        Button buttonAdd = (Button) view.findViewById(R.id.button_add_task);
        buttonAdd.setOnClickListener(this);
        Button buttonChangeTargetPath = (Button) view.findViewById(R.id.button_change_download_path);
        buttonChangeTargetPath.setOnClickListener(this);
        textViewTargetPath = (TextView) view.findViewById(R.id.textview_download_path);
        refresh();
        return view;
    }

    private String mTargetFolder;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add_task:
                ApkInfo apkInfo = apks.get(num);
                GetRequest request = OkHttpUtils.get(apkInfo.getUrl())//
                        .headers("headerKey1", "headerValue1")//
                        .headers("headerKey2", "headerValue2")//
                        .params("paramKey1", "paramValue1")//
                        .params("paramKey2", "paramValue2");
                downloadManager = DownloadService.getDownloadManager();
                downloadManager.setTargetFolder(mTargetFolder);
                downloadManager.addTask(apkInfo.getUrl(), request, null);
                num++;
                refresh();
                break;
            case R.id.button_change_download_path:
                String DM_TARGET_FOLDER = File.separator + "download" + File.separator; //下载管理目标文件夹
                downloadManager.setTargetFolder(Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER);//default filepat
                textViewTargetPath.setText(Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER);
                DialogProperties properties=new DialogProperties();
                properties.selection_mode= DialogConfigs.SINGLE_MODE;
                properties.selection_type=DialogConfigs.DIR_SELECT;
                properties.root=new File(DialogConfigs.DIRECTORY_SEPERATOR);
                properties.extensions=null;
                FilePickerDialog dialog = new FilePickerDialog(getActivity(),properties);
                dialog.show();
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        textViewTargetPath.setText(files[0]);
                        mTargetFolder = files[0];
                    }
                });
                break;
            case R.id.button_delete_task:
                mAdapter.downLoadAdapterHandler.sendEmptyMessage(DELETE_TASK);
                hideDeleteButton();
                break;
            default:
                break;
        }
    }

    // 返回键按下时会被调用
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            hideDeleteButton();
            return true;
        }
        return false;
    }

    @Override
    public void onItemLongClick() {
        showDeleteButton();
    }

    private void refresh() {
        mDownloadList = downloadManager.getAllTask();
        mAdapter.setInfoList(mDownloadList);
        mAdapter.notifyDataSetChanged();
    }

    private void initApksData() {
        apks = new ArrayList<>();
        ApkInfo apkInfo1 = new ApkInfo();
        apkInfo1.setName("美丽加");
        apkInfo1.setUrl("http://download.apk8.com/d2/soft/meilijia.apk");
        apks.add(apkInfo1);
        ApkInfo apkInfo2 = new ApkInfo();
        apkInfo2.setName("果然方便");
        apkInfo2.setUrl("http://download.apk8.com/d2/soft/guoranfangbian.apk");
        apks.add(apkInfo2);
        ApkInfo apkInfo3 = new ApkInfo();
        apkInfo3.setName("薄荷");
        apkInfo3.setUrl("http://download.apk8.com/d2/soft/bohe.apk");
        apks.add(apkInfo3);
        ApkInfo apkInfo4 = new ApkInfo();
        apkInfo4.setName("GG助手");
        apkInfo4.setUrl("http://download.apk8.com/d2/soft/GGzhushou.apk");
        apks.add(apkInfo4);
        ApkInfo apkInfo5 = new ApkInfo();
        apkInfo5.setName("红包惠锁屏");
        apkInfo5.setUrl("http://download.apk8.com/d2/soft/hongbaohuisuoping.apk");
        apks.add(apkInfo5);
        ApkInfo apkInfo6 = new ApkInfo();
        apkInfo6.setName("快的打车");
        apkInfo6.setUrl("http://download.apk8.com/soft/2015/%E5%BF%AB%E7%9A%84%E6%89%93%E8%BD%A6.apk");
        apks.add(apkInfo6);
        ApkInfo apkInfo7 = new ApkInfo();
        apkInfo7.setName("叮当快药");
        apkInfo7.setUrl("http://d2.apk8.com:8020/soft/dingdangkuaiyao.apk");
        apks.add(apkInfo7);
        ApkInfo apkInfo8 = new ApkInfo();
        apkInfo8.setName("悦跑圈");
        apkInfo8.setUrl("http://d2.apk8.com:8020/soft/yuepaoquan.apk");
        apks.add(apkInfo8);
        ApkInfo apkInfo9 = new ApkInfo();
        apkInfo9.setName("悠悠导航");
        apkInfo9.setUrl("http://d2.apk8.com:8020/soft/%E6%82%A0%E6%82%A0%E5%AF%BC%E8%88%AA2.3.32.1.apk");
        apks.add(apkInfo9);
        ApkInfo apkInfo10 = new ApkInfo();
        apkInfo10.setName("虎牙直播");
        apkInfo10.setUrl("http://download.apk8.com/down4/soft/hyzb.apk");
        apks.add(apkInfo10);
    }

    private void hideDeleteButton() {
        ObjectAnimator//
                .ofFloat(buttonDelete, "y", buttonDelete.getY(), buttonDelete.getY()+ ConvertUtils.dp2px(getActivity(),50))//
                .setDuration(300)//
                .start();
    }

    private void showDeleteButton() {
        ObjectAnimator//
                .ofFloat(buttonDelete, "y", buttonDelete.getY(), buttonDelete.getY()- ConvertUtils.dp2px(getActivity(),50))//
                .setDuration(300)//
                .start();
    }
}
