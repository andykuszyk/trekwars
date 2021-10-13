package trekwars.players;

public class PlayerMetadata {
    private final String name;
    private final String universe;
    public static final String STARTREK = "Star Trek";
    public static final String STARWARS = "Star Wars";
    private final String race;

    private PlayerMetadata(String name, String race, String universe) {
        this.name = name;
        this.universe = universe;
        this.race = race;
    }

    public String getName() {
        return this.name;
    }

    public String getUniverse() {
        return this.universe;
    }

    public String getRace() {
        return this.race;
    }

    public String getFormattedMetadata() {
        return String.format("Name     : %s\nRace      : %s\nUniverse : %s", name, race, universe);
    }

    public static PlayerMetadata fromPlayerFactoryType(PlayerFactoryType playerFactoryType) {
        switch(playerFactoryType) {
            case Voyager:
                return new PlayerMetadata("USS Voyager", "Federation", STARTREK);
            case Brel:
                return new PlayerMetadata("B'rel Bird of Prey", "Klingon", STARTREK);
            case Defiant:
                return new PlayerMetadata("USS Defiant", "Federation", STARTREK);
            case EnterpriseE:
                return new PlayerMetadata("USS Enterprise-E", "Federation", STARTREK);
            case Prometheus:
                return new PlayerMetadata("USS Prometheus", "Federation", STARTREK);
            case BorgTacticalCube:
                return new PlayerMetadata("Tactical Cube", "Borg", STARTREK);
            case CardassianGalor:
                return new PlayerMetadata("Galor", "Cardassian", STARTREK);
            case StarDestroyer:
                return new PlayerMetadata("Star Destroyer", "Imperial", STARWARS);
            case BorgCube:
                return new PlayerMetadata("Cube", "Borg", STARTREK);
            default:
                return new PlayerMetadata("unknown", "unknown", "unknown");
        }
    }
}
