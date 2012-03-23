package fr.imag.recommender.stackexchange;

import java.util.List;

import com.google.code.stackexchange.client.query.QuestionApiQuery;
import com.google.code.stackexchange.client.query.SearchApiQuery;
import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.client.query.TagApiQuery;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.Tag;

/**
 * 
 * @author jccastrejon
 * 
 */
public class StackExchangeService {

	/**
	 * 
	 */
	private static StackExchangeApiQueryFactory queryFactory;

	/**
	 * 
	 */
	private static QuestionApiQuery questionApi;

	/**
	 * 
	 */
	private static SearchApiQuery searchApi;

	/**
	 * 
	 */
	private static TagApiQuery tagApi;

	static {
		queryFactory = StackExchangeApiQueryFactory.newInstance(null);
		questionApi = queryFactory.newQuestionApiQuery();
		searchApi = queryFactory.newSearchApiQuery();
		tagApi = queryFactory.newTagApiQuery();
	}

	/**
	 * 
	 * @param keywords
	 */
	public static List<Question> getQuestionsWithTags(final List<String> tags) {
		List<Question> returnValue;

		returnValue = null;
		if ((tags != null) && (!tags.isEmpty())) {
			returnValue = questionApi.withTags(tags).list();
		}

		return returnValue;
	}

	/**
	 * 
	 * @param title
	 * @return
	 */
	public static List<Question> getQuestionsInTitle(final String title) {
		List<Question> returnValue;

		returnValue = null;
		if (title != null) {
			returnValue = searchApi.withInTitle(title).list();
		}

		return returnValue;
	}

	/**
	 * 
	 * @return
	 */
	public static List<Question> getUnansweredQuestions() {
		return questionApi.listUnansweredQuestions();
	}

	/**
	 * 
	 * @return
	 */
	public static List<Tag> getPopularTags() {
		return tagApi.list();
	}
}