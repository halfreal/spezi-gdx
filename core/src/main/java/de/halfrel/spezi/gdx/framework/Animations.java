package de.halfrel.spezi.gdx.framework;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import de.halfreal.spezi.gdx.system.Assets;

public class Animations {

	public static interface AnimationListener {
		void animationFinished();
	}

	public static AlphaAction alphaAction(Color destination, float newAlpha) {
		AlphaAction alpha = Actions.alpha(newAlpha);
		alpha.setColor(destination);
		return alpha;
	}

	public static AlphaAction alphaAction(Color destination, float newAlpha,
			float time) {
		AlphaAction alpha = Actions.alpha(newAlpha, time);
		alpha.setColor(destination);
		return alpha;
	}

	public static AlphaAction alphaAction(Color destination, float newAlpha,
			float time, Interpolation interpolation) {
		AlphaAction alpha = Actions.alpha(newAlpha, time, interpolation);
		alpha.setColor(destination);
		return alpha;
	}

	public static Action attackToLeft() {
		return sequence(moveBy(-Assets.dip(30), 0, 0.2f), delay(0.2f),
				moveBy(Assets.dip(30), 0, 0.1f));
	}

	public static Action attackToRight() {
		return sequence(moveBy(Assets.dip(30), 0, 0.2f), delay(0.2f),
				moveBy(-Assets.dip(30), 0, 0.1f));
	}

	public static Action blink(AnimationListener listener) {
		return sequence(color(ColorHelper.TRANSPARENT, 1f), color(Color.WHITE),
				finish(listener));
	}

	public static Action blink(Color startColor, Color targetColor,
			float duration) {
		return sequence(color(targetColor, duration / 2),
				color(startColor, duration / 2));
	}

	public static Action blinkForever() {
		return forever(sequence(color(ColorHelper.TRANSPARENT, 1f),
				color(Color.WHITE)));
	}

	public static Action blinkProgress() {

		return Actions.repeat(5, Actions.sequence(Actions.fadeOut(0f),
				Actions.delay(0.1f), Actions.fadeIn(0f), Actions.delay(0.1f)));
	}

	public static Action blinkSlow() {
		return forever(sequence(delay(2f), fadeOut(0.5f), fadeIn(0.5f)));
	}

	public static Action completeAction(final TransitionListener listener) {
		return run(new Runnable() {

			@Override
			public void run() {
				listener.complete();
			}
		});
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

	public static Action fadeInAnimation(TransitionListener listener) {
		return sequence(fadeInAnimation(), Animations.completeAction(listener));
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

	public static Action fadeOutAnimation(TransitionListener listener) {
		return sequence(fadeOutAnimation(), Animations.completeAction(listener));
	}

	public static Action fadeOutLongAndLeave() {
		return sequence(color(ColorHelper.TRANSPARENT, 2f), removeActor());
	}

	public static Action finish(final AnimationListener listener) {
		return run(new Runnable() {

			@Override
			public void run() {
				listener.animationFinished();
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

	public static Action hitBlue() {
		return parallel(
				sequence(color(Color.BLUE, 0.5f, Interpolation.elastic),
						color(Color.WHITE, 0.05f, Interpolation.elasticOut)),
				sequence(fadeOut(0.25f, Interpolation.elasticIn),
						fadeIn(0.25f, Interpolation.elastic)));
	}

	public static Action hitGreen() {
		return parallel(
				sequence(color(Color.GREEN, 0.5f, Interpolation.elastic),
						color(Color.WHITE, 0.05f, Interpolation.elasticOut)),
				sequence(fadeOut(0.25f, Interpolation.elasticIn),
						fadeIn(0.25f, Interpolation.elastic)),
				sequence(scaleBy(0.1f, 0.1f, 0.25f),
						scaleBy(-0.1f, -0.1f, 0.25f)));
	}

	public static Action hitRed() {
		return Actions.parallel(Actions.sequence(Actions.alpha(1f),
				Actions.color(Color.RED, 0.45f, Interpolation.elastic),
				Actions.color(Color.WHITE, 0.05f, Interpolation.elasticOut)),
				Actions.sequence(
						Actions.alpha(0.1f, 0.25f, Interpolation.elasticIn),
						Actions.alpha(0.5f, 0.25f, Interpolation.elastic),
						Actions.alpha(1f, 0.2f)));
	}

	public static TemporalAction interpolate(TemporalAction action,
			Interpolation interpolation) {
		action.setInterpolation(interpolation);
		return action;
	}

	public static Action moveLeftAndLeave() {
		return sequence(
				moveBy(-AbstractScreen.WIDTH, 0, TimingHelper.UI_ELEMENT_SNAPIN),
				removeActor());
	}

	public static Action moveRightAndLeave() {
		return sequence(
				moveBy(AbstractScreen.WIDTH, 0, TimingHelper.UI_ELEMENT_SNAPIN),
				removeActor());
	}

	public static Action moveRightAndLeave(TransitionListener listener) {
		return sequence(moveBy(AbstractScreen.WIDTH, 0, 0.2f),
				Animations.completeAction(listener));
	}

	public static Action positiveEffect() {
		return parallel(sequence(
				color(Color.GREEN, 0.25f, Interpolation.swingIn),
				color(Color.WHITE, 0.25f, Interpolation.swingOut)));
	}

	public static Action pulse() {
		return Actions.forever(sequence(
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

	public static Action rotateForever() {
		return Actions.forever(Actions.rotateBy(360f, 2f));
	}

	public static Action rotateForeverClock() {
		return Actions.forever(Actions.rotateBy(-360f, 2f));
	}

	public static Action rotateToAction(float degree) {
		return Animations.interpolate(
				rotateTo(degree, TimingHelper.ROTATION_ANIMATION_TIME),
				Interpolation.pow2In);
	}

	public static Action shake() {

		return Actions.repeat(2, Actions.sequence(
				Actions.moveBy(AbstractScreen.dip(5f), 0, 0.02f),
				Actions.moveBy(-AbstractScreen.dip(5f), 0, 0.02f),
				Actions.moveBy(-AbstractScreen.dip(5f), 0, 0.02f),
				Actions.moveBy(AbstractScreen.dip(5f), 0, 0.02f)));
	}

	public static Action shakeStrong() {

		return Actions.repeat(2, Actions.sequence(
				Actions.moveBy(AbstractScreen.dip(15f), 0, 0.05f),
				Actions.moveBy(-AbstractScreen.dip(15f), 0, 0.05f),
				Actions.moveBy(-AbstractScreen.dip(15f), 0, 0.05f),
				Actions.moveBy(AbstractScreen.dip(15f), 0, 0.05f)));
	}

	public static Action startLeftMoveRight() {
		return sequence(moveBy(-AbstractScreen.WIDTH, 0),
				moveBy(AbstractScreen.WIDTH, 0, 0.2f));
	}

	public static Action startLeftMoveRight(TransitionListener listener) {
		return sequence(startLeftMoveRight(),
				Animations.completeAction(listener));
	}

	public static Action startTopRightAndMoveLeft(float topOffset,
			float rightOffset) {
		return sequence(
				moveTo(AbstractScreen.WIDTH, AbstractScreen.HEIGHT - topOffset),
				moveBy(-rightOffset, 0, TimingHelper.DIALOG_FADE));
	}

}
