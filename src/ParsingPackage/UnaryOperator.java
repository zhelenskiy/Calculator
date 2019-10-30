package ParsingPackage;

import ParsingPackage.Exceptions.Int32Exception;
import kgeorgiy.expression.TripleExpression;

import java.util.function.ToLongFunction;

public class UnaryOperator extends Operator {
    private ToLongFunction<Integer> IFuncLongEx;

    private Evaluator a;

    private int eval(int a) throws Int32Exception {
        long res = IFuncLongEx.applyAsLong(a);
        if (res == (int) res) {
            return (int) res;
        } else {
            throw new Int32Exception("The number must be a 32-bit integer.");
        }
    }

    public TripleExpression getFunction() {
        return (x, y, z) -> eval(a.getFunction().evaluate(x, y, z));
    }

    UnaryOperator(Evaluator a, ToLongFunction<Integer> func) {
        this.a = a;
        this.IFuncLongEx = func;
    }

    @Override
    Operator setLastOperand(Evaluator value) {
        a = value;
        return this;
    }

    @Override
    public UnaryOperator clone() {
        return new UnaryOperator(a, IFuncLongEx);
    }
}
