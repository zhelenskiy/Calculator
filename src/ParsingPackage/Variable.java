package ParsingPackage;

import ParsingPackage.Exceptions.NotFoundException;
import kgeorgiy.expression.TripleExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Variable extends Operand {
    private final String name;

    private Variable(String name) {
        this.name = name;
    }

    @Nullable
    static Variable create(@NotNull String name) throws NotFoundException {
        return name.equals("x") || name.equals("y") || name.equals("z") ? new Variable(name) : null;
    }

    public TripleExpression getFunction() {
        return (x, y, z) -> name.equals("x") ? x : name.equals("y") ? y : z;
    }
}
