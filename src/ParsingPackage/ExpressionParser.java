package ParsingPackage;

import ParsingPackage.Exceptions.*;
import kgeorgiy.expression.TripleExpression;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public class ExpressionParser implements kgeorgiy.expression.parser.Parser {
    private State curState;
    private Deque<State> stack;
    private Expectant expectant;

    @NotNull
    @Contract("_ -> new")
    private Evaluator putInBrackets(Evaluator value) {
        return new UnaryOperator(value, a -> a);
    }

    @Override
    public TripleExpression parse(String str) {
        expectant = new Expectant(str, 0);
        stack = new ArrayDeque<>();
        curState = new State(null, null);
        while (true) {
            Evaluator operand = readOperand();
            curState = curState.addOperand(operand);
            closeBrackets();
            if (expectant.isEnd()) {
                checkForUnclosedBrackets(str);
                return curState.outer.getFunction();
            }
            BinaryOperator operator = expectant.takeBinaryOperator();
            checkForNoOperator(operator);
            curState = operator.merge(curState.outer);
        }
    }

    private void checkForNoOperator(BinaryOperator operator) {
        if (operator == null) {
            int old = expectant.getErrorIndex();
            throw new NotFoundException("An operator expected, \"" + expectant.takeLexeme() + "\" found at position " + old + ".");
        }
    }

    private void checkForUnclosedBrackets(String str) {
        if (stack.size() > 0) {
            throw new WrongBracketException("There are unclosed brackets at the end of \"" + str + "\" at position " + expectant.getErrorIndex() + ".");
        }
    }

    private void closeBrackets() {
        while (expectant.takeLexeme(")") != null) {
            if (stack.size() == 0) {
                throw new WrongBracketException("There is extra closing bracket at position " + expectant.getErrorIndex() + ".");
            }
            Evaluator old = putInBrackets(curState.outer);
            curState = stack.pop().addOperand(old);
        }
    }

    static class State {
        Evaluator inner;
        final Evaluator outer;

        State(Evaluator inner, Evaluator outer) {
            this.inner = inner;
            this.outer = outer;
        }

        private State addOperand(Evaluator value) {
            return addOperand(new State(value, value));
        }

        private State addOperand(State value) {
            if (inner == null) {
                return value;
            } else {
                this.inner = ((Operator) this.inner).setLastOperand(value.outer);
                if (value.inner != null) {
                    return new State(value.inner, this.outer);
                }
                return this;
            }
        }
    }

    @SafeVarargs
    @Nullable
    private static Evaluator firstNotNull(@NotNull Supplier<Evaluator>... args) {
        for (Supplier<Evaluator> evaluator : args) {
            Evaluator res = evaluator.get();
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    private Evaluator readOperand() {
        State localState = new State(null, null);
        while (true) {
            Evaluator res = firstNotNull(
                    () -> expectant.takeInt32(),
                    () -> expectant.takeVariable(),
                    () -> expectant.takeUnaryOperator());
            if (res == null) {
                if (expectant.takeLexeme("(") == null) {
                    int errorIndex = expectant.getErrorIndex();
                    throw new NotFoundException("Operand (Int32, \"x\", \"y\", \"z\" or prefix unary operator) expected, \"" + expectant.takeLexeme() + "\" is found at position " + errorIndex + ".");
                }
                stack.push(this.curState.addOperand(localState));
                this.curState = new State(null, null);
                localState = new State(null, null);
                continue;
            }
            if (res instanceof UnaryOperator) {
                localState = localState.addOperand(res);
            } else /*res is a variable or number*/ {
                localState = localState.addOperand(res);
                return localState.outer;
            }
        }
    }
}
