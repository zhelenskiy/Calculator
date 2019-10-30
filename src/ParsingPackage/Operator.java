package ParsingPackage;

abstract class Operator extends Evaluator implements Cloneable {
    abstract Operator setLastOperand(Evaluator value);
}
