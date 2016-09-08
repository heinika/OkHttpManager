package cn.fingersoft.imag.okhttpmanager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import cn.fingersoft.imag.okhttpmanager.download.DownLoadFragment;
import cn.fingersoft.imag.okhttpmanager.okhttp.okhttpsever.download.DownloadManager;
import cn.fingersoft.imag.okhttpmanager.upload.UpLoadFragment;

import static cn.fingersoft.imag.okhttpmanager.download.list.DownLoadAdapter.BACK_EVENT;

public class MainActivity extends AppCompatActivity {

    private DownloadManager downloadManager;
    private FileLoadAdapter fileLoadAdapter;
    private DownLoadFragment downLoadFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_download);
        setUpViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout_download);
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setUpViewPager(ViewPager viewPager){
        fileLoadAdapter = new FileLoadAdapter(getSupportFragmentManager());
        downLoadFragment = new DownLoadFragment();
        fileLoadAdapter.addFragment(downLoadFragment,"download");
        fileLoadAdapter.addFragment(new UpLoadFragment(),"upload");
        viewPager.setAdapter(fileLoadAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            if(downLoadFragment.getAdapter().isBackEnable()) {
                downLoadFragment.getAdapter().downLoadAdapterHandler.sendEmptyMessage(BACK_EVENT);
                downLoadFragment.onKeyDown(keyCode, event);
                return true;
            }else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    //    public String mFilePath;
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != Activity.RESULT_OK) {
//            finish();
//        } else if (requestCode == INFILE_CODE) {
//            mFilePath = Uri.decode(data.getDataString());
//            //通过data.getDataString()得到的路径如果包含中文路径，则会出现乱码现象，经过Uri.decode()函数进行解码，得到正确的路径。但是此时路径为Uri路径，必须转换为String路径，网上有很多方法，本人通过对比发现，Uri路径里多了file：
//            //字符串，所以采用以下方法将前边带的字符串截取掉，获得String路径，可能通用性不够好，下一步会学习更好的方法。
//            mFilePath = mFilePath.substring(7, mFilePath.length());
//            DownLoadFragment downLoadFragment = new DownLoadFragment();
//        }
//    }
}
