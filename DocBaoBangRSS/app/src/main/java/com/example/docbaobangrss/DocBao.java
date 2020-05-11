package com.example.docbaobangrss;

public class DocBao implements ContentData {
    public String title;
    public String link;
    public String hinhanh;

    public DocBao(String title, String link, String hinhanh) {
        this.title = title;
        this.link = link;
        this.hinhanh = hinhanh;
    }

    @Override
    public String getLink() {
        return link;
    }
}
