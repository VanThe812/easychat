package va.vanthe.app_chat_2.entity;

public class Emoji {
    private String emoji;
    private boolean checker;

    public Emoji() {}

    public Emoji(String emoji, boolean checker) {
        this.emoji = emoji;
        this.checker = checker;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public boolean isChecker() {
        return checker;
    }

    public void setChecker(boolean checker) {
        this.checker = checker;
    }
}
