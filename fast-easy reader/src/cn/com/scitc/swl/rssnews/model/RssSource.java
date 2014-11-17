package cn.com.scitc.swl.rssnews.model;

public class RssSource {

	public int id;

	public String name;
	
	public String url;

	public int imgId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
}
