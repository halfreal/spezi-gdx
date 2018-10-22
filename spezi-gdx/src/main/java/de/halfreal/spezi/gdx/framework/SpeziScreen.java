package de.halfreal.spezi.gdx.framework;

public abstract class SpeziScreen implements ExtendedScreen {
    private final SpeziGame game;

    public SpeziScreen(SpeziGame game) {
        this.game = game;
    }

    public SpeziGame getGame() {
        return game;
    }
}
