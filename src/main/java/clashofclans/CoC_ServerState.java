package clashofclans;

/**
 * Hulpklasse waar de mogelijke server statussen in zijn opgeslagen
 */
public enum CoC_ServerState {

    /* Verschillende statussen */
    COCWENTOFFLINE("<<Clash of Clans>>\nServer is OFFLINE gegaan!"),
    COCWENTONLINE("<<Clash of Clans>>\nServer is weer ONLINE!"),
    COCONLINE("<<Clash of Clans>>\nServer is online"),
    COCOFFLINE("<<Clash of Clans>>\nServer is offline");

    /* Veld */
    private final String stateDescription;

    /* Constructor */
    CoC_ServerState(String stateDescription) {
        this.stateDescription = stateDescription;
    }

    /* Getter */
    public String getStateDescription() {
        return stateDescription;
    }
}
