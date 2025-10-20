package com.notesapi.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notes", indexes = {
    @Index(columnList = "server_id"),
    @Index(columnList = "channel_id"),
    @Index(columnList = "discord_user_id")
})
public class Note {
    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updatedAt = new Date();

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long serverId;

    @Column(nullable = false)
    private Long channelId;

    @Column(name = "discord_user_id", nullable = false)
    private Long discordUserId;

    @Column(nullable = false)
    private String visibility = "private";

    @Temporal(TemporalType.TIMESTAMP)
    private Date alertAt;

    public Note() {
        Date now = new Date();

        this.createdAt = now;
        this.updatedAt = now;
    }

    public Note(String title, String content, Long serverId, Long channelId, Long discordUserId) {
        this();

        this.title = title;
        this.content = content;
        this.serverId = serverId;
        this.channelId = channelId;
        this.discordUserId = discordUserId;
    }

    public Long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getAlertAt() {
        return alertAt;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getServerId() {
        return serverId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Long getDiscordUserId() {
        return discordUserId;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public void setDiscordUserId(Long discordUserId) {
        this.discordUserId = discordUserId;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setAlertAt(Date alertAt) {
        this.alertAt = alertAt;
    }
}
