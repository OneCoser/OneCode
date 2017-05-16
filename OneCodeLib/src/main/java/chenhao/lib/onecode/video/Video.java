package chenhao.lib.onecode.video;

import android.os.Parcel;
import android.os.Parcelable;
import chenhao.lib.onecode.base.BaseModule;
import chenhao.lib.onecode.utils.ImageShow;
import chenhao.lib.onecode.utils.StringUtils;

public class Video extends BaseModule implements Parcelable {

    public String id;
    public long size;
    public String url;
    public String name;
    public String path;
    public String thumbPath;

    public String getShowUrl(){
        return url+"?vframe/jpg/offset/1";
    }

    public String checkShowUrl(){
        if (StringUtils.startWithHttp(url)){
            return url+"?vframe/jpg/offset/1";
        }else{
            return ImageShow.getFileUri(thumbPath);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.size);
        dest.writeString(this.url);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.thumbPath);
    }

    public Video() {
    }

    protected Video(Parcel in) {
        this.id = in.readString();
        this.size = in.readLong();
        this.url = in.readString();
        this.name = in.readString();
        this.path = in.readString();
        this.thumbPath = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
