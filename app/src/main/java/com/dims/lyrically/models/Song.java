package com.dims.lyrically.models;

import java.io.Serializable;

public class Song implements Serializable {
    private String fullTitle, title, songArtImageThumbnailUrl, url, titleWithFeatured, artistName;
    private int id;

    public Song(String fullTitle, String title, String songArtImageThumbnailUrl,
                String url, String titleWithFeatured, int id, String artistName){
        this.fullTitle = fullTitle;
        this.title = title;
        this.songArtImageThumbnailUrl = songArtImageThumbnailUrl;
        this.url = url;
        this.titleWithFeatured = titleWithFeatured;
        this.id = id;
        this.artistName = artistName;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSongArtImageThumbnailUrl() {
        return songArtImageThumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getTitleWithFeatured() {
        return titleWithFeatured;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getId() {
        return id;
    }
}
