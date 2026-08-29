package player;

public class CharacterAppearance {
    public enum Gender { MALE, FEMALE }

    private Gender gender = Gender.MALE;
    private int skinColorIndex = 0;
    private String eyeShape = "round";
    private String mouthExpression = "neutral";
    private String hairStyle = "short";
    private String faceMarkingId = "none";
    private String bodyMarkingId = "none";
    private String equippedTopId = "tshirt";
    private String equippedBottomId = "shorts";
    private String equippedAccessoryId = null;

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public int getSkinColorIndex() { return skinColorIndex; }
    public void setSkinColorIndex(int skinColorIndex) { this.skinColorIndex = skinColorIndex; }
    public String getEyeShape() { return eyeShape; }
    public void setEyeShape(String eyeShape) { this.eyeShape = eyeShape; }
    public String getMouthExpression() { return mouthExpression; }
    public void setMouthExpression(String mouthExpression) { this.mouthExpression = mouthExpression; }
    public String getHairStyle() { return hairStyle; }
    public void setHairStyle(String hairStyle) { this.hairStyle = hairStyle; }
    public String getFaceMarkingId() { return faceMarkingId; }
    public void setFaceMarkingId(String faceMarkingId) { this.faceMarkingId = faceMarkingId; }
    public String getBodyMarkingId() { return bodyMarkingId; }
    public void setBodyMarkingId(String bodyMarkingId) { this.bodyMarkingId = bodyMarkingId; }
    public String getEquippedTopId() { return equippedTopId; }
    public void setEquippedTopId(String equippedTopId) { this.equippedTopId = equippedTopId; }
    public String getEquippedBottomId() { return equippedBottomId; }
    public void setEquippedBottomId(String equippedBottomId) { this.equippedBottomId = equippedBottomId; }
    public String getEquippedAccessoryId() { return equippedAccessoryId; }
    public void setEquippedAccessoryId(String equippedAccessoryId) { this.equippedAccessoryId = equippedAccessoryId; }
}
