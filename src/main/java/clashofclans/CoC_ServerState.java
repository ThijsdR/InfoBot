package clashofclans;

import utility.TextFormatting;

/**
 * Hulpklasse waar de mogelijke server statussen in zijn opgeslagen
 */
public enum CoC_ServerState
{

    /* Verschillende statussen */
    COCWENTOFFLINE(TextFormatting.toBold("<<Clash of Clans>>\nServer is OFFLINE gegaan!")),
    COCWENTONLINE(TextFormatting.toBold("<<Clash of Clans>>\nServer is weer ONLINE!")),
    COCONLINE(TextFormatting.toBold("<<Clash of Clans>>\nServer is online")),
    COCOFFLINE(TextFormatting.toBold("<<Clash of Clans>>\nServer is offline"));

    /* Veld */
    private final String stateDescription;

    /* Constructor */
    CoC_ServerState(String stateDescription)
    {
        this.stateDescription = stateDescription;
    }

    /* Getter */
    public String getStateDescription()
    {
        return stateDescription;
    }
}
