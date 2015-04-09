package com.duke.android.ufowatch.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the 'library' of all the users videos
 * 
 * @author paul.blundell
 */
public class Library implements Serializable {
	private static final long serialVersionUID = 7602146391401965702L;
	// A list of videos that the user owns
	private List<Video> videos;

	public Library(List<Video> videos) {
		this.videos = videos;
	}

	public Library() {
		this.videos = new ArrayList<Video>();
	}

	/**
	 * @return the videos
	 */
	public List<Video> getVideos() {
		return videos;
	}

	/**
	 * @param videos
	 */
	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	public int size() {
		// return the videos list size
		return this.videos != null ? this.videos.size() : 0;
	}

	// get the video at position
	public Video get(int position) {
		// return (this.videos != null) ? this.videos.get(position) : null;
		return this.videos.get(position);
	}

	public boolean isEmpty() {
		// return the videos list size
		return this.videos.isEmpty();
	}

	public void add(Video video) {
		this.videos.add(video);
	}

}