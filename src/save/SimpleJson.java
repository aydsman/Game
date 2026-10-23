package save;

import java.util.*;

/**
 * Simple JSON parser/writer that doesn't require external libraries.
 * Handles basic types: strings, numbers, booleans, arrays, and objects.
 */
public class SimpleJson {
    
    public static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",\n");
            first = false;
            sb.append("  \"").append(escape(entry.getKey())).append("\": ");
            sb.append(valueToJson(entry.getValue()));
        }
        sb.append("\n}");
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    private static String valueToJson(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + escape((String) value) + "\"";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return value.toString();
        if (value instanceof List) {
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            List<?> list = (List<?>) value;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",\n");
                sb.append("    ").append(valueToJson(list.get(i)));
            }
            sb.append("\n  ]");
            return sb.toString();
        }
        if (value instanceof Map) {
            return toJson((Map<String, Object>) value);
        }
        return "\"" + escape(value.toString()) + "\"";
    }
    
    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    public static Map<String, Object> fromJson(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }
        
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return result;
        
        List<String> pairs = splitPairs(json);
        for (String pair : pairs) {
            int colonIdx = pair.indexOf(':');
            if (colonIdx > 0) {
                String key = unescape(pair.substring(0, colonIdx).trim());
                String value = pair.substring(colonIdx + 1).trim();
                result.put(stripQuotes(key), parseValue(value));
            }
        }
        
        return result;
    }
    
    private static List<String> splitPairs(String json) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        boolean inString = false;
        
        for (char c : json.toCharArray()) {
            if (c == '"' && (current.length() == 0 || current.charAt(current.length() - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString) {
                if (c == '{' || c == '[') depth++;
                if (c == '}' || c == ']') depth--;
            }
            
            if (c == ',' && depth == 0 && !inString) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            result.add(current.toString().trim());
        }
        
        return result;
    }
    
    private static Object parseValue(String value) {
        value = value.trim();
        
        if (value.equals("null")) return null;
        if (value.equals("true")) return true;
        if (value.equals("false")) return false;
        
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return unescape(stripQuotes(value));
        }
        
        if (value.startsWith("[") && value.endsWith("]")) {
            return parseArray(value);
        }
        
        if (value.startsWith("{") && value.endsWith("}")) {
            return fromJson(value);
        }
        
        // Try to parse as number
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return value;
        }
    }
    
    private static List<Object> parseArray(String value) {
        List<Object> result = new ArrayList<>();
        value = value.substring(1, value.length() - 1).trim();
        if (value.isEmpty()) return result;
        
        List<String> items = splitPairs(value);
        for (String item : items) {
            result.add(parseValue(item));
        }
        return result;
    }
    
    private static String stripQuotes(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
    
    private static String unescape(String s) {
        return s.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }
}
