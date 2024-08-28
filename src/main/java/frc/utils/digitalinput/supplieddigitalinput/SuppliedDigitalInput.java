package frc.utils.digitalinput.supplieddigitalinput;

import edu.wpi.first.math.filter.Debouncer;
import frc.utils.digitalinput.DigitalInputInputsAutoLogged;
import frc.utils.digitalinput.IDigitalInput;

import java.util.function.Supplier;

public class SuppliedDigitalInput implements IDigitalInput {

	private final Debouncer debouncer;

	private final Supplier<Boolean> isTrueConsumer;

	public SuppliedDigitalInput(Supplier<Boolean> isTrueConsumer, double debounceTime, Debouncer.DebounceType debounceType) {
		this.isTrueConsumer = isTrueConsumer;
		this.debouncer = new Debouncer(debounceTime, debounceType);
	}

	@Override
	public void updateInputs(DigitalInputInputsAutoLogged inputs) {
		inputs.isTrueWithDebouncer = debouncer.calculate(isTrueConsumer.get());
		inputs.isTrueWithoutDebouncer = isTrueConsumer.get();
	}

}
