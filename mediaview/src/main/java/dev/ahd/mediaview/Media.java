package dev.ahd.mediaview;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class Media {

    public static final String VIDEO = "video";
    public static final String IMAGE = "image";

    private String contentLink;
    private String contentType;

    @Retention(SOURCE)
    @StringDef({VIDEO, IMAGE})
    @interface contentType {
    }

    public Media(String contentLink, @NonNull @contentType String contentType) {
        this.contentLink = contentLink;
        this.contentType = contentType;
    }

    public String getContentLink() {
        return contentLink;
    }

    public void setContentLink(String contentLink) {
        this.contentLink = contentLink;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(@NonNull @contentType String contentType) {
        this.contentType = contentType;
    }
}
