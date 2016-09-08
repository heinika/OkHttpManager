package cn.fingersoft.imag.okhttpmanager.download.list;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.fingersoft.imag.okhttpmanager.MainActivity;
import cn.fingersoft.imag.okhttpmanager.R;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.download.DownloadInfo;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.download.DownloadManager;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.download.DownloadService;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.listener.DownloadListener;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by heinika on 16-9-2.
 */

public class DownLoadAdapter extends RecyclerView.Adapter<DownLoadAdapter.ViewHplder> {
    private DownloadManager mDownloadManager;
    public static final int BACK_EVENT = 0x01;
    public static final int DELETE_TASK = 0x02;
    private List<DownloadInfo> infoList;
    private Activity mContext;
    private boolean backEnable;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public DownLoadAdapter(Activity context) {
        mContext = context;
        mDownloadManager = DownloadService.getDownloadManager();
        notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(mContext);
    }

    public boolean isBackEnable() {
        if(infoList!=null){
            return infoList.get(0).isCheckBoxShow();
        }
        return false;
    }


    public  Handler downLoadAdapterHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case BACK_EVENT:{
                    for(int i=0;i<infoList.size();i++){
                        DownloadInfo downloadInfo = infoList.get(i);
                        downloadInfo.setNeedDelete(false);
                        downloadInfo.setCheckBoxShow(false);
                    }
                    notifyDataSetChanged();
                }
                break;
                case DELETE_TASK:{
                    for(int i=0;i<infoList.size();i++){
                        DownloadInfo downloadInfo = infoList.get(i);
                        if (downloadInfo.isNeedDelete()) {
                            mDownloadManager.removeTask(downloadInfo.getUrl());
                            i--;   //因为移除了一个，所以下一个的index减了1
                        } else {
                            downloadInfo.setCheckBoxShow(false);
                        }
                        notifyDataSetChanged();
                    }
                }
                break;
                default:
                    break;
            }
        }
    } ;

    public void setInfoList(List<DownloadInfo> infoList) {
        this.infoList = infoList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHplder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHplder(LayoutInflater.from(mContext).inflate(R.layout.item_download_file, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHplder holder, final int position) {
        final DownloadInfo downloadInfo = infoList.get(position);

        String fileName = downloadInfo.getFileName();
        if (fileName != null) {
            holder.textViewName.setText(fileName);
            if (fileName.endsWith(".apk")) {
                holder.imageViewFileIcon.setImageResource(R.mipmap.fileicon_apk);
            } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                holder.imageViewFileIcon.setImageResource(R.mipmap.fileicon_image);
            }
        }
        if(downloadInfo.isCheckBoxShow()){
            holder.checkBox.setVisibility(View.VISIBLE);
            if(downloadInfo.isNeedDelete()){
                holder.checkBox.setChecked(true);
            }else {
                holder.checkBox.setChecked(false);
            }
        }else {
            holder.checkBox.setVisibility(View.GONE);
        }
        String downloadLength = Formatter.formatFileSize(mContext, downloadInfo.getDownloadLength());
        String totalLength = Formatter.formatFileSize(mContext, downloadInfo.getTotalLength());
        holder.textViewFileProgress.setText(downloadLength + "/" + totalLength);
        String networkSpeed = Formatter.formatFileSize(mContext, downloadInfo.getNetworkSpeed());
        holder.textViewSpeed.setText(networkSpeed + "/s");
        holder.progressBar.setMax((int) downloadInfo.getTotalLength());
        holder.progressBar.setProgress((int) downloadInfo.getDownloadLength());
        switch (downloadInfo.getState()) {
            case DownloadManager.NONE:
                holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_download);
                break;
            case DownloadManager.DOWNLOADING:
                holder.textViewStatus.setText("DOWNLOADING");
                holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_pause);
                break;
            case DownloadManager.PAUSE:
                holder.textViewStatus.setText("PAUSE");
                holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_download);
                break;
            case DownloadManager.WAITING:
                holder.textViewStatus.setText("WAITING");
                break;
            case DownloadManager.ERROR:
                holder.textViewStatus.setText("ERROR");
                break;
            case DownloadManager.FINISH:
                holder.textViewStatus.setText("FINISH");
                holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_download);
                break;
        }


        final DownloadListener downloadListener = new DownloadListener() {
            @Override
            public void onProgress(DownloadInfo downloadInfo) {
                String fileName = downloadInfo.getFileName();
                if (fileName != null) {
                    holder.textViewName.setText(fileName);
                    if (fileName.endsWith(".apk")) {
                        holder.imageViewFileIcon.setImageResource(R.mipmap.fileicon_apk);
                    } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                        holder.imageViewFileIcon.setImageResource(R.mipmap.fileicon_image);
                    }
                }

                builder.setSmallIcon(R.mipmap.fileicon_apk);
                builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.fileicon_apk));
                builder.setAutoCancel(true);
                builder.setContentTitle(fileName);
                builder.setProgress((int) downloadInfo.getTotalLength(), (int) downloadInfo.getDownloadLength(), false);
                PendingIntent pi = PendingIntent.getActivity(mContext, 0, new Intent(mContext,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pi);
                notificationManager.notify(position,builder.build());


                String downloadLength = Formatter.formatFileSize(mContext, downloadInfo.getDownloadLength());
                String totalLength = Formatter.formatFileSize(mContext, downloadInfo.getTotalLength());
                holder.textViewFileProgress.setText(downloadLength + "/" + totalLength);
                String networkSpeed = Formatter.formatFileSize(mContext, downloadInfo.getNetworkSpeed());
                holder.textViewSpeed.setText(networkSpeed + "/s");
                holder.progressBar.setMax((int) downloadInfo.getTotalLength());
                holder.progressBar.setProgress((int) downloadInfo.getDownloadLength());
                switch (downloadInfo.getState()) {
                    case DownloadManager.NONE:
                        holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_download);
                        break;
                    case DownloadManager.DOWNLOADING:
                        holder.textViewStatus.setText("DOWNLOADING");
                        holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_pause);
                        break;
                    case DownloadManager.PAUSE:
                        holder.textViewStatus.setText("PAUSE");
                        holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_download);
                        break;
                    case DownloadManager.WAITING:
                        holder.textViewStatus.setText("WAITING");
                        break;
                    case DownloadManager.ERROR:
                        holder.textViewStatus.setText("ERROR");
                        break;
                    case DownloadManager.FINISH:
                        holder.textViewStatus.setText("FINISH");
                        break;
                }
            }

            @Override
            public void onFinish(DownloadInfo downloadInfo) {

            }

            @Override
            public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {

            }
        };
        downloadInfo.setListener(downloadListener);

        holder.imageViewDownloadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (downloadInfo.getState()) {
                    case DownloadManager.PAUSE:
                    case DownloadManager.NONE:
                    case DownloadManager.ERROR:
                        mDownloadManager.addTask(downloadInfo.getUrl(), downloadInfo.getRequest(), downloadListener);
                        break;
                    case DownloadManager.DOWNLOADING:
                        mDownloadManager.pauseTask(downloadInfo.getUrl());
                        break;
                    case DownloadManager.FINISH:
                        break;
                }
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
                if (!downloadInfo.isCheckBoxShow()) {
                    for(DownloadInfo info:infoList){
                        info.setCheckBoxShow(true);
                    }
                    if(onItemLongClickListener!=null){
                        onItemLongClickListener.onItemLongClick();
                    }
//                    DeleteTaskPopupWindow window = new DeleteTaskPopupWindow(mContext);
//                    //设置layout在PopupWindow中显示的位置
//                    window.showAtLocation(mContext.findViewById(R.id.activity_down_load), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                }
//                mDownloadManager.removeTask(downloadInfo.getUrl());
                notifyDataSetChanged();
            }
        });
        holder.itemDownload.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return gestureDetector.onGenericMotionEvent(event);
                } else {
                    return gestureDetector.onTouchEvent(event);
                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                downloadInfo.setNeedDelete(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoList == null ? 0 : infoList.size();
    }


    public class ViewHplder extends RecyclerView.ViewHolder {
        TextView textViewFileProgress;
        TextView textViewSpeed;
        ImageView imageViewFileIcon;
        ImageView imageViewDownloadStatus;
        ProgressBar progressBar;
        LinearLayout itemDownload;
        TextView textViewName;
        TextView textViewStatus;
        CheckBox checkBox;

        public ViewHplder(View itemView) {
            super(itemView);
            textViewFileProgress = (TextView) itemView.findViewById(R.id.textview_progress);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar_download);
            imageViewFileIcon = (ImageView) itemView.findViewById(R.id.imageview_fileicon);
            imageViewDownloadStatus = (ImageView) itemView.findViewById(R.id.imageview_download_status);
            textViewSpeed = (TextView) itemView.findViewById(R.id.textview_speed);
            itemDownload = (LinearLayout) itemView.findViewById(R.id.item_download);
            textViewName = (TextView) itemView.findViewById(R.id.textview_filename);
            textViewStatus = (TextView) itemView.findViewById(R.id.textview_download_status);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_delete);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private OnItemLongClickListener onItemLongClickListener;
    public interface OnItemLongClickListener{
        void onItemLongClick();
    }
}
