package com.dims.lyrically.models;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id &&
                Objects.equals(fullTitle, song.fullTitle) &&
                Objects.equals(title, song.title) &&
                Objects.equals(songArtImageThumbnailUrl, song.songArtImageThumbnailUrl) &&
                Objects.equals(url, song.url) &&
                Objects.equals(titleWithFeatured, song.titleWithFeatured) &&
                Objects.equals(artistName, song.artistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullTitle, title, songArtImageThumbnailUrl, url, titleWithFeatured, artistName, id);
    }
}
