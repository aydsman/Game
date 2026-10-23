package shop;

import combat.lootboxes.LootboxCatalog;
import java.util.ArrayList;
import java.util.List;

public final class ShopCatalog {
    private ShopCatalog() {}

    public static List<ShopEntry> smallOffers() {
        List<ShopEntry> list = new ArrayList<>();
        list.add(new ShopItemOffer(
                "offer_test_crate_single",
                "Test Crate Token",
                "Adds 1 Test Crate token to your lootbox inventory.",
                1000,
                0,
                "assets/items/ranged/rifles/rifle1/ak.png",
                ShopReward.lootboxToken(LootboxCatalog.TEST_CRATE_ID, 1)
        ));
        list.add(new ShopItemOffer(
                "offer_test_crate_pack",
                "Test Crate x5",
                "Bulk pack: 5 Test Crate tokens.",
                4500,
                0,
                "assets/items/charms/damage/simple_damage_charm.png",
                ShopReward.lootboxToken(LootboxCatalog.TEST_CRATE_ID, 5)
        ));
        list.add(new ShopItemOffer(
                "offer_clothes_crate",
                "Clothes Crate 1",
                "Gem crate focused on clothing/style unlocks.",
                0,
                25,
                "assets/items/consumables/potions/speed/medium_speed_potion.png",
                ShopReward.lootboxToken(LootboxCatalog.CLOTHES_CRATE_1_ID, 1)
        ));
        list.add(new ShopItemOffer(
                "offer_power_crate",
                "Power Surge Crate",
                "Shop-exclusive crate with power-focused weighted rolls.",
                0,
                40,
                "assets/player/shared/accessories/headband.png",
                ShopReward.lootboxToken(LootboxCatalog.SHOP_EXCLUSIVE_POWER_CRATE_ID, 1)
        ));
        list.add(new ShopItemOffer(
                "offer_fruit_crate",
                "Fruit Crate",
                "Devil-fruit focused crate with T2-T5 fruit powers.",
                0,
                55,
                "assets/items/powers/infinity/infinity_icon.png",
                ShopReward.lootboxToken(LootboxCatalog.FRUIT_CRATE_ID, 1)
        ));
        list.add(new ShopItemOffer(
                "offer_accessory_glasses",
                "Glasses",
                "Unlock accessory item: Glasses.",
                900,
                0,
                "assets/player/shared/accessories/glasses.png",
                ShopReward.unlockClothing("Glasses")
        ));
        list.add(new ShopItemOffer(
                "offer_gems_small",
                "Gem Pouch",
                "Get +25 gems.",
                1500,
                0,
                "assets/items/powers/infinity/infinity_icon.png",
                ShopReward.gems(25)
        ));
        list.add(new ShopItemOffer(
                "offer_charm_health_t2",
                "Health Charm II",
                "Unlock HealthCharm2 Tier II.",
                1200,
                0,
                "assets/items/charms/health/health_charm.png",
                ShopReward.item("HealthCharm2", 2, 1)
        ));
        list.add(new ShopItemOffer(
                "offer_cash_stack",
                "Cash Stack",
                "Get +1,000 cash.",
                0,
                16,
                "assets/items/powers/earth/earth_icon.png",
                ShopReward.cash(1000)
        ));
        list.add(new ShopItemOffer(
                "offer_charm_speed_t3",
                "Speed Charm III",
                "Unlock SpeedCharm3 Tier III.",
                0,
                42,
                "assets/items/charms/speed/advanced_speed_charm.png",
                ShopReward.item("SpeedCharm3", 3, 1)
        ));
        return list;
    }

    public static List<ShopBundle> featuredBundles() {
        List<ShopBundle> bundles = new ArrayList<>();

        bundles.add(new ShopBundle(
                "bundle_rookie_power",
                "Rookie Power Bundle",
                "Combat starter bundle with crate tokens and charm value.",
                3600,
                0,
                "assets/items/charms/damage/damage_charm.png"
        ).add(ShopReward.lootboxToken(LootboxCatalog.TEST_CRATE_ID, 3))
         .add(ShopReward.item("DamageCharm2", 2, 1))
         .add(ShopReward.cash(500)));

        bundles.add(new ShopBundle(
                "bundle_style_drop",
                "Style Drop Bundle",
                "Unlock fashion styles that are not in lootboxes.",
                0,
                65,
                "assets/player/shared/tops/hoodie.png"
        ).add(ShopReward.unlockStyle("Headband", "Purple"))
         .add(ShopReward.unlockStyle("Fedora", "Gold"))
         .add(ShopReward.unlockStyle("SuitTop", "Gold"))
         .add(ShopReward.unlockStyle("SuitBottom", "Gold")));

        bundles.add(new ShopBundle(
                "bundle_hunter",
                "Arena Hunter Bundle",
                "High-impact loadout bundle for wave pushes + tokens.",
                0,
                95,
                "assets/items/ranged/shotguns/pump.png"
        ).add(ShopReward.lootboxToken(LootboxCatalog.SHOP_EXCLUSIVE_POWER_CRATE_ID, 2))
         .add(ShopReward.item("Shotgun1", 3, 1))
         .add(ShopReward.item("DamageCharm3", 3, 1))
         .add(ShopReward.item("SpeedPotion3", 3, 3))
         .add(ShopReward.gems(15)));

        return bundles;
    }
}
