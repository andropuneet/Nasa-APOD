package org.nasa_apod.vo;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.squareup.moshi.Json;

import org.jetbrains.annotations.NotNull;

@Entity(primaryKeys = {"date"})
public class Image {
    @NonNull
    @Json(name = "date")
    public final String date;
    @Json(name = "hdurl")
    public final String hdurl;
    @Json(name = "copyright")
    public final String copyright;
    @Json(name = "explanation")
    public final String explanation;
    @Json(name = "media_type")
    public final String media_type;
    @Json(name = "service_version")
    public final String service_version;
    @Json(name = "title")
    public final String title;
    @Json(name = "url")
    public final String url;
    public Image(String explanation, String copyright, @NotNull String date, String hdurl, String media_type, String service_version, String title, String url) {
        this.copyright = copyright;
        this.date = date;
        this.hdurl = hdurl;
        this.explanation = explanation;
        this.media_type = media_type;
        this.service_version = service_version;
        this.title = title;
        this.url = url;
    }

}
