package app.filters;

import org.javalite.activeweb.controller_filters.HttpSupportFilter;
import org.javalite.common.Collections;

public class CatchFilter extends HttpSupportFilter{

	@Override
	public void onException(Exception e) {
		logError(e.toString(), e);
		if (e != null) {
			render("/system/error", Collections.map("code", 400, "message", e.toString()));
		} 
		render("/system/error", Collections.map("code", 400, "message", "Something went wrong"));
	}

}
