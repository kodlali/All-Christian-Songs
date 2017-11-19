package com.jesus.christiansongs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class MinistryEngine {
	String ministryName;
	Vector<String> albums;
	HashMap<String, Vector<SongInfo>> albumSongs;

	ArrayList<String> song_names;
	ArrayList<String> song_urls;

	public MinistryEngine() {
		// TODO Auto-generated constructor stub
		albums = new Vector<String>();
		albumSongs = new HashMap<String, Vector<SongInfo>>();
		song_names = new ArrayList<String>();
		song_urls = new ArrayList<String>();
	}

	public String getMinistryName() {
		return ministryName;
	}

	public void setMinistryName(String ministry_name) {
		this.ministryName = ministry_name;
	}

	public Vector<String> getAlbums() {
		return albums;
	}

	public void setAlbums(String album) {
		albums.add(album);
	}

	public HashMap<String, Vector<SongInfo>> getAlbumSongs() {
		return albumSongs;
	}

	public ArrayList<String> getSongNames() {
		return song_names;
	}
	public void setAlbumSongs(String album, Vector<SongInfo> songs) {
		albumSongs.put(album, songs);
		for (SongInfo songInfo : songs) {
			song_names.add(songInfo.getName());
			song_urls.add(songInfo.getUrl());
		}
	}

}
