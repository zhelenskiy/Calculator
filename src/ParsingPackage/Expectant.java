package ParsingPackage;

import ParsingPackage.Exceptions.NotFoundException;
import ParsingPackage.Exceptions.Int32Exception;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class Expectant {
    private int index;

    int getErrorIndex() {
        return index + 1;
    }

    private final String str;

    Expectant(String str, int index) {
        this.str = str;
        this.index = index;
    }

    Int32 takeInt32() throws Int32Exception {
        SafeReturner returner = new SafeReturner();
        int errorIndex = getErrorIndex();
        return returner.safeReturn(Int32.create(parseInt32(takeInt(), errorIndex)));
    }

    @Contract("null, _ -> null")
    @Nullable
    private Integer parseInt32(String str, int oldIndex) throws Int32Exception {
        if (str == null) {
            return null;
        }
        final BigInteger bigInteger = new BigInteger(str);
        if (bigInteger.compareTo(BigInteger.valueOf(bigInteger.intValue())) == 0) {
            return bigInteger.intValue();
        }
        throw new Int32Exception("Integer " + str + " at position " + oldIndex + " is too big for Int32.");
    }

    private static Map<String, BinaryOperator> binaryOperators = new HashMap<String, BinaryOperator>() {{
        put("+", new BinaryOperator(null, null, (a, b) -> (long) a + b, 1));
        put("-", new BinaryOperator(null, null, (a, b) -> (long) a - b, 1));
        put("*", new BinaryOperator(null, null, (a, b) -> (long) a * b, 2));
        put("/", new BinaryOperator(null, null, (a, b) -> (long) a / b, 2));
    }};

    private static Map<String, UnaryOperator> unaryOperators = Map.of(
            "high", new UnaryOperator(null, Integer::highestOneBit),
            "low", new UnaryOperator(null, Integer::lowestOneBit),
            "-", new UnaryOperator(null, a -> -(long) a)
    );
//        put("high", new UnaryOperator(null, Integer::highestOneBit));
//        put("low", new UnaryOperator(null, Integer::lowestOneBit));
//        put("-", new UnaryOperator(null, a -> -(long) a));
//    }};

    String takeLexeme() {
        skipSpaces();
        if (index == str.length()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean isAlphabetic = Character.isLetter(str.charAt(index));
        do {
            sb.append(str.charAt(index++));
        } while (index < str.length() && isAlphabetic && Character.isLetter(str.charAt(index)));
        return sb.toString();
    }

    @Nullable
    private String takeInt() {
        SafeReturner returner = new SafeReturner();
        if (index == str.length()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean negative = str.charAt(index) == '-';
        if (negative) {
            sb.append(str.charAt(index++));
            skipSpaces();
            if (index == str.length()) {
                returner.safeReturn(null);
            }
        }
        while (index < str.length() && Character.isDigit(str.charAt(index))) {
            sb.append(str.charAt(index++));
        }
        return sb.length() == 0 || negative && sb.length() == 1 ? null : sb.toString();
    }

    String takeLexeme(String str) {
        return (new SafeReturner()).safeReturn(takeLexeme().equals(str) ? str : null);
    }

    boolean isEnd() {
        return (new SafeReturner()).safeReturn(takeLexeme().isEmpty() ? 0 : null) != null;
    }

    @Nullable
    private <T> T takeOperator(@NotNull Map<String, T> operators) throws NotFoundException {
        SafeReturner returner = new SafeReturner();
        String lexeme = takeLexeme();
        return returner.safeReturn(operators.getOrDefault(lexeme, null));
    }

    BinaryOperator takeBinaryOperator() throws NotFoundException {
        BinaryOperator res = takeOperator(binaryOperators);
        return res == null ? null : res.clone();
    }

    UnaryOperator takeUnaryOperator() throws NotFoundException {
        UnaryOperator res = takeOperator(unaryOperators);
        return res == null ? null : res.clone();
    }

    Variable takeVariable() {
        return (new SafeReturner()).safeReturn(Variable.create(takeLexeme()));
    }

    private void skipSpaces() {
        while (index < str.length() && Character.isWhitespace(str.charAt(index))) {
            ++index;
        }
    }

    /**
     * Recovers the state of index before some action, if its result is null.
     */
    private class SafeReturner {
        private int old;

        SafeReturner() {
            skipSpaces();
            old = index;
        }

        <T> T safeReturn(@Nullable T value) {
            if (value == null) {
                index = old;
            }
            return value;
        }
    }
}
