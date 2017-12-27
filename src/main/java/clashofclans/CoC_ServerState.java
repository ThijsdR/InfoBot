package clashofclans;

public enum CoC_ServerState {
    COCWENTOFFLINE("<<Clash of Clans>>\nServer is OFFLINE gegaan!"),
    COCWENTONLINE("<<Clash of Clans>>\nServer is weer ONLINE!"),
    COCONLINE("<<Clash of Clans>>\nServer is online"),
    COCOFFLINE("<<Clash of Clans>>\nServer is offline");

    private final String stateDescription;

    CoC_ServerState(String stateDescription) {
        this.stateDescription = stateDescription;
    }

    public String getStateDescription() {
        return stateDescription;
    }
}
