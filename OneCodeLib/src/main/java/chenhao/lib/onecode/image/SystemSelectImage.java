package chenhao.lib.onecode.image;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import java.io.File;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.base.BaseActivity;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;
import chenhao.lib.onecode.view.FilletBtView;

/**
 * 所属项目：OneCode
 * 创建日期：2018/1/11
 * 创建人：onecode
 * 修改日期：2018/1/11
 * 修改人：onecode
 * 描述：调用系统默认方式选择图片
 */

public class SystemSelectImage extends BaseActivity{

    private static final int RECODE_OPEN_CAMERA=19;
    public static final int RECODE_SELECT_IMAGE=20;
    public static void start(Activity a,GetPhotoInfo info){
        Intent intent=new Intent(a,SystemSelectImage.class);
        intent.putExtra("info",info);
        a.startActivityForResult(intent, RECODE_SELECT_IMAGE);
    }

    private GetPhotoInfo info;
    private String selectImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info=getIntent().getParcelableExtra("info");
        if (null==info){
            info=GetPhotoInfo.getDefualtInfo();
        }
        setContentView(R.layout.onecode_activity_system_select_image);
        FilletBtView selectCancel=findV(R.id.system_select_image_cacel);
        FilletBtView selectList=findV(R.id.system_select_image_list);
        FilletBtView selectCamera=findV(R.id.system_select_image_camera);
        selectCancel.setOnClickListener(onClickListener);
        selectList.setOnClickListener(onClickListener);
        selectCamera.setOnClickListener(onClickListener);
        setVis(selectCamera,info.canCamera);
        setVis(selectList,selectCamera.getVisibility());
        setVis(selectCancel,selectCamera.getVisibility());
        if (info.onlyCamera){
            openCamera();
        }else if (!info.canCamera){
            openSystemSelect();
        }
    }

    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.system_select_image_cacel){
                finish();
            }else if(v.getId()==R.id.system_select_image_list){
                openSystemSelect();
            }else if(v.getId()==R.id.system_select_image_camera){
                openCamera();
            }
        }
    };

    private void openSystemSelect(){
        try {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, RECODE_SELECT_IMAGE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openCamera(){
        if (null!= OneCode.getConfig()){
            try {
                selectImagePath = OneCode.getConfig().getCachePath() + System.currentTimeMillis() + ".jpg";
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imageFile = new File(selectImagePath);
                imageFile.getParentFile().mkdirs();
                cameraIntent.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
                        .putExtra("return-data", true)
                        .putExtra("autofocus", true);
                startActivityForResult(cameraIntent, RECODE_OPEN_CAMERA);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String getSystemSelectImage(Intent data){
        String s="";
        if (null!=data){
            try {
                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor =getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                s = cursor.getString(columnIndex);  //获取照片路径
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return s;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == RECODE_SELECT_IMAGE){
                String s=getSystemSelectImage(data);
                if (info.needCrop&&StringUtils.isNotEmpty(s)){
                    AlbumListActivity.goCrop(this,s,info.cropWidth,info.cropHeight,info.cropIsFixed);
                }else{
                    reImageSelect(s);
                }
            }else if(requestCode == RECODE_OPEN_CAMERA){
                if (info.needCrop){
                    AlbumListActivity.goCrop(this,selectImagePath,info.cropWidth,info.cropHeight,info.cropIsFixed);
                }else{
                    reImageSelect(selectImagePath);
                }
            }else if(requestCode == AlbumListActivity.RECODE_GET_PHOTO_CROP){
                reImageSelect(data.getStringExtra("data"));
            }
        }
    }

    private void reImageSelect(String image){
        if (StringUtils.isNotEmpty(image)){
            selectImagePath=image;
            UiUtil.init().showDialog(this,false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String str="";
                    try {
                        str=new ReSizeImage(selectImagePath).launch();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (StringUtils.isEmpty(str)){
                        str=selectImagePath;
                    }
                    EventBus.getDefault().post(str,"system_select_resize_success");
                }
            }).start();
        }else{
            finish();
        }
    }

    @Subscriber(tag = "system_select_resize_success")
    public void eventSelect(String image){
        if (null!=OneCode.getConfig()){
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(OneCode.getConfig().getCachePath())));
            sendBroadcast(scanIntent);
        }
        UiUtil.init().cancelDialog();
        setResult(RESULT_OK, new Intent().putExtra("data",image));
        finish();
    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void systemStatusAction(int status) {

    }
}
