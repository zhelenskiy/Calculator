package ParsingPackage;

import ParsingPackage.Exceptions.Int32Exception;
import ParsingPackage.ExpressionParser.State;
import kgeorgiy.expression.TripleExpression;
import org.jetbrains.annotations.NotNull;

import java.util.function.ToLongBiFunction;

public class BinaryOperator extends Operator {
    private final ToLongBiFunction<Integer, Integer> IFuncLongEx;

    private final int priority;

    private Evaluator a, b;

    private int eval(int a, int b) throws Int32Exception {
        long res = IFuncLongEx.applyAsLong(a, b);
        if (res == (int) res) {
            return (int) res;
        } else {
            throw new Int32Exception("The number must be a 32-bit integer, got " + res + " during computation of " + a + " and " + b + ".");
        }
    }

    public TripleExpression getFunction() {
        return (x, y, z) -> eval(a.getFunction().evaluate(x, y, z), b.getFunction().evaluate(x, y, z));
    }

    @Override
    Operator setLastOperand(Evaluator value) {
        b = value;
        return this;
    }

    BinaryOperator(Evaluator a, Evaluator b, ToLongBiFunction<Integer, Integer> func, int priority) {
        this.a = a;
        this.b = b;
        this.IFuncLongEx = func;
        this.priority = priority;
    }

    @Override
    public BinaryOperator clone() {
        return new BinaryOperator(a, b, IFuncLongEx, priority);
    }

    @NotNull State merge(@NotNull Evaluator operand) {
        final BinaryOperator leftOperator = operand instanceof BinaryOperator ? (BinaryOperator) operand : null;
        if (leftOperator != null && leftOperator.priority < this.priority) {
            State state = this.merge(leftOperator.b);
            leftOperator.setLastOperand(state.outer);
            return new State(state.inner, leftOperator);
        } else {
            this.a = operand;
            return new State(this, this);
        }
    }
}
