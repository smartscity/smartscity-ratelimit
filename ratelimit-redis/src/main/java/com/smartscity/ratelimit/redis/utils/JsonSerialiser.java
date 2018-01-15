package com.smartscity.ratelimit.redis.utils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.smartscity.ratelimit.core.limiter.request.RequestLimitRule;

public class JsonSerialiser {

    public String encode(Iterable<RequestLimitRule> rules) {
        JsonArray jsonArray = Json.array().asArray();
        rules.forEach(rule -> jsonArray.add(toJsonArray(rule)));
        return jsonArray.toString();
    }

    private JsonArray toJsonArray(RequestLimitRule rule) {
        return Json.array().asArray()
                .add(rule.getDurationSeconds())
                .add(rule.getLimit())
                .add(rule.getPrecision());
    }
}
