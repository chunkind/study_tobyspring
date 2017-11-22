package springbook.learningtest.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class CalcSumTest {
	Calculator calculator;
	String numFilepath;
	
	@Before
	public void setUp() {
		this.calculator = new Calculator();
		this.numFilepath = getClass().getResource("numbers.txt").getPath();
	}
	
	@Test
	public void sumOfNumbers() throws IOException{
		//old
//		Calculator calculator = new Calculator();
//		int sum = calculator.calcSum(getClass().getResource("numbers.txt").getPath());
//		assertThat(sum, is(10));
		//new
		assertThat(calculator.calcSum(this.numFilepath), is(10));
	}
	
	@Test
	public void multiplyOfNumbers() throws IOException{
		assertThat(calculator.calcMultiply(this.numFilepath), is(24));
	}
	
	@Test
	public void concatenateStrings() throws IOException{
		assertThat(calculator.concatename(this.numFilepath), is("1234"));
	}
	
}
