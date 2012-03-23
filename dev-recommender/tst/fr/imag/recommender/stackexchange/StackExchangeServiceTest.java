package fr.imag.recommender.stackexchange;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.Tag;

/**
 * 
 * @author jccastrejon
 * 
 */
public class StackExchangeServiceTest {

	@Test
	public void testGetQuestions() {
		List<String> tags;
		List<Question> questions;

		tags = new ArrayList<String>();
		tags.add("java");
		tags.add("eclipse");

		// Valid tags
		questions = StackExchangeService.getQuestionsWithTags(tags);
		assertTrue(!questions.isEmpty());

		// Invalid tags
		tags.add("asdqe11");
		tags.add("qwe1356");

		// Valid tags
		questions = StackExchangeService.getQuestionsWithTags(tags);
		assertTrue((questions == null) || (questions.isEmpty()));
	}

	@Test
	public void testGetUnansweredQuestions() {
		List<Question> questions;

		questions = StackExchangeService.getUnansweredQuestions();
		assertTrue(!questions.isEmpty());
	}

	@Test
	public void testGetPopularTags() {
		List<Tag> tags;

		tags = StackExchangeService.getPopularTags();
		assertTrue(!tags.isEmpty());
	}
}
