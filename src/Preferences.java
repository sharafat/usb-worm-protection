import java.io.Serializable;

public class Preferences implements Serializable {
        boolean shouldDoSilentScan = true;
        int suspectedFileAction = 0;    // 0 = Prompt, 1 = Delete, 2 = Quarantine, 3 = Leave
        int protectionEnableStatus = 0; // 0 = Enabled or Enable at next program restart,
                                        // 1 = Enable at user request only
        boolean shouldShowSlideInScanDialog = true;
        boolean shouldRemoveSlideInDialogAfterScan = false;
        boolean shouldAlwaysDisplayScanDetails = false;
}