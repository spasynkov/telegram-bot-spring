package com.example.telegrambotspring.utils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public interface SafeCallable {
	Logger LOGGER = LoggerFactory.getLogger(SafeCallable.class);

	default JSONObject safeCall(Callable<JSONObject> lambda) {
		try {
			return lambda.call();
		} catch (Exception e) {
			LOGGER.error("Exception occurred while processing callable function", e);

			JSONObject resp = new JSONObject();
			resp.put("error", e.getLocalizedMessage());
			return resp;
		}
	}


}
