package sportclub;

import sportclub.domain.usecase.ClubManager;

public class SportClubApplication {

    public static void main(String[] args) {
        ClubManager clubManager = new ClubManager();
        clubManager.showMenu();
    }
}
