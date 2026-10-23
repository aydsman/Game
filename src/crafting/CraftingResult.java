package crafting;

public class CraftingResult {
    private final boolean success;
    private final String message;

    private CraftingResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static CraftingResult ok(String message) {
        return new CraftingResult(true, message);
    }

    public static CraftingResult fail(String message) {
        return new CraftingResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
