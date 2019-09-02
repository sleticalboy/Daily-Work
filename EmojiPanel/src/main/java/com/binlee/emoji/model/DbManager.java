package com.binlee.emoji.model;

import java.util.List;

public final class DbManager {

    private DbManager() {
    }

    public void updateLastUsedEmojiGroup(List<EmojiGroup> groups, int uid) {
    }

    public List<Emoji> queryRecentUsedEmojis(int uid) {
        return null;
    }

    public void updateRecentUsedEmojis(List<Emoji> emojis, int uid) {
    }

    public List<Emoji> queryEmojiListByGroupUuid(String uuid, int uid) {
        return null;
    }

    public List<Emoji> queryCustomEmojis(int uid) {
        return null;
    }

    public boolean hasEmojiGroup(String uuid) {
        return false;
    }

    public boolean hasCustomEmoji(String sha1) {
        return false;
    }

    public EmojiGroup getCustomEmojiGroup(int userId) {
        return null;
    }

    public EmojiGroup queryEmojiGroupByType(int type, int userId) {
        return null;
    }

    public void insertEmojiGroup(EmojiGroup group, int userId) {
    }

    public void insertEmojiList(List<Emoji> list, int userId) {
    }

    public List<EmojiGroup> queryAllEmojiGroup(int userId) {
        return null;
    }

    public void deleteEmojisByGroup(String uuid) {
    }

    public void updateEmojiGroupNameThumbnailCount(EmojiGroup group, int userId) {
    }

    public EmojiGroup queryEmojiGroupByUuid(String uuid, int userId) {
        return null;
    }

    public void deleteEmojiList(int userId, List<Emoji> list) {
    }

    private static class Singleton {
        static DbManager sManager = new DbManager();
    }

    public static DbManager getInstance() {
        return Singleton.sManager;
    }
}
