package com.dims.lyrically.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "favourites")
public class Favourites implements Serializable {
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "full_title")
    private String fullTitle;

    private String title;
    @ColumnInfo(name = "thumbnail_url")
    private String songArtImageThumbnailUrl;

    private String url;
    @ColumnInfo(name = "title_featured")
    private String titleWithFeatured;
    @ColumnInfo(name = "artist_name")
    private String artistName;

    public int getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favourites that = (Favourites) o;
        return id == that.id &&
                Objects.equals(fullTitle, that.fullTitle) &&
                Objects.equals(title, that.title) &&
                Objects.equals(songArtImageThumbnailUrl, that.songArtImageThumbnailUrl) &&
                Objects.equals(url, that.url) &&
                Objects.equals(titleWithFeatured, that.titleWithFeatured) &&
                Objects.equals(artistName, that.artistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullTitle, title, songArtImageThumbnailUrl, url, titleWithFeatured, artistName);
    }

    public Favourites(int id, String fullTitle, String title, String songArtImageThumbnailUrl,
                      String url, String titleWithFeatured, String artistName){
        this.id = id;
        this.fullTitle = fullTitle;
        this.title = title;
        this.songArtImageThumbnailUrl = songArtImageThumbnailUrl;
        this.url = url;
        this.titleWithFeatured = titleWithFeatured;
        this.artistName = artistName;
    }
}
