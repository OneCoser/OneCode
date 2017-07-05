package chenhao.lib.onecode.image;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 所属项目：KongKong
 * 创建日期：2017/3/12
 * 创建人：onecode
 * 修改日期：2017/3/12
 * 修改人：onecode
 * 描述：GetPhotoInfo
 */

public class GetPhotoInfo implements Parcelable {

    public boolean onlyCamera;
    public boolean canCamera;
    public int hasCount;
    public int maxCount;
    public boolean selectOnly;
    public boolean needCrop;
    public int cropWidth;
    public int cropHeight;
    public boolean cropIsFixed;

    public static GetPhotoInfo getDefualtInfo(){
        GetPhotoInfo info=new GetPhotoInfo();
        info.onlyCamera=false;
        info.canCamera=true;
        info.hasCount=0;
        info.maxCount=1;
        info.selectOnly=true;
        info.needCrop=false;
        info.cropWidth=400;
        info.cropHeight=400;
        info.cropIsFixed=false;
        return info;
    }

    public static GetPhotoInfo getHeadInfo(){
        GetPhotoInfo info=new GetPhotoInfo();
        info.onlyCamera=false;
        info.canCamera=true;
        info.hasCount=0;
        info.maxCount=1;
        info.selectOnly=true;
        info.needCrop=true;
        info.cropWidth=400;
        info.cropHeight=400;
        info.cropIsFixed=true;
        return info;
    }

    public static GetPhotoInfo getMoreInfo(int has,int max){
        GetPhotoInfo info=new GetPhotoInfo();
        info.onlyCamera=false;
        info.canCamera=true;
        info.hasCount=has;
        info.maxCount=max;
        info.selectOnly=false;
        info.needCrop=false;
        info.cropWidth=250;
        info.cropHeight=250;
        info.cropIsFixed=false;
        return info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.onlyCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canCamera ? (byte) 1 : (byte) 0);
        dest.writeInt(this.hasCount);
        dest.writeInt(this.maxCount);
        dest.writeByte(this.selectOnly ? (byte) 1 : (byte) 0);
        dest.writeByte(this.needCrop ? (byte) 1 : (byte) 0);
        dest.writeInt(this.cropWidth);
        dest.writeInt(this.cropHeight);
        dest.writeByte(this.cropIsFixed ? (byte) 1 : (byte) 0);
    }

    public GetPhotoInfo() {
    }

    protected GetPhotoInfo(Parcel in) {
        this.onlyCamera = in.readByte() != 0;
        this.canCamera = in.readByte() != 0;
        this.hasCount = in.readInt();
        this.maxCount = in.readInt();
        this.selectOnly = in.readByte() != 0;
        this.needCrop = in.readByte() != 0;
        this.cropWidth = in.readInt();
        this.cropHeight = in.readInt();
        this.cropIsFixed = in.readByte() != 0;
    }

    public static final Parcelable.Creator<GetPhotoInfo> CREATOR = new Parcelable.Creator<GetPhotoInfo>() {
        @Override
        public GetPhotoInfo createFromParcel(Parcel source) {
            return new GetPhotoInfo(source);
        }

        @Override
        public GetPhotoInfo[] newArray(int size) {
            return new GetPhotoInfo[size];
        }
    };
}
