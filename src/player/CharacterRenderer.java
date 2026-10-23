package player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.File;

public class CharacterRenderer {

    /** Exact palette keys used by every recolorable body sprite. */
    private static final Color SOURCE_HIGHLIGHT = new Color(0xBB, 0xBB, 0xBB);
    private static final Color SOURCE_BASE = new Color(0xAA, 0xAA, 0xAA);
    private static final Color SOURCE_SHADOW = new Color(0x77, 0x77, 0x77);
    private static final Color SOURCE_EYE_HIGHLIGHT = new Color(0x8F, 0xCB, 0xFF);
    private static final Color SOURCE_EYE_BASE = new Color(0x4A, 0x82, 0xB8);
    private static final Color SOURCE_EYE_SHADOW = new Color(0x24, 0x49, 0x6B);
    private static final Color SOURCE_HAIR_HIGHLIGHT = new Color(0xDD, 0xDD, 0xDD);
    private static final Color SOURCE_HAIR_BASE = new Color(0x99, 0x99, 0x99);
    private static final Color SOURCE_HAIR_SHADOW = new Color(0x55, 0x55, 0x55);
    private static final Color[][] DEFAULT_SKIN_PALETTES = {
        {new Color(255, 224, 189), new Color(255, 206, 152), new Color(255, 188, 115)},
        {new Color(241, 194, 125), new Color(223, 166, 83), new Color(205, 138, 41)},
        {new Color(198, 134, 66), new Color(141, 85, 36), new Color(83, 36, 7)},
        {new Color(165, 107, 70), new Color(120, 63, 4), new Color(77, 25, 0)}
    };
    private static final Color[][] DEFAULT_EYE_PALETTES = {
        {new Color(143, 203, 255), new Color(74, 130, 184), new Color(36, 73, 107)},
        {new Color(210, 162, 104), new Color(126, 78, 42), new Color(70, 39, 25)},
        {new Color(152, 220, 151), new Color(72, 142, 79), new Color(31, 79, 43)},
        {new Color(215, 219, 229), new Color(125, 134, 151), new Color(57, 63, 76)}
    };
    private static final Color[][] DEFAULT_HAIR_PALETTES = {
        {new Color(97, 73, 61), new Color(57, 40, 34), new Color(29, 23, 22)},
        {new Color(66, 63, 70), new Color(33, 31, 37), new Color(15, 14, 18)},
        {new Color(244, 213, 126), new Color(200, 158, 75), new Color(119, 85, 39)},
        {new Color(185, 88, 58), new Color(124, 52, 37), new Color(69, 28, 26)}
    };

    private final Map<String, BufferedImage> cachedAssets = new HashMap<>();

    public static Color[][] getDefaultSkinPalettes() {
        return copyPalettes(DEFAULT_SKIN_PALETTES);
    }

    public static Color[][] getDefaultEyePalettes() {
        return copyPalettes(DEFAULT_EYE_PALETTES);
    }

    public static Color[][] getDefaultHairPalettes() {
        return copyPalettes(DEFAULT_HAIR_PALETTES);
    }

    private static Color[][] copyPalettes(Color[][] palettes) {
        Color[][] copy = new Color[palettes.length][];
        for (int i = 0; i < palettes.length; i++) {
            copy[i] = palettes[i].clone();
        }
        return copy;
    }

    public void loadAssets(CharacterAppearance appearance, Color[][] skinPalettes) {
        loadAssets(appearance, skinPalettes, Map.of());
    }

    public void loadAssets(
            CharacterAppearance appearance,
            Color[][] skinPalettes,
            Map<String, String> equippedClothingStyles) {
        cachedAssets.clear();

        if (skinPalettes == null || skinPalettes.length == 0) {
            throw new IllegalArgumentException("At least one skin palette is required");
        }
        int paletteIndex = Math.max(0, Math.min(appearance.getSkinColorIndex(), skinPalettes.length - 1));
        Color[] palette = skinPalettes[paletteIndex];
        if (palette == null || palette.length != 3) {
            throw new IllegalArgumentException("Each skin palette must contain highlight, base, and shadow");
        }

        loadFrame("idle_down", "assets/player/body/man/idle/idle_front.png", palette);
        loadFrame("idle_down_right", "assets/player/body/man/idle/idle_front_right.png", palette);
        cachedAssets.put("idle_down_left", flipHorizontal(cachedAssets.get("idle_down_right")));
        loadFrame("idle_up", "assets/player/body/man/idle/idle_back.png", palette);
        loadFrame("idle_up_right", "assets/player/body/man/idle/idle_back_right.png", palette);
        cachedAssets.put("idle_up_left", flipHorizontal(cachedAssets.get("idle_up_right")));
        loadFrame("idle_right", "assets/player/body/man/idle/idle_right.png", palette);
        cachedAssets.put("idle_left", flipHorizontal(cachedAssets.get("idle_right")));

        loadFrame("walk_down_0", "assets/player/body/man/walking/forward/front_walk1.png", palette);
        loadFrame("walk_down_1", "assets/player/body/man/walking/forward/front_walk2.png", palette);
        loadFrame("walk_down_right_0", "assets/player/body/man/walking/forward_right/front_right_walk1.png", palette);
        loadFrame("walk_down_right_1", "assets/player/body/man/walking/forward_right/front_right_walk2.png", palette);
        cachedAssets.put("walk_down_left_0", flipHorizontal(cachedAssets.get("walk_down_right_0")));
        cachedAssets.put("walk_down_left_1", flipHorizontal(cachedAssets.get("walk_down_right_1")));
        loadFrame("walk_up_0", "assets/player/body/man/walking/backward/back_walk1.png", palette);
        loadFrame("walk_up_1", "assets/player/body/man/walking/backward/back_walk2.png", palette);
        loadFrame("walk_up_right_0", "assets/player/body/man/walking/backward_right/back_right_walk1.png", palette);
        loadFrame("walk_up_right_1", "assets/player/body/man/walking/backward_right/back_right_walk2.png", palette);
        cachedAssets.put("walk_up_left_0", flipHorizontal(cachedAssets.get("walk_up_right_0")));
        cachedAssets.put("walk_up_left_1", flipHorizontal(cachedAssets.get("walk_up_right_1")));
        loadFrame("walk_right_0", "assets/player/body/man/walking/right/right_walk1.png", palette);
        loadFrame("walk_right_1", "assets/player/body/man/walking/right/right_walk2.png", palette);
        cachedAssets.put("walk_left_0", flipHorizontal(cachedAssets.get("walk_right_0")));
        cachedAssets.put("walk_left_1", flipHorizontal(cachedAssets.get("walk_right_1")));

        loadFrame("gun_idle_down", "assets/player/body/man/gun/idle/idle_front.png", palette);
        loadFrame("gun_idle_down_right", "assets/player/body/man/gun/idle/idle_front_right.png", palette);
        cachedAssets.put("gun_idle_down_left", flipHorizontal(cachedAssets.get("gun_idle_down_right")));
        loadFrame("gun_idle_up", "assets/player/body/man/gun/idle/idle_back.png", palette);
        loadFrame("gun_idle_up_right", "assets/player/body/man/gun/idle/idle_back_right.png", palette);
        cachedAssets.put("gun_idle_up_left", flipHorizontal(cachedAssets.get("gun_idle_up_right")));
        loadFrame("gun_idle_right", "assets/player/body/man/gun/idle/idle_right.png", palette);
        cachedAssets.put("gun_idle_left", flipHorizontal(cachedAssets.get("gun_idle_right")));
        loadFrame("gun_walk_down_0", "assets/player/body/man/gun/walking/forward/front_walk1.png", palette);
        loadFrame("gun_walk_down_1", "assets/player/body/man/gun/walking/forward/front_walk2.png", palette);
        loadFrame("gun_walk_down_right_0", "assets/player/body/man/gun/walking/forward_right/front_right_walk1.png", palette);
        loadFrame("gun_walk_down_right_1", "assets/player/body/man/gun/walking/forward_right/front_right_walk2.png", palette);
        cachedAssets.put("gun_walk_down_left_0", flipHorizontal(cachedAssets.get("gun_walk_down_right_0")));
        cachedAssets.put("gun_walk_down_left_1", flipHorizontal(cachedAssets.get("gun_walk_down_right_1")));
        loadFrame("gun_walk_up_0", "assets/player/body/man/gun/walking/backward/back_walk1.png", palette);
        loadFrame("gun_walk_up_1", "assets/player/body/man/gun/walking/backward/back_walk2.png", palette);
        loadFrame("gun_walk_up_right_0", "assets/player/body/man/gun/walking/backward_right/back_right_walk1.png", palette);
        loadFrame("gun_walk_up_right_1", "assets/player/body/man/gun/walking/backward_right/back_right_walk2.png", palette);
        cachedAssets.put("gun_walk_up_left_0", flipHorizontal(cachedAssets.get("gun_walk_up_right_0")));
        cachedAssets.put("gun_walk_up_left_1", flipHorizontal(cachedAssets.get("gun_walk_up_right_1")));
        loadFrame("gun_walk_right_0", "assets/player/body/man/gun/walking/right/right_walk1.png", palette);
        loadFrame("gun_walk_right_1", "assets/player/body/man/gun/walking/right/right_walk2.png", palette);
        cachedAssets.put("gun_walk_left_0", flipHorizontal(cachedAssets.get("gun_walk_right_0")));
        cachedAssets.put("gun_walk_left_1", flipHorizontal(cachedAssets.get("gun_walk_right_1")));

        for (AimDirection aim : AimDirection.values()) {
            String prefix = "assets/player/body/man/arms/gun/" + aim.fileName;
            loadFrame("gun_arm_" + aim.fileName + "_rear", prefix + "_rear.png", palette);
            loadFrame("gun_arm_" + aim.fileName + "_front", prefix + "_front.png", palette);
        }

        loadAppearanceLayers(appearance);
        loadClothingLayers(appearance,
                equippedClothingStyles == null ? Map.of() : equippedClothingStyles);
        loadRedesignPreviewFrames(palette, appearance);
        composeAppearanceFrames();

        // Keep the existing preview API working while the layered renderer is built out.
        cachedAssets.put("testCharacter", cachedAssets.get("idle_down"));
    }

    private void loadRedesignPreviewFrames(Color[] palette, CharacterAppearance appearance) {
        String root = "assets/player/body/man/redesign/";
        loadFrame("redesign_down", root + "front_idle.png", palette);
        loadFrame("redesign_down_right", root + "front_right_idle.png", palette);
        loadFrame("redesign_right", root + "right_idle.png", palette);
        loadFrame("redesign_up_right", root + "back_right_idle.png", palette);
        loadFrame("redesign_up", root + "back_idle.png", palette);
        loadFrame("redesign_up_left", root + "back_left_idle.png", palette);
        loadFrame("redesign_left", root + "left_idle.png", palette);
        loadFrame("redesign_down_left", root + "front_left_idle.png", palette);
        loadFrame("redesign_gun_down", root + "front_gun.png", palette);
        loadFrame("redesign_gun_down_right", root + "front_right_gun.png", palette);
        loadFrame("redesign_gun_right", root + "right_gun.png", palette);
        loadFrame("redesign_gun_up_right", root + "back_right_gun.png", palette);
        loadFrame("redesign_gun_up", root + "back_gun.png", palette);

        String eyeStyle = safeAssetId(appearance.getEyeShape(), "round");
        String mouth = safeAssetId(appearance.getMouthExpression(), "neutral");
        String hairStyle = safeAssetId(appearance.getHairStyle(), "fringe");
        if ("short".equals(hairStyle)
                || !new File(root + "features/hair/" + hairStyle + "_front.png").isFile()) {
            hairStyle = "fringe";
        }
        Color[] eyePalette = paletteAt(DEFAULT_EYE_PALETTES, appearance.getEyeColorIndex());
        Color[] hairPalette = paletteAt(DEFAULT_HAIR_PALETTES, appearance.getHairColorIndex());
        Color[] eyeSource = {SOURCE_EYE_HIGHLIGHT, SOURCE_EYE_BASE, SOURCE_EYE_SHADOW};
        Color[] hairSource = {SOURCE_HAIR_HIGHLIGHT, SOURCE_HAIR_BASE, SOURCE_HAIR_SHADOW};
        for (String direction : new String[] {"front", "front_right", "right", "back_right", "back"}) {
            loadFrame("redesign_hair_" + direction,
                    root + "features/hair/" + hairStyle + "_" + direction + ".png",
                    hairSource, hairPalette);
            loadFrame("redesign_hair_rear_" + direction,
                    root + "features/hair/" + hairStyle + "_" + direction + "_rear.png",
                    hairSource, hairPalette);
        }
        for (String direction : new String[] {"front", "front_right", "right"}) {
            loadFrame("redesign_eyes_" + direction,
                    root + "features/eyes/" + eyeStyle + "_" + direction + ".png",
                    eyeSource, eyePalette);
            loadUnchangedFrame("redesign_mouth_" + direction,
                    root + "features/mouth/" + mouth + "_" + direction + ".png");
        }
        composeRedesignAppearance("redesign_down", "front", true);
        composeRedesignAppearance("redesign_down_right", "front_right", true);
        composeRedesignAppearance("redesign_right", "right", true);
        composeRedesignAppearance("redesign_up_right", "back_right", false);
        composeRedesignAppearance("redesign_up", "back", false);
        composeRedesignAppearance("redesign_gun_down", "front", true);
        composeRedesignAppearance("redesign_gun_down_right", "front_right", true);
        composeRedesignAppearance("redesign_gun_right", "right", true);
        composeRedesignAppearance("redesign_gun_up_right", "back_right", false);
        composeRedesignAppearance("redesign_gun_up", "back", false);
        cachedAssets.put("redesign_down_left",
                flipHorizontal(cachedAssets.get("redesign_down_right")));
        cachedAssets.put("redesign_left",
                flipHorizontal(cachedAssets.get("redesign_right")));
        cachedAssets.put("redesign_up_left",
                flipHorizontal(cachedAssets.get("redesign_up_right")));
        cachedAssets.put("redesign_gun_down_left",
                flipHorizontal(cachedAssets.get("redesign_gun_down_right")));
        cachedAssets.put("redesign_gun_left",
                flipHorizontal(cachedAssets.get("redesign_gun_right")));
        cachedAssets.put("redesign_gun_up_left",
                flipHorizontal(cachedAssets.get("redesign_gun_up_right")));
    }

    private void composeRedesignAppearance(
            String bodyKey, String direction, boolean drawFace) {
        BufferedImage body = cachedAssets.get(bodyKey);
        BufferedImage composed = new BufferedImage(
                body.getWidth(), body.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = composed.createGraphics();
        drawLayer(g, "redesign_hair_rear_" + direction, 0);
        g.drawImage(body, 0, 0, null);
        drawLayer(g, "feature_body_marking_" + direction, 0);
        if (drawFace) {
            drawLayer(g, "redesign_eyes_" + direction, 0);
            drawLayer(g, "redesign_mouth_" + direction, 0);
            drawLayer(g, "feature_face_marking_" + direction, 0);
        }
        drawLayer(g, "redesign_hair_" + direction, 0);
        g.dispose();
        cachedAssets.put(bodyKey, composed);
    }

    private void loadAppearanceLayers(CharacterAppearance appearance) {
        String eyeStyle = safeAssetId(appearance.getEyeShape(), "round");
        String mouth = safeAssetId(appearance.getMouthExpression(), "neutral");
        String hairStyle = safeAssetId(appearance.getHairStyle(), "fringe");
        String faceMarking = safeAssetId(appearance.getFaceMarkingId(), "none");
        String bodyMarking = safeAssetId(appearance.getBodyMarkingId(), "none");
        if ("short".equals(hairStyle)
                || !new File("assets/player/shared/hair/" + hairStyle + "_front.png").isFile()) {
            hairStyle = "fringe";
        }

        Color[] eyePalette = paletteAt(DEFAULT_EYE_PALETTES, appearance.getEyeColorIndex());
        Color[] hairPalette = paletteAt(DEFAULT_HAIR_PALETTES, appearance.getHairColorIndex());
        Color[] eyeSource = {SOURCE_EYE_HIGHLIGHT, SOURCE_EYE_BASE, SOURCE_EYE_SHADOW};
        Color[] hairSource = {SOURCE_HAIR_HIGHLIGHT, SOURCE_HAIR_BASE, SOURCE_HAIR_SHADOW};

        loadFrame("feature_eyes_front", "assets/player/shared/eyes/" + eyeStyle + "_front.png",
                eyeSource, eyePalette);
        loadFrame("feature_eyes_front_right", "assets/player/shared/eyes/" + eyeStyle + "_front_right.png",
                eyeSource, eyePalette);
        loadFrame("feature_eyes_right", "assets/player/shared/eyes/" + eyeStyle + "_right.png",
                eyeSource, eyePalette);
        loadUnchangedFrame("feature_mouth_front", "assets/player/shared/mouth/" + mouth + "_front.png");
        loadUnchangedFrame("feature_mouth_front_right",
                "assets/player/shared/mouth/" + mouth + "_front_right.png");
        loadUnchangedFrame("feature_mouth_right", "assets/player/shared/mouth/" + mouth + "_right.png");
        loadFrame("feature_hair_front", "assets/player/shared/hair/" + hairStyle + "_front.png",
                hairSource, hairPalette);
        loadFrame("feature_hair_front_right", "assets/player/shared/hair/" + hairStyle + "_front_right.png",
                hairSource, hairPalette);
        loadFrame("feature_hair_right", "assets/player/shared/hair/" + hairStyle + "_right.png",
                hairSource, hairPalette);
        loadFrame("feature_hair_back_right", "assets/player/shared/hair/" + hairStyle + "_back_right.png",
                hairSource, hairPalette);
        loadFrame("feature_hair_back", "assets/player/shared/hair/" + hairStyle + "_back.png",
                hairSource, hairPalette);
        for (String direction : new String[] {"front", "front_right", "right", "back_right", "back"}) {
            loadFrame("feature_hair_rear_" + direction,
                    "assets/player/shared/hair/" + hairStyle + "_" + direction + "_rear.png",
                    hairSource, hairPalette);
        }
        if (!"none".equals(faceMarking)) {
            loadUnchangedFrame("feature_face_marking_front",
                    "assets/player/shared/face_markings/" + faceMarking + "_front.png");
            loadUnchangedFrame("feature_face_marking_front_right",
                    "assets/player/shared/face_markings/" + faceMarking + "_front_right.png");
            loadUnchangedFrame("feature_face_marking_right",
                    "assets/player/shared/face_markings/" + faceMarking + "_right.png");
        }
        if (!"none".equals(bodyMarking)) {
            loadUnchangedFrame("feature_body_marking_front",
                    "assets/player/shared/body_markings/" + bodyMarking + "_front.png");
            loadUnchangedFrame("feature_body_marking_front_right",
                    "assets/player/shared/body_markings/" + bodyMarking + "_front_right.png");
            loadUnchangedFrame("feature_body_marking_right",
                    "assets/player/shared/body_markings/" + bodyMarking + "_right.png");
            loadUnchangedFrame("feature_body_marking_back_right",
                    "assets/player/shared/body_markings/" + bodyMarking + "_back_right.png");
            loadUnchangedFrame("feature_body_marking_back",
                    "assets/player/shared/body_markings/" + bodyMarking + "_back.png");
        }
    }

    private static String safeAssetId(String value, String fallback) {
        if (value == null || !value.matches("[a-zA-Z0-9_-]+")) {
            return fallback;
        }
        return value.toLowerCase();
    }

    private static Color[] paletteAt(Color[][] palettes, int index) {
        return palettes[Math.max(0, Math.min(index, palettes.length - 1))];
    }

    private void loadClothingLayers(
            CharacterAppearance appearance,
            Map<String, String> equippedClothingStyles) {
        String top = safeAssetId(appearance.getEquippedTopId(), "none");
        String bottom = safeAssetId(appearance.getEquippedBottomId(), "none");
        String accessory = safeAssetId(appearance.getEquippedAccessoryId(), "none");
        String[] directions = {"front", "front_right", "right", "back_right", "back"};

        if (!"none".equals(top)
                && new File("assets/player/shared/tops/" + top + "/front_idle.png").isFile()) {
            Color[] palette = clothingPalette(top, equippedClothingStyles.get(top));
            for (String direction : directions) {
                for (String pose : new String[] {"idle", "walk1", "walk2", "gun"}) {
                    loadFrame("clothing_top_" + direction + "_" + pose,
                            "assets/player/shared/tops/" + top + "/" + direction + "_" + pose + ".png",
                            new Color[] {SOURCE_HAIR_HIGHLIGHT, SOURCE_HAIR_BASE, SOURCE_HAIR_SHADOW},
                            palette);
                }
            }
        }
        if (!"none".equals(bottom)
                && new File("assets/player/shared/bottoms/" + bottom + "/front_idle.png").isFile()) {
            Color[] palette = clothingPalette(bottom, equippedClothingStyles.get(bottom));
            for (String direction : directions) {
                for (String pose : new String[] {"idle", "walk1", "walk2"}) {
                    loadFrame("clothing_bottom_" + direction + "_" + pose,
                            "assets/player/shared/bottoms/" + bottom + "/" + direction + "_" + pose + ".png",
                            new Color[] {SOURCE_HAIR_HIGHLIGHT, SOURCE_HAIR_BASE, SOURCE_HAIR_SHADOW},
                            palette);
                }
            }
        }
        if (!"none".equals(accessory)
                && new File("assets/player/shared/accessories/" + accessory + "/front_front.png").isFile()) {
            Color[] palette = clothingPalette(accessory, equippedClothingStyles.get(accessory));
            for (String direction : directions) {
                loadFrame("clothing_accessory_rear_" + direction,
                        "assets/player/shared/accessories/" + accessory + "/" + direction + "_rear.png",
                        new Color[] {SOURCE_HAIR_HIGHLIGHT, SOURCE_HAIR_BASE, SOURCE_HAIR_SHADOW},
                        palette);
                loadFrame("clothing_accessory_front_" + direction,
                        "assets/player/shared/accessories/" + accessory + "/" + direction + "_front.png",
                        new Color[] {SOURCE_HAIR_HIGHLIGHT, SOURCE_HAIR_BASE, SOURCE_HAIR_SHADOW},
                        palette);
            }
        }
    }

    private static Color[] clothingPalette(String itemId, String style) {
        String key = style == null ? "" : style.toLowerCase();
        Color base = switch (key) {
            case "white" -> new Color(225, 227, 232);
            case "gray", "silver" -> new Color(130, 137, 148);
            case "navy", "dark wash", "striped blue" -> new Color(45, 66, 108);
            case "gold" -> new Color(215, 170, 45);
            case "green" -> new Color(54, 130, 77);
            case "red", "striped red" -> new Color(165, 52, 57);
            case "blue", "light wash" -> new Color(72, 126, 178);
            case "purple" -> new Color(116, 70, 151);
            case "beige" -> new Color(177, 153, 111);
            case "dark", "black" -> new Color(43, 45, 53);
            default -> defaultClothingColor(itemId);
        };
        return new Color[] {shiftColor(base, 38), base, shiftColor(base, -38)};
    }

    private static Color defaultClothingColor(String itemId) {
        return switch (itemId) {
            case "jorts", "baggyjeans" -> new Color(67, 104, 153);
            case "swimshorts" -> new Color(35, 151, 178);
            case "crown" -> new Color(215, 170, 45);
            case "cape" -> new Color(145, 40, 48);
            case "fedora" -> new Color(85, 65, 48);
            case "allseeingeye" -> new Color(132, 62, 174);
            case "headband" -> new Color(51, 55, 65);
            case "ninjaheadband" -> new Color(88, 94, 106);
            case "glasses", "shades", "aviators" -> new Color(44, 48, 56);
            case "hoodie", "sweatpants" -> new Color(91, 96, 108);
            default -> new Color(45, 47, 55);
        };
    }

    private static Color shiftColor(Color color, int amount) {
        return new Color(
                Math.max(0, Math.min(255, color.getRed() + amount)),
                Math.max(0, Math.min(255, color.getGreen() + amount)),
                Math.max(0, Math.min(255, color.getBlue() + amount)));
    }

    private void composeAppearanceFrames() {
        composeBodySet("");
        composeBodySet("gun_");
    }

    private void composeBodySet(String prefix) {
        composeFrame(prefix + "idle_down", "front", 0);
        composeFrame(prefix + "idle_down_right", "front_right", 0);
        cachedAssets.put(prefix + "idle_down_left",
                flipHorizontal(cachedAssets.get(prefix + "idle_down_right")));
        composeFrame(prefix + "idle_up", "back", 0);
        composeFrame(prefix + "idle_up_right", "back_right", 0);
        cachedAssets.put(prefix + "idle_up_left",
                flipHorizontal(cachedAssets.get(prefix + "idle_up_right")));
        composeFrame(prefix + "idle_right", "right", 0);
        cachedAssets.put(prefix + "idle_left",
                flipHorizontal(cachedAssets.get(prefix + "idle_right")));

        for (int frame = 0; frame < 2; frame++) {
            composeFrame(prefix + "walk_down_" + frame, "front", 1);
            composeFrame(prefix + "walk_down_right_" + frame, "front_right", 1);
            cachedAssets.put(prefix + "walk_down_left_" + frame,
                    flipHorizontal(cachedAssets.get(prefix + "walk_down_right_" + frame)));
            composeFrame(prefix + "walk_up_" + frame, "back", 1);
            composeFrame(prefix + "walk_up_right_" + frame, "back_right", 1);
            cachedAssets.put(prefix + "walk_up_left_" + frame,
                    flipHorizontal(cachedAssets.get(prefix + "walk_up_right_" + frame)));
            composeFrame(prefix + "walk_right_" + frame, "right", 1);
            cachedAssets.put(prefix + "walk_left_" + frame,
                    flipHorizontal(cachedAssets.get(prefix + "walk_right_" + frame)));
        }
    }

    private void composeFrame(String bodyKey, String direction, int yOffset) {
        BufferedImage body = cachedAssets.get(bodyKey);
        if (body == null) {
            return;
        }
        BufferedImage composed = new BufferedImage(body.getWidth(), body.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = composed.createGraphics();
        drawLayer(g, "clothing_accessory_rear_" + direction, yOffset);
        drawLayer(g, "feature_hair_rear_" + direction, yOffset);
        g.drawImage(body, 0, 0, null);
        drawLayer(g, "feature_body_marking_" + direction, yOffset);
        String pose = bodyKey.contains("walk")
                ? (bodyKey.endsWith("_0") ? "walk1" : "walk2")
                : "idle";
        drawLayer(g, "clothing_bottom_" + direction + "_" + pose, 0);
        boolean gunBody = bodyKey.startsWith("gun_");
        drawLayer(g, "clothing_top_" + direction
                + (gunBody ? "_gun" : "_" + pose), gunBody ? yOffset : 0);
        if (!direction.startsWith("back")) {
            drawLayer(g, "feature_eyes_" + direction, yOffset);
            drawLayer(g, "feature_mouth_" + direction, yOffset);
            drawLayer(g, "feature_face_marking_" + direction, yOffset);
        }
        drawLayer(g, "feature_hair_" + direction, yOffset);
        drawLayer(g, "clothing_accessory_front_" + direction, yOffset);
        g.dispose();
        cachedAssets.put(bodyKey, composed);
    }

    private void drawLayer(Graphics2D g, String key, int yOffset) {
        BufferedImage layer = cachedAssets.get(key);
        if (layer != null) {
            g.drawImage(layer, 0, yOffset, null);
        }
    }

    private void loadFrame(String key, String path, Color[] palette) {
        loadFrame(
                key,
                path,
                new Color[] {SOURCE_HIGHLIGHT, SOURCE_BASE, SOURCE_SHADOW},
                palette);
    }

    private void loadFrame(String key, String path, Color[] sourcePalette, Color[] targetPalette) {
        try {
            BufferedImage original = ImageIO.read(new File(path));
            if (original == null) {
                throw new IllegalArgumentException("Unsupported image: " + path);
            }
            cachedAssets.put(key, recolor(
                    original,
                    sourcePalette,
                    targetPalette));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load character frame: " + path, e);
        }
    }

    private void loadUnchangedFrame(String key, String path) {
        try {
            BufferedImage original = ImageIO.read(new File(path));
            if (original == null) {
                throw new IllegalArgumentException("Unsupported image: " + path);
            }
            cachedAssets.put(key, original);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load character frame: " + path, e);
        }
    }

    public BufferedImage recolor(BufferedImage src, Color[] from, Color[] to) {
        if (src == null || from == null || to == null || from.length != to.length) {
            throw new IllegalArgumentException("Source image and equally-sized palettes are required");
        }
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                int rgb = src.getRGB(x, y);
                Color pixel = new Color(rgb, true);
                boolean matched = false;
                for (int i = 0; i < from.length; i++) {
                    if (pixel.getRed() == from[i].getRed() &&
                            pixel.getGreen() == from[i].getGreen() &&
                            pixel.getBlue() == from[i].getBlue()) {
                        // Preserve original alpha
                        Color replacement = to[i];
                        int newRgb = (pixel.getAlpha() << 24) |
                                (replacement.getRed() << 16) |
                                (replacement.getGreen() << 8) |
                                replacement.getBlue();
                        result.setRGB(x, y, newRgb);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    result.setRGB(x, y, rgb); // keep original pixel
                }
            }
        }
        return result;
    }

    public BufferedImage flipHorizontal(BufferedImage src) {
        if (src == null) {
            return null;
        }
        BufferedImage flipped = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                flipped.setRGB(src.getWidth() - 1 - x, y, src.getRGB(x, y));
            }
        }
        return flipped;
    }

    public void draw(Graphics2D g, int x, int y, Direction direction, int animFrame, WeaponType weaponType, double mouseAngle) {
        boolean walking = animFrame >= 0;
        BufferedImage frame = getFrame(direction, walking, animFrame);
        if (frame != null) {
            g.drawImage(frame, x, y, null);
        }
        // Future layers will be drawn here in this order: body markings, eyes, mouth,
        // face markings, hair, top, bottom, accessory, then held-weapon overlays.
    }

    public BufferedImage getFrame(Direction direction, boolean walking, int animFrame) {
        return getRedesignedFrame(direction, false);
    }

    public BufferedImage getGunBodyFrame(Direction direction, boolean walking, int animFrame) {
        return getRedesignedFrame(direction, true);
    }

    public BufferedImage getRedesignedBodyFrame(Direction direction) {
        return getRedesignedFrame(direction, false);
    }

    private BufferedImage getRedesignedFrame(Direction direction, boolean gunBody) {
        String facing = switch (direction) {
            case DOWN -> "down";
            case DOWN_RIGHT -> "down_right";
            case RIGHT -> "right";
            case UP_RIGHT -> "up_right";
            case UP -> "up";
            case UP_LEFT -> "up_left";
            case LEFT -> "left";
            case DOWN_LEFT -> "down_left";
        };
        return cachedAssets.get("redesign_" + (gunBody ? "gun_" : "") + facing);
    }

    public BufferedImage getGunArmFrame(AimDirection aim, boolean rearLayer) {
        if (aim == null) {
            return null;
        }
        return cachedAssets.get(
                "gun_arm_" + aim.fileName + (rearLayer ? "_rear" : "_front"));
    }

    /** Snaps a screen-space aim angle to one of sixteen evenly spaced directions. */
    public static AimDirection getAimDirection(double angleRadians) {
        int octant = Math.floorMod(
                (int) Math.round(angleRadians / (Math.PI / 8.0)), 16);
        return AimDirection.values()[octant];
    }

    public BufferedImage getGunArmSprite(double mouseAngle) {
        return getGunArmFrame(getAimDirection(mouseAngle), false);
    }

    public BufferedImage getTestCharacter() {
        return cachedAssets.get("testCharacter");
    }

    public enum Direction {
        DOWN, DOWN_LEFT, LEFT, UP_LEFT, UP, UP_RIGHT, RIGHT, DOWN_RIGHT
    }

    public enum WeaponType {
        NONE, PISTOL, RIFLE, MELEE
    }

    public enum AimDirection {
        E("e"), ESE("ese"), SE("se"), SSE("sse"),
        S("s"), SSW("ssw"), SW("sw"), WSW("wsw"),
        W("w"), WNW("wnw"), NW("nw"), NNW("nnw"),
        N("n"), NNE("nne"), NE("ne"), ENE("ene");

        private final String fileName;

        AimDirection(String fileName) {
            this.fileName = fileName;
        }
    }
}
