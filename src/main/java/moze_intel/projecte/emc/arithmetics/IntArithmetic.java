package moze_intel.projecte.emc.arithmetics;

import java.util.Collection;

public class IntArithmetic implements IValueArithmetic<Integer>{
	@Override
	public boolean isZero(Integer value) {
		return value == 0;
	}

	@Override
	public Integer getZero() {
		return 0;
	}

	@Override
	public Integer add(Collection<Integer> valueList) {
		int a = 0;
		for (Integer i: valueList) {
			if (isFree(i)) continue;
			a += i;
		}
		return a;
	}

	@Override
	public Integer mul(int a, Integer b) {
		if (this.isFree(b)) return getFree();
		return a * b;
	}

	@Override
	public Integer div(Integer a, int b) {
		if (this.isFree(a)) return getFree();
		return a / b;
	}

	@Override
	public Integer getFree() {
		return Integer.MIN_VALUE;
	}

	@Override
	public boolean isFree(Integer value) {
		return value == Integer.MIN_VALUE;
	}
}
