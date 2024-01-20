package com.smart.helper;

/**
 * Represents a message with content and type.
 */
public record Message(String content, String type) {

	/**
	 * Constructs a new Message.
	 *
	 * @param content The content of the message.
	 * @param type    The type of the message.
	 */
	public Message {
	}

	/**
	 * Gets the content of the message.
	 *
	 * @return The content of the message.
	 */
	@Override
	public String content() {
		return content;
	}

	/**
	 * Gets the type of the message.
	 *
	 * @return The type of the message.
	 */
	@Override
	public String type() {
		return type;
	}
}
