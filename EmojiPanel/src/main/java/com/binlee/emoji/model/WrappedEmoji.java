package com.binlee.emoji.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 19-7-30.
 *
 * @author leebin
 */
public class WrappedEmoji implements Serializable {
    
    private static final long serialVersionUID = 3392703176362269326L;
    
    public Emoji mEmoji;
    public EmojiGroup mGroup;
    public List<Emoji> mEmojiList;
}
