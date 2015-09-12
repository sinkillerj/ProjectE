package moze_intel.projecte.emc.arithmetics;

import java.util.Collection;

public class LongArithmetic implements IValueArithmetic<Long> {
	@Override
	public boolean isZero(Long value) {
		return value == 0;
	}

	@Override
	public Long getZero() {
		return 0L;
	}

	@Override
	public Long add(Collection<Long> valueList) {
		long a = 0;
		for (Long i: valueList) {
			if (isFree(i)) continue;
			a += i;
		}
		return a;
	}

	@Override
	public Long mul(int a, Long b) {
		return a * b;
	}

	@Override
	public Long div(Long a, int b) {
		return a / b;
	}

	@Override
	public Long getFree() {
		return Long.MIN_VALUE;
	}

	@Override
	public boolean isFree(Long value) {
		return value == Long.MIN_VALUE;
	}
}
