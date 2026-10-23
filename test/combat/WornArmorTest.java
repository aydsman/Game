package combat;

import combat.armor.WornArmor;
import testutil.TestSupport;
import player.CharacterAppearance;

public final class WornArmorTest {

    public static void run(TestSupport t) {
        t.checkEquals("null appearance is 0", 0.0, WornArmor.mitigationFor(null), 0.0001);

        CharacterAppearance empty = new CharacterAppearance();
        empty.setEquippedTopId(null);
        empty.setEquippedBottomId(null);
        empty.setEquippedAccessoryId(null);
        t.checkEquals("empty outfit is 0", 0.0, WornArmor.mitigationFor(empty), 0.0001);

        CharacterAppearance starter = new CharacterAppearance();
        t.checkEquals("default t-shirt and pants",
                WornArmor.TOP_MITIGATION + WornArmor.BOTTOM_MITIGATION,
                WornArmor.mitigationFor(starter),
                0.0001);

        starter.setEquippedAccessoryId("glasses");
        t.checkEquals("full outfit includes accessory",
                WornArmor.TOP_MITIGATION + WornArmor.BOTTOM_MITIGATION + WornArmor.ACCESSORY_MITIGATION,
                WornArmor.mitigationFor(starter),
                0.0001);

        starter.setEquippedTopId("none");
        t.checkEquals("none is treated as unequipped",
                WornArmor.BOTTOM_MITIGATION + WornArmor.ACCESSORY_MITIGATION,
                WornArmor.mitigationFor(starter),
                0.0001);
    }
}
