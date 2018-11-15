package utility;

import org.telegram.telegrambots.api.methods.send.SendMessage;

/**
 * Een klasse noodzakelijk om een commando op te bouwen
 */
public class CommandContainer
{

    /* Velden */
    private String messageText;
    private String[] commands;
    private String[] locatieCommands;
    private SendMessage sendMessage;
    private long chatID;

    /* Constructor */
    public CommandContainer(String messageText, String[] commands, String[] locatieCommands, SendMessage sendMessage, long chatID)
    {
        this.messageText = messageText;
        this.commands = commands;
        this.locatieCommands = locatieCommands;
        this.sendMessage = sendMessage;
        this.chatID = chatID;
    }

    /* Getters */
    public String getMessageText()
    {
        return messageText;
    }

    public String[] getCommands()
    {
        return commands;
    }

    public String[] getLocatieCommands()
    {
        return locatieCommands;
    }

    public SendMessage getSendMessage()
    {
        return sendMessage;
    }

    public long getChatID()
    {
        return chatID;
    }
}
