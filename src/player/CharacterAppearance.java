package player;

public class CharacterAppearance {

    public enum Gender {
        MALE, FEMALE
    }

    private Gender gender;
    private int skinColorIndex;
    private String eyeShape;
    private int eyeColorIndex;
    private String mouthExpression;
    private String hairStyle;
    private int hairColorIndex;
    private String faceMarkingId;
    private String bodyMarkingId;
    private String equippedTopId;
    private String equippedBottomId;
    private String equippedAccessoryId;

    // Default constructor for first-time setup
    public CharacterAppearance() {
        this.gender = Gender.MALE;
        this.skinColorIndex = 0;
        this.eyeShape = "round";
        this.eyeColorIndex = 0;
        this.mouthExpression = "neutral";
        this.hairStyle = "fringe";
        this.hairColorIndex = 0;
        this.faceMarkingId = "none";
        this.bodyMarkingId = "none";
        this.equippedTopId = "tshirt"; // Default starting top
        this.equippedBottomId = "basicpants"; // Default starting bottom
        this.equippedAccessoryId = null;
    }

    // Getters
    public Gender getGender() { return gender; }
    public int getSkinColorIndex() { return skinColorIndex; }
    public String getEyeShape() { return eyeShape; }
    public int getEyeColorIndex() { return eyeColorIndex; }
    public String getMouthExpression() { return mouthExpression; }
    public String getHairStyle() { return hairStyle; }
    public int getHairColorIndex() { return hairColorIndex; }
    public String getFaceMarkingId() { return faceMarkingId; }
    public String getBodyMarkingId() { return bodyMarkingId; }
    public String getEquippedTopId() { return equippedTopId; }
    public String getEquippedBottomId() { return equippedBottomId; }
    public String getEquippedAccessoryId() { return equippedAccessoryId; }

    // Setters
    public void setGender(Gender gender) { this.gender = gender; }
    public void setSkinColorIndex(int skinColorIndex) { this.skinColorIndex = skinColorIndex; }
    public void setEyeShape(String eyeShape) { this.eyeShape = eyeShape; }
    public void setEyeColorIndex(int eyeColorIndex) { this.eyeColorIndex = eyeColorIndex; }
    public void setMouthExpression(String mouthExpression) { this.mouthExpression = mouthExpression; }
    public void setHairStyle(String hairStyle) { this.hairStyle = hairStyle; }
    public void setHairColorIndex(int hairColorIndex) { this.hairColorIndex = hairColorIndex; }
    public void setFaceMarkingId(String faceMarkingId) { this.faceMarkingId = faceMarkingId; }
    public void setBodyMarkingId(String bodyMarkingId) { this.bodyMarkingId = bodyMarkingId; }
    public void setEquippedTopId(String equippedTopId) { this.equippedTopId = equippedTopId; }
    public void setEquippedBottomId(String equippedBottomId) { this.equippedBottomId = equippedBottomId; }
    public void setEquippedAccessoryId(String equippedAccessoryId) { this.equippedAccessoryId = equippedAccessoryId; }

    public boolean isFirstTimeSetup() {
        // For now, assume if gender is default, it's first time. Could be improved with a flag.
        return false; // TODO: Implement proper check, perhaps add a boolean field
    }
}
