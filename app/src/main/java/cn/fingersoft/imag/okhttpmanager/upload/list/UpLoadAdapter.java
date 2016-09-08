package cn.fingersoft.imag.okhttpmanager.upload.list;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.fingersoft.imag.okhttpmanager.MainActivity;
import cn.fingersoft.imag.okhttpmanager.R;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.listener.UploadListener;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.upload.UploadInfo;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.upload.UploadManager;
import okhttp3.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by heinika on 16-9-2.
 */

public class UpLoadAdapter extends RecyclerView.Adapter<UpLoadAdapter.ViewHplder> {
    private UploadManager mUploadManager;
    private List<UploadInfo> infoList;
    private Activity mContext;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public UpLoadAdapter(Activity context) {
        mContext = context;
        mUploadManager = UploadManager.getInstance();
        notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(mContext);
    }
    
    

    public void setInfoList(List<UploadInfo> infoList) {
        this.infoList = infoList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHplder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHplder(LayoutInflater.from(mContext).inflate(R.layout.item_download_file, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHplder holder, final int position) {
        final UploadInfo uploadInfo = infoList.get(position);

        String fileName = uploadInfo.getTaskKey();
        if (fileName != null) {
            holder.textViewName.setText(fileName);
            if (fileName.endsWith(".apk")) {
                holder.imageViewFileIcon.setImageResource(R.mipmap.fileicon_apk);
            } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                holder.imageViewFileIcon.setImageResource(R.mipmap.fileicon_image);
            }
        }

        String downloadLength = Formatter.formatFileSize(mContext, uploadInfo.getUploadLength());
        String totalLength = Formatter.formatFileSize(mContext, uploadInfo.getTotalLength());
        holder.textViewFileProgress.setText(downloadLength + "/" + totalLength);
        String networkSpeed = Formatter.formatFileSize(mContext, uploadInfo.getNetworkSpeed());
        holder.textViewSpeed.setText(networkSpeed + "/s");
        holder.progressBar.setMax((int) uploadInfo.getTotalLength());
        holder.progressBar.setProgress((int) uploadInfo.getUploadLength());
        switch (uploadInfo.getState()) {
            case UploadManager.NONE:
                holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_download);
                break;
            case UploadManager.UPLOADING:
                holder.textViewStatus.setText("UPLOADING");
                break;
            case UploadManager.WAITING:
                holder.textViewStatus.setText("WAITING");
                break;
            case UploadManager.ERROR:
                holder.textViewStatus.setText("ERROR");
                break;
            case UploadManager.FINISH:
                holder.textViewStatus.setText("FINISH");
                holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_download);
                break;
        }


        final UploadListener downloadListener = new UploadListener() {
            @Override
            public void onProgress(UploadInfo uploadInfo) {
                String fileName = uploadInfo.getTaskKey();
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
                builder.setProgress((int) uploadInfo.getTotalLength(), (int) uploadInfo.getUploadLength(), false);
                PendingIntent pi = PendingIntent.getActivity(mContext, 0, new Intent(mContext,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pi);
                notificationManager.notify(position,builder.build());


                String downloadLength = Formatter.formatFileSize(mContext, uploadInfo.getUploadLength());
                String totalLength = Formatter.formatFileSize(mContext, uploadInfo.getTotalLength());
                holder.textViewFileProgress.setText(downloadLength + "/" + totalLength);
                String networkSpeed = Formatter.formatFileSize(mContext, uploadInfo.getNetworkSpeed());
                holder.textViewSpeed.setText(networkSpeed + "/s");
                holder.progressBar.setMax((int) uploadInfo.getTotalLength());
                holder.progressBar.setProgress((int) uploadInfo.getUploadLength());
                switch (uploadInfo.getState()) {
                    case UploadManager.NONE:
                        holder.imageViewDownloadStatus.setImageResource(R.mipmap.dl_operation_download);
                        break;
                    case UploadManager.UPLOADING:
                        holder.textViewStatus.setText("UPLOADING");
                        break;
                    case UploadManager.WAITING:
                        holder.textViewStatus.setText("WAITING");
                        break;
                    case UploadManager.ERROR:
                        holder.textViewStatus.setText("ERROR");
                        break;
                    case UploadManager.FINISH:
                        holder.textViewStatus.setText("FINISH");
                        break;
                }
            }

            @Override
            public void onFinish(Object o) {

            }


            @Override
            public void onError(UploadInfo uploadInfo, String errorMsg, Exception e) {

            }

            @Override
            public Object parseNetworkResponse(Response response) throws Exception {
                return null;
            }
        };
        uploadInfo.setListener(downloadListener);

        holder.imageViewDownloadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (uploadInfo.getState()) {
                    case UploadManager.NONE:
                    case UploadManager.ERROR:
                        mUploadManager.addTask(uploadInfo.getUrl(), uploadInfo.getRequest(),null );
                        break;
                    case UploadManager.FINISH:
                        break;
                }
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
