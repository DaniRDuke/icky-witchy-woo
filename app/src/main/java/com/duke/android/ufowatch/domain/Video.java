package com.duke.android.ufowatch.domain;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * This is a representation of a users video off YouTube
 * 
 * @author paul.blundell
 */
public class Video implements Serializable {
	private static final long serialVersionUID = -6606324903202400802L;
	// The id of the video
	private String id;
	// The title of the video
	private String title;
	// A link to the video on youtube
	private String url;
	// A link to a still image of the youtube video
	private String thumbUrl;
	// A link to a high quality still image of the youtube video
	private String thumbUrlHq;
	// Duration of the video (seconds)
	private String duration;
	// The description of the video
	private String description;
	// The upload date of the video
	private String publishedAt;
	// The date video added to playlist
	private String addedToPlaylistDt;
	// The id of the channel that published the video
	private String channelId;
	// The title of the channel that published the video
	private String channelTitle;
	// Number of times viewed
	private BigInteger viewCount;
	private BigInteger likes;
	private BigInteger dislikes;

	public Video() {
		super();
	}

	public Video(String id, String title, String url, String thumbUrl, String thumbUrlHq, String duration,
			String description, String publishedAt, String channelId, BigInteger viewCount) {
		super();
		this.id = id;
		this.title = title;
		this.url = url;
		this.thumbUrl = thumbUrl;
		this.thumbUrlHq = thumbUrlHq;
		this.duration = duration;
		this.description = description;
		this.publishedAt = publishedAt;
		this.channelId = channelId;
		this.viewCount = viewCount;
	}

	/**
	 * @return the id of the video
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the title of the video
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the url to this video on youtube
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the thumbUrl of a still image representation of this video
	 */
	public String getThumbUrl() {
		return thumbUrl;
	}

	/**
	 * @return the thumbUrl of a high quality still image representation of this
	 *         video
	 */
	public String getThumbUrlHq() {
		return thumbUrlHq;
	}

	/**
	 * @return the duration of this video
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @return the description of the video
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the date publishedAt
	 */
	public String getUploadedDt() {
		return publishedAt;
	}

	/**
	 * @return the channelId of the uploader
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @return the number of times viewed
	 */
	public BigInteger getViewCount() {
		return viewCount;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setViewCount(BigInteger viewCount) {
		this.viewCount = viewCount;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public void setThumbUrlHq(String thumbUrlHq) {
		this.thumbUrlHq = thumbUrlHq;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUploadedDt(String uploaded) {
		this.publishedAt = uploaded;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public BigInteger getLikes() {
		return likes;
	}

	public BigInteger getDislikes() {
		return dislikes;
	}

	public void setLikes(BigInteger likes) {
		this.likes = likes;
	}

	public void setDislikes(BigInteger dislikes) {
		this.dislikes = dislikes;
	}

	public String getChannelTitle() {
		return channelTitle;
	}

	public void setChannelTitle(String channelTitle) {
		this.channelTitle = channelTitle;
	}

	public String getAddedToPlaylistDt() {
		return addedToPlaylistDt;
	}

	public void setAddedToPlaylistDt(String addedToPlaylistDt) {
		this.addedToPlaylistDt = addedToPlaylistDt;
	}

}