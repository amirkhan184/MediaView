package dev.ahd.mediaview;

public class PlayData {
    private int currentWindow;
    private long currentPosition;
    private int index;
    private boolean canPlay;

    public PlayData(int index, int currentWindow, long currentPosition, boolean canPlay) {
        this.currentWindow = currentWindow;
        this.currentPosition = currentPosition;
        this.index = index;
        this.canPlay = canPlay;
    }

    public boolean canPlay() {
        return canPlay;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }

    public int getCurrentWindow() {
        return currentWindow;
    }

    public void setCurrentWindow(int currentWindow) {
        this.currentWindow = currentWindow;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isEmpty() {
        return currentWindow == 0 && currentPosition == 0 && !canPlay;
    }
}
