package io.kowalski.ozone.models;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = -6815751153340207542L;

    private String chatId;
    private String sender;
    private String recipient;
    private String message;

    public Message() {

    }

    public final String getChatId() {
        return chatId;
    }

    public final void setChatId(final String chatId) {
        this.chatId = chatId;
    }

    public final String getSender() {
        return sender;
    }

    public final void setSender(final String sender) {
        this.sender = sender;
    }

    public final String getRecipient() {
        return recipient;
    }

    public final void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public final String getMessage() {
        return message;
    }

    public final void setMessage(final String message) {
        this.message = message;
    }

}
