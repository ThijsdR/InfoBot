package botCommands;

import org.telegram.telegrambots.api.methods.send.SendMessage;

public class CommandBuilder {

    private String messageText;
    private String[] commands;
    private String[] locatieCommands;
    private SendMessage sendMessage;
    private long chatID;

    public CommandBuilder(String messageText, String[] commands, String[] locatieCommands, SendMessage sendMessage, long chatID) {
        this.messageText = messageText;
        this.commands = commands;
        this.locatieCommands = locatieCommands;
        this.sendMessage = sendMessage;
        this.chatID = chatID;
    }

    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String[] getCommands() {
        return commands;
    }
    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    public String[] getLocatieCommands() {
        return locatieCommands;
    }
    public void setLocatieCommands(String[] locatieCommands) {
        this.locatieCommands = locatieCommands;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
    }
    public void setSendMessage(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    public long getChatID() {
        return chatID;
    }
    public void setChatID(long chatID) {
        this.chatID = chatID;
    }
}
