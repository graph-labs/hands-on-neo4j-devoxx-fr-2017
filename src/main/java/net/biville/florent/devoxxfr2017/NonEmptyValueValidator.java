package net.biville.florent.devoxxfr2017;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class NonEmptyValueValidator implements IValueValidator<String> {

    @Override
    public void validate(String name, String value) throws ParameterException {
        if (value.trim().isEmpty()) {
            throw new ParameterException(String.format("Parameter %s should not be empty", name));
        }
    }
}
