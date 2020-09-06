package com.binlee.emoji.model

class DbManager private constructor() {

    fun updateLastUsedEmojiGroup(groups: List<EmojiGroup?>?, uid: Int) {}

    fun queryRecentUsedEmojis(uid: Int): List<Emoji>? {
        return null
    }

    fun updateRecentUsedEmojis(emojis: List<Emoji?>?, uid: Int) {}
    fun queryEmojiListByGroupUuid(uuid: String?, uid: Int): List<Emoji>? {
        return null
    }

    fun queryCustomEmojis(uid: Int): List<Emoji>? {
        return null
    }

    fun hasEmojiGroup(uuid: String?): Boolean {
        return false
    }

    fun hasCustomEmoji(sha1: String?): Boolean {
        return false
    }

    fun getCustomEmojiGroup(userId: Int): EmojiGroup? {
        return null
    }

    fun queryEmojiGroupByType(type: Int, userId: Int): EmojiGroup? {
        return null
    }

    fun insertEmojiGroup(group: EmojiGroup?, userId: Int) {}

    fun insertEmojiList(list: List<Emoji>?, userId: Int) {}

    fun queryAllEmojiGroup(userId: Int): List<EmojiGroup>? {
        return null
    }

    fun deleteEmojisByGroup(uuid: String?) {}

    fun updateEmojiGroupNameThumbnailCount(group: EmojiGroup?, userId: Int) {}

    fun queryEmojiGroupByUuid(uuid: String?, userId: Int): EmojiGroup? {
        return null
    }

    fun deleteEmojiList(userId: Int, list: List<Emoji>?) {}

    private object Singleton {
        val manager = DbManager()
    }

    companion object {
        fun get(): DbManager = Singleton.manager
    }
}