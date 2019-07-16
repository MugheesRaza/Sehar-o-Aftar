package com.example.mughees.chat_app.Model;

public class post {
    String title;
    String description;
    String Category;

    public post(String title, String description,String category) {
        this.title = title;
        this.description = description;
        this.Category = category;
    }

    public post() {
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
