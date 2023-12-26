package ninjabrainbot.data.divine;

import java.util.Random;

public interface IDivinable {
    Random random = new Random();

    boolean seedSatisfiesDivineCondition(long seed);
}
