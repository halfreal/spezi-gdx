package de.halfreal.spezi.gdx.framework;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import de.halfreal.spezi.gdx.system.Assets;

// TODO Clean up and make API more consistent:
// * Are all methods needed here?
// * Replace hard-coded values with constants or make them variable.
public class SpeziActions {

	public interface ActionFinishedListener {
		void onActionFinished();
	}

	public static AlphaAction alphaColor(Color color, float alpha) {
		AlphaAction action = alpha(alpha);
		action.setColor(color);
		return action;
	}

	public static AlphaAction alphaColor(Color color, float alpha,
			float duration) {
		AlphaAction action = alpha(alpha, duration);
		action.setColor(color);
		return action;
	}

	public static AlphaAction alphaColor(Color color, float alpha,
			float duration, Interpolation interpolation) {
		AlphaAction action = alpha(alpha, duration, interpolation);
		action.setColor(color);
		return action;
	}

	public static Action blink() {
		return sequence(color(Color.CLEAR, 1f), color(Color.WHITE));
	}

	public static Action blink(Color startColor, Color targetColor,
			float duration) {
		return sequence(color(targetColor, duration / 2),
				color(startColor, duration / 2));
	}

	public static Action blinkForever() {
		return forever(blink());
	}

	public static Action blinkProgress() {
		return repeat(5,
				sequence(fadeOut(0f), delay(0.1f), fadeIn(0f), delay(0.1f)));
	}

	public static Action blinkSlow() {
		return forever(sequence(delay(2f), fadeOut(0.5f), fadeIn(0.5f)));
	}

	public static Action fadeInAnimation() {
		return sequence(fadeOut(0), fadeIn(0.2f));
	}

	public static Action fadeInAnimation(float delay) {
		return sequence(fadeOut(0), delay(delay), fadeIn(0.2f));
	}

	public static Action fadeInAnimation(float delay, float maxAlpha) {
		return sequence(fadeOut(0), delay(delay), alpha(maxAlpha, 0.2f));
	}

	public static Action fadeInAnimationSlow() {
		return sequence(fadeOut(0), fadeIn(0.5f));
	}

	public static Action fadeInAnimationSlow(float duration) {
		return sequence(fadeOut(0), fadeIn(duration));
	}

	public static Action fadeOutAndLeave() {
		return sequence(alpha(0f, TimingHelper.DIALOG_FADE), removeActor());
	}

	public static Action fadeOutAndLeave(float delay) {
		return sequence(delay(delay), alpha(0f, TimingHelper.DIALOG_FADE),
				removeActor());
	}

	public static Action fadeOutAndRun(Runnable runnable) {
		return sequence(alpha(0f, TimingHelper.DIALOG_FADE), run(runnable));

	}

	public static Action fadeOutAnimation() {
		return sequence(fadeIn(0), fadeOut(0.5f));
	}

	public static Action fadeOutAnimation(float alpha) {
		return sequence(fadeIn(0), alpha(alpha, 0.5f));
	}

	public static Action fadeOutLongAndLeave() {
		return sequence(color(Color.CLEAR, 2f), removeActor());
	}

	public static Action finish(Action action, ActionFinishedListener listener) {
		return sequence(action, finish(listener));
	}

	public static Action finish(final ActionFinishedListener listener) {
		return run(new Runnable() {
			@Override
			public void run() {
				if (listener != null) {
					listener.onActionFinished();
				}
			}
		});
	}

	public static Action floating() {
		return sequence(
				delay((float) (Math.random() * 2)),
				forever(sequence(
						moveBy(0, Assets.dip(5), 1f, Interpolation.pow2),
						moveBy(0, -Assets.dip(5), 1f, Interpolation.pow2))));
	}

	public static TemporalAction interpolate(TemporalAction action,
			Interpolation interpolation) {
		action.setInterpolation(interpolation);
		return action;
	}

	public static Action moveLeftAndLeave() {
		return sequence(
				moveBy(-AbstractScreen.width, 0, TimingHelper.UI_ELEMENT_SNAPIN),
				removeActor());
	}

	public static Action moveRightAndLeave() {
		return sequence(
				moveBy(AbstractScreen.width, 0, TimingHelper.UI_ELEMENT_SNAPIN),
				removeActor());
	}

	public static Action pulse() {
		return forever(sequence(
				scaleTo(1.5f, 1.5f, 0.5f, Interpolation.circleIn),
				scaleTo(1f, 1f, 0.5f, Interpolation.circleOut)));
	}

	public static Action rotateAndFade() {
		return sequence(
				fadeOut(0),
				fadeIn(0.1f),
				forever(parallel(
						sequence(delay(5f), fadeOut(2f), fadeIn(2f)),
						sequence(
								rotateBy(40,
										TimingHelper.ROTATION_ANIMATION_TIME),
								delay((float) Math.random()),
								rotateBy(-40,
										TimingHelper.ROTATION_ANIMATION_TIME)))));
	}

	public static Action rotateForeverClockwise() {
		return forever(rotateBy(-360f, 2f));
	}

	public static Action rotateForeverCounterClockwise() {
		return forever(rotateBy(360f, 2f));
	}

	public static Action rotateToInterpolate(float degree) {
		return interpolate(
				rotateTo(degree, TimingHelper.ROTATION_ANIMATION_TIME),
				Interpolation.pow2In);
	}

	public static Action shake() {
		return repeat(
				2,
				sequence(moveBy(AbstractScreen.dip(5f), 0, 0.02f),
						moveBy(-AbstractScreen.dip(5f), 0, 0.02f),
						moveBy(-AbstractScreen.dip(5f), 0, 0.02f),
						moveBy(AbstractScreen.dip(5f), 0, 0.02f)));
	}

	public static Action shakeStrong() {
		return repeat(
				2,
				sequence(moveBy(AbstractScreen.dip(15f), 0, 0.05f),
						moveBy(-AbstractScreen.dip(15f), 0, 0.05f),
						moveBy(-AbstractScreen.dip(15f), 0, 0.05f),
						moveBy(AbstractScreen.dip(15f), 0, 0.05f)));
	}

	public static Action startLeftMoveRight() {
		return sequence(moveBy(-AbstractScreen.width, 0),
				moveBy(AbstractScreen.width, 0, 0.2f));
	}

	public static Action startLeftMoveRight(ActionFinishedListener listener) {
		return sequence(startLeftMoveRight(), finish(listener));
	}

	public static Action startTopRightAndMoveLeft(float topOffset,
			float rightOffset) {
		return sequence(
				moveTo(AbstractScreen.width, AbstractScreen.height - topOffset),
				moveBy(-rightOffset, 0, TimingHelper.DIALOG_FADE));
	}

}
