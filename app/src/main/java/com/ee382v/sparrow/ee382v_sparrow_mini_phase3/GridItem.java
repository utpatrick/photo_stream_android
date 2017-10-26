package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

/**
 * Created by kyle on 10/20/17.
 */

public class GridItem {

    private String title;
    private String link;
    private String extra;

    public GridItem(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public GridItem(String title, String link, String extra) {
        this(title, link);
        this.extra = extra;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getExtra() {
        return extra;
    }

}
