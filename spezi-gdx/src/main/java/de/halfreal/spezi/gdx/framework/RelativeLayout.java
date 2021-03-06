package de.halfreal.spezi.gdx.framework;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class RelativeLayout {

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignAbove(
			ASSIGNEE actor, RELATIVE group) {

		return RelativeLayout.alignAbove(actor, group, 0);

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignAbove(
			ASSIGNEE actor, RELATIVE group, float yOffset) {

		actor.setY(group.getY() + group.getHeight() + yOffset);
		return actor;

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignBelow(
			ASSIGNEE actor, RELATIVE group) {
		return RelativeLayout.alignBelow(actor, group, 0);
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignBelow(
			ASSIGNEE actor, RELATIVE group, float yOffset) {

		actor.setY(group.getY() - actor.getHeight() - yOffset);

		return actor;
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignBottom(
			ASSIGNEE actor, RELATIVE group) {

		return RelativeLayout.alignBottom(actor, group, 0);
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignBottom(
			ASSIGNEE actor, RELATIVE group, float yOffset) {

		actor.setY(0 + yOffset);

		return actor;
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignCenter(
			ASSIGNEE actor, RELATIVE group) {

		actor.setPosition(group.getWidth() / 2f - actor.getWidth() / 2f,
				group.getHeight() / 2f - actor.getHeight() / 2f);

		return actor;
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignCenterHorizontal(
			ASSIGNEE actor, RELATIVE group) {
		if (actor.getParent() == group) {
			actor.setX(group.getWidth() / 2f - actor.getWidth() / 2f);
		} else {
			actor.setX(group.getX() + group.getWidth() / 2f - actor.getWidth()
					/ 2f);
		}

		return actor;
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignCenterOf(
			ASSIGNEE actor, RELATIVE group) {

		actor.setPosition(
				group.getX() + group.getWidth() / 2f - actor.getWidth() / 2f,
				group.getY() + group.getHeight() / 2f - actor.getHeight() / 2f);

		return actor;
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignCenterVertical(
			ASSIGNEE actor, RELATIVE group) {

		actor.setY(group.getHeight() / 2f - actor.getHeight() / 2f);

		return actor;
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignLeft(
			ASSIGNEE actor, RELATIVE group) {

		return alignLeft(actor, group, 0);

	}

	/**
	 * Add Actor to group before layouting!
	 * 
	 * @param actor
	 * @param group
	 * @param xOffset
	 * @return
	 */
	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignLeft(
			ASSIGNEE actor, RELATIVE group, float xOffset) {
		// TODO change aligns in all methods to support inside / outside group
		// alignment
		if (actor.getParent() != group) {
			xOffset += group.getX();
		}
		actor.setX(xOffset);
		return actor;

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignLeftOf(
			ASSIGNEE actor, RELATIVE group) {

		return alignLeftOf(actor, group, 0);

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignLeftOf(
			ASSIGNEE actor, RELATIVE group, float xOffset) {
		actor.setX(group.getX() - actor.getWidth() - xOffset);
		return actor;

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignRight(
			ASSIGNEE actor, RELATIVE group) {
		return alignRight(actor, group, 0);

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignRight(
			ASSIGNEE actor, RELATIVE group, float xOffset) {

		actor.setX(group.getWidth() - actor.getWidth() - xOffset);
		return actor;

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignRightOf(
			ASSIGNEE actor, RELATIVE group) {

		return alignRightOf(actor, group, 0);

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignRightOf(
			ASSIGNEE actor, RELATIVE group, float xOffset) {
		actor.setX(group.getWidth() + group.getX() + xOffset);
		return actor;

	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignSame(
			ASSIGNEE actor, RELATIVE group) {

		actor.setPosition(group.getX(), group.getY());

		return actor;
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignTop(
			ASSIGNEE actor, RELATIVE group) {
		return RelativeLayout.alignTop(actor, group, 0);
	}

	public static <RELATIVE extends Actor, ASSIGNEE extends Actor> ASSIGNEE alignTop(
			ASSIGNEE actor, RELATIVE group, float yOffset) {

		actor.setY(group.getHeight() - actor.getHeight() - yOffset);

		return actor;
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE center(ASSIGNEE actor) {
		actor.setPosition(AbstractScreen.WIDTH / 2f - actor.getWidth() / 2f,
				AbstractScreen.HEIGHT / 2f - actor.getHeight() / 2f);

		return actor;
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE centerHorizontal(
			ASSIGNEE actor) {

		actor.setX(AbstractScreen.WIDTH / 2f - actor.getWidth() / 2f);

		return actor;
	}

	public static <ASSIGNEE extends Actor, RELATIVE extends Actor> ASSIGNEE centerHorizontal(
			ASSIGNEE actor, RELATIVE relative) {

		actor.setX(relative.getX() + relative.getWidth() / 2 - actor.getWidth()
				/ 2f);

		return actor;
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE centerVertical(
			ASSIGNEE actor) {

		actor.setY(AbstractScreen.HEIGHT / 2f - actor.getHeight() / 2f);

		return actor;
	}

	public static WidgetGroup group(final float width, final float height,
			Actor... actors) {
		WidgetGroup group = new WidgetGroup() {

			@Override
			public float getPrefHeight() {
				return height;
			}

			@Override
			public float getPrefWidth() {
				return width;
			}
		};
		group.setWidth(width);
		group.setHeight(height);
		if (actors != null) {
			for (Actor actor : actors) {
				group.addActor(actor);
			}
		}

		return group;
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE marginBottom(
			ASSIGNEE actor, float margin) {

		actor.setY(actor.getY() + margin);

		return actor;
	}

	public static void marginBottom(float margin, Actor... actors) {
		for (Actor actor : actors) {
			actor.setY(actor.getY() + margin);
		}
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE marginLeft(ASSIGNEE actor,
			float margin) {

		actor.setX(actor.getX() + margin);

		return actor;
	}

	public static void marginLeft(float margin, Actor... actors) {
		for (Actor actor : actors) {
			actor.setX(actor.getX() + margin);
		}
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE marginRight(ASSIGNEE actor,
			float margin) {
		return marginLeft(actor, -margin);
	}

	public static void marginRight(float margin, Actor... actors) {
		marginLeft(-margin, actors);
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE marginTop(ASSIGNEE actor,
			float margin) {
		return marginBottom(actor, -margin);
	}

	public static void marginTop(float margin, Actor... actors) {
		marginBottom(-margin, actors);
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE pad(ASSIGNEE actor,
			float padX, float padY) {
		actor.setPosition(actor.getX() + padX, actor.getY() + padY);
		return actor;
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE right(ASSIGNEE actor) {
		return right(actor, 0);

	}

	public static <ASSIGNEE extends Actor> ASSIGNEE right(ASSIGNEE actor,
			float rightMargin) {
		actor.setX(AbstractScreen.WIDTH - actor.getWidth() - rightMargin);
		return actor;

	}

	public static <ASSIGNEE extends Actor> ASSIGNEE top(ASSIGNEE actor) {
		return top(actor, 0);
	}

	public static <ASSIGNEE extends Actor> ASSIGNEE top(ASSIGNEE actor,
			float topPadding) {
		actor.setY(AbstractScreen.HEIGHT - actor.getHeight() - topPadding);
		return actor;
	}

}
