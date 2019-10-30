package ParsingPackage;

import kgeorgiy.expression.TripleExpression;
import org.jetbrains.annotations.Contract;

public class Int32 extends Operand {
    private final int number;

    private Int32(int number) {
        this.number = number;
    }

    @Contract("null -> null; !null -> new")
    static Int32 create(Integer number) {
        return number == null ? null : new Int32(number);
    }

    public TripleExpression getFunction() {
        return (x, y, z) -> number;
    }
}
