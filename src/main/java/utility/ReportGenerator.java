package utility;

import clashofclans.CoC_ClanFile;
import java.util.TimerTask;

/**
 * Deze klasse voert een periodieke taak uit
 */
public class ReportGenerator extends TimerTask {

    /**
     * Voert automatisch deze taak uit
     */
    @Override
    public void run() {
        CoC_ClanFile.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getDefaultURL(), true);
    }
}
