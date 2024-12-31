package example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSample {
    @Test
    public void testAdd() {
        // Arrange
        Calculator calculator = new Calculator();

        // Act
        int result = calculator.add(2, 3);

        // Print result
        System.out.println(result);

        // Assert
        assertEquals(5, result, "2 + 3 should equal 5");
    }
}
