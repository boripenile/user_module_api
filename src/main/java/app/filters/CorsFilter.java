package app.filters;

import org.javalite.activeweb.controller_filters.HttpSupportFilter;

public class CorsFilter extends HttpSupportFilter {

	private static final String headerAllowHeaders = "Access-Control-Allow-Headers";
	private static final String headerAllowOrigin = "Access-Control-Allow-Origin";
	private static final String headerRequestMethod = "Access-Control-Request-Method";
	private static final String wildcard = "*";
	
	@Override
	public void before() {
		header(headerAllowHeaders, wildcard);
		header(headerAllowOrigin, wildcard);
		header(headerRequestMethod, "OPTIONS, PATCH, POST, PUT, DELETE, GET");
		header(headerAllowHeaders, wildcard);
		System.out.println("Done checking headers");
	}
}
