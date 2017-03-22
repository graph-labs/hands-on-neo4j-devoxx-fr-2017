package net.biville.florent.devoxxfr2017;

import com.beust.jcommander.ParameterException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class NonEmptyValueValidatorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private NonEmptyValueValidator validator = new NonEmptyValueValidator();

    @Test
    public void validates_non_empty_string() {
        thrown.expect(ParameterException.class);
        thrown.expectMessage("Parameter foo should not be empty");

        validator.validate("foo", "   ");
    }
}
