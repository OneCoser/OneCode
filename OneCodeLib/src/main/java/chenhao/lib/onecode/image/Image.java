package chenhao.lib.onecode.image;

import android.os.Parcel;
import android.os.Parcelable;

import chenhao.lib.onecode.base.BaseModule;
import chenhao.lib.onecode.utils.ImageShow;
import chenhao.lib.onecode.utils.StringUtils;

/**
 * 一个图片对象
 * @author onecode
 */
public class Image extends BaseModule implements Parcelable {

	public String id;
	public String url;
	public String name;
	public String path;
	public String thumbPath;


	public String existsPath(){
		return StringUtils.isNotEmpty(path)?path:thumbPath;
	}

	public String checkUrl(){
		if (StringUtils.startWithHttp(url)){
			return url;
		}else{
			return ImageShow.getFileUri(existsPath());
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.url);
		dest.writeString(this.name);
		dest.writeString(this.path);
		dest.writeString(this.thumbPath);
	}

	public Image() {
	}

	protected Image(Parcel in) {
		this.id = in.readString();
		this.url = in.readString();
		this.name = in.readString();
		this.path = in.readString();
		this.thumbPath = in.readString();
	}

	public static final Creator<Image> CREATOR = new Creator<Image>() {
		@Override
		public Image createFromParcel(Parcel source) {
			return new Image(source);
		}

		@Override
		public Image[] newArray(int size) {
			return new Image[size];
		}
	};
}
