package cn.fingersoft.imag.okhttpmanager.upload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import cn.fingersoft.imag.okhttpmanager.R;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.upload.UploadManager;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.OkHttpUtils;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttputils.request.PostRequest;
import cn.fingersoft.imag.okhttpmanager.upload.list.UpLoadAdapter;
import cn.fingersoft.imag.okhttpmanager.utils.Urls;


/**
 * Created by heinika on 16-9-1.
 * 上传界面
 */

public class UpLoadFragment extends Fragment implements View.OnClickListener{
    private TextView textViewFilePath;
    private UploadManager uploadManager;
    private String[] mFiles;
    private RecyclerView recyclerView;
    private UpLoadAdapter upLoadAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload,null);
        Button buttonSelete = (Button) view.findViewById(R.id.button_select_upload_file);
        buttonSelete.setOnClickListener(this);
        textViewFilePath = (TextView) view.findViewById(R.id.textview_upload_filepath);
        uploadManager = UploadManager.getInstance();
        uploadManager.getThreadPool().setCorePoolSize(3);   //同时可上传数量
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_upload);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        upLoadAdapter = new UpLoadAdapter(getActivity());
        recyclerView.setAdapter(upLoadAdapter);
        refresh();
        return view;
    }

    private void refresh() {
        upLoadAdapter.setInfoList(uploadManager.getAllTask());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_select_upload_file:
                DialogProperties properties=new DialogProperties();
                properties.selection_mode= DialogConfigs.MULTI_MODE;
                properties.selection_type=DialogConfigs.FILE_SELECT;
                properties.root=new File(DialogConfigs.DIRECTORY_SEPERATOR);
                properties.extensions=null;
                FilePickerDialog dialog = new FilePickerDialog(getActivity(),properties);
                dialog.show();
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        StringBuilder sb = new StringBuilder();
                        mFiles = files;
                        for (String filePath:files) {
                            sb.append(filePath+"\n");
                        }
                        textViewFilePath.setText(sb.toString());
                        if (mFiles != null) {
                            for (int i = 0; i < mFiles.length; i++) {
                                PostRequest postRequest = OkHttpUtils.post(Urls.URL_FORM_UPLOAD)//
                                        .headers("headerKey1", "headerValue1")//
                                        .headers("headerKey2", "headerValue2")//
                                        .params("paramKey1", "paramValue1")//
                                        .params("paramKey2", "paramValue2")//
                                        .params("fileKey" + i, new File(mFiles[i]));
                                uploadManager.addTask(mFiles[i], postRequest, null);
                            }
                            upLoadAdapter.setInfoList(uploadManager.getAllTask());
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
