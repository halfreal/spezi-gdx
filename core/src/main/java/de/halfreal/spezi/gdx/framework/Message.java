package de.halfreal.spezi.gdx.framework;

import java.awt.TrayIcon.MessageType;

public class Message<M> {

	private MessageType category;
	private long id;
	private long lastUpdate;
	private M message;
	private boolean read;
	private boolean rewardCollected;
	private long timestamp;
	private Object[] values;

	public Message() {
	}

	public Message(MessageType category, long id, long lastUpdate, M message,
			boolean read, boolean rewardCollected, long timestamp,
			Object[] values) {
		this.category = category;
		this.id = id;
		this.lastUpdate = lastUpdate;
		this.message = message;
		this.read = read;
		this.rewardCollected = rewardCollected;
		this.timestamp = timestamp;
		this.values = values;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Message) {
			return id == ((Message) obj).id;
		}

		return false;
	}

	public MessageType getCategory() {
		return category;
	}

	public long getId() {
		return id;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public M getMessage() {
		return message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Object[] getValues() {
		return values;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

	public boolean isRead() {
		return read;
	}

	public boolean isRewardCollected() {
		return rewardCollected;
	}

	public void setCategory(MessageType category) {
		this.category = category;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setMessage(M message) {
		this.message = message;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public void setRewardCollected(boolean rewardCollected) {
		this.rewardCollected = rewardCollected;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

}
